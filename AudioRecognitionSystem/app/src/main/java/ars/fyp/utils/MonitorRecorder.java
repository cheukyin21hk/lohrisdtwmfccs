package ars.fyp.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lohris on 3/4/15.
 */
public class MonitorRecorder {
    private final static int[] sampleRates = {44100, 22050, 11025, 8000};
    private static final int TIMER_INTERVAL = 120;
    private boolean foundPattern;
    private AudioRecord audioRecorder = null;
    private State state;
    private short nChannels, mBitsPersample;
    private int sRate, mBufferSize, mAudioSource, aFormat, mPeriodInFrames, sampleLength;
    private List<Integer> targetPattern, bufferedData, lcsBuffer;
    private short[] buffer;
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        public void onPeriodicNotification(AudioRecord recorder) {
            if (State.STOPPED == state) {
                Log.d(this.getClass().getName(), "recorder stopped");
                return;
            }
            int numOfBytes = audioRecorder.read(buffer, 0, buffer.length); // read audio data to bufferˆ
            Log.wtf(this.getClass().toString(),"Keep reading");
            soundAnalysis();
        }

        public void onMarkerReached(AudioRecord recorder) {
        }
    };

    public void setUpForLCS(int sampleLength,List<Integer> targetPattern)
    {
        this.sampleLength = sampleLength;
        this.targetPattern = targetPattern;
    }


    public MonitorRecorder(int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        try {
            mBitsPersample = 16;
            nChannels = 1;
            mAudioSource = audioSource;
            sRate = sampleRate;
            aFormat = audioFormat;
            foundPattern = true;
            mPeriodInFrames = sampleRate * TIMER_INTERVAL / 1000;
            mBufferSize = mPeriodInFrames * 2 * nChannels * mBitsPersample / 8;
            if (mBufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
                // Check to make sure buffer size is not smaller than the smallest allowed one
                mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                // Set frame period and timer interval accordingly
                mPeriodInFrames = mBufferSize / (2 * mBitsPersample * nChannels / 8);
                Log.w(MonitorRecorder.class.getName(), "Increasing buffer size to " + Integer.toString(mBufferSize));
            }

            audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, mBufferSize);
            if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                audioRecorder.release();
                throw new Exception("AudioRecord initialization failed from Constructor");
            }
            audioRecorder.setRecordPositionUpdateListener(updateListener);
            audioRecorder.setPositionNotificationPeriod(mPeriodInFrames);
            state = State.INITIALIZING;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.wtf(MonitorRecorder.class.getName(), e.getMessage());
            } else {
                Log.wtf(MonitorRecorder.class.getName(), "Unknown error occured while initializing recording");
            }
            state = State.ERROR;
        }
    }

    public static MonitorRecorder getInstance() {
        MonitorRecorder result = null;
        int i = 0;
        do {
            result = new MonitorRecorder(MediaRecorder.AudioSource.MIC,
                    sampleRates[i],
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
        }
        while ((++i < sampleRates.length) & !(result.getState() == MonitorRecorder.State.INITIALIZING));
        return result;
    }

    public State getState() {
        return state;
    }

    public void prepare() {
        try {
            if (state == State.INITIALIZING) {
                if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    // write file header
                    buffer = new short[mPeriodInFrames * nChannels];
                    state = State.READY;
                } else {
                    Log.wtf(MonitorRecorder.class.getName(), "prepare() method called on uninitialized recorder");
                    state = State.ERROR;
                }
            } else {
                Log.wtf(MonitorRecorder.class.getName(), "prepare() method called on illegal state");
                release();
                state = State.ERROR;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.wtf(MonitorRecorder.class.getName(), e.getMessage());
            } else {
                Log.wtf(MonitorRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            state = State.ERROR;
        }
    }

    public void release() {
        if (state == State.RECORDING) {
            stop();
        } else {
            if (state == State.READY) {
            }
        }

        if (audioRecorder != null) {
            Log.wtf(this.getClass().toString(),audioRecorder.toString());
            audioRecorder.release();
        }
    }

    public void reset() {
        try {
            if (state != State.ERROR) {
                release();
                audioRecorder = new AudioRecord(mAudioSource, sRate, nChannels, aFormat, mBufferSize);
                if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    audioRecorder.release();
                    throw new Exception("AudioRecord initialization failed");
                }

                audioRecorder.setRecordPositionUpdateListener(updateListener);
                audioRecorder.setPositionNotificationPeriod(mPeriodInFrames);
                state = State.INITIALIZING;
            }
        } catch (Exception e) {
            Log.wtf(MonitorRecorder.class.getName(), e.getMessage());
            state = State.ERROR;
        }
    }

    public void start() {
        if (state == State.READY) {
            audioRecorder.startRecording();
            audioRecorder.read(buffer, 0, buffer.length);
            bufferedData = new ArrayList<Integer>();
            foundPattern = false;
            state = State.RECORDING;
        } else {
            Log.wtf(MonitorRecorder.class.getName(), "start() called on illegal state");
            state = State.ERROR;
        }
    }

    public void stop() {
        if (state == State.RECORDING) {
            audioRecorder.stop();
            state = State.STOPPED;
        } else {
            Log.wtf(MonitorRecorder.class.getName(), "stop() called on illegal state");
            state = State.ERROR;
        }
    }

    public void soundAnalysis() {
        int avgAmp = 0;
        int crossingZero = 0;
        Log.wtf(this.getClass().toString(),"keep comparing");
        for (int i = 0; i < buffer.length - 1; i++) {
            if (buffer[i] < 0 && buffer[i + 1] >= 0)
                crossingZero++;
            else if (buffer[i + 1] < 0 && buffer[i] >= 0)
                crossingZero++;
            if (i % 2 == 0)
                avgAmp += (buffer[i] * buffer[i]) + (buffer[i + 1] * buffer[i + 1]);
        }
        avgAmp = (int) Math.round(Math.sqrt(avgAmp / buffer.length) / 150.0);
        crossingZero = (int) Math.round(crossingZero * 44100 / buffer.length / 2000.0);

        //buffer data read write
        if (bufferedData.size() < sampleLength) {
            bufferedData.add(avgAmp);
            bufferedData.add(crossingZero);
        } else {
            bufferedData.add(avgAmp);
            bufferedData.add(crossingZero);
            List<Integer> tmp;
            tmp = bufferedData.subList(2, bufferedData.size());
            bufferedData = new ArrayList<Integer>();
            bufferedData.addAll(tmp);
        }
        Log.wtf(this.getClass().toString(),targetPattern+"");
        lcsBuffer = LCSHelper.findLCS(LCSHelper.getKeyFeature(bufferedData), targetPattern);
        if (lcsBuffer.size() == targetPattern.size()) {
            foundPattern = true;
            Log.wtf("result- true",foundPattern+"");
            release();
        }
    }
    public void resetFoundPattern()
    {
        this.foundPattern = false;
    }


    public boolean getFoundPattern() {
        return foundPattern;
    }


    public enum State {
        INITIALIZING, READY, RECORDING, ERROR, STOPPED
    }
}
