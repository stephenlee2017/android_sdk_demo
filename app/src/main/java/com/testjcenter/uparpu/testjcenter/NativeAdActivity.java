package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.uparpu.api.AdError;
import com.uparpu.nativead.api.NativeAd;
import com.uparpu.nativead.api.UpArpuNative;
import com.uparpu.nativead.api.UpArpuNativeAdView;
import com.uparpu.nativead.api.UpArpuNativeNetworkListener;

/**
 * Created by Z on 2018/1/11.
 */
public class NativeAdActivity extends Activity {

    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_all
            , DemoApplicaion.mPlacementId_facebook
            , DemoApplicaion.mPlacementId_admob
            , DemoApplicaion.mPlacementId_inmobi
            , DemoApplicaion.mPlacementId_flurry
            , DemoApplicaion.mPlacementId_applovin
            , DemoApplicaion.mPlacementId_mobivsta};

    String unitGroupName[] = new String[]{
            "All network",
            "facebook",
            "admob",
            "inmobi",
            "flurry",
            "applovin",
            "mobvista"
    };

    UpArpuNative upArapuNatives[] = new UpArpuNative[unitIds.length];
    UpArpuNativeAdView upArpuNativeAdView;
    NativeAd mNativeAd;

    RadioGroup mRadioGroup;

    int mCurrentSelectIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_nativead_layout);
        Fresco.initialize(this);

        mRadioGroup = (RadioGroup)findViewById(R.id.placement_select_group);

        for (int i = 0; i < unitIds.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(20, 20, 20, 20);                 // 设置文字距离按钮四周的距离
            radioButton.setText(unitGroupName[i]);
            radioButton.setId(i);
            mRadioGroup.addView(radioButton);
        }

        mRadioGroup.check(0);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mCurrentSelectIndex = i;
            }
        });

//        UpArpuNative upArpuNative = new UpArpuNative(Context,placementid,UpArpuNativeNetworkListener);

        UpArpuRender upArpuRender = new UpArpuRender(this);

//        upArapuNatives[0].coerceCleanAllAdCache();

        for (int i = 0; i < unitIds.length; i++) {
            upArapuNatives[i] = new UpArpuNative(this, unitIds[i], new UpArpuNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    if (nativeAd != null) {
                        mNativeAd = nativeAd;
                        try{
                            mNativeAd.renderAdView(upArpuNativeAdView);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        upArpuNativeAdView.setVisibility(View.VISIBLE);
                        mNativeAd.prepare(upArpuNativeAdView);
                    }
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    Toast.makeText(NativeAdActivity.this, "load fail...：" + adError.getDesc(),Toast.LENGTH_LONG).show();

                }
            }, upArpuRender);

            if (upArpuNativeAdView == null) {
                upArpuNativeAdView = new UpArpuNativeAdView(this);
            }
        }




        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    upArapuNatives[mCurrentSelectIndex].makeAdRequest();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.loadcache_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeAd nativeAd = upArapuNatives[mCurrentSelectIndex].showAds();
                if (nativeAd != null) {
                    mNativeAd = nativeAd;
                    try {
                        mNativeAd.renderAdView(upArpuNativeAdView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    upArpuNativeAdView.setVisibility(View.VISIBLE);
                    mNativeAd.prepare(upArpuNativeAdView);
                } else {
                    Toast.makeText(NativeAdActivity.this, "this placement no cache!",Toast.LENGTH_LONG).show();

                }

            }
        });

        upArpuNativeAdView.setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.ad_container)).addView(upArpuNativeAdView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNativeAd != null) {
            mNativeAd.destory();
        }
        if (upArapuNatives != null) {
            for (UpArpuNative upArpuNative : upArapuNatives) {
                upArpuNative.coerceCleanAllAdCache();
                upArpuNative.destory();
            }
        }
        Fresco.shutDown();
        upArpuNativeAdView.removeAllViews();
    }
}
