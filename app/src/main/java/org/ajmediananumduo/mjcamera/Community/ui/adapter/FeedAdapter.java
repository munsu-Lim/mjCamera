package org.ajmediananumduo.mjcamera.Community.ui.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;
import org.ajmediananumduo.mjcamera.Community.ui.activity.ImageDTO;
import org.ajmediananumduo.mjcamera.R;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

//어댑터
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;
    public static final String ACTION_LIKE_BUTTON_CLICKED = "touch_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "touch_image";
    private List<ImageDTO> feedItems;
    private List<String> auidLists;
    private Context context;
    private OnFeedItemClickListener onFeedItemClickListener;
    private int size;
    private boolean showLoadingView = false;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public FeedAdapter(Context context,List<ImageDTO> imageDTOs, List<String> uidList) {
        this.context = context;
        feedItems = imageDTOs;
        size =feedItems.size();
        auidLists = uidList;
    }

    //리사이클뷰의 홀더정의
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        }
        return null;
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        //화면을 눌러도 좋아요++
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                onStarClicked(database.getReference().child("images").child(auidLists.get(adapterPosition)));
            }
        });
        //좋아요 버튼을 눌러도 좋아요++
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                onStarClicked(database.getReference().child("images").child(auidLists.get(adapterPosition)));
            }
        });
    }

    //피드로 결합
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ((CellFeedViewHolder) viewHolder).bindView(feedItems.get(position));
    }

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
        return feedItems.size();
    }

    //메인에서 호출
    public void updateItems(boolean animated) {

        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
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

    public class CellFeedViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.vBgLike)
        View vBgLike;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @BindView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @BindView(R.id.userId)
        TextView userId;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        //객체화
        public void bindView(ImageDTO thisDTO) {
            ImageDTO thisItem = thisDTO;
            int adapterPosition = getAdapterPosition();
            Picasso.with(itemView.getContext()).load(thisItem.imageUrl).into(ivFeedCenter);
            userId.setText(thisItem.userId);
            conText.setText(thisItem.description);
            filterText.setText(thisItem.title);
            //stars에서 ui를 확인 후 속빈하트, 속찬하트
            if (thisItem.stars.containsKey(auth.getCurrentUser().getUid())) {
                btnLike.setImageResource(R.drawable.ic_heart_red);

            }else {
                btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
            tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                    R.plurals.likes_count, thisItem.likesCount, thisItem.likesCount
            ));
        }
    }


    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);
    }

    //트랜잭션 적용
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ImageDTO p = mutableData.getValue(ImageDTO.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(auth.getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    p.likesCount = p.likesCount - 1;
                    p.stars.remove(auth.getCurrentUser().getUid());
                } else {
                    // Star the post and add self to stars
                    p.likesCount = p.likesCount + 1;
                    p.stars.put(auth.getCurrentUser(). getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }

}