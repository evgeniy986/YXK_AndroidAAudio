package com.yxk.aaudiotest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int INVALID_PTR = 0;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private long mEngineHandle = INVALID_PTR;
    private static final String TEST_FILE_PATH = "ka.pcm";
    private static final int AUDIO_SAMPLERATE = 48000;
    private static final int AUDIO_CHANNELS = 1;
    private static final int AUDIO_FORMAT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEngineHandle = nativeCreateAAudioEngine(getAssets(), TEST_FILE_PATH, AUDIO_SAMPLERATE, AUDIO_CHANNELS, AUDIO_FORMAT);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        Button button = findViewById(R.id.button);
        if(button != null){
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_MOVE:

                            break;
                        case MotionEvent.ACTION_UP:

                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (mEngineHandle != INVALID_PTR) {
                                nativeAAudioEngineStop(mEngineHandle);
                                nativeDestroyAAudioEngine(mEngineHandle);
                                mEngineHandle = INVALID_PTR;
                            }
                            if (mEngineHandle == INVALID_PTR) {
                                mEngineHandle = nativeCreateAAudioEngine(getAssets(), TEST_FILE_PATH, AUDIO_SAMPLERATE, AUDIO_CHANNELS, AUDIO_FORMAT);
                            }
                            if (mEngineHandle != INVALID_PTR) {
                                nativeAAudioEnginePlay(mEngineHandle);
                            }
                            break;
                        default:break;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native long nativeCreateAAudioEngine(AssetManager assetMgr, String filePath, int sampleRate, int audioChannel, int audioFormat);

    public native void nativeDestroyAAudioEngine(long engineHandle);

    public native void nativeAAudioEnginePlay(long engineHandle);

    public native void nativeAAudioEnginePause(long engineHandle);

    public native void nativeAAudioEngineStop(long engineHandle);
}
