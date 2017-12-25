package com.lws.allenglish.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by _SOLID
 * Date:2016/11/9
 * Time:11:02
 * <p>
 * Desc:A TextView that can get every word in content.
 * </p>
 */

public class GetWordTextView extends android.support.v7.widget.AppCompatTextView {

    private CharSequence mText;
    private BufferType mBufferType;

    private OnWordClickListener mOnWordClickListener;
    private SpannableString mSpannableString;

    private Context mContext;

    private boolean isLoaded = false;

    public GetWordTextView(Context context) {
        this(context, null);
    }

    public GetWordTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GetWordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setOnTouchListener(new LinkMovementMethodOverride());
    }

    @Override
    public void setText(final CharSequence text, final BufferType type) {
        if (TextUtils.isEmpty(text) || (!TextUtils.isEmpty(text) && isLoaded)) {
            super.setText(mSpannableString, mBufferType);
        } else {
            new Thread() {
                @Override
                public void run() {
                    mText = text;
                    mBufferType = type;
                    setHighlightColor(Color.TRANSPARENT);
                    setMovementMethod(LinkMovementMethod.getInstance());
                    mSpannableString = new SpannableString(mText);
                    dealEnglish();
                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            if (!isLoaded) {
                                isLoaded = true;
                                setText(mSpannableString, mBufferType);
                            }
                        }
                    });
                }
            }.start();
        }
    }

    private void dealEnglish() {
        String text = mText.toString();
        List<WordInfo> wordInfoList = Utils.getEnglishWordIndices(text);
        for (WordInfo wordInfo : wordInfoList) {
            int start = wordInfo.start;
            int end = wordInfo.end;
            String possibleWord = new String(text.substring(start, end).toCharArray());
            mSpannableString.setSpan(getClickableSpan(possibleWord), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    // TODO: 2017/10/13 点击单词高亮
    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (!word.matches("^[A-Za-z\\-']+$")) {
                    return; // 不是英文单词
                }
                if (mOnWordClickListener != null) {
                    mOnWordClickListener.onClick(word);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
            }

        };
    }

    public void setOnWordClickListener(OnWordClickListener listener) {
        this.mOnWordClickListener = listener;
    }

    public interface OnWordClickListener {
        void onClick(String word);
    }

    private class LinkMovementMethodOverride implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextView widget = (TextView) v;
            Object text = widget.getText();
            if (text instanceof Spanned) {
                Spanned buffer = (Spanned) text;

                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);

                    ClickableSpan[] link = buffer.getSpans(off, off,
                            ClickableSpan.class);

                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        return true;
                    }
                }
            }

            return false;
        }

    }
}


class Utils {

    private static List<Character> sPunctuations;

    static {
//        Character[] arr = new Character[]{'\n','(',')', ',', '.', ';', '!', '"', '，', '。', '！', '；', '、', '：', ':', '“', '”', '?', '？'};
        Character[] arr = new Character[]{'\n','(',')', ',', '.', ';', '!', '"', ':', '“', '”', '?'};
        sPunctuations = Arrays.asList(arr);
    }

    @NonNull
    static List<WordInfo> getEnglishWordIndices(String content) {
        List<Integer> separatorIndices = getSeparatorIndices(content, ' ');
        for (Character punctuation : sPunctuations) {
            separatorIndices.addAll(getSeparatorIndices(content, punctuation));
        }
        Collections.sort(separatorIndices);
        List<WordInfo> wordInfoList = new ArrayList<>();
        int start = 0;
        int end;
        for (int i = 0; i < separatorIndices.size(); i++) {
            end = separatorIndices.get(i);
            if (start == end) {
                start++;
            } else {
                WordInfo wordInfo = new WordInfo();
                wordInfo.start = start;
                wordInfo.end = end;
                wordInfoList.add(wordInfo);
                start = end + 1;
            }
        }
        return wordInfoList;
    }

    /**
     * Get every word's index array of text
     *
     * @param word the content
     * @param ch   separate char
     * @return index array
     */
    private static List<Integer> getSeparatorIndices(String word, char ch) {
        int pos = word.indexOf(ch);
        List<Integer> indices = new ArrayList<>();
        while (pos != -1) {
            indices.add(pos);
            pos = word.indexOf(ch, pos + 1);
        }
        return indices;
    }
}


class WordInfo {
    int start;
    int end;
}