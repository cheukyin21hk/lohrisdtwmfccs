package ars.fyp.utils;

/**
 * modified by Lohris Ng on 25/2/15.
 */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioPatterRecorder {
    private final static int[] sampleRates = {44100, 22050, 11025, 8000};

    public static AudioPatterRecorder getInstance() {
        AudioPatterRecorder result = null;
        int i=0;
        do {
            result = new AudioPatterRecorder(AudioSource.MIC,
                    sampleRates[i],
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
        } while((++i<sampleRates.length) & !(result.getState() == AudioPatterRecorder.State.INITIALIZING));
        return result;
    }

    /**
     * INITIALIZING : recorder is initializing;
     * READY : recorder has been initialized, recorder not yet started
     * RECORDING : recording
     * ERROR : reconstruction needed
     * STOPPED: reset needed
     */
    public enum State {INITIALIZING, READY, RECORDING, ERROR, STOPPED};

    // The interval in which the recorded samples are output to the file
    // Used only in uncompressed mode
    private static final int TIMER_INTERVAL = 120;

    // Recorder used for uncompressed recording
    private AudioRecord     audioRecorder = null;

    // Output file path
    private String          filePath = null;

    // Recorder state; see State
    private State          	state;

    // File writer (only in uncompressed mode)
    private RandomAccessFile randomAccessWriter;

    // Number of channels, sample rate, sample size(size in bits), buffer size, audio source, sample size(see AudioFormat)
    private short                    nChannels;
    private int                      sRate;
    private short                    mBitsPersample;
    private int                      mBufferSize;
    private int                      mAudioSource;
    private int                      aFormat;

    // Number of frames/samples written to file on each output(only in uncompressed mode)
    private int                      mPeriodInFrames;

    // Buffer for output(only in uncompressed mode)
    private short[]                   buffer;

    // Number of bytes written to file after header(only in uncompressed mode)
    // after stop() is called, this size is written to the header/data chunk in the wave file

    /**
     *
     * Returns the state of the recorder in a AudioPatterRecorder.State typed object.
     * Useful, as no exceptions are thrown.
     *
     * @return recorder state
     */
    public State getState() {
        return state;
    }


    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        //	periodic updates on the progress of the record head
        public void onPeriodicNotification(AudioRecord recorder) {
            if (State.STOPPED == state) {
                Log.d(AudioPatterRecorder.this.getClass().getName(), "recorder stopped");
                return;
            }
            int numOfBytes = audioRecorder.read(buffer, 0, buffer.length); // read audio data to buffer
            try {
                soundAnalysis();
            } catch (IOException e) {
                Log.wtf("error",e.toString());
            }

        }
        //	reached a notification marker set by setNotificationMarkerPosition(int)
        public void onMarkerReached(AudioRecord recorder) {
        }
    };
    /**
     *
     *
     * Default constructor
     *
     * Instantiates a new recorder
     * In case of errors, no exception is thrown, but the state is set to ERROR
     *
     */
    public AudioPatterRecorder(int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        try {
            if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                mBitsPersample = 16;
            } else {
                mBitsPersample = 8;
            }

            if (channelConfig == AudioFormat.CHANNEL_IN_MONO) {
                nChannels = 1;
            } else {
                nChannels = 2;
            }

            mAudioSource = audioSource;
            sRate   = sampleRate;
            aFormat = audioFormat;

            mPeriodInFrames = sampleRate * TIMER_INTERVAL / 1000;		//?
            mBufferSize = mPeriodInFrames * 2  * nChannels * mBitsPersample / 8;		//?
            if (mBufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
                // Check to make sure buffer size is not smaller than the smallest allowed one
                mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                // Set frame period and timer interval accordingly
                mPeriodInFrames = mBufferSize / ( 2 * mBitsPersample * nChannels / 8 );
                Log.w(AudioPatterRecorder.class.getName(), "Increasing buffer size to " + Integer.toString(mBufferSize));
            }

            audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, mBufferSize);
            if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new Exception("AudioRecord initialization failed");
            }
            audioRecorder.setRecordPositionUpdateListener(updateListener);
            audioRecorder.setPositionNotificationPeriod(mPeriodInFrames);
            filePath = null;
            state = State.INITIALIZING;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(AudioPatterRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(AudioPatterRecorder.class.getName(), "Unknown error occured while initializing recording");
            }
            state = State.ERROR;
        }
    }

    /**
     * Sets output file path, call directly after construction/reset.
     *
     * @param output file path
     *
     */
    public void setOutputFile(String argPath) {
        try {
            if (state == State.INITIALIZING) {
                filePath = argPath;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(AudioPatterRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(AudioPatterRecorder.class.getName(), "Unknown error occured while setting output path");
            }
            state = State.ERROR;
        }
    }


    /**
     *
     * Prepares the recorder for recording, in case the recorder is not in the INITIALIZING state and the file path was not set
     * the recorder is set to the ERROR state, which makes a reconstruction necessary.
     * In case uncompressed recording is toggled, the header of the wave file is written.
     * In case of an exception, the state is changed to ERROR
     *
     */
    public void prepare() {
        try {
            if (state == State.INITIALIZING) {
                if ((audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) & (filePath != null)) {
                    // write file header
                    randomAccessWriter = new RandomAccessFile(filePath, "rw");
                    randomAccessWriter.setLength(0);

                    //mPeriodInFrames * 2   * mBitsPersample/8* nChannels
                    buffer = new short[mPeriodInFrames*nChannels];
                    state = State.READY;
                } else {
                    Log.e(AudioPatterRecorder.class.getName(), "prepare() method called on uninitialized recorder");
                    state = State.ERROR;
                }
            } else {
                Log.e(AudioPatterRecorder.class.getName(), "prepare() method called on illegal state");
                release();
                state = State.ERROR;
            }
        } catch(Exception e) {
            if (e.getMessage() != null) {
                Log.e(AudioPatterRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(AudioPatterRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            state = State.ERROR;
        }
    }

    /**
     *
     *
     *  Releases the resources associated with this class, and removes the unnecessary files, when necessary
     *
     */
    public void release() {
        if (state == State.RECORDING) {
            stop();
        } else {
            if (state == State.READY){
                try {
                    randomAccessWriter.close(); // Remove prepared file
                } catch (IOException e) {
                    Log.e(AudioPatterRecorder.class.getName(), "I/O exception occured while closing output file");
                }
                (new File(filePath)).delete();
            }
        }

        if (audioRecorder != null) {
            audioRecorder.release();
        }
    }

    /**
     *
     *
     * Resets the recorder to the INITIALIZING state, as if it was just created.
     * In case the class was in RECORDING state, the recording is stopped.
     * In case of exceptions the class is set to the ERROR state.
     *
     */
    public void reset() {
        try {
            if (state != State.ERROR) {
                release();
                filePath = null; // Reset file path
                audioRecorder = new AudioRecord(mAudioSource, sRate, nChannels, aFormat, mBufferSize);
                if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    throw new Exception("AudioRecord initialization failed");
                }
                audioRecorder.setRecordPositionUpdateListener(updateListener);
                audioRecorder.setPositionNotificationPeriod(mPeriodInFrames);
                state = State.INITIALIZING;
            }
        } catch (Exception e) {
            Log.e(AudioPatterRecorder.class.getName(), e.getMessage());
            state = State.ERROR;
        }
    }

    /**
     *
     *
     * Starts the recording, and sets the state to RECORDING.
     * Call after prepare().
     *
     */
    public void start() {
        if (state == State.READY) {
            audioRecorder.startRecording();
            audioRecorder.read(buffer, 0, buffer.length);	//[TODO: is this necessary]read the existing data in audio hardware, but don't do anything
            try {
                soundAnalysis();
            } catch (IOException e) {
                e.printStackTrace();
            }
            state = State.RECORDING;
        } else {
            Log.e(AudioPatterRecorder.class.getName(), "start() called on illegal state");
            state = State.ERROR;
        }
    }

    /**
     *
     *
     *  Stops the recording, and sets the state to STOPPED.
     * In case of further usage, a reset is needed.
     * Also finalizes the wave file in case of uncompressed recording.
     *
     */
    public void stop() {
        if (state == State.RECORDING) {
            audioRecorder.stop();
            try {

                randomAccessWriter.close();
            } catch(IOException e) {
                Log.e(AudioPatterRecorder.class.getName(), "I/O exception occured while closing output file");
                state = State.ERROR;
            }
            state = State.STOPPED;
        } else {
            Log.e(AudioPatterRecorder.class.getName(), "stop() called on illegal state");
            state = State.ERROR;
        }
    }

    public void soundAnalysis() throws IOException {
        for(int i =0; i < buffer.length ; i++)
        {
            randomAccessWriter.writeShort(buffer[i]);
        }
    }

    public void printFrequency(int offsetSize)
    {

    }

    public void printAmplitudeDistribution()
    {
        short amplitude1 = 0;
        short amplitude2 = 0;
        int numberOfLoop = (buffer.length/4)*4;
        int counting[] = new int[10];
        int countingAverage[] = new int[10];
        for(int i = 0;i<10;i++)
        {
            counting[i] = 0;
            countingAverage[i] =0 ;
        }
        for(int i = 0;i<numberOfLoop;i+=2)
        {
            amplitude1 = buffer[i];
            amplitude2 = buffer[i+1];
            compareAmplitude(counting,countingAverage,amplitude1);
            compareAmplitude(counting,countingAverage,amplitude2);
        }
        if(counting[0] != 0)
            Log.wtf("Method : periodic - ","signal btw -100000 - -10000 : " + countingAverage[0]/counting[0]);
        if(counting[1] != 0)
            Log.wtf("Method : periodic - ","signal btw -10000 - -1000 : " + countingAverage[1]/counting[1]);
        if(counting[2] != 0)
            Log.wtf("Method : periodic - ","signal btw -1000 - -100 : " + countingAverage[2]/counting[2]);
        if(counting[3] != 0)
            Log.wtf("Method : periodic - ","signal btw -100 - -10 : " + countingAverage[3]/counting[3]);
        if(counting[4] != 0)
            Log.wtf("Method : periodic - ","signal btw -10 - 0 : " + countingAverage[4]/counting[4]);
        if(counting[5] != 0)
            Log.wtf("Method : periodic - ","signal btw 0 - 10 : " + countingAverage[5]/counting[5]);
        if(counting[6] != 0)
            Log.wtf("Method : periodic - ","signal btw 10 - 100 : " + countingAverage[6]/counting[6]);
        if(counting[7] != 0)
            Log.wtf("Method : periodic - ","signal btw 100 - 1000 : " + countingAverage[7]/counting[7]);
        if(counting[8] != 0)
            Log.wtf("Method : periodic - ","signal btw 1000 - 10000 : " + countingAverage[8]/counting[8]);
        if(counting[9] != 0)
            Log.wtf("Method : periodic - ","signal btw 10000 - 100000 : " + countingAverage[9]/counting[9]);
    }

    public void compareAmplitude(int[] counting,int[] countingAverage,int compareValue)
    {
        if(compareValue >=-100000 && compareValue <-10000) {
            counting[0]++;
            countingAverage[0]+=compareValue;
        }
        else if(compareValue >=-10000 && compareValue <-1000)
        {
            counting[1]++;
            countingAverage[1]+=compareValue;
        }
        else if(compareValue >=-1000 && compareValue <-100)
        {   counting[2]++;
            countingAverage[2]+=compareValue;
        }
        else if(compareValue >=-100 && compareValue <-10)
        {   counting[3]++;
            countingAverage[3]+=compareValue;
        }
        else if(compareValue >-10 && compareValue <0)
        {   counting[4]++;
            countingAverage[4]+=compareValue;
        }
        else if(compareValue >= 0 && compareValue <10)
        {   counting[5]++;
            countingAverage[5]+=compareValue;
        }
        else if(compareValue >= 10 && compareValue <100)
        {   counting[6]++;
            countingAverage[6]+=compareValue;
        }
        else if(compareValue >= 100 && compareValue <1000)
        {   counting[7]++;
            countingAverage[7]+=compareValue;
        }
        else if(compareValue >= 1000 && compareValue <10000) {
            counting[8]++;
            countingAverage[8]+=compareValue;
        }
        else if(compareValue >= 10000 && compareValue <100000) {
            counting[9]++;
            countingAverage[9]+=compareValue;
        }

    }



}
