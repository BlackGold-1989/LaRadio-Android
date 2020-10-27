package com.firecast.laradiosv.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.firecast.laradiosv.Config;
import com.firecast.laradiosv.Config;
import com.firecast.laradiosv.activities.MainActivity;
import com.firecast.laradiosv.utilities.Utils;
import com.firecast.laradiosv.services.ArtGetter;
import com.firecast.laradiosv.services.NotificationBuilder;
import com.firecast.laradiosv.services.ParserURL;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
//import Config;
import com.firecast.laradiosv.R;
//import ArtGetter;
//import NotificationBuilder;
//import ParserURL;
//import Utils;

import java.io.IOException;

import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;

public class FragmentRadio extends Fragment implements OnClickListener, RadioListener {

    private MainActivity mainActivity;
    private RadioManager radioManager;
    private boolean runningOnOldConnection;
    private String urlToPlay = Config.RADIO_STREAM_URL;
    private Activity activity;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView buttonPlay, buttonStopPlay, albumArt;
    private static int RETRY_INTERVAL = 7000;
    private int errorCount = 0;
    private static int RETRY_MAX = 2;
    static int audioSessionID = 0;
    private InterstitialAd interstitialAd;
    ImageView imageView;
    int counter = 1;

    public FragmentRadio() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_radio_player, container, false);

        initializeUIElements();
        albumArt = (ImageView) linearLayout.findViewById(R.id.image);

        return linearLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();

        Utils.isNetworkAvailable(activity, true);

        radioManager = RadioManager.with(activity);

        radioManager.registerListener(NotificationBuilder.getStaticNotificationUpdater(activity.getBaseContext()));

        if (!radioManager.isConnected()) {
            radioManager.connect();
            runningOnOldConnection = false;
        } else {
            runningOnOldConnection = true;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                urlToPlay = (ParserURL.getUrl(urlToPlay));
                if (isPlaying()) {
                    if (!radioManager.getService().getRadioUrl().equals(urlToPlay)) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, getResources().getString(R.string.alert_network), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }

        });
    }

    private void initializeUIElements() {
        progressBar = (ProgressBar) linearLayout.findViewById(R.id.loadingIndicator);
        progressBar.setMax(100);
        progressBar.setVisibility(View.INVISIBLE);

        textView = (TextView) linearLayout.findViewById(R.id.loadingText);
        textView.setVisibility(View.INVISIBLE);

        buttonPlay = (ImageView) linearLayout.findViewById(R.id.btn_play);
        buttonPlay.setOnClickListener(this);

        buttonStopPlay = (ImageView) linearLayout.findViewById(R.id.btn_pause);
        buttonStopPlay.setOnClickListener(this);

        updateButtons();
    }

    public void updateButtons() {
        if (isPlaying() || progressBar.getVisibility() == View.VISIBLE || textView.getVisibility() == View.VISIBLE) {

            buttonPlay.setEnabled(true);
            buttonStopPlay.setEnabled(true);

            buttonPlay.setVisibility(View.GONE);
            buttonStopPlay.setVisibility(View.VISIBLE);

        } else {
            buttonPlay.setEnabled(true);
            buttonStopPlay.setEnabled(true);

            updateMediaInfoFromBackground(null);
        }
    }

    public void onClick(View view) {
        if (view == buttonPlay) {

            if (urlToPlay != null) {
                startPlaying();

                buttonPlay.setVisibility(View.GONE);
                buttonStopPlay.setVisibility(View.VISIBLE);

                AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
                int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (volume_level < 1) {
                    Toast.makeText(activity, getResources().getString(R.string.volume_low), Toast.LENGTH_SHORT).show();
                }

                if (counter == Config.INTERSTITIAL_COUNTER_NUMBER) {
                    interstitialAd = new InterstitialAd(getActivity());
                    interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitialAd.loadAd(adRequest);
                    interstitialAd.setAdListener(new AdListener() {
                        public void onAdLoaded() {
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            }
                        }
                    });
                    counter = 1;
                } else {
                    counter++;
                }


            } else {
                Log.d("INFO", "The loading of urlToPlay should happen almost instantly, so this code should never be reached");
            }

        } else if (view == buttonStopPlay) {
            stopPlaying();

            buttonPlay.setVisibility(View.VISIBLE);
            buttonStopPlay.setVisibility(View.GONE);
        }
    }

    private void startPlaying() {

        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);

