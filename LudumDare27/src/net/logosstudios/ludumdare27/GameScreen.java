package net.logosstudios.ludumdare27;

import java.util.ArrayList;
import java.util.Iterator;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.semperhilaris.smoothcam.SmoothCamPoint;
import com.semperhilaris.smoothcam.SmoothCamSubject;
import com.semperhilaris.smoothcam.SmoothCamWorld;

public class GameScreen implements Screen, ContactListener{
	public static final int BOX2D_TO_SCREEN = 10;
	private LudumDare27 game;
	private SpriteBatch batch, hudBatch;
	private Camera camera;
	private BitmapFont font, fontSmall;
	private World world;
	private Ship ship;
	private Earth earth;
	private SmoothCamSubject playerCamera;
	public static SmoothCamWorld smoothCamWorld;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<Bullet> bullets;
	public static ArrayList<Body> bodiesToRemove;
	public static long score;
	public static Sprite background;
	public static RayHandler rayHandler;
	public static long lastScoreTime, startTime, elapsedTime;
	public static int enemiesToSpawn;
	public static ArrayList<PooledEffect> effects;
	public static ParticleEffectPool enemyDeath, flameThrower, shipDeath, taserEffect;
	public static boolean shipGameOver, earthGameOver;
	public static ArrayList<Vector2[]> lightningToDraw;
	public static int roundCounter;
	
	public GameScreen(LudumDare27 game)
	{
		this.game = game;
		reset();
	}
	
	public void reset()
	{
		shipGameOver = false;
		earthGameOver = false;
		batch = game.getBatch();
		hudBatch = new SpriteBatch();
		camera = game.getCamera();
		font = LudumDare27.terminalFont;
		fontSmall = LudumDare27.terminalFontSmall;
		world = new World(new Vector2(0.0f, 0.0f), true);
		ship = new Ship(new Vector2(LudumDare27.WIDTH/2, LudumDare27.HEIGHT/2), new Vector2(0.0f, 0.0f), world);
		earth = new Earth(world);
		background = LudumDare27.background;
		background.setOrigin(background.getWidth()/2, background.getHeight()/2);
		background.setPosition(-background.getWidth()/2, -background.getHeight()/2);
		background.setScale(5.0f);
		
		playerCamera = new SmoothCamSubject();
		playerCamera.setVelocityRadius(30.0f);

		smoothCamWorld = new SmoothCamWorld(playerCamera);
		smoothCamWorld.setBoundingBox(camera.viewportWidth * 0.8f, camera.viewportHeight * 0.8f);

		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<Bullet>();
		bodiesToRemove = new ArrayList<Body>();
		world.setContactListener(this);
		SmoothCamPoint testpoi2 = new SmoothCamPoint();
		testpoi2.setPosition(0f, 0f);
		testpoi2.setInnerRadius(100f);
		testpoi2.setOuterRadius(600f);
		testpoi2.setPolarity(SmoothCamPoint.ATTRACT);
		testpoi2.setZoom(0.7f);
		smoothCamWorld.addPoint(testpoi2);
		
		RayHandler.useDiffuseLight(true);
		RayHandler.setGammaCorrection(true);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.3f);
		rayHandler.setCulling(true);	
		rayHandler.setCombinedMatrix(camera.combined);
		PointLight light = new PointLight(rayHandler, 75, new Color(0,0,1,1), 15, ship.body.getPosition().x, ship.body.getPosition().y);
		light.attachToBody(ship.body, 0.0f, 0.0f);
		light.setXray(true);
		
		light = new PointLight(rayHandler, 75, new Color(0,0.5f,0.5f,1), 30, 0, 0);
		light.attachToBody(earth.body, 0.0f, 0.0f);
		light.setXray(true);
		
		startTime = TimeUtils.millis();
		enemiesToSpawn = 4;
		spawnEnemies(enemiesToSpawn);
		
		effects = new ArrayList<PooledEffect>();
		shipDeath = new ParticleEffectPool(LudumDare27.shipDeath, 1, 5);
		enemyDeath = new ParticleEffectPool(LudumDare27.enemyDeath, 2, 20);
		flameThrower = new ParticleEffectPool(LudumDare27.flameEffect, 3, 30);
		taserEffect = new ParticleEffectPool(LudumDare27.taserEffect, 3, 12);
		
