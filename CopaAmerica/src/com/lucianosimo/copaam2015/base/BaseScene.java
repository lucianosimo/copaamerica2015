package com.lucianosimo.copaam2015.base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.lucianosimo.copaam2015.GameActivity;
import com.lucianosimo.copaam2015.manager.ResourcesManager;
import com.lucianosimo.copaam2015.manager.SceneManager.SceneType;

public abstract class BaseScene extends Scene{

	public Engine engine;
	public SmoothCamera camera;
	public GameActivity activity;
	public VertexBufferObjectManager vbom;
	public ResourcesManager resourcesManager;
	
	public BaseScene() {
		this.resourcesManager = ResourcesManager.getInstance();
		this.engine = resourcesManager.engine;
		this.camera = resourcesManager.camera;
		this.activity = resourcesManager.activity;
		this.vbom = resourcesManager.vbom;
		createScene();
	}
	
	public abstract void createScene();
	public abstract void onBackKeyPressed();
	public abstract SceneType getSceneType();
	public abstract void disposeScene();
	public abstract void handleOnPause();
}