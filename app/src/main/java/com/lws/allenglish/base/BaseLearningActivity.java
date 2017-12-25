package com.lws.allenglish.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.NativeADDataRef;
import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.LeanCloudApiBean;
import com.lws.allenglish.model.BaseLearningModel;
import com.lws.allenglish.model.OnBaseLearningListener;
import com.lws.allenglish.model.impl.BaseLearningModelImpl;
import com.lws.allenglish.util.CommonUtils;
import com.lws.allenglish.util.PlayAudio;
import com.lws.allenglish.util.VolleySingleton;
import com.lws.allenglish.util.common.NetWorkUtils;
import com.lws.allenglish.util.common.TimeUtils;
import com.lws.allenglish.view.BasicExplainPopupWindow;
import com.lws.allenglish.view.GetWordTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BaseLearningActivity extends BaseActivity {
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.a_title)
    TextView title;
    @BindView(R.id.content)
    GetWordTextView content;
    @BindView(R.id.rootView)
    LinearLayout rootView;
    @BindView(R.id.img_poster)
    ImageView imgPoster;
    @BindView(R.id.relative_layout)
    RelativeLayout relativeLayout;
    @BindView(R.id.linear_layout)
    LinearLayout linearLayout;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    protected LeanCloudApiBean.ResultsEntity leanCloudBean;
    protected String bilingualReadingTag;
    protected String tableName;
    private BaseLearningModel baseLearningModel;
    // true表示显示图片，false表示不显示图片
    private boolean isShowPicture = false;
    private PlayAudio playAudio;
    protected List<LeanCloudApiBean.ResultsEntity> mList = new ArrayList<>();

    private NativeADDataRef nativeADDataRef1;
    private NativeADDataRef nativeADDataRef2;
    private CardView adCardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leanCloudBean = (LeanCloudApiBean.ResultsEntity) getIntent().getSerializableExtra("BEAN");
        isShowPicture = getIntent().getBooleanExtra("SHOW_PICTURE", false);
        bilingualReadingTag = getIntent().getStringExtra("BILINGUAL_READING_TAG");
        tableName = getIntent().getStringExtra("TABLE_NAME");
        playAudio = new PlayAudio();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toolbar != null) {
            toolbar.setTitle(bilingualReadingTag);
        }
    }

    protected void init() {
        initView();
        if (!NetWorkUtils.getNetworkTypeName(BaseApplication.getInstance()).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
            baseLearningModel = new BaseLearningModelImpl(mContext, new CustomOnBaseLearningListener());
            baseLearningModel.getLeanCloudBean(tableName, 4, 1, bilingualReadingTag, leanCloudBean.createdAt);
            baseLearningModel.getImageAds();
        }
    }

    private void initView() {
        progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.grey), PorterDuff.Mode.MULTIPLY);
        title.setText(leanCloudBean.title);
        content.setOnWordClickListener(new GetWordTextView.OnWordClickListener() {
            @Override
            public void onClick(String word) {
                progressBar.setVisibility(View.VISIBLE);
                baseLearningModel.getDetailedWord(1, word);
            }
        });
        String str = leanCloudBean.content.trim();
        if (tableName.equals(Constants.Video)) {
            if (str.isEmpty()) {
                content.setText("暂无文本内容");
            } else {
                content.setText(str);
            }
        } else {
            content.setText(Html.fromHtml(CommonUtils.replaceImgTag(str)));
        }

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);

                // Calculate the scrolldiff
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY())) - linearLayout.getHeight() / 5 * 2;
                // if diff is zero, then the bottom has been reached
                if (diff <= 0) {
                    // notify that we have reached the bottom
                    if (nativeADDataRef1 != null && !nativeADDataRef1.isExposured()) {
                        nativeADDataRef1.onExposured(imgPoster);
                    }
                    if (nativeADDataRef2 != null && !nativeADDataRef2.isExposured()) {
                        nativeADDataRef2.onExposured(adCardView);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playAudio.killTTS();
    }

    private class CustomOnBaseLearningListener implements OnBaseLearningListener {

        @Override
        public void onGetBaseWordSuccess(BaseWord baseWord) {
            progressBar.setVisibility(View.GONE);
            BasicExplainPopupWindow popupWindow = new BasicExplainPopupWindow(BaseLearningActivity.this, baseWord, playAudio);
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }

        @Override
        public void onGetBaseWordError() {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onGetLeanCloudSuccess(List<LeanCloudApiBean.ResultsEntity> list) {
            mList.addAll(list);
            addView(list);
        }

        @Override
        public void onGetAdsImageSuccess(Bitmap bitmap, final IFLYNativeAd iflyNativeAd, final NativeADDataRef nativeADDataRef) {
            relativeLayout.setVisibility(View.VISIBLE);
            imgPoster.setImageBitmap(bitmap);
            CommonUtils.setAds(imgPoster, iflyNativeAd, nativeADDataRef);
            nativeADDataRef1 = nativeADDataRef;
        }

    }

    private void addView(List<LeanCloudApiBean.ResultsEntity> list) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //实例化一个LinearLayout
        LinearLayout linear = new LinearLayout(this);
        //设置LinearLayout属性(宽和高)
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        //将以上的属性赋给LinearLayout
        linear.setLayoutParams(layoutParams);
        ImageLoader imageLoader = new ImageLoader(VolleySingleton.getInstance().getRequestQueue(), BaseApplication.getInstance().getBitmapCache());
        for (final LeanCloudApiBean.ResultsEntity lc :
                list) {
            // 获取需要添加的布局
            CardView layout = (CardView) inflater.inflate(
                    R.layout.item_article_list, linear).findViewById(R.id.card_view);
            if (layout.getParent() != null)
                ((LinearLayout) layout.getParent()).removeView(layout); // <- fix
            // 将布局加入到当前布局中
            linearLayout.addView(layout);
            TextView title = (TextView) layout.findViewById(R.id.title);
            TextView source = (TextView) layout.findViewById(R.id.source);
            NetworkImageView picture = (NetworkImageView) layout.findViewById(R.id.picture);
            if (lc.type == 0) {
                title.setText(lc.title);
                try {
                    source.setText(lc.source + "   " + TimeUtils.DATE_FORMAT_DATE.format(TimeUtils.FULL_DATE_FORMAT_DATE.parse(lc.postTime.iso)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (isShowPicture) {
                    if (lc.imageUrl.isEmpty()) {
                        picture.setVisibility(View.GONE);
                    } else {
                        picture.setVisibility(View.VISIBLE);
                        picture.setDefaultImageResId(R.drawable.ic_default);
                        picture.setErrorImageResId(R.drawable.ic_default);
                        picture.setImageUrl(lc.imageUrl, imageLoader);
                    }
                } else {
                    picture.setVisibility(View.GONE);
                }
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        intent.putExtra("BEAN", lc);
                        intent.putExtra("TABLE_NAME", tableName);
                        intent.putExtra("BILINGUAL_READING_TAG", bilingualReadingTag);
                        finish();
                        startActivity(intent);
                    }
                });
            } else {
                NativeADDataRef nativeADDataRef = lc.nativeADDataRef;
                title.setText(nativeADDataRef.getTitle() != null ? nativeADDataRef.getTitle() : nativeADDataRef.getSubTitle());
                source.setText("广告");
                if (isShowPicture) {
                    picture.setImageUrl(nativeADDataRef.getImage(), imageLoader);
                    picture.setDefaultImageResId(R.drawable.ic_default);
                    picture.setErrorImageResId(R.drawable.ic_default);
                } else {
                    picture.setVisibility(View.GONE);
                }
                CommonUtils.setAds(layout, lc.iflyNativeAd, nativeADDataRef);
                adCardView = layout;
                nativeADDataRef2 = nativeADDataRef;
            }
        }
    }
}
