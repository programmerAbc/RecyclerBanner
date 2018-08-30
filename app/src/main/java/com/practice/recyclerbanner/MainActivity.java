package com.practice.recyclerbanner;

import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.practice.recyclerbanner.adapter.BannerAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    RecyclerView banner;
    BannerAdapter bannerAdapter;
    List<BannerData> bannerDataList = new LinkedList<>();
    LinearLayoutManager linearLayoutManager;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new LinearSnapHelper();
        bannerDataList.add(BannerData.newBuilder()
                .title("1")
                .build());
        bannerDataList.add(BannerData.newBuilder()
                .title("2")
                .build());
        bannerDataList.add(BannerData.newBuilder()
                .title("3")
                .build());
        bannerDataList.add(BannerData.newBuilder()
                .title("4")
                .build());
        banner = findViewById(R.id.banner);
        snapHelper.attachToRecyclerView(banner);
        bannerAdapter = new BannerAdapter();
        banner.setLayoutManager(linearLayoutManager);
        banner.setAdapter(bannerAdapter);
        bannerAdapter.setNewData(bannerDataList);
        banner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int first = linearLayoutManager.findFirstVisibleItemPosition();
                int last = linearLayoutManager.findLastVisibleItemPosition();
                int center = banner.getLeft() + banner.getWidth() / 2;
                for (int i = first; i <= last; ++i) {
                    View view = linearLayoutManager.findViewByPosition(i);
                    float scale = 1f - (float) Math.abs(view.getLeft() + view.getWidth() / 2 - center) / banner.getWidth() / 4f;
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            }
        });
        banner.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(-15, 0, -15, 0);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = (int) (parent.getWidth() * 0.8);
                view.setLayoutParams(layoutParams);
            }
        });
        linearLayoutManager.scrollToPosition(bannerAdapter.getItemCount() / 2 - bannerAdapter.getItemCount() / 2 % bannerDataList.size());
        banner.smoothScrollToPosition(bannerAdapter.getItemCount() / 2 - bannerAdapter.getItemCount() / 2 % bannerDataList.size());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (banner.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) return;
                        int centerIndex = getCenterItemIndex();
                        linearLayoutManager.scrollToPosition(centerIndex);
                        banner.smoothScrollToPosition(centerIndex + 1);
                    }
                });
            }
        }, 0, 3000);

    }

    public int getCenterItemIndex() {
        int first = linearLayoutManager.findFirstVisibleItemPosition();
        int last = linearLayoutManager.findLastVisibleItemPosition();
        return first + (last - first) / 2;
    }
}
