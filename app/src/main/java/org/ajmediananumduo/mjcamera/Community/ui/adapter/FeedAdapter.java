package org.ajmediananumduo.mjcamera.Community.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.ajmediananumduo.mjcamera.Community.Utils;
import org.ajmediananumduo.mjcamera.Community.ui.activity.FeedItem;
import org.ajmediananumduo.mjcamera.Community.ui.activity.ImageDTO;
import org.ajmediananumduo.mjcamera.Community.ui.activity.MainActivity;
import org.ajmediananumduo.mjcamera.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

//어댑터
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";
    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;
    private List<ImageDTO> imageDTOitems;
    private List<String> uidList = new ArrayList<>();
    private List<FeedItem> feedItems = new ArrayList<>();
    private Context context;
    private OnFeedItemClickListener onFeedItemClickListener;
    private int size;
    private boolean showLoadingView = false;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public FeedAdapter(Context context,List<ImageDTO> imageDTOs) {
        this.context = context;
        imageDTOitems = imageDTOs;
        size =imageDTOitems.size();
    }

    //리사이클뷰의 홀더정의
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
            //로더 일 경우
        } /*else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }*/
        return null;
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        //화면을 눌러도 좋아요++
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                //imageDTOitems.get(adapterPosition).likesCount++;
                notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }
            }
        });
        //좋아요 버튼을 눌러도 좋아요++
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
               // imageDTOitems.get(adapterPosition).likesCount++;
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }
            }
        });
    }

    //피드로 결합
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CellFeedViewHolder) viewHolder).bindView(imageDTOitems.get(position));

        /*if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }*/
    }

    /*private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener
                (new LoadingFeedItemView.OnLoadingFinishedListener() {
                    @Override
                    public void onLoadingFinished() {
                        showLoadingView = false;
                        notifyItemChanged(0);
                    }
                });
        holder.loadingFeedItemView.startLoading();
    }*/

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        //returnn imageDTO.size();
        return imageDTOitems.size();
    }
    //main에서 호출
    public void updateItems(boolean animated) {
       // feedItems.clear();
        //item 개수만큼 생성하도록
        //아이템 개수만큼 생성하게
        /*
        feedItems.addAll(Arrays.asList(
                new FeedItem(0, false),
                new FeedItem(0, false),
                new FeedItem(0, false),
                new FeedItem(0, false),
                new FeedItem(0, false),
                new FeedItem(0, false),
                new FeedItem(0, false)
        ));
        */
        if (animated) {
            notifyItemRangeInserted(0, imageDTOitems.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivFeedCenter) //이미지 부분
                ImageView ivFeedCenter;
        @BindView(R.id.filterText)  // 필터명
                TextView filterText;
        @BindView(R.id.conText)     //설명
                TextView conText;
        @BindView(R.id.btnComments)
        ImageButton btnComments;
        @BindView(R.id.btnLike)
        ImageButton btnLike;
        @BindView(R.id.btnMore)
        ImageButton btnMore;
        @BindView(R.id.vBgLike)
        View vBgLike;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @BindView(R.id.userProfile)
        ImageView ivUserProfile;
        @BindView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @BindView(R.id.userId)
                TextView userId;
        FeedItem feedItem;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        //객체화
        public void bindView(ImageDTO imageDTOitems) {
            ImageDTO thisitem = imageDTOitems;
            //this.feedItem = feedItem;
            int adapterPosition = getAdapterPosition();
            //게시하는 사진 이미지 불러옴
            //ivFeedCenter.setImageResource(adapterPosition % 2 == 0 ? R.drawable.feed1 : R.drawable.feed2);
            Picasso.with(itemView.getContext()).load(thisitem.imageUrl).into(ivFeedCenter);
            userId.setText(thisitem.userId);
            conText.setText(thisitem.description);
            filterText.setText(thisitem.title);
            //댓글 이미지 불러옴
            //ivFeedBottom.setImageResource(adapterPosition % 2 == 0 ? R.drawable.feed_bottom1 : R.drawable.feed_bottom2);
            //회색하트,  빨간하트
        //    btnLike.setImageResource(thisitem.isLiked ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
           // tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
          //          R.plurals.likes_count, thisitem.likesCount, thisitem.likesCount
           // ));
        }

        public FeedItem getFeedItem() {
            return feedItem;
        }
    }

    /*public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(FeadItem feedItem) {
            super.bindView(feedItem);
        }
    }*/



    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);
    }

    public static class FeedItemAnimator extends DefaultItemAnimator {
        private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
        private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
        private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

        Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimationsMap = new HashMap<>();
        Map<RecyclerView.ViewHolder, AnimatorSet> heartAnimationsMap = new HashMap<>();

        private int lastAddAnimatedItem = -2;

        @Override
        public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @NonNull
        @Override
        public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state,
                                                         @NonNull RecyclerView.ViewHolder viewHolder,
                                                         int changeFlags, @NonNull List<Object> payloads) {
            if (changeFlags == FLAG_CHANGED) {
                for (Object payload : payloads) {
                    if (payload instanceof String) {
                        return new FeedItemHolderInfo((String) payload);
                    }
                }
            }

            return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        }

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == VIEW_TYPE_DEFAULT) {
                if (viewHolder.getLayoutPosition() > lastAddAnimatedItem) {
                    lastAddAnimatedItem++;
                    runEnterAnimation((CellFeedViewHolder) viewHolder);
                    return false;
                }
            }

            dispatchAddFinished(viewHolder);
            return false;
        }

        private void runEnterAnimation(final CellFeedViewHolder holder) {
            final int screenHeight = Utils.getScreenHeight(holder.itemView.getContext());
            holder.itemView.setTranslationY(screenHeight);
            holder.itemView.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            dispatchAddFinished(holder);
                        }
                    })
                    .start();
        }

        @Override
        public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                     @NonNull RecyclerView.ViewHolder newHolder,
                                     @NonNull ItemHolderInfo preInfo,
                                     @NonNull ItemHolderInfo postInfo) {
            cancelCurrentAnimationIfExists(newHolder);

            if (preInfo instanceof FeedItemHolderInfo) {
                FeedItemHolderInfo feedItemHolderInfo = (FeedItemHolderInfo) preInfo;
                CellFeedViewHolder holder = (CellFeedViewHolder) newHolder;

                animateHeartButton(holder);
                updateLikesCounter(holder, holder.getFeedItem().likesCount);
                if (ACTION_LIKE_IMAGE_CLICKED.equals(feedItemHolderInfo.updateAction)) {
                    animatePhotoLike(holder);
                }
            }

            return false;
        }

        private void cancelCurrentAnimationIfExists(RecyclerView.ViewHolder item) {
            if (likeAnimationsMap.containsKey(item)) {
                likeAnimationsMap.get(item).cancel();
            }
            if (heartAnimationsMap.containsKey(item)) {
                heartAnimationsMap.get(item).cancel();
            }
        }

        private void animateHeartButton(final CellFeedViewHolder holder) {
            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
            rotationAnim.setDuration(300);
            rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
            bounceAnimX.setDuration(300);
            bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
            bounceAnimY.setDuration(300);
            bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
            bounceAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    heartAnimationsMap.remove(holder);
                    dispatchChangeFinishedIfAllAnimationsEnded(holder);
                }
            });

            animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
            animatorSet.start();

            heartAnimationsMap.put(holder, animatorSet);
        }

        private void updateLikesCounter(CellFeedViewHolder holder, int toValue) {
            String likesCountTextFrom = holder.tsLikesCounter.getResources().getQuantityString(
                    R.plurals.likes_count, toValue - 1, toValue - 1
            );
            holder.tsLikesCounter.setCurrentText(likesCountTextFrom);

            String likesCountTextTo = holder.tsLikesCounter.getResources().getQuantityString(
                    R.plurals.likes_count, toValue, toValue
            );
            holder.tsLikesCounter.setText(likesCountTextTo);
        }

        private void animatePhotoLike(final CellFeedViewHolder holder) {
            holder.vBgLike.setVisibility(View.VISIBLE);
            holder.ivLike.setVisibility(View.VISIBLE);

            holder.vBgLike.setScaleY(0.1f);
            holder.vBgLike.setScaleX(0.1f);
            holder.vBgLike.setAlpha(1f);
            holder.ivLike.setScaleY(0.1f);
            holder.ivLike.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1f);
            bgScaleYAnim.setDuration(200);
            bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1f);
            bgScaleXAnim.setDuration(200);
            bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    likeAnimationsMap.remove(holder);
                    resetLikeAnimationState(holder);
                    dispatchChangeFinishedIfAllAnimationsEnded(holder);
                }
            });
            animatorSet.start();

            likeAnimationsMap.put(holder, animatorSet);
        }

        private void dispatchChangeFinishedIfAllAnimationsEnded(CellFeedViewHolder holder) {
            if (likeAnimationsMap.containsKey(holder) || heartAnimationsMap.containsKey(holder)) {
                return;
            }

            dispatchAnimationFinished(holder);
        }

        private void resetLikeAnimationState(CellFeedViewHolder holder) {
            holder.vBgLike.setVisibility(View.INVISIBLE);
            holder.ivLike.setVisibility(View.INVISIBLE);
        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder item) {
            super.endAnimation(item);
            cancelCurrentAnimationIfExists(item);
        }

        @Override
        public void endAnimations() {
            super.endAnimations();
            for (AnimatorSet animatorSet : likeAnimationsMap.values()) {
                animatorSet.cancel();
            }
        }

        public static class FeedItemHolderInfo extends ItemHolderInfo {
            public String updateAction;

            public FeedItemHolderInfo(String updateAction) {
                this.updateAction = updateAction;
            }
        }
    }
}