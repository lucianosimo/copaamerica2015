package com.lucianosimo.copaam2015.scene;

import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.lucianosimo.copaam2015.base.BaseScene;
import com.lucianosimo.copaam2015.manager.SceneManager.SceneType;
import com.lucianosimo.copaam2015.object.Ball;

public class GameScene extends BaseScene implements IOnSceneTouchListener{
	
	//Shared Preferences
	private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//HUD sprites
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	private float centerX;
	private float centerY;	
	
	//Parallax entity
	private AutoParallaxBackground background;
	
	//Modifiers
	
	//Balls
	private Ball[] balls;
	
	//Booleans
	private boolean availablePause;
	private boolean gameStarted;
	
	//Sprites
	private Sprite gameOverWindow;
	private Sprite resumeButton;
	private Sprite retryButton;
	private Sprite quitButton;
	
	private Sprite menuTapToStartWindow;
	private Sprite menuLeaderboardButton;
	private Sprite menuRateButton;
	private Sprite menuFacebookButton;
	private Sprite menuTwitterButton;

	//Counters

	//CONSTANTS
	
	private final static int NUMBER_OF_BALLS = 10;
	private final static int TAP_FRAME_DURATION = 35;
	
	//X OFFSETS
	private final static int TAP_WINDOW_OFFSET_X = 0;
	private final static int LEADERBOARD_BUTTON_OFFSET_X = 0;
	private final static int RATE_BUTTON_OFFSET_X = 0;
	private final static int FACEBOOK_BUTTON_OFFSET_X = -100;
	private final static int TWITTER_BUTTON_OFFSET_X = 100;
	
	//Y OFFSETS
	private final static int TAP_WINDOW_OFFSET_Y = 300;
	private final static int LEADERBOARD_BUTTON_OFFSET_Y = 25;
	private final static int RATE_BUTTON_OFFSET_Y = -125;
	private final static int FACEBOOK_BUTTON_OFFSET_Y = -500;
	private final static int TWITTER_BUTTON_OFFSET_Y = -500;
	
	//BALL VARIABLES
	private final static int BALL_MIN_X = 50;
	private final static int BALL_MAX_X = 670;
	private final static int BALL_MIN_SPEED = 5;
	private final static int BALL_MAX_SPEED = 15;
	private final static int BALL_INITIAL_Y = 1400;
		
	//If negative, never collides between groups, if positive yes
	//private static final int GROUP_ENEMY = -1;

	@Override
	public void createScene() {
		initializeVariables();
		//createHud();
		createBackground();
		createPhysics();
		//createWindows();
		createBalls();
		createMenu();
		GameScene.this.setOnSceneTouchListener(this);
		//Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
		//checkSoundEnabledOrNo();
	}
	
	/*private void checkSoundEnabledOrNo() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
		int soundEnabled = sp.getInt("soundEnabled", 0);
		if (soundEnabled == 1) {
			activity.enableSound(false);
			activity.enableMusic(false);
		} else if (soundEnabled == 0) {
			activity.enableSound(true);
			activity.enableMusic(true);
		}
	}*/
	
