package com.practice.recyclerbanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    RecyclerBanner recyclerBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerBanner = findViewById(R.id.recyclerBanner);
        recyclerBanner.setNewData(Arrays.asList(new RecyclerBanner.Data("1"), new RecyclerBanner.Data("2")));
    }

    @Override
    protected void onDestroy() {
        recyclerBanner.shutdown();
        super.onDestroy();
    }
}
