package net.logosstudios.ludumdare27;

import aurelienribon.bodyeditor.BodyEditorLoader;
import box2dLight.Light;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class LudumDare27 extends Game {
	public static  int WIDTH, HEIGHT;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	public static BitmapFont terminalFont, terminalFontSmall;
	public static TextureAtlas textureAtlas;
	public static Sprite shipSprite, earthSprite, bigBullet, normalBullet, background, flameThrower, lightning;
	public static Sprite[] defenderBar, earthBar, machineGun, enemyColors, introScreen;
	public static BodyEditorLoader shipLoader, enemyLoader;
	public static ParticleEffect shipDeath, enemyDeath, taserEffect, flameEffect;
	public static Sound ufoDeath, pcDeath, gameOver, gameStart, machineGunShoot, shoot, powerUp, flameThrowerShoot, flameThrowerLoop, taserShoot, music;
	
	@Override
	public void create() {		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, (float)HEIGHT/WIDTH);
		camera.setToOrtho(false, WIDTH, HEIGHT);
		batch = new SpriteBatch();
		
		terminalFont = new BitmapFont(Gdx.files.internal("data/terminal.fnt"), Gdx.files.internal("data/terminal.png"), false);
		terminalFontSmall = new BitmapFont(Gdx.files.internal("data/terminalSmall.fnt"), Gdx.files.internal("data/terminalSmall.png"), false);
		textureAtlas = new TextureAtlas(Gdx.files.internal("data/textures.txt"));
		shipSprite = textureAtlas.createSprite("ship");
		earthSprite = textureAtlas.createSprite("earth");
		bigBullet = textureAtlas.createSprite("bigBullet");
		normalBullet = textureAtlas.createSprite("normalBullet");
		background = textureAtlas.createSprite("spaceBG");
		flameThrower = textureAtlas.createSprite("flamethrower");
		lightning = textureAtlas.createSprite("lightning");
		shipLoader = new BodyEditorLoader(Gdx.files.internal("data/shipBodyEdit.json"));
		enemyLoader = new BodyEditorLoader(Gdx.files.internal("data/enemyBodyEdit.json"));
		introScreen = new Sprite[]{
				textureAtlas.createSprite("introScreen1"), 
				textureAtlas.createSprite("introScreen2")};
		machineGun = new Sprite[]{
				textureAtlas.createSprite("machineGunIdle"),
				textureAtlas.createSprite("machineGun1"),
				textureAtlas.createSprite("machineGun2")
		};
		defenderBar = new Sprite[]{
				textureAtlas.createSprite("DefenderBar0"),
				textureAtlas.createSprite("DefenderBar1"),
				textureAtlas.createSprite("DefenderBar2"),
				textureAtlas.createSprite("DefenderBar3"),
				textureAtlas.createSprite("DefenderBar4"),
				textureAtlas.createSprite("DefenderBar5"),
				textureAtlas.createSprite("DefenderBar6")
		};
		earthBar = new Sprite[]{
				textureAtlas.createSprite("EarthBar0"),
				textureAtlas.createSprite("EarthBar1"),
				textureAtlas.createSprite("EarthBar2"),
				textureAtlas.createSprite("EarthBar3"),
				textureAtlas.createSprite("EarthBar4"),
				textureAtlas.createSprite("EarthBar5"),
				textureAtlas.createSprite("EarthBar6")
		};
		enemyColors = new Sprite[]{
				textureAtlas.createSprite("enemyRed"),
				textureAtlas.createSprite("enemyYellow"),
				textureAtlas.createSprite("enemyGreen")
		};
		shipDeath = new ParticleEffect();
		shipDeath.load(Gdx.files.internal("data/PCdeath.txt"), textureAtlas);
		enemyDeath = new ParticleEffect();
		enemyDeath.load(Gdx.files.internal("data/UFOdeath.txt"), textureAtlas);
		flameEffect = new ParticleEffect();
		flameEffect.load(Gdx.files.internal("data/flamethrower.txt"), textureAtlas);
		taserEffect = new ParticleEffect();
		taserEffect.load(Gdx.files.internal("data/taser.txt"), textureAtlas);
		
		ufoDeath = Gdx.audio.newSound(Gdx.files.internal("data/UFOdeath.wav"));
		pcDeath = Gdx.audio.newSound(Gdx.files.internal("data/PCearthDeath.wav"));
		gameOver = Gdx.audio.newSound(Gdx.files.internal("data/gameOver.wav"));
		gameStart = Gdx.audio.newSound(Gdx.files.internal("data/gameStart.wav"));
		machineGunShoot =  Gdx.audio.newSound(Gdx.files.internal("data/machineGunShoot.wav"));
		shoot = Gdx.audio.newSound(Gdx.files.internal("data/shoot.wav"));
		powerUp = Gdx.audio.newSound(Gdx.files.internal("data/powerUp.wav"));
		flameThrowerShoot = Gdx.audio.newSound(Gdx.files.internal("data/flamethrowerSound.wav"));
		taserShoot = Gdx.audio.newSound(Gdx.files.internal("data/taserShoot.wav"));
		music = Gdx.audio.newSound(Gdx.files.internal("data/bu-a-dogs-boat.ogg"));
		//flameThrowerLoop = Gdx.audio.newSound(Gdx.files.internal("data/flamethrowerLoop.wav"));
		
		this.setScreen(new IntroScreen(this));
	}

	@Override
	public void dispose() {
		batch.dispose();
		terminalFont.dispose();
		terminalFontSmall.dispose();
		textureAtlas.dispose();
		shipDeath.dispose();
		enemyDeath.dispose();
		//TODO taserEffect.dispose();
		flameEffect.dispose();
		ufoDeath.dispose();
		pcDeath.dispose();
		gameOver.dispose();
		gameStart.dispose();
		machineGunShoot.dispose();
		shoot.dispose();
		powerUp.dispose();
		flameThrowerShoot.dispose();
	}

	@Override
	public void render() {		
		super.render();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void setBatch(SpriteBatch batch) {
		this.batch = batch;
	}
}
