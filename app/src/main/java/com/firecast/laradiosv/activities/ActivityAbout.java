package com.firecast.laradiosv.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

//import com.firecast.laradiosv.Config;
import com.firecast.laradiosv.Config;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
//import Config;
import com.firecast.laradiosv.R;
import com.firecast.laradiosv.adapters.AdapterAbout;

public class ActivityAbout extends AppCompatActivity {

    ListView list;
    String[] titleId;
    String[] subtitleId;

    Integer[] imageId = {
            R.drawable.ic_other_appname,
            R.drawable.ic_other_build,
            R.drawable.ic_other_email,
            R.drawable.ic_other_copyright,
            R.drawable.ic_other_share,
            R.drawable.ic_other_rate,
            R.drawable.ic_other_more

    };

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.drawer_about);
        }

        loadAdMobBannerAd();

        titleId = getResources().getStringArray(R.array.title);
        subtitleId = getResources().getStringArray(R.array.subtitle);

        AdapterAbout adapter = new AdapterAbout(this, titleId, subtitleId, imageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    Intent sendInt = new Intent(Intent.ACTION_SEND);
                    sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "\n" + getString(R.string.share_text) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                    sendInt.setType("text/plain");
                    startActivity(Intent.createChooser(sendInt, "Share"));
                }
                if (position == 5) {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                }
                if (position == 6) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void loadAdMobBannerAd() {
        if (Config.ENABLE_ADMOB_ADS) {
            adView = (AdView) findViewById(R.id.adView);
            adView.loadAd(new AdRequest.Builder().build());
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            Log.d("Log", "Admob Banner is Disabled");
        }
    }

}
