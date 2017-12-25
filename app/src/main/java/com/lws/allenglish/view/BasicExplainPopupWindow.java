package com.lws.allenglish.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.database.AllEnglishDatabaseManager;
import com.lws.allenglish.util.PlayAudio;

public class BasicExplainPopupWindow extends PopupWindow implements View.OnClickListener {
    private View contentView;
    private String aWord;
    private BaseWord baseWord;
    private PlayAudio playAudio;
    private ImageView collection;
    private TextView word;
    private TextView symbols;
    private ImageButton close;
    private LinearLayout sound;

    public BasicExplainPopupWindow(AppCompatActivity context, BaseWord bean, PlayAudio playAudio) {
        this.playAudio = playAudio;
        initPopupWindow(context, bean);
    }

    private void initPopupWindow(AppCompatActivity context, BaseWord bean) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.ppw_basic_explain, null);
        //设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(android.R.color.transparent));
//        设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        initView();
        initData(bean);
    }

    private void initView() {
        word = (TextView) contentView.findViewById(R.id.word);
        symbols = (TextView) contentView.findViewById(R.id.symbols);
        collection = (ImageView) contentView.findViewById(R.id.collection);
        collection.setOnClickListener(this);
        close = (ImageButton) contentView.findViewById(R.id.close);
        close.setOnClickListener(this);
        sound = (LinearLayout) contentView.findViewById(R.id.sound);
        sound.setOnClickListener(this);
    }

    private void initData(BaseWord bean) {
        this.baseWord = bean;
        aWord = baseWord.word;
        word.setText(aWord);
        if (AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).isCollectedWord(aWord)) {
            collection.setSelected(true);
        }
        String means = baseWord.means;
        if (means == null) {
            symbols.setText("真的很抱歉，这个词难倒词典君了~~");
        } else {
            symbols.setText(means);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                this.dismiss();
                break;
            case R.id.sound:
                playAudio.play(BaseApplication.getInstance(), aWord, 6);
                break;
            case R.id.collection:
                if (!collection.isSelected()) {
                    collection.setSelected(true);
                    AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).saveCollectedWord(baseWord);
                } else {
                    collection.setSelected(false);
                    AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).cancelCollectedWord(aWord);
                }
                break;
        }
    }
}
