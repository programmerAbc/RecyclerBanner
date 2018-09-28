package com.practice.recyclerbanner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RecyclerBanner extends FrameLayout {
    RecyclerView bannerRv;
    BannerAdapter bannerAdapter;
    BannerLayoutManager bannerLayoutManager;
    SnapHelper snapHelper;
    boolean forbidTouch = true;

    public RecyclerBanner(@NonNull Context context) {
        super(context);
        init();
    }

    public RecyclerBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.recycler_banner, this);
        bannerRv = findViewById(R.id.bannerRv);
        bannerLayoutManager = new BannerLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        bannerRv.setLayoutManager(bannerLayoutManager);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(bannerRv);
        bannerAdapter = new BannerAdapter(bannerRv, bannerLayoutManager);
        bannerRv.setAdapter(bannerAdapter);
        bannerRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int first = bannerLayoutManager.findFirstVisibleItemPosition();
                int last = bannerLayoutManager.findLastVisibleItemPosition();
                int center = bannerRv.getLeft() + bannerRv.getWidth() / 2;
                for (int i = first; i <= last; ++i) {
                    View view = bannerLayoutManager.findViewByPosition(i);
                    float scale = 1f - (float) Math.abs(view.getLeft() + view.getWidth() / 2 - center) / bannerRv.getWidth() / 4f;
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            }
        });
        bannerRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(dp2px(-5), 0, dp2px(-5), 0);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = (int) (parent.getWidth() * 0.8);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return forbidTouch || super.onInterceptTouchEvent(ev);
    }

    private int dp2px(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp);
    }

    public void setNewData(List<Data> data) {
        forbidTouch = data == null || data.isEmpty() || data.size() == 1;
        bannerAdapter.setNewData(data);
    }

    public void shutdown() {
        bannerAdapter.shutdown();
    }


    public static class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
        List<Data> bannerDataList = new LinkedList<>();
        RecyclerView bannerRv;
        BannerLayoutManager bannerLayoutManager;
        int startIndex = 0;
        Timer timer = null;
        Handler mainHandler;
        boolean shouldAutoPlay = false;

        public BannerAdapter(RecyclerView bannerRv, BannerLayoutManager bannerLayoutManager) {
            this.bannerRv = bannerRv;
            this.bannerLayoutManager = bannerLayoutManager;
            mainHandler = new Handler(Looper.getMainLooper());
        }

        public void shutdown() {
            if (timer != null) {
                timer.cancel();
            }
        }

        public void setNewData(List<Data> data) {
            shouldAutoPlay = false;
            if (timer != null) {
                timer.cancel();
            }
            bannerLayoutManager.scrollToPosition(0);
            bannerDataList.clear();
            if (data != null && !data.isEmpty()) {
                bannerDataList.addAll(data);
            }
            notifyDataSetChanged();
            if (!bannerDataList.isEmpty()) {
                startIndex = getItemCount() / 2 - getItemCount() / 2 % bannerDataList.size();
                bannerLayoutManager.scrollToPosition(startIndex);
                bannerRv.smoothScrollToPosition(startIndex);
            } else {
                startIndex = 0;
            }
            if (!bannerDataList.isEmpty() && bannerDataList.size() > 1) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (shouldAutoPlay && bannerRv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                                    int centerIndex = getCenterItemIndex();
                                    bannerRv.smoothScrollToPosition(centerIndex + 1);
                                }
                            }
                        });
                    }
                }, 0, 3000);
            }
        }

        public int getCenterItemIndex() {
            int first = bannerLayoutManager.findFirstVisibleItemPosition();
            int last = bannerLayoutManager.findLastVisibleItemPosition();
            return first + (last - first) / 2;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bindData(bannerDataList.get(i % bannerDataList.size()));
        }

        @Override
        public int getItemCount() {
            if (bannerDataList.isEmpty()) {
                return 0;
            } else if (bannerDataList.size() == 1) {
                return 3;
            } else {
                return Integer.MAX_VALUE;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView bannerTv;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                bannerTv = itemView.findViewById(R.id.bannerTv);
            }

            public void autoConfigAutoPlay() {
                if (!shouldAutoPlay) {
                    shouldAutoPlay = bannerDataList.size() > 1 && getAdapterPosition() == startIndex;
                }
            }

            void bindData(Data data) {
                autoConfigAutoPlay();
                bannerTv.setText(data.getTitle());
            }
        }
    }


    public static class BannerLayoutManager extends LinearLayoutManager {

        public BannerLayoutManager(Context context) {
            super(context);
        }

        public BannerLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public BannerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                private static final float MILLISECONDS_PER_INCH = 300f;

                @Override
                public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                    return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }

    public static class Data {
        String title;

        public Data() {
        }

        public Data(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
