package com.lucianosimo.copaam2015.scene;

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
import org.andengine.util.modifier.IModifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.google.android.gms.games.Games;
import com.lucianosimo.copaam2015.base.BaseScene;
import com.lucianosimo.copaam2015.manager.SceneManager.SceneType;
import com.lucianosimo.copaam2015.object.Ball;

public class GameScene extends BaseScene implements IOnSceneTouchListener{
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//HUD
	private Text timerText;
	private Text scoreText;
	private Text highScoreText;
	private Sprite gameClock;
	
	//Modifiers
	private DelayModifier goWindowModifier;
	private DelayModifier gameDurationModifier;
	private DelayModifier rushDurationModifier;
	private DelayModifier gameOverWindowModifier;
	private DelayModifier adsModifier;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	private float centerX;
	private float centerY;	
	
	//Parallax entity
	private AutoParallaxBackground background;
	
	//Balls
	private Ball[] balls;
	private Ball[] rushBalls;
	
	//Floor
	private Rectangle bottomLimit;
	
	//Booleans
	private boolean availablePause = false;
	private boolean onMainMenu = true;
	private boolean gameFinished;
	
	//Sprites
	private Sprite menuTapToStartWindow;
	private Sprite menuLeaderboardButton;
	private Sprite menuRateButton;
	private Sprite menuTwitterButton;
	private Sprite menuBestScore;
	
	//Windows
	private Sprite goWindow;
	private Sprite gamePauseWindow;
	private Sprite gameRushWindow;

	//Counters
	private int score;
	private int secondsElapsed;
	private int updates;
	private int highScore;

	//CONSTANTS
	private final static int GAME_DURATION = 45;
	private final static float GO_WINDOW_DURATION = 2f;
	private final static float RUSH_WINDOW_DURATION = 1.5f;
	private final static float GAME_OVER_WINDOW_DURATION = 5f;
	private final static float ADS_DELAY_DURATION = 4f;
	
	private final static int NUMBER_OF_BALLS = 10;
	private final static int NUMBER_OF_RUSH_BALLS = 6;
	
	private final static int TAP_FRAME_DURATION = 35;

	private final static int BOTTOM_LIMIT_Y = -75;
	
	private final static int GO_WINDOW_X = 360;
	private final static int GO_WINDOW_Y = 750;
	
	private final static int GAME_PAUSE_WINDOW_X = 360;
	private final static int GAME_PAUSE_WINDOW_Y = 640;
	
	private final static int RUSH_WINDOW_X = 360;
	private final static int RUSH_WINDOW_Y = 640;
	
	private final static int BALL_ORIGINAL_SCORE = 2;
	private final static int BALL_BRONZE_SCORE = 4;
	private final static int BALL_SILVER_SCORE = 6;
	private final static int BALL_GOLD_SCORE = 8;
	
	//HUD
	private final static int TIMER_TEXT_X = 130;
	private final static int TIMER_TEXT_Y = 1200;
	private final static int SCORE_TEXT_X = 525;
	private final static int SCORE_TEXT_Y = 1200;
	private final static int CLOCK_X = 55;
	private final static int CLOCK_Y = 1205;
	private final static int HIGH_SCORE_TEXT_X = 625;
	private final static int HIGH_SCORE_TEXT_Y = 1200;
	private final static int BEST_SCORE_X = 625;
	private final static int BEST_SCORE_Y = 1140;
	
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
	
