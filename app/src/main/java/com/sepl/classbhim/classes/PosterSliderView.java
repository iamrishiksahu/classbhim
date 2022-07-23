package com.sepl.classbhim.classes;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.sepl.classbhim.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PosterSliderView {

    private ViewPager posterViewPager;
    private List<String> arrangedList;
    private int currentPage;
    private Timer timer;
    final static int DELAY_TIME = 3000, PERIOD_TIME = 3000;

    public PosterSliderView(View view) {
        posterViewPager = view.findViewById(R.id.poster_slider_view_pager);
    }

    public void setPosters(List<String> imageUrls) {

        if (timer != null) {
            timer.cancel();
        }

        currentPage = 2;
        arrangedList = new ArrayList<>();

        for (int x = 0; x < imageUrls.size(); x++) {
            arrangedList.add(imageUrls.get(x));
        }
        arrangedList.add(0, imageUrls.get(imageUrls.size() - 1));
        arrangedList.add(1, imageUrls.get(imageUrls.size() - 2));

        arrangedList.add(imageUrls.get(0));
        arrangedList.add(imageUrls.get(1));

        PosterAdapter adapter = new PosterAdapter(arrangedList);
        posterViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        posterViewPager.setPageMargin(20);

        posterViewPager.setCurrentItem(currentPage);

        startSliding(arrangedList);


        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    createInfiniteLoop(arrangedList);
                }

            }
        };

        posterViewPager.addOnPageChangeListener(listener);

        posterViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                createInfiniteLoop(arrangedList);
                stopSliding();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startSliding(arrangedList);
                }
                return false;
            }
        });

    }

    private void createInfiniteLoop(List<String> list) {

        if (currentPage == list.size() - 2) {
            currentPage = 2;
            posterViewPager.setCurrentItem(currentPage, false);
        }
        if (currentPage == 1) {
            currentPage = list.size() - 3;
            posterViewPager.setCurrentItem(currentPage, false);
        }


    }

    private void startSliding(List<String> list) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage >= list.size()) {
                    currentPage = 1;
                }
                posterViewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, DELAY_TIME, PERIOD_TIME);
    }

    private void stopSliding() {
        timer.cancel();
    }

}