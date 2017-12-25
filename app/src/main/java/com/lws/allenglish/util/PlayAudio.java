package com.lws.allenglish.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import com.lws.allenglish.connector.OnFinishPlayAudioListener;
import com.lws.allenglish.util.common.ToastUtils;

import java.util.HashMap;
import java.util.Locale;

/**
 * 音频播放工具类
 * Created by Wilson on 2016/12/10.
 */
public class PlayAudio {
    private MediaPlayer mMediaPlayer;
    private TextToSpeech mTts;
    private String mTempUrl;
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
        try {
            switch (voiceType) {
                case 1:
                    playAudioByUrl("http://dict.youdao.com/dictvoice?audio=" + text + "&type=" + voiceType, voiceType);
                    break;
                case 2:
                    playAudioByUrl("http://dict.youdao.com/dictvoice?audio=" + text + "&type=" + voiceType, voiceType);
                    break;
                case 3:
                    playAudioByUrl(text, voiceType);
                    break;
                case 4:
                    playAudioByUrl(text, voiceType);
                    break;
                case 5:
                    playAudioByTTS(context, text, Locale.SIMPLIFIED_CHINESE);
                    break;
                case 6:
                    playAudioByTTS(context, text, Locale.ENGLISH);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (voiceType == 5 || voiceType == 6) {
                ToastUtils.show(context, "TTS发音失败,请稍后再试");
            } else {
                mTempUrl = null;
                ToastUtils.show(context, "获取网络发音失败,请检查网络设置");
                if (voiceType != 4) {
                    mOnFinishPlayAudioListener.OnFinishPlayAudio();
                }
            }
        }
    }

    /**
     * 播放来自网络的URL音频文件
     *
     * @param url       url
     * @param voiceType voiceType
     * @throws Exception
     */
    private void playAudioByUrl(String url, int voiceType) throws Exception {
        if (url == null || url.equals(mTempUrl)) {
            return;
        }
        this.mTempUrl = url;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        if (voiceType == 4 && mMediaPlayer.isPlaying() || (mTts != null && mTts.isSpeaking())) {
            return;
        }
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mTempUrl = null;
                if (mOnFinishPlayAudioListener != null) {
                    mOnFinishPlayAudioListener.OnFinishPlayAudio();
                }
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mTempUrl = null;
                if (mOnFinishPlayAudioListener != null) {
                    mOnFinishPlayAudioListener.OnFinishPlayAudio();
                }
                return true;
            }
        });
    }

    /**
     * 通过TextToSpeech播放音频
     *
     * @param context
     * @param text
     */
    private void playAudioByTTS(final Context context, final String text, final Locale locale) throws Exception {
        if (mTts != null && mTts.isSpeaking() || (mMediaPlayer != null && mMediaPlayer.isPlaying())) {
            return;
        }
        if (mTts == null) {
            mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mTts.setLanguage(locale);
                        if (result != TextToSpeech.LANG_MISSING_DATA &&
                                result != TextToSpeech.LANG_NOT_SUPPORTED) {
                            convertTextToSpeech(context, text);
                        }
                    }
                }
            });
        } else {
            convertTextToSpeech(context, text);
        }
    }

    public void killMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void killTTS() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            mTts = null;
        }
    }

    private void convertTextToSpeech(Context context, String s) {
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
}
