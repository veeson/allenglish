package com.lws.allenglish.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.voiceads.NativeADDataRef;
import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.DetailedWord;
import com.lws.allenglish.connector.OnFinishPlayAudioListener;
import com.lws.allenglish.util.CommonUtils;
import com.lws.allenglish.util.PlayAudio;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WordDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFinishPlayAudioListener {
    private final static int TYPE_ONE = 0;
    private final static int TYPE_TWE = 1;
    private final static int TYPE_THESE = 2;
    private final static int TYPE_FOUR = 3;
    private final static int ITEM_COUNT_1 = 1;
    private final static int ITEM_COUNT_4 = 4;

    private PlayAudio mPlayAudio;
    private DetailedWord mDetailedWord;
    private Context mContext;
    private List<AnimationDrawable> mSentenceHornAnimations = new ArrayList<>();

    private LinearLayout adLinearLayout;

    public WordDetailsAdapter(DetailedWord mDetailedWord, Context mContext, PlayAudio mPlayAudio, List<AnimationDrawable> mSentenceHornAnimations) {
        this.mDetailedWord = mDetailedWord;
        this.mContext = mContext;
        this.mSentenceHornAnimations.addAll(mSentenceHornAnimations);
        this.mPlayAudio = mPlayAudio;
        this.mPlayAudio.setOnFinishPlayAudioListener(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONE: // 基本释义
                return new BaseInfoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
            case TYPE_TWE: // 例句
                return new ExampleSentenceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
            case TYPE_THESE: // 短语
                return new PhraseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
            case TYPE_FOUR: // 英英释义
                return new EEMeanViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseInfoViewHolder) {
            ((BaseInfoViewHolder) holder).loadData();
        } else if (holder instanceof ExampleSentenceViewHolder) {
            ((ExampleSentenceViewHolder) holder).loadData();
        } else if (holder instanceof PhraseViewHolder) {
            ((PhraseViewHolder) holder).loadData();
        } else if (holder instanceof EEMeanViewHolder) {
            ((EEMeanViewHolder) holder).loadData();
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case TYPE_ONE:
                return TYPE_ONE;
            case TYPE_TWE:
                if (mDetailedWord.sentence != null) {
                    return TYPE_TWE;
                }
            case TYPE_THESE:
                if (mDetailedWord.phrase != null) {
                    return TYPE_THESE;
                }
            case TYPE_FOUR:
                if (mDetailedWord.ee_mean != null) {
                    return TYPE_FOUR;
                }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mDetailedWord.baesInfo == null) {
            return ITEM_COUNT_1;
        }
        int itemCount = ITEM_COUNT_4;
        if (mDetailedWord.sentence == null) {
            --itemCount;
        }
        if (mDetailedWord.phrase == null) {
            --itemCount;
        }
        if (mDetailedWord.ee_mean == null) {
            --itemCount;
        }
        return itemCount;
    }

    private void setRecyclerViewContent(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getInstance()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 单词基本释义ViewHolder
     */
    public class BaseInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.explain_head)
        TextView explainHead;
        @BindView(R.id.sentence)
        RecyclerView sentence;
        @BindView(R.id.divider)
        View divider;

        public BaseInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            explainHead.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        public void loadData() {
            setRecyclerViewContent(sentence, new BaseInfoAdapter());
        }

        class BaseInfoAdapter extends RecyclerView.Adapter<BaseInfoAdapter.ViewHolder> {
            @Override
            public BaseInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
                return new BaseInfoAdapter.ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(BaseInfoAdapter.ViewHolder holder, int position) {
                StringBuilder sb = new StringBuilder();
                sb.append(mDetailedWord.baseWord.means);
                if (mDetailedWord.baesInfo != null) {
                    String word_er;
                    String word_est;
                    String word_pl;
                    StringBuilder subSb = new StringBuilder();
                    try {
                        word_er = mDetailedWord.baesInfo.exchange.word_er.get(0);
                        subSb.append("比较级: ").append(word_er).append(" ");
                    } catch (Exception ignored) {
                    }
                    try {
                        word_est = mDetailedWord.baesInfo.exchange.word_est.get(0);
                        subSb.append("最高级: ").append(word_est).append(" ");
                    } catch (Exception ignored) {
                    }
                    try {
                        word_pl = mDetailedWord.baesInfo.exchange.word_pl.get(0);
                        subSb.append("复数: ").append(word_pl).append(" ");
                    } catch (Exception ignored) {
                    }
                    if (subSb.length() > 0) {
                        sb.append("\n").append(subSb);
                    }
                }
                holder.sentenceContent.setText(sb);

                handleAds(holder);
            }

            private void handleAds(BaseInfoAdapter.ViewHolder holder) {
                if (mDetailedWord.iflyNativeAd != null && mDetailedWord.nativeADDataRef != null) {
                    holder.view.setVisibility(View.VISIBLE);
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    CommonUtils.setUrlImageToImageView(holder.adsPicture, mDetailedWord.nativeADDataRef.getImage());
                    holder.adsTitle.setText(mDetailedWord.nativeADDataRef.getTitle());
                    CommonUtils.setAds(holder.linearLayout, mDetailedWord.iflyNativeAd, mDetailedWord.nativeADDataRef);
                    adLinearLayout = holder.linearLayout;
                }
            }

            @Override
            public int getItemCount() {
                return 1;
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.sentence_content)
                TextView sentenceContent;
                @BindView(R.id.horn)
                ImageView horn;
                @BindView(R.id.view)
                View view;
                @BindView(R.id.linear_layout)
                LinearLayout linearLayout;
                @BindView(R.id.ads_picture)
                ImageView adsPicture;
                @BindView(R.id.ads_title)
                TextView adsTitle;

                public ViewHolder(View itemView) {
                    super(itemView);
                    ButterKnife.bind(this, itemView);
                    horn.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void exposureAd(NativeADDataRef nativeADDataRef) {
        if (!nativeADDataRef.isExposured() && adLinearLayout != null) {
            nativeADDataRef.onExposured(adLinearLayout);
        }
    }

    /**
     * 例句ViewHolder
     */
    public class ExampleSentenceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.explain_head)
        TextView explainHead;
        @BindView(R.id.sentence)
        RecyclerView sentence;

        public ExampleSentenceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            explainHead.setText("双语例句");
        }

        public void loadData() {
            setRecyclerViewContent(sentence, new ExampleSentenceContentAdapter());
        }

        public class ExampleSentenceContentAdapter extends RecyclerView.Adapter<ExampleSentenceContentAdapter.ViewHolder> {

            @Override
            public ExampleSentenceContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(ExampleSentenceContentAdapter.ViewHolder holder, int position) {
                final int pos = position;
                StringBuilder sb = new StringBuilder();
                sb.append(mDetailedWord.sentence.get(pos).Network_en).append("\n").append(mDetailedWord.sentence.get(pos).Network_cn);
                if (pos != getItemCount() - 1) {
                    sb.append("\n");
                }
                holder.sentenceContent.setText(sb);
                ImageView horn = holder.horn;
                horn.setBackgroundResource(R.drawable.animation_horn_color);
                mSentenceHornAnimations.add((AnimationDrawable) horn.getBackground());
                horn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSentenceHornAnimations.get(pos + 2).isRunning()) {
                            return;
                        }
                        stopAnimation();
                        mSentenceHornAnimations.get(pos + 2).start();
                        mPlayAudio.play(BaseApplication.getInstance(), mDetailedWord.sentence.get(pos).tts_mp3, 3);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mDetailedWord.sentence.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.sentence_content)
                TextView sentenceContent;
                @BindView(R.id.horn)
                ImageView horn;

                public ViewHolder(View itemView) {
                    super(itemView);
                    ButterKnife.bind(this, itemView);
                }
            }
        }
    }

    @Override
    public void OnFinishPlayAudio() {
        stopAnimation();
    }

    public void stopAnimation() {
        for (AnimationDrawable animation :
                mSentenceHornAnimations) {
            if (animation.isRunning()) {
                animation.selectDrawable(0); // 选择当前动画的第一帧，然后停止
                animation.stop();
            }
        }
    }

    /**
     * 词组ViewHolder
     */
    public class PhraseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.explain_head)
        TextView explainHead;
        @BindView(R.id.sentence)
        RecyclerView sentence;

        public PhraseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            explainHead.setText("词组搭配");
        }

        public void loadData() {
            setRecyclerViewContent(sentence, new PhraseViewHolder.PhraseContentAdapter());
        }

        class PhraseContentAdapter extends RecyclerView.Adapter<PhraseContentAdapter.ViewHolder> {
            @Override
            public PhraseContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
                return new PhraseViewHolder.PhraseContentAdapter.ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(PhraseContentAdapter.ViewHolder holder, int position) {
                StringBuilder sb = new StringBuilder();
                try {
                    sb.append(mDetailedWord.phrase.get(position).cizu_name)
                            .append("\n")
                            .append(mDetailedWord.phrase.get(position).jx.get(0).jx_cn_mean)
                            .append("(")
                            .append(mDetailedWord.phrase.get(position).jx.get(0).jx_en_mean)
                            .append(")");
                } catch (Exception ignored) {
                }
                if (position != getItemCount() - 1) {
                    sb.append("\n");
                }
                holder.sentenceContent.setText(sb);
            }

            @Override
            public int getItemCount() {
                return mDetailedWord.phrase.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.sentence_content)
                TextView sentenceContent;
                @BindView(R.id.horn)
                ImageView horn;

                public ViewHolder(View itemView) {
                    super(itemView);
                    ButterKnife.bind(this, itemView);
                    horn.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 英英释义ViewHolder
     */
    public class EEMeanViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.explain_head)
        TextView explainHead;
        @BindView(R.id.sentence)
        RecyclerView sentence;

        public EEMeanViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            explainHead.setText("英英释义");
        }

        public void loadData() {
            setRecyclerViewContent(sentence, new EEMeanContentAdapter());
        }

        class EEMeanContentAdapter extends RecyclerView.Adapter<EEMeanContentAdapter.ViewHolder> {
            @Override
            public EEMeanContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
                return new EEMeanContentAdapter.ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(EEMeanContentAdapter.ViewHolder holder, int position) {
                StringBuilder sb = new StringBuilder();
                try {
                    sb.append(mDetailedWord.ee_mean.get(position).part_name).append("\n");
                    for (DetailedWord.EeMeanEntity.MeansEntity xx : mDetailedWord.ee_mean.get(position).means) {
                        sb.append(xx.word_mean).append("\n");
                        for (DetailedWord.EeMeanEntity.MeansEntity.SentencesEntity sentences : xx.sentences) {
                            sb.append(sentences.sentence).append("\n");
                        }
                    }
                    sb.setLength(sb.length() - 1);
                } catch (Exception ignored) {
                }
                if (position != getItemCount() - 1) {
                    sb.append("\n");
                }
                holder.sentenceContent.setText(sb);
            }

            @Override
            public int getItemCount() {
                return mDetailedWord.ee_mean.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.sentence_content)
                TextView sentenceContent;
                @BindView(R.id.horn)
                ImageView horn;

                public ViewHolder(View itemView) {
                    super(itemView);
                    ButterKnife.bind(this, itemView);
                    horn.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

}
