package com.lucianosimo.copaam2015;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.chartboost.sdk.Chartboost;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GoogleBaseGameActivity;
import com.lucianosimo.copaam2015.manager.ResourcesManager;
import com.lucianosimo.copaam2015.manager.SceneManager;

public class GameActivity extends GoogleBaseGameActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private SmoothCamera camera;
	private GoogleApiClient mGoogleApiClient;
	
	private final static float SPLASH_DURATION = 5f;
	
	private final static String CHARTBOOST_APP_ID = "55776ee6c909a607de533657";
	private final static String CHARTBOOST_APP_SIGNATURE = "96c1be53a40c20d79b565087bc3dbdaa02ac57f3";
	
	private final static String HIGHEST_SCORE_LEADERBOARD_ID = "CgkIrqXyopUGEAIQAQ";
	
	private final static String SIGN_IN_OTHER_ERROR = "There was an issue with sign in. Please try again later.";
	
	private static int RC_SIGN_IN = 9001;
	private static int RC_LEADERBOARD = 9002;

	private boolean mResolvingConnectionFailure = false;
	private boolean mAutoStartSignInFlow = true;

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	            .addConnectionCallbacks(this)
	            .addOnConnectionFailedListener(this)
	            .addApi(Games.API).addScope(Games.SCOPE_GAMES)
	            .build();
		Chartboost.startWithAppId(this, CHARTBOOST_APP_ID, CHARTBOOST_APP_SIGNATURE);
	    Chartboost.onCreate(this);
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new SmoothCamera(0, 0, 720, 1280, 0, 0, 0);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		Chartboost.onPause(this);
		SceneManager.getInstance().getCurrentScene().handleOnPause();
		mEngine.getSoundManager().setMasterVolume(0);
		mEngine.getMusicManager().setMasterVolume(0);
	}
	
	@Override
	protected synchronized void onResume() {
		super.onResume();
		Chartboost.onResume(this);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
		if (soundEnabled == 1) {
			enableSound(false);
			enableMusic(false);
		} else if (soundEnabled == 0) {
			enableSound(true);
			enableMusic(true);
		}
	}
	
	public void enableSound(boolean enable) {
		if (enable) {
			mEngine.getSoundManager().setMasterVolume(1);
		} else {
			mEngine.getSoundManager().setMasterVolume(0);
		}
	}
	
	public void enableMusic(boolean enable) {
		if (enable) {
			mEngine.getMusicManager().setMasterVolume(1);
		} else {
			mEngine.getMusicManager().setMasterVolume(0);
		}
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws IOException {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)	throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);		
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(SPLASH_DURATION, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().loadGameScene(mEngine);
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Chartboost.onDestroy(this);
		mGoogleApiClient.disconnect();
		System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (Chartboost.onBackPressed()) {
				 return false;
			 } else {
				 SceneManager.getInstance().getCurrentScene().onBackKeyPressed(); 
			 }
		}
		return false;
	}

    @Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
		Chartboost.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
		Chartboost.onStop(this);
	}	
	
	public String getHighestScoreLeaderboardID() {
		return HIGHEST_SCORE_LEADERBOARD_ID;
	}

	public GoogleApiClient getGoogleApiClient() {
		return mGoogleApiClient;
	}
	
	public void displayLeaderboard() {
		startActivityForResult(Games.Leaderboards.getLeaderboardIntent(this.getGoogleApiClient(),
		        this.getHighestScoreLeaderboardID()), RC_LEADERBOARD);
	}

	@Override
	public void onSignInFailed() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(GameActivity.this, "There was an error. Please try to sign to Google Play Games again", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onSignInSucceeded() {
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (mResolvingConnectionFailure) {
	        // Already resolving
	        return;
	    }
		
		if (mAutoStartSignInFlow) {
	        mAutoStartSignInFlow = false;
	        mResolvingConnectionFailure = true;

	        // Attempt to resolve the connection failure using BaseGameUtils.
	        // The R.string.signin_other_error value should reference a generic
	        // error string in your strings.xml file, such as "There was
	        // an issue with sign in, please try again later."
	        if (!BaseGameUtils.resolveConnectionFailure(this,
	                mGoogleApiClient, connectionResult,
	                RC_SIGN_IN, SIGN_IN_OTHER_ERROR)) {
	            mResolvingConnectionFailure = false;
	        }
	    }

	}

	@Override
	public void onConnected(Bundle arg0) {
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		
	}
}