//        MediaPlayer audioPlayer = new MediaPlayer();
//        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            audioPlayer.setDataSource(getActivity(), Uri.parse(urlToPlay));
//            audioPlayer.prepare();
//            audioPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        radioManager.startRadio(urlToPlay);
//        radioManager.startRadio("http://192.111.140.6:9779/stream");
        radioManager.updateNotification(
                activity.getResources().getString(R.string.notification_title),
                activity.getResources().getString(R.string.notification_subtitle),
                R.mipmap.app_icon,
                BitmapFactory.decodeResource(activity.getResources(), R.mipmap.app_icon)
        );
        updateButtons();
    }

    private void stopPlaying() {

        radioManager.stopRadio();
        progressBar.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        updateButtons();
        if (runningOnOldConnection) {
            resetRadioManager();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateMediaInfoFromBackground(String info) {

        TextView nowPlayingTitle = (TextView) linearLayout.findViewById(R.id.now_playing_title);
        TextView nowPlaying = (TextView) linearLayout.findViewById(R.id.now_playing);

        if (info != null) {
            nowPlaying.setText(info);
        }
        if (info != null && nowPlayingTitle.getVisibility() == View.GONE) {
            nowPlayingTitle.setVisibility(View.VISIBLE);
            nowPlaying.setVisibility(View.VISIBLE);
        } else if (info == null) {
            nowPlayingTitle.setVisibility(View.GONE);
            nowPlaying.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRadioLoading() {

    }

    @Override
    public void onRadioConnected() {
        startPlaying();
    }

    @Override
    public void onRadioStarted() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                updateButtons();
            }
        });
    }

    @Override
    public void onRadioStopped() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                updateButtons();

                if (FragmentRadio.this.isVisible())
                    RadioManager.getService().cancelNotification();
            }
        });
    }

    @Override
    public void onMetaDataReceived(String key, final String value) {
        if (key != null && (key.equals("StreamTitle") || key.equals("title")) && !value.equals("")) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMediaInfoFromBackground(value);
                }
            });

            updateAlbumArt(value);

        }
    }

    @Override
    public void onAudioSessionId(int i) {
        audioSessionID = i;
    }

    @Override
    public void onError() {
        Log.d("INFO", "onError");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (errorCount < RETRY_MAX) {
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            errorCount += 1;
                            startPlaying();
                        }
                    }, RETRY_INTERVAL);
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.error_retry), Toast.LENGTH_SHORT).show();
                    Log.v("INFO", "Received various errors, tried to create a new RadioManager");

                    resetRadioManager();

                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    updateButtons();
                }
            }
        });
    }

    @Override
    public void onResume() {
        updateButtons();
        super.onResume();
        radioManager.registerListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        radioManager.unregisterListener(this);
    }

    private boolean isPlaying() {
        return (null != radioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying());
    }

    private void resetRadioManager() {
        try {
            radioManager.disconnect();
        } catch (Exception e) {
        }
        RadioManager.flush();
        radioManager = RadioManager.with(activity);
        radioManager.connect();
        radioManager.registerListener(this);
        radioManager.registerListener(NotificationBuilder.getStaticNotificationUpdater(activity.getBaseContext()));
        runningOnOldConnection = false;
    }

    private void updateAlbumArt(String infoString) {
        if (imageView != null) {
            ArtGetter.getImageForQuery(infoString, new ArtGetter.AlbumCallback() {
                @Override
                public void finished(Bitmap art) {
                    if (art != null) {
                        imageView.setImageBitmap(art);
                    }
                }
            }, activity);
        }
    }

}