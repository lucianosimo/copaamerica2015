package com.lucianosimo.copaam2015.manager;

import java.util.Random;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import com.lucianosimo.copaam2015.GameActivity;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public SmoothCamera camera;
	public GameActivity activity;
	public VertexBufferObjectManager vbom;
	
	//Splash items
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	//Game audio
	
	//Game fonts
	public Font scoreFont;
	public Font timerFont;
	
	//Game HUD
	
	//Animated
	public ITiledTextureRegion game_tap_animation_region;
	
	//Balls
	public ITextureRegion game_ball_original_region;
	public ITextureRegion game_ball_bronze_region;
	public ITextureRegion game_ball_silver_region;
	public ITextureRegion game_ball_gold_region;
	
	//Platforms
	
	//Backgrounds
	public ITextureRegion game_background_region;

	//Windows
	public ITextureRegion menu_tap_window_region;
	public ITextureRegion menu_best_score_window_region;
	public ITextureRegion game_go_window_region;
	public ITextureRegion game_high_score_window_region;
	public ITextureRegion game_rush_window_region;
	public ITextureRegion game_over_window_region;

	//Buttons
	public ITextureRegion menu_button_tw_region;
	public ITextureRegion menu_button_rate_region;
	public ITextureRegion menu_button_leaderboard_region;
	
	//Score tiles;
	
	//Game Textures
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private BuildableBitmapTextureAtlas gameBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas gameAnimatedTextureAtlas;
	
	//Splash Methods
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	//Game Methods
	public void loadGameResources() {
		loadGameGraphics();
		loadGameAudio();
		loadGameFonts();
	}
	
	public void unloadGameResources() {
		unloadGameTextures();
		unloadGameFonts();	
		unloadGameAudio();
	}
	
	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		gameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		gameAnimatedTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 800, 160, TextureOptions.BILINEAR);
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		
		//BALLS
		game_ball_original_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ball_original.png");
		game_ball_bronze_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ball_bronze.png");
		game_ball_silver_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ball_silver.png");
		game_ball_gold_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ball_gold.png");
		
		//ANIMATED
		game_tap_animation_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_tap_animation.png", 5, 1);
		
		//WINDOWS
		menu_tap_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_tap_window.png");
		menu_best_score_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_best_score_window.png");
		game_go_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_go_window.png");
		game_high_score_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_high_score_window.png");
		game_rush_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_rush_window.png");
		game_over_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_over_window.png");
		
		//BUTTONS
		menu_button_tw_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_button_tw.png");
		menu_button_rate_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_button_rate.png");
		menu_button_leaderboard_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_button_leaderboard.png");
		
		//BACKGROUNDS
		Random rand = new Random();
		int back = rand.nextInt(3) + 1;
		switch (back) {
		case 1:
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_1.png");
			break;
		case 2:
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_2.png");
			break;
		case 3:
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_3.png");
			break;
		default:
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_1.png");
			break;
		}
		

		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameAnimatedTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
			this.gameTextureAtlas.load();
			this.gameBackgroundTextureAtlas.load();
			this.gameAnimatedTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadGameAudio() {
		MusicFactory.setAssetBasePath("sound/game/");
		SoundFactory.setAssetBasePath("sound/game/");
		/*try {

		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public void unloadGameAudio() {
		System.gc();
	}
	
	private void loadGameFonts() {
		FontFactory.setAssetBasePath("fonts/game/");
		final ITexture scoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture timerTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		scoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), scoreTexture, activity.getAssets(), "myriad.ttf", 60, true, Color.WHITE_ARGB_PACKED_INT, 2f, Color.BLACK_ARGB_PACKED_INT);
		timerFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), timerTexture, activity.getAssets(), "myriad.ttf", 60, true, Color.WHITE_ARGB_PACKED_INT, 2f, Color.BLACK_ARGB_PACKED_INT);
		scoreFont.load();
		timerFont.load();
	}
	
	private void unloadGameTextures() {
		this.menuTextureAtlas.unload();
		this.gameTextureAtlas.unload();
		this.gameBackgroundTextureAtlas.unload();
		this.gameAnimatedTextureAtlas.unload();
	}
	
	private void unloadGameFonts() {
		
	}
	
	
	//Manager Methods
	public static void prepareManager(Engine engine, GameActivity activity, SmoothCamera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;		
	}
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}

}