		lightningToDraw = new ArrayList<Vector2[]>();
		
		LudumDare27.gameStart.play(1.0f);
		LudumDare27.music.loop(1.0f);
	}
	
	public void spawnEnemies(int num)
	{
		for(int i = 0; i < num; i++)
		{
			int rand = (int)(Math.random()*4+1);
			if(rand == 1)
			{
				float x = (float)((-background.getWidth()/2*background.getScaleX()-100) + (background.getWidth()*background.getScaleX()+200)*Math.random())/BOX2D_TO_SCREEN;
				float y = (float)(background.getHeight()/2*background.getScaleY()+Math.random()*100)/BOX2D_TO_SCREEN;
				enemies.add(new Enemy(new Vector2(x, y), new Vector2(0.0f, 0.0f), world));
			}
			if(rand == 2)
			{
				float x = (float)((background.getWidth()/2*background.getScaleX()) + 100*Math.random())/BOX2D_TO_SCREEN;
				float y = (float)((background.getHeight()*background.getScaleY()+100)*Math.random())/BOX2D_TO_SCREEN;
				enemies.add(new Enemy(new Vector2(x, y), new Vector2(0.0f, 0.0f), world));
			}
			if(rand == 3)
			{
				float x = (float)((-background.getWidth()/2*background.getScaleX()) + 100*Math.random())/BOX2D_TO_SCREEN;
				float y = (float)((background.getHeight()*background.getScaleY()+100)*Math.random())/BOX2D_TO_SCREEN;
				enemies.add(new Enemy(new Vector2(x, y), new Vector2(0.0f, 0.0f), world));
			}
			if(rand == 4)
			{
				float x = (float)((-background.getWidth()/2*background.getScaleX()) + (background.getWidth()*background.getScaleX())*Math.random())/BOX2D_TO_SCREEN;
				float y = (float)(-background.getHeight()/2*background.getScaleY()+Math.random()*100)/BOX2D_TO_SCREEN;
				enemies.add(new Enemy(new Vector2(x, y), new Vector2(0.0f, 0.0f), world));
			}
		}
	}
	
	public void pollInput()
	{
		if(!shipGameOver && !earthGameOver)
		{
			boolean key = false;
			if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
			{
				ship.left();
				key = true;
			}
			if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
			{
				ship.right();
				key = true;
			}
			if(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
			{
				ship.up();
				key = true;
			}
			if(Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
			{
				ship.down();
				key = true;
			}
			
			ship.keyIsDown(key);
			int mouseX = Gdx.input.getX();
			int mouseY = Gdx.input.getY();
			Vector3 vec = new Vector3(mouseX, mouseY, 0);
			camera.unproject(vec);
			int dx = (int) (ship.body.getPosition().x - vec.x/BOX2D_TO_SCREEN);
			int dy = (int) (ship.body.getPosition().y - vec.y/BOX2D_TO_SCREEN);
			ship.rotate(dx, dy);
			if (Gdx.input.isButtonPressed(0)) 
			{
				ship.mouseDown();
			}
			else
			{
				ship.lightningStart = true;
				ship.flameStart = true;
				LudumDare27.flameThrowerShoot.stop();
			}
		}
		else
		{
			if(Gdx.input.isKeyPressed(Keys.ENTER))
			{
				this.reset();
			}
		}
	}
	
	public void logic()
	{
		long time = TimeUtils.millis();
		elapsedTime = time-startTime;
		if(elapsedTime >= 10000)
		{
			roundCounter++;
			enemiesToSpawn += 2;
			spawnEnemies(enemiesToSpawn);
			startTime = time;
			if(roundCounter == 3)
			{
				ship.powerUp = "machineGun";
				LudumDare27.powerUp.play(1.0f);
			}
		}
		ship.logic();
		for(int i = 0; i < enemies.size(); i++)
			enemies.get(i).logic();
		world.step(1.0f/60.0f, 6, 2);
		
		if(ship.body.getPosition().y*BOX2D_TO_SCREEN > background.getHeight()/2 * background.getScaleY() && ship.body.getLinearVelocity().y > 0.1f)
		{
			ship.body.setTransform(new Vector2(ship.body.getPosition().x, -background.getHeight()/2*background.getScaleY()/BOX2D_TO_SCREEN), ship.body.getAngle());
		}
		if(ship.body.getPosition().y*BOX2D_TO_SCREEN < -background.getHeight()/2 * background.getScaleY() && ship.body.getLinearVelocity().y < -0.1f)
		{
			ship.body.setTransform(new Vector2(ship.body.getPosition().x, background.getHeight()/2*background.getScaleY()/BOX2D_TO_SCREEN), ship.body.getAngle());
		}
		if(ship.body.getPosition().x*BOX2D_TO_SCREEN < -background.getWidth()/2 * background.getScaleX() && ship.body.getLinearVelocity().x < -0.1f)
		{
			ship.body.setTransform(new Vector2(background.getWidth()/2*background.getScaleX()/BOX2D_TO_SCREEN, ship.body.getPosition().y), ship.body.getAngle());
		}
		if(ship.body.getPosition().x*BOX2D_TO_SCREEN > background.getWidth()/2 * background.getScaleX() && ship.body.getLinearVelocity().x > 0.1f)
		{
			ship.body.setTransform(new Vector2(-background.getWidth()/2*background.getScaleX()/BOX2D_TO_SCREEN, ship.body.getPosition().y), ship.body.getAngle());
		}
	}
	
	@Override
	public void render(float delta) {
		pollInput();
		logic();
		
		playerCamera.setPosition(ship.body.getPosition().x*BOX2D_TO_SCREEN, ship.body.getPosition().y*BOX2D_TO_SCREEN);
		playerCamera.setVelocity(ship.body.getLinearVelocity().x / Ship.maxVelocity, ship.body.getLinearVelocity().y / Ship.maxVelocity);
		smoothCamWorld.update();
		
        camera.position.set(smoothCamWorld.getX(), smoothCamWorld.getY(), 0);
        camera.viewportWidth = LudumDare27.WIDTH * smoothCamWorld.getZoom();
        camera.viewportHeight = LudumDare27.HEIGHT * smoothCamWorld.getZoom();
        camera.update();
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(camera.combined);
        rayHandler.setCombinedMatrix(camera.combined.cpy().scale(BOX2D_TO_SCREEN, BOX2D_TO_SCREEN, 0));
        batch.begin();
        background.draw(batch);
        /*
        for(int i = -LudumDare27.WIDTH*4; i < LudumDare27.WIDTH*4; i+=LudumDare27.WIDTH/4)
        {
        	for(int j = -LudumDare27.HEIGHT*4; j < LudumDare27.HEIGHT*4; j+=LudumDare27.HEIGHT/4)
        	{
        		font.draw(batch, "Game", i, j);
        	}
        }
        */
        for(int i = 0; i < lightningToDraw.size(); i++)
        {
        	Sprite lightning = new Sprite(LudumDare27.lightning);
        	Vector2 start = lightningToDraw.get(i)[0].mul(BOX2D_TO_SCREEN);
        	Vector2 finish = lightningToDraw.get(i)[1].mul(BOX2D_TO_SCREEN);
        	float length = finish.sub(start).len();
        	float angle = finish.sub(start).angle();
        	
        	lightning.setOrigin(0, lightning.getHeight()/2);
        	lightning.setScale(length/lightning.getWidth());
        	lightning.setRotation(angle);
        	lightning.setPosition(start.x, start.y-lightning.getHeight()/2);
        	lightning.setColor(new Color(0,0,1,0.5f));
        	lightning.draw(batch);
        }
        lightningToDraw.clear();
        if(!earthGameOver)
        	earth.draw(batch);
        for(int i = 0; i < bullets.size(); i++)
        	bullets.get(i).draw(batch);
        if(!shipGameOver)
        	ship.draw(batch);
        for(int i = 0; i < enemies.size(); i++)
        	enemies.get(i).draw(batch);
        for(int i = effects.size()-1; i >= 0; i--)
        {
        	PooledEffect effect = effects.get(i);
        	effect.draw(batch, Gdx.graphics.getDeltaTime());
        	if(effect.isComplete())
        	{
        		effect.free();
        		effects.remove(i);
        	}
        }
        batch.end();
        rayHandler.updateAndRender();
        
        hudBatch.begin();
        if(!earthGameOver && !shipGameOver)
        	font.draw(hudBatch, "Next wave in " + (int)(10-Math.floor(elapsedTime/1000.0f)), 50, 50);
        if(earthGameOver || shipGameOver)
        {
        	String gameOver = "GAME OVER";
        	String enter = "PRESS ENTER TO RETRY";
        	TextBounds textbounds1 = font.getBounds(gameOver);
        	TextBounds textbounds2 = fontSmall.getBounds(enter);
        	if((int)(10-Math.floor(elapsedTime/1000.0f))%2==0)
        	{
        		font.draw(hudBatch, gameOver, LudumDare27.WIDTH/2-textbounds1.width/2, LudumDare27.HEIGHT/2+30);
        		fontSmall.draw(hudBatch, enter, LudumDare27.WIDTH/2-textbounds2.width/2, LudumDare27.HEIGHT/2-textbounds2.height);
        	}
        	if(!ship.flameStart)
        	{
        		LudumDare27.flameThrowerShoot.stop();
        	}
        }
        String scoreString = "Score: " + score;
        TextBounds bounds = fontSmall.getBounds(scoreString);
        fontSmall.draw(hudBatch, scoreString, LudumDare27.WIDTH-bounds.width-5, LudumDare27.HEIGHT-bounds.height/2);
        hudBatch.draw(ship.defenderBar, 0, LudumDare27.HEIGHT-ship.defenderBar.getHeight());
        hudBatch.end();
        
        //smoothCamDebug.render(smoothCamWorld, camera.combined);
        //debugRenderer.render(world, camera.combined.cpy().scale(BOX2D_TO_SCREEN, BOX2D_TO_SCREEN, 0));
        
        Iterator<Body> bodies = bodiesToRemove.iterator();
		while(bodies.hasNext())
		{
			world.destroyBody(bodies.next());
		}
		bodiesToRemove.clear();
	}
	public static void shipGameOver()
	{
		shipGameOver = true;
	}
	public static void earthGameOver()
	{
		earthGameOver = true;
	}
	@Override
	public void resize(int width, int height) {
		
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		rayHandler.dispose();
		hudBatch.dispose();
		world.dispose();
	}

	@Override
	public void beginContact(Contact contact) {
		Object one = contact.getFixtureA().getUserData();
		Object two = contact.getFixtureB().getUserData();
		
		//If bullets collide with anything, remove them
		if(one instanceof Bullet && !(two instanceof Ship))
		{
			if(!bodiesToRemove.contains(contact.getFixtureA().getBody()))
			{
				bodiesToRemove.add(contact.getFixtureA().getBody());
				bullets.remove(one);
			}
			if(one instanceof Flame)
			{
				GameScreen.rayHandler.lightList.removeValue(((Flame)two).light, true);
			}
		}
		if(two instanceof Bullet && !(one instanceof Ship))
		{
			if(!bodiesToRemove.contains(contact.getFixtureB().getBody()))
			{
				bodiesToRemove.add(contact.getFixtureB().getBody());
				bullets.remove(two);
			}
			if(two instanceof Flame)
			{
				GameScreen.rayHandler.lightList.removeValue(((Flame)two).light, true);
			}
		}
		
		//Enemy and bullet collision
		if(one instanceof Enemy && two instanceof Bullet)
		{
			((Enemy)one).damage(((Bullet)two).damage, true);
		}
		if(one instanceof Bullet && two instanceof Enemy)
		{
			((Enemy)two).damage(((Bullet)one).damage, true);
		}
		
		//Ship enemy collision
		if(one instanceof Ship && two instanceof Enemy)
		{
			((Enemy)two).damage(3, false);
			((Ship)one).damage();
		}
		if(one instanceof Enemy && two instanceof Ship)
		{
			((Enemy)one).damage(3, false);
			((Ship)two).damage();
		}
		
		//Enemy earth collision
		if(one instanceof Earth && two instanceof Enemy)
		{
			((Enemy)two).damage(3, false);
			((Earth)one).damage();
		}
		if(two instanceof Earth && one instanceof Enemy)
		{
			((Enemy)one).damage(3, false);
			((Earth)two).damage();
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	

}