	private void initializeVariables() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		centerX = screenWidth / 2;
		centerY = screenHeight / 2;
		availablePause = true;
		gameStarted = false;
	}
	
	private void createHud() {
		gameHud = new HUD();

		camera.setHUD(gameHud);
	}
	
	private void createBackground() {
		background = new AutoParallaxBackground(0, 0, 0, 0);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(centerX, centerY, resourcesManager.game_background_region, vbom)));
		this.setBackground(background);
	}
	
	private void createMenu() {
		menuTapToStartWindow = new Sprite(centerX + TAP_WINDOW_OFFSET_X, centerY + TAP_WINDOW_OFFSET_Y, resourcesManager.menu_tap_window_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				gameStarted = true;
				GameScene.this.unregisterTouchArea(menuTapToStartWindow);
				menuTapToStartWindow.setVisible(false);
				menuLeaderboardButton.setVisible(false);
				menuRateButton.setVisible(false);
				menuFacebookButton.setVisible(false);
				menuTwitterButton.setVisible(false);
				setBallsSpeed();
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		menuLeaderboardButton = new Sprite(centerX + LEADERBOARD_BUTTON_OFFSET_X, centerY + LEADERBOARD_BUTTON_OFFSET_Y, resourcesManager.menu_button_leaderboard_region, vbom);
		menuRateButton = new Sprite(centerX + RATE_BUTTON_OFFSET_X, centerY + RATE_BUTTON_OFFSET_Y, resourcesManager.menu_button_rate_region, vbom);
		menuFacebookButton = new Sprite(centerX + FACEBOOK_BUTTON_OFFSET_X, centerY + FACEBOOK_BUTTON_OFFSET_Y, resourcesManager.menu_button_fb_region, vbom);
		menuTwitterButton = new Sprite(centerX + TWITTER_BUTTON_OFFSET_X, centerY + TWITTER_BUTTON_OFFSET_Y, resourcesManager.menu_button_tw_region, vbom);
		
		GameScene.this.registerTouchArea(menuTapToStartWindow);
		
		GameScene.this.attachChild(menuTapToStartWindow);
		GameScene.this.attachChild(menuLeaderboardButton);
		GameScene.this.attachChild(menuRateButton);
		GameScene.this.attachChild(menuFacebookButton);
		GameScene.this.attachChild(menuTwitterButton);
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -1), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createWindows() {
		gameOverWindow = new Sprite(10000, 10000, resourcesManager.game_over_window_region, vbom);
		GameScene.this.attachChild(gameOverWindow);
	}
	
	private void createBalls() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		
		ITextureRegion ballRegion;
		float ballX = 0;
		String userData;
		balls = new Ball[NUMBER_OF_BALLS];
		
		for (int i = 0; i < NUMBER_OF_BALLS; i++) {
			int ball = rand.nextInt(NUMBER_OF_BALLS) + 1;
			switch (ball) {
			case 1:
			case 2:
			case 3:
			case 4:
				ballRegion = resourcesManager.game_ball_original_region;
				userData = "ballOriginal";
				break;
			case 5:
			case 6:
			case 7:
				ballRegion = resourcesManager.game_ball_bronze_region;
				userData = "ballBronze";
				break;
			case 8:
			case 9:
				ballRegion = resourcesManager.game_ball_silver_region;
				userData = "ballSilver";
				break;
			case 10:
				ballRegion = resourcesManager.game_ball_gold_region;
				userData = "ballGold";
				break;
			default:
				ballRegion = resourcesManager.game_ball_original_region;
				userData = "ballOriginal";
				break;
			}
			
			//(max - min + 1) + min
			ballX = rand.nextInt(BALL_MAX_X - BALL_MIN_X + 1) + BALL_MIN_X;
			balls[i] = new Ball(ballX, BALL_INITIAL_Y, vbom, camera, physicsWorld, ballRegion) {
				@Override
				protected void onManagedUpdate(float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if (this.getY() < -75) {
						this.getBallBody().setTransform(this.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, BALL_INITIAL_Y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, this.getBallBody().getAngle());
						this.setPosition(this.getX(), BALL_INITIAL_Y);
					}
				}
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						createTapAnimation(this.getX(), this.getY());					
						this.getBallBody().setTransform(this.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, BALL_INITIAL_Y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, this.getBallBody().getAngle());
						this.setPosition(this.getX(), BALL_INITIAL_Y);
						setTouchAreaBindingOnActionDownEnabled(false);
						setTouchAreaBindingOnActionMoveEnabled(false);
					}
					//true: touch listener stops processing the remaining touch events
					return true;
				}
			};
			balls[i].setBallUserData(userData);
			balls[i].setCullingEnabled(true);
			GameScene.this.registerTouchArea(balls[i]);
			GameScene.this.attachChild(balls[i]);
		}
	}
	
	private void createTapAnimation(float x, float y) {
		final AnimatedSprite tapAnimation = new AnimatedSprite(x, y, resourcesManager.game_tap_animation_region, vbom);
		final long[] TAP_ANIMATE = new long[] {TAP_FRAME_DURATION, TAP_FRAME_DURATION, TAP_FRAME_DURATION, TAP_FRAME_DURATION, TAP_FRAME_DURATION};
		tapAnimation.animate(TAP_ANIMATE, 0, 4, false);
		tapAnimation.registerUpdateHandler(new IUpdateHandler() {
			final AnimatedSprite tapRef = tapAnimation;
			
			@Override
			public void reset() {
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				final IUpdateHandler upd = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (!tapRef.isAnimationRunning()) {
							tapRef.setVisible(false);
							tapRef.unregisterUpdateHandler(upd);
							tapRef.setIgnoreUpdate(true);
						}						
					}
				});
				
			}
		});
		GameScene.this.attachChild(tapAnimation);
		tapAnimation.setCullingEnabled(true);
	}
	
	private void setBallsSpeed() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int speed = 0;
		for (int i = 0; i < NUMBER_OF_BALLS; i++) {
			speed = rand.nextInt(BALL_MAX_SPEED - BALL_MIN_SPEED + 1) + BALL_MIN_X;
			balls[i].setBallFallingSpeed(speed);
		}
		
	}
	
	
	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
	
			}
			
			@Override
			public void endContact(Contact contact) {
				
			}
			
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("leftWall")) {
					
				}
				
			}
		};
		return contactListener;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		
	}
	
	private void myGarbageCollection() {
		Iterator<Body> allMyBodies = physicsWorld.getBodies();
        while(allMyBodies.hasNext()) {
        	try {
        		final Body myCurrentBody = allMyBodies.next();
                	physicsWorld.destroyBody(myCurrentBody);                
            } catch (Exception e) {
            	Debug.e(e);
            }
        }
               
        this.clearChildScene();
        this.detachChildren();
        this.reset();
        this.detachSelf();
        physicsWorld.clearForces();
        physicsWorld.clearPhysicsConnectors();
        physicsWorld.reset();
 
        System.gc();
	}
	
	@Override
	public void handleOnPause() {

	}
	
	
	@Override
	public void onBackKeyPressed() {
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				
			}
		});
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return false;
	}
	
	/*private void saveCoins(String key, int coins) {
		Editor editor = sharedPreferences.edit();
		int coinsCounter = sharedPreferences.getInt(key, 0);
		coinsCounter += coins;
		editor.putInt(key, coinsCounter);
		editor.commit();
	}*/

}
