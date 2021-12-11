package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.DiscontinuityReason
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.ui.TimeBar.OnScrubListener
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.player_custom_control.*
import kotlinx.android.synthetic.main.player_custom_control.exo_progress
import kotlinx.android.synthetic.main.player_custom_control.exo_volume_icon
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*

class VideoActivity : AppCompatActivity(),
    View.OnClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener {
    companion object {
        const val DRM_SCHEME_EXTRA = "drm_scheme"
        const val DRM_LICENSE_URL_EXTRA = "drm_license_url"
        const val DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties"
        const val DRM_MULTI_SESSION_EXTRA = "drm_multi_session"
        const val PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders"
        const val ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW"
        const val EXTENSION_EXTRA = "extension"
        const val ACTION_VIEW_LIST = "com.google.android.exoplayer.demo.action.VIEW_LIST"
        const val URI_LIST_EXTRA = "uri_list"
        const val EXTENSION_LIST_EXTRA = "extension_list"
        const val AD_TAG_URI_EXTRA = "ad_tag_uri"
        const val ABR_ALGORITHM_EXTRA = "abr_algorithm"
        const val ABR_ALGORITHM_DEFAULT = "default"
        const val ABR_ALGORITHM_RANDOM = "random"
        const val SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode"
        const val SPHERICAL_STEREO_MODE_MONO = "mono"
        const val SPHERICAL_STEREO_MODE_TOP_BOTTOM = "top_bottom"
        const val SPHERICAL_STEREO_MODE_LEFT_RIGHT = "left_right"

        // For backwards compatibility only.
        private const val DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid"

        // Saved instance state keys.
        private const val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"
        private var DEFAULT_COOKIE_MANAGER: CookieManager? = null

        // Activity lifecycle
        private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
            if (e.type != ExoPlaybackException.TYPE_SOURCE) {
                return false
            }
            var cause: Throwable? = e.sourceException
            while (cause != null) {
                if (cause is BehindLiveWindowException) {
                    return true
                }
                cause = cause.cause
            }
            return false
        }

        init {
            DEFAULT_COOKIE_MANAGER = CookieManager()
            DEFAULT_COOKIE_MANAGER?.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        }
    }

    var tapCount = 1
    private var player: SimpleExoPlayer? = null
    private var mediaDrm: FrameworkMediaDrm? = null
    private var mediaSource: MediaSource? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private var startAutoPlay = false
    private var startWindow = 0

    // Fields used only for ad playback. The ads loader is loaded via reflection.
    private var startPosition: Long = 0
    private var adsLoader: AdsLoader? = null
    private var loadedAdTagUri: Uri? = null

    private var handler: Handler? = null
    private var mFormatBuilder: StringBuilder? = null
    private var mFormatter: Formatter? = null
    private var url: String? = null
    private var channelName = ""
    var cacheDataSourceFactory:DefaultDataSourceFactory?=null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (CookieHandler.getDefault() !== DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER)
        }
        setContentView(R.layout.activity_video)
        if (savedInstanceState != null) {
            trackSelectorParameters =savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS)
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            trackSelectorParameters = ParametersBuilder().build()
            clearStartPosition()
        }

       /* ll_main.visibility=View.GONE
        player_frame.visibility=View.VISIBLE
*/


        tv_play_back_speed?.setOnClickListener(this)
        tv_play_back_speed?.text = "" + tapCount

        tv_play_back_speed_symbol?.setOnClickListener(this)
        img_full_screen_enter_exit?.setOnClickListener(this)
        img_setting?.setOnClickListener(this)
        player_view1?.setControllerVisibilityListener(this)
        player_view1?.setErrorMessageProvider(PlayerErrorMessageProvider())
        player_view1?.requestFocus()
        if (intent != null) {
            if (intent.hasExtra("url")) {
                url = intent.getStringExtra("url")
            }
            if (intent.hasExtra("channelName")) {
                channelName = intent?.getStringExtra("channelName")!!
            }
        }
        player_view1.useController=true
        exo_progress?.isClickable = false
        exo_progress?.isEnabled = false
        exo_progress?.stopNestedScroll()
        exo_progress?.filterTouchesWhenObscured = false
        exo_progress?.isHorizontalScrollBarEnabled = false
        exo_progress?.isSelected = false
        exo_progress?.addListener(object : OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                timeBar.setEnabled(false)
            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                timeBar.setEnabled(false)
            }

            override fun onScrubStop(
                timeBar: TimeBar,
                position: Long,
                canceled: Boolean
            ) {
                timeBar.setEnabled(false)
            }
        })
        setProgress()
        exo_volume_icon?.setOnClickListener(View.OnClickListener {
            player?.volume = 0f
            setVolumeIcon()
        })
        exo_volume_icon_off?.setOnClickListener(View.OnClickListener {
            player?.volume = 1f
            setVolumeIcon()
        })
    }

    private fun setVolumeIcon() {
        if (player?.volume!! > 0) {
            exo_volume_icon?.visibility = View.VISIBLE
            exo_volume_icon_off?.visibility = View.GONE
        } else {
            exo_volume_icon?.visibility = View.GONE
            exo_volume_icon_off?.visibility = View.VISIBLE
        }
    }

    @SuppressLint("MissingSuperCall")
    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        releasePlayer()
        releaseAdsLoader()
        clearStartPosition()
        setIntent(intent)
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            if (player_view1 != null) {
                player_view1?.onResume()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            if (player_view1 != null) {
                player_view1?.onResume()
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            if (player_view1 != null) {
                player_view1?.onPause()
            }
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            if (player_view1 != null) {
                player_view1?.onPause()
            }
            releasePlayer()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        releaseAdsLoader()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.size == 0) {
            // Empty results are triggered if a permission is requested while another request was already
            // pending and can be safely ignored in this case.
            return
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer()
        } else {
            showToast(R.string.storage_permission_denied)
            finish()
        }
    }

    // Activity input
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateTrackSelectorParameters()
        updateStartPosition()
        outState.putParcelable(
            KEY_TRACK_SELECTOR_PARAMETERS,
            trackSelectorParameters
        )
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_WINDOW, startWindow)
        outState.putLong(KEY_POSITION, startPosition)
    }

    // OnClickListener methods
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // See whether the player view wants to handle media or DPAD keys events.
        return player_view1?.dispatchKeyEvent(event)!! || super.dispatchKeyEvent(event)
    }

    // PlaybackControlView.PlaybackPreparer implementation
    override fun onClick(view: View) {
        if (view.id == R.id.img_setting) {
            /*MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {

//                int rendererType = mappedTrackInfo.getRendererType(2);
//                boolean allowAdaptiveSelections =
//                        rendererType == C.TRACK_TYPE_VIDEO
//                                || (rendererType == C.TRACK_TYPE_AUDIO
//                                && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
//                                == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);
                Pair<AlertDialog, TrackSelectionView> dialogPair =
                        TrackSelectionView.getDialog(this, "Select Video Resolution", trackSelector, 0);
                dialogPair.second.setShowDisableOption(false);
                dialogPair.second.setAllowAdaptiveSelections(false);
                dialogPair.first.show();
            }*/
        } else if (view.id == R.id.img_full_screen_enter_exit) {
            val display =
                (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val orientation = display.orientation
            if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                frame_layout_main?.layoutParams = LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                supportActionBar?.show()
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                //toolbar.setVisibility(View.VISIBLE);
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                frame_layout_main?.layoutParams = LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                // playerView.setLayoutParams(new PlayerView.LayoutParams(PlayerView.LayoutParams.MATCH_PARENT, PlayerView.LayoutParams.WRAP_CONTENT));
                supportActionBar?.hide()
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                // toolbar.setVisibility(View.GONE);
            }
        } else if (view.id == R.id.tv_play_back_speed || view.id == R.id.tv_play_back_speed_symbol) {
            if (tv_play_back_speed?.text == "1") {
                tapCount++
                val param = PlaybackParameters(2f)
                player?.playbackParameters = param
                tv_play_back_speed?.text = "" + 2
            } else if (tv_play_back_speed?.text == "2") {
                tapCount++
                val param = PlaybackParameters(3f)
                player?.playbackParameters = param
                tv_play_back_speed?.text = "" + 3
            } else if (tv_play_back_speed?.text == "3") {
                tapCount++
                val param = PlaybackParameters(4f)
                player?.playbackParameters = param
                tv_play_back_speed?.text = "" + 4
            } else if (tv_play_back_speed?.text == "4") {
                tapCount++
                val param = PlaybackParameters(5f)
                player?.playbackParameters = param
                tv_play_back_speed?.text = "" + 5
            } else {
                tapCount = 0
                player?.playbackParameters = null
                tv_play_back_speed?.text = "" + 1
            }
        }
    }

    // PlaybackControlView.VisibilityListener implementation
    override fun preparePlayback() {
        initializePlayer()
    }

    // Internal methods
    private fun initializePlayer() {
        val trackSelectionFactory: TrackSelection.Factory
        trackSelectionFactory = AdaptiveTrackSelection.Factory()
        val renderersFactory = DefaultRenderersFactory(
            this, null,
            DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
        )
        trackSelector = DefaultTrackSelector(trackSelectionFactory)
        trackSelector?.parameters = trackSelectorParameters
        lastSeenTrackGroupArray = null
        val defaultAllocator =
            DefaultAllocator(true, 200 * C.DEFAULT_BUFFER_SEGMENT_SIZE)
        val defaultLoadControl = DefaultLoadControl(
            defaultAllocator,
            DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
            DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
            DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES,
            DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS
        )
        player = ExoPlayerFactory.newSimpleInstance( /* context= */this@VideoActivity,
            renderersFactory,
            trackSelector,
            defaultLoadControl
        )
        player?.addListener(PlayerEventListener())
        player?.playWhenReady = startAutoPlay
        player?.addAnalyticsListener(EventLogger(trackSelector))
        player_view1.useController=true
        player_view1?.player = player
        player_view1?.setPlaybackPreparer(this)
        val bandwidthMeter = DefaultBandwidthMeter()
        val userAgent = Util.getUserAgent(
            this@VideoActivity,
            this@VideoActivity.getString(R.string.app_name)
        )
        cacheDataSourceFactory = DefaultDataSourceFactory(
            this@VideoActivity,
            bandwidthMeter,
            DefaultHttpDataSourceFactory(userAgent, bandwidthMeter)
        )
        mediaSource = buildMediaSource(Uri.parse(url))
        player?.prepare(mediaSource)
        updateButtonVisibilities()
        initBwd()
        initFwd()
    }

    private fun buildMediaSource(
        uri: Uri,
        overrideExtension: String? = null
    ): MediaSource {
        @C.ContentType val type =
            Util.inferContentType(uri, overrideExtension)


        return when (type) {
            C.TYPE_DASH -> DashMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    //    private DefaultDrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(
    //            UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray, boolean multiSession)
    //            throws UnsupportedDrmException {
    //        HttpDataSource.Factory licenseDataSourceFactory =
    //                ((App) getApplication()).buildHttpDataSourceFactory();
    //        HttpMediaDrmCallback drmCallback =
    //                new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
    //        if (keyRequestPropertiesArray != null) {
    //            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
    //                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
    //                        keyRequestPropertiesArray[i + 1]);
    //            }
    //        }
    //        releaseMediaDrm();
    //        mediaDrm = FrameworkMediaDrm.newInstance(uuid);
    //        return new DefaultDrmSessionManager<>(uuid, mediaDrm, drmCallback, null, multiSession);
    //    }
    private fun releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters()
            updateStartPosition()
            player?.release()
            player = null
            mediaSource = null
            trackSelector = null
        }
        releaseMediaDrm()
    }

    private fun releaseMediaDrm() {
        if (mediaDrm != null) {
            mediaDrm?.release()
            mediaDrm = null
        }
    }

    private fun releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader?.release()
            adsLoader = null
            loadedAdTagUri = null
            player_view1?.overlayFrameLayout?.removeAllViews()
        }
    }

    private fun updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector?.parameters
        }
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player?.playWhenReady!!
            startWindow = player?.currentWindowIndex!!
            startPosition = Math.max(0, player?.contentPosition!!)
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private fun updateButtonVisibilities() {
        if (player == null) {
            return
        }
        val mappedTrackInfo = trackSelector?.currentMappedTrackInfo ?: return
        for (i in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(i)
            if (trackGroups.length != 0) {
                var label: Int
                label = when (player?.getRendererType(i)) {
                    C.TRACK_TYPE_AUDIO -> R.string.exo_track_selection_title_audio
                    C.TRACK_TYPE_VIDEO -> R.string.exo_track_selection_title_video
                    C.TRACK_TYPE_TEXT -> R.string.exo_track_selection_title_text
                    else -> -1
                }
            }
        }
    }

    private fun showToast(messageId: Int) {
        showToast(getString(messageId))
    }

    private fun showToast(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onVisibilityChange(visibility: Int) {}
    private fun setProgress() {
        handler = Handler()
        //Make sure you update Seekbar on UI thread
        handler?.post(object : Runnable {
            override fun run() {
                if (player != null) {
                    tv_player_current_time?.text = stringForTime(
                        player?.currentPosition?.toInt()!!
                    )
                    tv_player_end_time?.text = stringForTime(player?.duration?.toInt()!!)
                    handler?.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun initBwd() {
        img_bwd?.requestFocus()
        img_bwd?.setOnClickListener { player?.seekTo(player?.currentPosition!! - 10000) }
    }

    private fun initFwd() {
        img_fwd?.requestFocus()
        img_fwd?.setOnClickListener { player?.seekTo(player?.currentPosition!! + 10000) }
    }

    private fun stringForTime(timeMs: Int): String {
        mFormatBuilder = StringBuilder()
        mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        mFormatBuilder?.setLength(0)
        return if (hours > 0) {
            mFormatter?.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter?.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    private inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            when (playbackState) {
                ExoPlayer.STATE_READY -> loading?.visibility = View.GONE
                ExoPlayer.STATE_BUFFERING -> loading?.visibility = View.VISIBLE
            }
            updateButtonVisibilities()
        }

        override fun onPositionDiscontinuity(@DiscontinuityReason reason: Int) {
            if (player?.playbackError != null) {
                // The user has performed a seek whilst in the error state. Update the resume position so
                // that if the user then retries, playback resumes from the position to which they seeked.
                updateStartPosition()
            }
        }

        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition()
                initializePlayer()
            } else {
                updateStartPosition()
                updateButtonVisibilities()
                //                showControls();
            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            updateButtonVisibilities()
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector?.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast(R.string.error_unsupported_video)
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast(R.string.error_unsupported_audio)
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

    private inner class PlayerErrorMessageProvider :
        ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString = getString(R.string.error_generic)
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    val decoderInitializationException =
                        cause
                    errorString = if (decoderInitializationException.decoderName == null) {
                        if (decoderInitializationException.cause is DecoderQueryException) {
                            getString(R.string.error_querying_decoders)
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            getString(
                                R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType
                            )
                        } else {
                            getString(
                                R.string.error_no_decoder,
                                decoderInitializationException.mimeType
                            )
                        }
                    } else {
                        getString(
                            R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName
                        )
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed();
        finish()
    }
}