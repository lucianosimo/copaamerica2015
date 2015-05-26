package com.lucianosimo.copaam2015.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.lucianosimo.copaam2015.base.BaseScene;
import com.lucianosimo.copaam2015.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene{

	private Sprite splash;
	private float screenWidth;
	private float screenHeight;	
	
	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();	
		
		//incrementPlayedGames();
		
		setBackground(new Background(Color.WHITE));
		
		splash = new Sprite(0, 0, resourcesManager.splash_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		splash.setPosition(screenWidth/2, screenHeight/2);		
		
		attachChild(splash);
	}

	@Override
	public void onBackKeyPressed() {
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public void handleOnPause() {
		
	}
	
	private void incrementPlayedGames() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int played = sharedPreferences.getInt("played", 0);
		Editor editor = sharedPreferences.edit();
		played++;
		editor.putInt("played", played);
		editor.commit();
	}

}