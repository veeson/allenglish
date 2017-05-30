package com.lws.allenglish.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import com.lws.allenglish.connector.OnFinishPlayAudioListener;

import java.util.HashMap;
import java.util.Locale;

/**
 * 音频播放工具类
 * Created by Wilson on 2016/12/10.
 */
public class PlayAudio {
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private TextToSpeech mTts;
    private OnFinishPlayAudioListener mOnFinishPlayAudioListener;

    public void setOnFinishPlayAudioListener(OnFinishPlayAudioListener mOnFinishPlayAudioListener) {
        this.mOnFinishPlayAudioListener = mOnFinishPlayAudioListener;
    }

    /**
     * 播放音频
     *
     * @param text
     * @param voiceType
     */
    public void play(Context context, String text, int voiceType) {
        new PlayAudioTask(context, text, voiceType).execute();
    }

    /**
     * 播放来自网络的URL音频文件
     *
     * @param url
     * @throws Exception
     */
    private void playAudioByUrl(String url) throws Exception {
//        killMediaPlayer();
//        mOnFinishPlayAudioListener.OnFinishPlayAudio();
//        mMediaPlayer = MediaPlayer.create(context, Uri.parse(url));
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
//        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mMediaPlayer.start();
//            }
//        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mOnFinishPlayAudioListener != null) {
                    mOnFinishPlayAudioListener.OnFinishPlayAudio();
                }
//                killMediaPlayer();
            }
        });
    }

    public void killMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void stopMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    /**
     * 通过TextToSpeech播放音频
     *
     * @param context
     * @param text
     */
    private void playAudioByTTS(final Context context, final String text, final Locale locale) {
        mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            killTTS();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            killTTS();
                        }
                    });
                    int result = mTts.setLanguage(locale);
                    if (result != TextToSpeech.LANG_MISSING_DATA &&
                            result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        ConvertTextToSpeech(context, text);
                    }
                }
            }
        });
    }

    public void killTTS() {
        if (mTts != null) {

            mTts.stop();
            mTts.shutdown();
            mTts = null;
        }
    }

    private void ConvertTextToSpeech(Context context, String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(context, s);
        } else {
            ttsUnder20(s);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(Context context, String text) {
        String utteranceId = context.hashCode() + "";
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    private class PlayAudioTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String text;
        private int voiceType;
        private boolean success = false;

        PlayAudioTask(Context context, String text, int voiceType) {
            this.context = context;
            this.text = text;
            this.voiceType = voiceType;
        }

        @Override
        protected void onPreExecute() {
            if (voiceType == 4) {
                Toast.makeText(context, "正在联网获取发音", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                switch (voiceType) {
                    case 1:
                        playAudioByUrl("http://dict.youdao.com/dictvoice?audio=" + text + "&type=" + voiceType);
                        break;
                    case 2:
                        playAudioByUrl("http://dict.youdao.com/dictvoice?audio=" + text + "&type=" + voiceType);
                        break;
                    case 3:
                        playAudioByUrl(text);
                        break;
                    case 4:
                        playAudioByUrl(text);
                        break;
                    case 5:
                        playAudioByTTS(context, text, Locale.SIMPLIFIED_CHINESE);
                        break;
                }
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!success) {
                if (voiceType == 5) {
                    Toast.makeText(context, "TTS发音失败,请稍后再试", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "获取网络发音失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                    if (voiceType != 4) {
                        mOnFinishPlayAudioListener.OnFinishPlayAudio();
                    }
                }
            }
        }
    }
}
