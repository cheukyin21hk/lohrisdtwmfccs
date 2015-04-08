package ars.fyp.utils;

/**
 * modified by Lohris Ng on 25/2/15.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AudioPatterRecorder {
    private final static int[] sampleRates = {44100, 22050, 11025, 8000};
    private static final int TIMER_INTERVAL = 120;
    private AudioRecord audioRecorder = null;
    private String filePath = null;
    private State state;
    private RandomAccessFile randomAccessWriter;
    private short nChannels,mBitsPersample;
    private int sRate,mBufferSize,mAudioSource,aFormat,mPeriodInFrames;
    private short[] buffer;
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        public void onPeriodicNotification(AudioRecord recorder) {
            if (State.STOPPED == state) {
                Log.d(AudioPatterRecorder.this.getClass().getName(), "recorder stopped");
                return;
            }
            int numOfBytes = audioRecorder.read(buffer, 0, buffer.length); // read audio data to bufferˆ
            soundAnalysis();
        }
        public void onMarkerReached(AudioRecord recorder) {
        }
    };

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
            sRate = sampleRate;
            aFormat = audioFormat;

            mPeriodInFrames = sampleRate * TIMER_INTERVAL / 1000;        //?
            mBufferSize = mPeriodInFrames * 2 * nChannels * mBitsPersample / 8;        //?
            if (mBufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
                // Check to make sure buffer size is not smaller than the smallest allowed one
                mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                // Set frame period and timer interval accordingly
                mPeriodInFrames = mBufferSize / (2 * mBitsPersample * nChannels / 8);
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

    public static AudioPatterRecorder getInstance() {
        AudioPatterRecorder result = null;
        int i = 0;
        do {
            result = new AudioPatterRecorder(AudioSource.MIC,
                    sampleRates[i],
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
        }
        while ((++i < sampleRates.length) & !(result.getState() == AudioPatterRecorder.State.INITIALIZING));
        return result;
    }

    public State getState() {
        return state;
    }

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

    public void prepare() {
        try {
            if (state == State.INITIALIZING) {
                if ((audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) & (filePath != null)) {
                    // write file header
                    randomAccessWriter = new RandomAccessFile(filePath, "rw");
                    randomAccessWriter.setLength(0);

                    buffer = new short[mPeriodInFrames * nChannels];
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
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(AudioPatterRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(AudioPatterRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            state = State.ERROR;
        }
    }

    public void release() {
        if (state == State.RECORDING) {
            stop();
        } else {
            if (state == State.READY) {
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

    public void start() {
        if (state == State.READY) {
            audioRecorder.startRecording();
            audioRecorder.read(buffer, 0, buffer.length);
            state = State.RECORDING;
        } else {
            Log.e(AudioPatterRecorder.class.getName(), "start() called on illegal state");
            state = State.ERROR;
        }
    }

    public void stop() {
        if (state == State.RECORDING) {
            audioRecorder.stop();
            try {

                randomAccessWriter.close();
            } catch (IOException e) {
                Log.e(AudioPatterRecorder.class.getName(), "I/O exception occured while closing output file");
                state = State.ERROR;
            }
            state = State.STOPPED;
        } else {
            Log.e(AudioPatterRecorder.class.getName(), "stop() called on illegal state");
            state = State.ERROR;
        }
    }

    public void soundAnalysis() {
        int avgAmp = 0;
        int crossingZero = 0 ;
        for (int i = 0; i < buffer.length-1; i ++) {
            if (buffer[i] < 0 && buffer[i + 1] >= 0)
                crossingZero++;
            else if (buffer[i+1] < 0 && buffer[i] >= 0)
                crossingZero++;
            if(i %2 ==0)
                avgAmp += (buffer[i] * buffer[i]) + (buffer[i+1] * buffer[i+1]);
        }

        avgAmp = (int)Math.round(Math.sqrt(avgAmp / buffer.length)/150.0);
        crossingZero = (int)Math.round(crossingZero * 44100/buffer.length/2000.0);
        Log.i(this.getClass().toString(),"Average amplitude from APR: "+ avgAmp);
        Log.i(this.getClass().toString(),"crossingZero from APR: "+ crossingZero);
        try {
            randomAccessWriter.writeInt(avgAmp);
            randomAccessWriter.writeInt(crossingZero);
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "The random access file write error occurs");
            Log.e(this.getClass().toString(), e.toString());
        }

    }

    public enum State {
        INITIALIZING, READY, RECORDING, ERROR, STOPPED
    }
}
