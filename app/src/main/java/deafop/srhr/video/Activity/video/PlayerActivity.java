package deafop.srhr.video.Activity.video;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;


public class PlayerActivity extends AppCompatActivity implements Player.EventListener  {

    SimpleExoPlayer player;
    private ImageView imageView;
    private PlayerView playerView;
    private String video_url;
    private ProgressBar progressBar;
    private boolean isFullScreen = false;

    Methods methods;
    int position;


    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode) {
            setAppTheme(R.style.AppTheme4);
        } else {
            setAppTheme(R.style.AppTheme3);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        methods = new Methods(this);
        position = getIntent().getIntExtra("pos2", 0);
        video_url = Setting.arrayList.get(position).getVideo_url();


        imageView = findViewById(R.id.imageView_full_video_play);
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progresbar_video_play);
        progressBar.setVisibility(View.VISIBLE);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        player = ExoPlayerFactory.newSimpleInstance(PlayerActivity.this, trackSelector);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory;
        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(PlayerActivity.this,
                Util.getUserAgent(PlayerActivity.this, getResources().getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        final String finalUrl = video_url;
        MediaSource videoSource;

        if (finalUrl.endsWith("_Other")) {
            finalUrl.replace("_Other", "");
        }

        if (finalUrl.endsWith("m3u8") || finalUrl.endsWith("M3U8")) {
            videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(false)
                    .setExtractorFactory(
                            new DefaultHlsExtractorFactory())
                    .createMediaSource(Uri.parse(finalUrl));
        }else {
            videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(finalUrl));
        }
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);

        player.addListener(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullScreen) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.exo_controls_fullscreen_enter));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    isFullScreen = false;
                }else{
                    imageView.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.exo_controls_fullscreen_exit));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    isFullScreen = true;
                }
            }
        });

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String json = JSONParser.okhttpPost(Setting.SERVER_URL, methods.getAPIRequest(Setting.METHOD_VIDEO, 0, "", Setting.arrayList.get(position).getId(), "", "", "", "", "", "","","","","","","","", null));
                return null;
            }
        }.execute();

    }

    private void setAppTheme(@StyleRes int style) {
        setTheme(style);
    }

    @Override
    public void onBackPressed() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED || !playWhenReady) {
            playerView.setKeepScreenOn(false);
        } else {
            playerView.setKeepScreenOn(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }
}