	private final static int BALL_ORIGINAL_MIN_SPEED = 10;
	private final static int BALL_ORIGINAL_MAX_SPEED = 14;
	private final static int BALL_BRONZE_MIN_SPEED = 13;
	private final static int BALL_BRONZE_MAX_SPEED = 17;
	private final static int BALL_SILVER_MIN_SPEED = 15;
	private final static int BALL_SILVER_MAX_SPEED = 18;
	private final static int BALL_GOLD_MIN_SPEED = 18;
	private final static int BALL_GOLD_MAX_SPEED = 22;
	
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
		loadHighScore();
		createMenu();
		createWindows();
		createBottomLimit();
		availablePause = false;
		onMainMenu = true;
		//Games.Leaderboards.submitScore(activity.getGoogleApiClient(), activity.getHighestScoreLeaderboardID() , highScore);
		GameScene.this.setOnSceneTouchListener(this);
		//Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
		//checkSoundEnabledOrNo();
	}
	
	private void initializeVariables() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		centerX = screenWidth / 2;
		centerY = screenHeight / 2;
	}
	
	private void createHud() {
		gameHud = new HUD();
		
		timerText = new Text(TIMER_TEXT_X, TIMER_TEXT_Y, resourcesManager.timerFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText = new Text(SCORE_TEXT_X, SCORE_TEXT_Y, resourcesManager.scoreFont, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		highScoreText = new Text(HIGH_SCORE_TEXT_X, HIGH_SCORE_TEXT_Y, resourcesManager.highScoreFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		gameClock = new Sprite(CLOCK_X, CLOCK_Y, resourcesManager.game_clock_region, vbom);
		menuBestScore = new Sprite(BEST_SCORE_X, BEST_SCORE_Y, resourcesManager.menu_best_score_window_region, vbom);
		
		timerText.setText("" + GAME_DURATION);
		scoreText.setText("Score: " + score);
		
		timerText.setVisible(false);
		scoreText.setVisible(false);
		gameClock.setVisible(false);
		
		highScoreText.setVisible(true);
		menuBestScore.setVisible(true);
		
		gameHud.attachChild(timerText);
		gameHud.attachChild(scoreText);
		gameHud.attachChild(gameClock);
		
		gameHud.attachChild(highScoreText);
		gameHud.attachChild(menuBestScore);

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
				gameFinished = false;
				initGame();
				toggleHudElementsVisibility(true);
				toggleMenuButtonsVisibility(false);	
				createBalls();
				createRushBalls();
				setTouchAreaBindingOnActionDownEnabled(false);
				setTouchAreaBindingOnActionMoveEnabled(false);
				return true;
			}
		};
		menuLeaderboardButton = new Sprite(centerX + LEADERBOARD_BUTTON_OFFSET_X, centerY + LEADERBOARD_BUTTON_OFFSET_Y, resourcesManager.menu_button_leaderboard_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				resourcesManager.game_ball_tap.play();
				if (activity.getGoogleApiClient() != null && activity.getGoogleApiClient().isConnected()) {
					activity.displayLeaderboard();
				} else {
					activity.getGoogleApiClient().connect();
				}
				return true;
			}
		};
		menuRateButton = new Sprite(centerX + RATE_BUTTON_OFFSET_X, centerY + RATE_BUTTON_OFFSET_Y, resourcesManager.menu_button_rate_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				resourcesManager.game_ball_tap.play();
				rateUs();
				return true;
			}
		};
		menuTwitterButton = new Sprite(centerX, centerY + TWITTER_BUTTON_OFFSET_Y, resourcesManager.menu_button_tw_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				resourcesManager.game_ball_tap.play();
				tweetScore();
				return true;
			}
		};
		
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
		highScoreText.setVisible(display);
		menuBestScore.setVisible(display);
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
	
	private void initGame() {
		updates = 0;
		secondsElapsed = 0;
		score = 0;
		
		timerText.setText("" + GAME_DURATION);
		scoreText.setText("Score: " + score);
		
		goWindowModifier = new DelayModifier(GO_WINDOW_DURATION, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				goWindow.setVisible(true);
				availablePause = true;
				onMainMenu = false;
				resourcesManager.game_referee_whistle.play();
				Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
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
				resourcesManager.game_background_sound.play();
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
				availablePause = false;
				onMainMenu = true;
				resourcesManager.game_referee_whistle.play();
				GameScene.this.registerEntityModifier(adsModifier);				
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				for (int i = 0; i < NUMBER_OF_BALLS; i++) {
					GameScene.this.unregisterTouchArea(balls[i]);
				}
				for (int i = 0; i < NUMBER_OF_RUSH_BALLS; i++) {
					GameScene.this.unregisterTouchArea(rushBalls[i]);
				}
				if (score > highScore) {
					Games.Leaderboards.submitScore(activity.getGoogleApiClient(), activity.getHighestScoreLeaderboardID() , score);
				}
				saveHighScore(score);
				loadHighScore();
				gamePauseWindow.setVisible(false);
				toggleHudElementsVisibility(false);
				toggleMenuButtonsVisibility(true);
			}
		});
		rushDurationModifier = new DelayModifier(RUSH_WINDOW_DURATION, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				gameRushWindow.setVisible(true);
				setRushBallsSpeed();
				resourcesManager.game_rush_whistle.play();
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				gameRushWindow.setVisible(false);
			}
		});
		
		adsModifier = new DelayModifier(ADS_DELAY_DURATION, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
			}
		});
		
		GameScene.this.registerEntityModifier(goWindowModifier);
		goWindowModifier.setAutoUnregisterWhenFinished(true);
		gameOverWindowModifier.setAutoUnregisterWhenFinished(true);
		rushDurationModifier.setAutoUnregisterWhenFinished(true);
		gameDurationModifier.setAutoUnregisterWhenFinished(true);
		adsModifier.setAutoUnregisterWhenFinished(true);
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -1), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createWindows() {
		gamePauseWindow = new Sprite(GAME_PAUSE_WINDOW_X, GAME_PAUSE_WINDOW_Y, resourcesManager.game_pause_window_region, vbom);
		goWindow = new Sprite(GO_WINDOW_X, GO_WINDOW_Y, resourcesManager.game_go_window_region, vbom);
		gameRushWindow = new Sprite(RUSH_WINDOW_X, RUSH_WINDOW_Y, resourcesManager.game_rush_window_region, vbom);
		
		GameScene.this.attachChild(gamePauseWindow);
		GameScene.this.attachChild(goWindow);
		GameScene.this.attachChild(gameRushWindow);
		
		gamePauseWindow.setVisible(false);
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
							removeBalls(this);
						}
					}
					
				}
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						
						resourcesManager.game_ball_tap.play();
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
						
						createTapAnimation(this.getX(), this.getY());
						
						if (!gameFinished) {
							regenerateBall(this);
						} else {
							removeBalls(this);
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
	
	private void createRushBalls() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		
		ITextureRegion ballRegion;
		float ballX = 0;
		int initialY = 0;
		
		String userData;
		rushBalls = new Ball[NUMBER_OF_RUSH_BALLS];
		
		for (int i = 0; i < NUMBER_OF_RUSH_BALLS; i++) {
			//int ball = rand.nextInt(NUMBER_OF_BALLS) + 1;
			switch (i) {
			case 0:
			case 1:
				ballRegion = resourcesManager.game_ball_original_region;
				userData = "ballOriginal";
				initialY = BALL_ORIGINAL_INITIAL_Y;
				break;
			case 2:
			case 3:
				ballRegion = resourcesManager.game_ball_bronze_region;
				userData = "ballBronze";
				initialY = BALL_BRONZE_INITIAL_Y;
				break;
			case 4:
				ballRegion = resourcesManager.game_ball_silver_region;
				userData = "ballSilver";
				initialY = BALL_SILVER_INITIAL_Y;
				break;
			case 5:
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
			rushBalls[i] = new Ball(ballX, initialY, vbom, camera, physicsWorld, ballRegion) {
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
					}
					
				}
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						
						resourcesManager.game_ball_tap.play();
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
						
						createTapAnimation(this.getX(), this.getY());
						removeBalls(this);
						
						setTouchAreaBindingOnActionDownEnabled(false);
						setTouchAreaBindingOnActionMoveEnabled(false);
					}
					//true: touch listener stops processing the remaining touch events
					return true;
				}
			};
			rushBalls[i].setBallUserData(userData);
			rushBalls[i].setCullingEnabled(true);
			GameScene.this.registerTouchArea(rushBalls[i]);
			GameScene.this.attachChild(rushBalls[i]);
		}
	}
	
	private void regenerateBall(Ball ball) {
		Random rand = new Random();
		int ballX = rand.nextInt(BALL_MAX_X - BALL_MIN_X + 1) + BALL_MIN_X;
		ball.getBallBody().setTransform(ballX / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, BALL_REGENERATE_Y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ball.getBallBody().getAngle());
		ball.setPosition(ballX, BALL_REGENERATE_Y);
		setBallSpeed(ball);
	}
	
	private void removeBalls(Ball ball) {
		ball.getBallBody().setTransform(5000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 5000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ball.getBallBody().getAngle());
		ball.setPosition(5000, 5000);
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
	
	private void setBallSpeed(Ball ball) {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int speed = 0;
		String userData = ball.getUserData().toString();
		switch (userData) {
		case "ballOriginal":
			speed = rand.nextInt(BALL_ORIGINAL_MAX_SPEED - BALL_ORIGINAL_MIN_SPEED + 1) + BALL_ORIGINAL_MIN_SPEED;
			ball.setBallFallingSpeed(speed);
			break;
		case "ballBronze":
			speed = rand.nextInt(BALL_BRONZE_MAX_SPEED - BALL_BRONZE_MIN_SPEED + 1) + BALL_BRONZE_MIN_SPEED;
			ball.setBallFallingSpeed(speed);
			break;
		case "ballSilver":
			speed = rand.nextInt(BALL_SILVER_MAX_SPEED - BALL_SILVER_MIN_SPEED + 1) + BALL_SILVER_MIN_SPEED;
			ball.setBallFallingSpeed(speed);
			break;
		case "ballGold":
			speed = rand.nextInt(BALL_GOLD_MAX_SPEED - BALL_GOLD_MIN_SPEED + 1) + BALL_GOLD_MIN_SPEED;
			ball.setBallFallingSpeed(speed);
			break;
		default:
			speed = rand.nextInt(BALL_ORIGINAL_MAX_SPEED - BALL_ORIGINAL_MIN_SPEED + 1) + BALL_ORIGINAL_MIN_SPEED;
			ball.setBallFallingSpeed(speed);
			break;
		}
	}
	
	private void setBallsSpeed() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int speed = 0;
		for (int i = 0; i < NUMBER_OF_BALLS; i++) {
			String userData = balls[i].getUserData().toString();
			switch (userData) {
			case "ballOriginal":
				speed = rand.nextInt(BALL_ORIGINAL_MAX_SPEED - BALL_ORIGINAL_MIN_SPEED + 1) + BALL_ORIGINAL_MIN_SPEED;
				balls[i].setBallFallingSpeed(speed);
				break;
			case "ballBronze":
				speed = rand.nextInt(BALL_BRONZE_MAX_SPEED - BALL_BRONZE_MIN_SPEED + 1) + BALL_BRONZE_MIN_SPEED;
				balls[i].setBallFallingSpeed(speed);
				break;
			case "ballSilver":
				speed = rand.nextInt(BALL_SILVER_MAX_SPEED - BALL_SILVER_MIN_SPEED + 1) + BALL_SILVER_MIN_SPEED;
				balls[i].setBallFallingSpeed(speed);
				break;
			case "ballGold":
				speed = rand.nextInt(BALL_GOLD_MAX_SPEED - BALL_GOLD_MIN_SPEED + 1) + BALL_GOLD_MIN_SPEED;
				balls[i].setBallFallingSpeed(speed);
				break;
			default:
				speed = rand.nextInt(BALL_ORIGINAL_MAX_SPEED - BALL_ORIGINAL_MIN_SPEED + 1) + BALL_ORIGINAL_MIN_SPEED;
				balls[i].setBallFallingSpeed(speed);
				break;
			}
		}
	}
	
	private void setRushBallsSpeed() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int speed = 0;
		for (int i = 0; i < NUMBER_OF_RUSH_BALLS; i++) {
			String userData = rushBalls[i].getUserData().toString();
			switch (userData) {
			case "ballOriginal":
				speed = rand.nextInt(BALL_ORIGINAL_MAX_SPEED - BALL_ORIGINAL_MIN_SPEED + 1) + BALL_ORIGINAL_MIN_SPEED;
				rushBalls[i].setBallFallingSpeed(speed);
				break;
			case "ballBronze":
				speed = rand.nextInt(BALL_BRONZE_MAX_SPEED - BALL_BRONZE_MIN_SPEED + 1) + BALL_BRONZE_MIN_SPEED;
				rushBalls[i].setBallFallingSpeed(speed);
				break;
			case "ballSilver":
				speed = rand.nextInt(BALL_SILVER_MAX_SPEED - BALL_SILVER_MIN_SPEED + 1) + BALL_SILVER_MIN_SPEED;
				rushBalls[i].setBallFallingSpeed(speed);
				break;
			case "ballGold":
				speed = rand.nextInt(BALL_GOLD_MAX_SPEED - BALL_GOLD_MIN_SPEED + 1) + BALL_GOLD_MIN_SPEED;
				rushBalls[i].setBallFallingSpeed(speed);
				break;
			default:
				speed = rand.nextInt(BALL_ORIGINAL_MAX_SPEED - BALL_ORIGINAL_MIN_SPEED + 1) + BALL_ORIGINAL_MIN_SPEED;
				rushBalls[i].setBallFallingSpeed(speed);
				break;
			}
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

	@Override
	public void handleOnPause() {
		if (availablePause && !onMainMenu) {
			availablePause = false;
			gamePauseWindow.setVisible(true);
			GameScene.this.setIgnoreUpdate(true);
		} else if(!availablePause && !onMainMenu) {
			availablePause = true;
			gamePauseWindow.setVisible(false);
			GameScene.this.setIgnoreUpdate(false);	
		}
	}
	
	
	@Override
	public void onBackKeyPressed() {
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				if (availablePause && !onMainMenu) {
					availablePause = false;
					gamePauseWindow.setVisible(true);
					GameScene.this.setIgnoreUpdate(true);
				} else if(!availablePause && !onMainMenu) {
					availablePause = true;
					gamePauseWindow.setVisible(false);
					GameScene.this.setIgnoreUpdate(false);
				}
				if (onMainMenu) {
					System.exit(0);
				}
			}
		});
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return false;
	}
	
	private void tweetScore() {
		Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    shareIntent.setPackage("com.twitter.android");
	    shareIntent.putExtra(Intent.EXTRA_TEXT, "Acabo de lograr " + highScore + 
	    		" pts en Copa América 2015: Ball Rush! Bajalo! https://play.google.com/store/apps/details?id=com.lucianosimo.copaam2015 #CopaAmerica2015");
	    activity.startActivity(Intent.createChooser(shareIntent, "Copa America 2015"));
	}
	
	private void rateUs() {
		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.lucianosimo.copaam2015")));
	}
	
	private void saveHighScore(int localScore) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		if (sharedPreferences.getInt("highScore", 0) < localScore) {
			editor.putInt("highScore", localScore);
		}		
		editor.commit();
	}
	
	private void loadHighScore() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		highScore = sharedPreferences.getInt("highScore", 0);
		highScoreText.setText("" + highScore);
	}

}
