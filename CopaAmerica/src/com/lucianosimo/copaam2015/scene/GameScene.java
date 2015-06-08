package com.lucianosimo.copaam2015.scene;

import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

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
	
	//HUD
	private Text timerText;
	private Text scoreText;
	private Sprite gameClock;
	
	//Modifiers
	private DelayModifier goWindowModifier;
	private DelayModifier gameDurationModifier;
	private DelayModifier rushDurationModifier;
	private DelayModifier gameOverWindowModifier;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	private float centerX;
	private float centerY;	
	
	//Parallax entity
	private AutoParallaxBackground background;
	
	//Balls
	private Ball[] balls;
	
	//Floor
	private Rectangle bottomLimit;
	
	//Booleans
	private boolean availablePause;
	private boolean gameStarted;
	private boolean gameFinished;
	
	//Sprites
	private Sprite menuTapToStartWindow;
	private Sprite menuLeaderboardButton;
	private Sprite menuRateButton;
	private Sprite menuTwitterButton;
	
	//Windows
	private Sprite goWindow;
	private Sprite gameOverWindow;
	private Sprite gameRushWindow;

	//Counters
	private int score;
	private int secondsElapsed;
	private int updates;

	//CONSTANTS
	private final static int GAME_DURATION = 45;
	private final static float GO_WINDOW_DURATION = 2f;
	private final static float RUSH_WINDOW_DURATION = 1.5f;
	private final static float GAME_OVER_WINDOW_DURATION = 7f;
	
	private final static int NUMBER_OF_BALLS = 10;
	private final static int TAP_FRAME_DURATION = 35;

	private final static int BOTTOM_LIMIT_Y = -75;
	
	private final static int GO_WINDOW_X = 360;
	private final static int GO_WINDOW_Y = 750;
	
	private final static int GAME_OVER_WINDOW_X = 360;
	private final static int GAME_OVER_WINDOW_Y = 640;
	
	private final static int RUSH_WINDOW_X = 360;
	private final static int RUSH_WINDOW_Y = 640;
	
	private final static int BALL_ORIGINAL_SCORE = 2;
	private final static int BALL_BRONZE_SCORE = 4;
	private final static int BALL_SILVER_SCORE = 6;
	private final static int BALL_GOLD_SCORE = 8;
	
	//HUD
	private final static int TIMER_TEXT_X = 125;
	private final static int TIMER_TEXT_Y = 1200;
	private final static int SCORE_TEXT_X = 525;
	private final static int SCORE_TEXT_Y = 1200;
	private final static int CLOCK_X = 55;
	private final static int CLOCK_Y = 1205;
	
	//X OFFSETS
	private final static int TAP_WINDOW_OFFSET_X = 0;
	private final static int LEADERBOARD_BUTTON_OFFSET_X = 0;
	private final static int RATE_BUTTON_OFFSET_X = 0;
	
	//Y OFFSETS
	private final static int TAP_WINDOW_OFFSET_Y = 300;
	private final static int LEADERBOARD_BUTTON_OFFSET_Y = 25;
	private final static int RATE_BUTTON_OFFSET_Y = -125;
	private final static int TWITTER_BUTTON_OFFSET_Y = -500;
	
	//BALL VARIABLES
	private final static int BALL_MIN_X = 50;
	private final static int BALL_MAX_X = 670;
	private final static int BALL_MIN_SPEED = 5;
	private final static int BALL_MAX_SPEED = 15;
	private final static int BALL_REGENERATE_Y = 2000;
	private final static int BALL_ORIGINAL_INITIAL_Y = 1400;
	private final static int BALL_BRONZE_INITIAL_Y = 1700;
	private final static int BALL_SILVER_INITIAL_Y = 2000;
	private final static int BALL_GOLD_INITIAL_Y = 2300;
		
	//If negative, never collides between groups, if positive yes
	//private static final int GROUP_ENEMY = -1;

	@Override
	public void createScene() {
		initializeVariables();
		createHud();
		createBackground();
		createPhysics();
		createWindows();
		createMenu();
		createBottomLimit();
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
		
		timerText = new Text(TIMER_TEXT_X, TIMER_TEXT_Y, resourcesManager.timerFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText = new Text(SCORE_TEXT_X, SCORE_TEXT_Y, resourcesManager.scoreFont, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		gameClock = new Sprite(CLOCK_X, CLOCK_Y, resourcesManager.game_clock_region, vbom);
		
		timerText.setText("" + GAME_DURATION);
		scoreText.setText("Score: " + score);
		
		timerText.setVisible(false);
		scoreText.setVisible(false);
		gameClock.setVisible(false);
		
		gameHud.attachChild(timerText);
		gameHud.attachChild(scoreText);
		gameHud.attachChild(gameClock);

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
				gameFinished = false;
				initTimer();
				toggleHudElementsVisibility(true);
				toggleMenuButtonsVisibility(false);	
				createBalls();
				setTouchAreaBindingOnActionDownEnabled(false);
				setTouchAreaBindingOnActionMoveEnabled(false);
				return true;
			}
		};
		menuLeaderboardButton = new Sprite(centerX + LEADERBOARD_BUTTON_OFFSET_X, centerY + LEADERBOARD_BUTTON_OFFSET_Y, resourcesManager.menu_button_leaderboard_region, vbom);
		menuRateButton = new Sprite(centerX + RATE_BUTTON_OFFSET_X, centerY + RATE_BUTTON_OFFSET_Y, resourcesManager.menu_button_rate_region, vbom);
		menuTwitterButton = new Sprite(centerX, centerY + TWITTER_BUTTON_OFFSET_Y, resourcesManager.menu_button_tw_region, vbom);
		
		GameScene.this.registerTouchArea(menuTapToStartWindow);
		GameScene.this.registerTouchArea(menuLeaderboardButton);
		GameScene.this.registerTouchArea(menuRateButton);
		GameScene.this.registerTouchArea(menuTwitterButton);
		
		GameScene.this.attachChild(menuTapToStartWindow);
		GameScene.this.attachChild(menuLeaderboardButton);
		GameScene.this.attachChild(menuRateButton);
		GameScene.this.attachChild(menuTwitterButton);
	}
	
	private void toggleHudElementsVisibility(boolean display) {
		timerText.setVisible(display);
		scoreText.setVisible(display);
		gameClock.setVisible(display);
	}
	
	private void toggleMenuButtonsVisibility(boolean display) {
		menuTapToStartWindow.setVisible(display);
		menuLeaderboardButton.setVisible(display);
		menuRateButton.setVisible(display);
		menuTwitterButton.setVisible(display);
		if (display) {
			GameScene.this.registerTouchArea(menuTapToStartWindow);
			GameScene.this.registerTouchArea(menuLeaderboardButton);
			GameScene.this.registerTouchArea(menuRateButton);
			GameScene.this.registerTouchArea(menuTwitterButton);
		} else {
			GameScene.this.unregisterTouchArea(menuTapToStartWindow);
			GameScene.this.unregisterTouchArea(menuLeaderboardButton);
			GameScene.this.unregisterTouchArea(menuRateButton);
			GameScene.this.unregisterTouchArea(menuTwitterButton);
		}
	}
	
	private void createBottomLimit() {
		bottomLimit = new Rectangle(screenWidth/2, BOTTOM_LIMIT_Y, screenWidth, 1f, vbom);

		bottomLimit.setColor(Color.RED);

		GameScene.this.attachChild(bottomLimit);
	}
	
	private void initTimer() {
		updates = 0;
		secondsElapsed = 0;
		timerText.setText("" + GAME_DURATION);
		
		goWindowModifier = new DelayModifier(GO_WINDOW_DURATION, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				goWindow.setVisible(true);				
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				goWindow.setVisible(false);
				setBallsSpeed();
				GameScene.this.registerEntityModifier(gameDurationModifier);
			}
		});
		gameDurationModifier = new DelayModifier(GAME_DURATION, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				engine.registerUpdateHandler(new IUpdateHandler() {
					
					@Override
					public void reset() {
						
					}
					
					@Override
					//pSecondsElapsed = 1/60 secs
					public void onUpdate(float pSecondsElapsed) {
						updates++;
						if ((updates % 60) == 0) {
							if (secondsElapsed < 45) {
								secondsElapsed++;
								timerText.setText("" + (int)(GAME_DURATION - secondsElapsed));
							}
							if (secondsElapsed == 20) {
								GameScene.this.registerEntityModifier(rushDurationModifier);
							}
							if (secondsElapsed >= 45) {
								engine.unregisterUpdateHandler(this);
							}
						}
					}
				});
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				gameFinished = true;
				GameScene.this.registerEntityModifier(gameOverWindowModifier);
			}
		});
		gameOverWindowModifier = new DelayModifier(GAME_OVER_WINDOW_DURATION, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				gameOverWindow.setVisible(true);
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				for (int i = 0; i < NUMBER_OF_BALLS; i++) {
					GameScene.this.unregisterTouchArea(balls[i]);
				}
				gameOverWindow.setVisible(false);
				toggleHudElementsVisibility(false);
				toggleMenuButtonsVisibility(true);
			}
		});
		rushDurationModifier = new DelayModifier(RUSH_WINDOW_DURATION, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				gameRushWindow.setVisible(true);
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				gameRushWindow.setVisible(false);
			}
		});
		
		GameScene.this.registerEntityModifier(goWindowModifier);
		goWindowModifier.setAutoUnregisterWhenFinished(true);
		gameOverWindowModifier.setAutoUnregisterWhenFinished(true);
		rushDurationModifier.setAutoUnregisterWhenFinished(true);
		gameDurationModifier.setAutoUnregisterWhenFinished(true);
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -1), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createWindows() {
		gameOverWindow = new Sprite(GAME_OVER_WINDOW_X, GAME_OVER_WINDOW_Y, resourcesManager.game_over_window_region, vbom);
		goWindow = new Sprite(GO_WINDOW_X, GO_WINDOW_Y, resourcesManager.game_go_window_region, vbom);
		gameRushWindow = new Sprite(RUSH_WINDOW_X, RUSH_WINDOW_Y, resourcesManager.game_rush_window_region, vbom);
		
		GameScene.this.attachChild(gameOverWindow);
		GameScene.this.attachChild(goWindow);
		GameScene.this.attachChild(gameRushWindow);
		
		gameOverWindow.setVisible(false);
		goWindow.setVisible(false);
		gameRushWindow.setVisible(false);
	}
	
	private void createBalls() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		
		ITextureRegion ballRegion;
		float ballX = 0;
		int initialY = 0;
		
		String userData;
		balls = new Ball[NUMBER_OF_BALLS];
		
		for (int i = 0; i < NUMBER_OF_BALLS; i++) {
			//int ball = rand.nextInt(NUMBER_OF_BALLS) + 1;
			switch (i) {
			case 0:
			case 1:
			case 2:
			case 3:
				ballRegion = resourcesManager.game_ball_original_region;
				userData = "ballOriginal";
				initialY = BALL_ORIGINAL_INITIAL_Y;
				break;
			case 4:
			case 5:
			case 6:
				ballRegion = resourcesManager.game_ball_bronze_region;
				userData = "ballBronze";
				initialY = BALL_BRONZE_INITIAL_Y;
				break;
			case 7:
			case 8:
				ballRegion = resourcesManager.game_ball_silver_region;
				userData = "ballSilver";
				initialY = BALL_SILVER_INITIAL_Y;
				break;
			case 9:
				ballRegion = resourcesManager.game_ball_gold_region;
				userData = "ballGold";
				initialY = BALL_GOLD_INITIAL_Y;
				break;
			default:
				ballRegion = resourcesManager.game_ball_original_region;
				userData = "ballOriginal";
				initialY = BALL_ORIGINAL_INITIAL_Y;
				break;
			}
			
			//(max - min + 1) + min
			ballX = rand.nextInt(BALL_MAX_X - BALL_MIN_X + 1) + BALL_MIN_X;
			balls[i] = new Ball(ballX, initialY + i * 100, vbom, camera, physicsWorld, ballRegion) {
				@Override
				protected void onManagedUpdate(float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					
					if (this.collidesWith(bottomLimit)) {
						String userData = this.getUserData().toString();
						switch (userData) {
						case "ballOriginal":
							reduceScore(BALL_ORIGINAL_SCORE/2);
							break;
						case "ballBronze":
							reduceScore(BALL_BRONZE_SCORE/2);
							break;
						case "ballSilver":
							reduceScore(BALL_SILVER_SCORE/2);
							break;
						case "ballGold":
							reduceScore(BALL_GOLD_SCORE/2);
							break;
						default:
							reduceScore(BALL_ORIGINAL_SCORE/2);
							break;
						}
						if (!gameFinished) {
							regenerateBall(this);
						}
						if (gameFinished && this.collidesWith(bottomLimit)) {
							this.getBallBody().setTransform(5000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 5000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, this.getBallBody().getAngle());
							this.setPosition(5000, 5000);
						}
					}
					
				}
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						
						String userData = this.getUserData().toString();
						switch (userData) {
						case "ballOriginal":
							addScore(BALL_ORIGINAL_SCORE);
							break;
						case "ballBronze":
							addScore(BALL_BRONZE_SCORE);
							break;
						case "ballSilver":
							addScore(BALL_SILVER_SCORE);
							break;
						case "ballGold":
							addScore(BALL_GOLD_SCORE);
							break;
						default:
							addScore(BALL_ORIGINAL_SCORE);
							break;
						}
						
						if (!gameFinished) {
							createTapAnimation(this.getX(), this.getY());
							regenerateBall(this);
						}						
						
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
	
	private void regenerateBall(Ball ball) {
		ball.getBallBody().setTransform(ball.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, BALL_REGENERATE_Y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ball.getBallBody().getAngle());
		ball.setPosition(ball.getX(), BALL_REGENERATE_Y);
	}
	
	private void addScore(int localScore) {
		score += localScore;
		scoreText.setText("Score: " + score);
	}
	
	private void reduceScore(int localScore) {
		if (score > 0 && score > localScore) {
			score -= localScore;
			scoreText.setText("Score: " + score);
		}
		if (score > 0 && score <= localScore) {
			score = 0;
			scoreText.setText("Score: " + score);
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
