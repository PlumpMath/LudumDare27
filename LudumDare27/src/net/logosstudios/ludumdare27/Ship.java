package net.logosstudios.ludumdare27;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class Ship {
	public Body body;
	public Vector2 origin;
	public static final float maxVelocity = 100.0f, friction = 0.89f, speed = 10.0f;
	public int health;
	public String powerUp;
	public Sprite shipSprite, flameThrower, currentSprite;
	public Sprite[] machineGun;
	public float animationTick;
	private boolean playMachineGunAnimation;
	public Sprite defenderBar;
	public Sprite[] defenderBars;
	private boolean keyDown;
	private long lastBulletTime, lastDamageTime;
	private int machineGunAmmo;
	public boolean flameStart, lightningStart;
	
	public Ship(Vector2 p, Vector2 v, World world)
	{
		shipSprite = LudumDare27.shipSprite;
		machineGun = LudumDare27.machineGun;
		flameThrower = LudumDare27.flameThrower;
		defenderBars = LudumDare27.defenderBar;
		defenderBar = defenderBars[6];
		defenderBar.rotate(90);
		currentSprite = shipSprite;
		powerUp = "";
		
		health = 6;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(0.0f, 0.0f);
		body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.6f;
		fixtureDef.filter.groupIndex = -10;
		LudumDare27.shipLoader.attachFixture(body, "shipBody", fixtureDef, currentSprite.getWidth()/GameScreen.BOX2D_TO_SCREEN);
		ArrayList<Fixture> fixtures = body.getFixtureList();
		for(int i = 0; i < fixtures.size(); i++)
		{
			fixtures.get(i).setUserData(this);
		}
		origin = LudumDare27.shipLoader.getOrigin("shipBody", currentSprite.getWidth()/GameScreen.BOX2D_TO_SCREEN);
	}
	public void logic()
	{
		if(Math.abs(body.getLinearVelocity().x) >= maxVelocity)
			body.setLinearVelocity(new Vector2(Math.signum(body.getLinearVelocity().x) * maxVelocity, 0.0f));
		if(Math.abs(body.getLinearVelocity().y) >= maxVelocity)
			body.setLinearVelocity(new Vector2(0.0f, Math.signum(body.getLinearVelocity().y) * maxVelocity));
		if(!keyDown)
			body.setLinearVelocity(body.getLinearVelocity().mul(friction));
		
		if(powerUp.equals("machineGun"))
		{
			currentSprite = machineGun[0];
			if(playMachineGunAnimation)
			{
				animationTick += 0.5f;
				currentSprite = machineGun[(int)(animationTick%machineGun.length)];
				if(animationTick >= machineGun.length)
				{
					playMachineGunAnimation = false;
					animationTick = 0;
				}
			}
		}
		if(powerUp.equals("flame"))
		{
			currentSprite = flameThrower;
		}
		if(GameScreen.score > 25000)
		{
			LudumDare27.powerUp.play(1.0f);
			powerUp = "lightning";
		}
		
	}
	public void draw(SpriteBatch batch)
	{
		Vector2 pos = body.getPosition().sub(origin);
		currentSprite.setPosition(pos.x*GameScreen.BOX2D_TO_SCREEN, pos.y*GameScreen.BOX2D_TO_SCREEN);
		currentSprite.setOrigin(origin.x*GameScreen.BOX2D_TO_SCREEN, origin.y*GameScreen.BOX2D_TO_SCREEN);
		currentSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		currentSprite.draw(batch);
	}
	
	public void left()
	{
		body.applyLinearImpulse(new Vector2(-speed, 0.0f), body.getWorldCenter());
	}
	public void right()
	{
		body.applyLinearImpulse(new Vector2(speed, 0.0f), body.getWorldCenter());
	}
	public void up()
	{
		float angle = (float) (body.getAngle() + 3*Math.PI/6);
		body.applyLinearImpulse(new Vector2(speed*(float)Math.cos(angle), speed*(float)Math.sin(angle)), body.getWorldCenter());
	}
	public void down()
	{
		float angle = (float) (body.getAngle() - 3*Math.PI/6);
		body.applyLinearImpulse(new Vector2(speed*(float)Math.cos(angle), speed*(float)Math.sin(angle)), body.getWorldCenter());
	}
	public void keyIsDown(boolean key)
	{
		keyDown = key;
	}
	public void mouseDown()
	{
		long time = TimeUtils.millis();
		if(time-lastBulletTime > 1500 && powerUp.equals("lightning") && lightningStart)
		{
			new Lightning(body.getWorld(), body.getWorldCenter(), (float)(body.getAngle()+Math.PI/2));
			LudumDare27.taserShoot.play(1.0f);
			lightningStart = false;
		}
		if(time-lastBulletTime > 200)
		{
			if(powerUp.equals("machineGun"))
			{
				float angle = (float) (body.getAngle() + Math.PI/4);
				Vector2 bulletOne = new Vector2(body.getPosition().x+2.0f*(float)Math.cos(angle), body.getPosition().y+2.0f*(float)Math.sin(angle));
				angle = (float) (body.getAngle() + 3*Math.PI/4);
				Vector2 bulletTwo = new Vector2(body.getPosition().x+2.0f*(float)Math.cos(angle), body.getPosition().y+2.0f*(float)Math.sin(angle));
				GameScreen.bullets.add(new Bullet(LudumDare27.bigBullet, body.getWorld(), bulletOne, body.getLinearVelocity(), (float)(body.getAngle()+Math.PI/2), 100.0f, 2));
				GameScreen.bullets.add(new Bullet(LudumDare27.bigBullet, body.getWorld(), bulletTwo, body.getLinearVelocity(), (float)(body.getAngle()+Math.PI/2), 100.0f, 2));
				playMachineGunAnimation = true;
				machineGunAmmo+=2;
				if(machineGunAmmo > 300)
				{
					powerUp = "flame";
					LudumDare27.powerUp.play(1.0f);
				}
				LudumDare27.machineGunShoot.play(1.0f);
				lastBulletTime = time;
			}
			else if(powerUp.equals(""))
			{
				GameScreen.bullets.add(new Bullet(LudumDare27.normalBullet, body.getWorld(), body.getPosition(), body.getLinearVelocity(), (float)(body.getAngle()+Math.PI/2), 100.0f, 1));
				LudumDare27.shoot.play(1.0f);
				lastBulletTime = time;
			}
		}
		if(powerUp.equals("flame"))
		{
			GameScreen.bullets.add(new Flame(null, body.getWorld(), body.getPosition(), body.getLinearVelocity(), (float)(body.getAngle()+Math.PI/2), 100.0f, 2, false));
			if(flameStart)
			{
				LudumDare27.flameThrowerShoot.loop(1.0f);
				flameStart = false;
			}
		}
	}
	public void damage()
	{
		long time = TimeUtils.millis();
		if(time - lastDamageTime > 200)
		{
			lastDamageTime = time;
			health--;
			if(health < 0)
			{
				PooledEffect effect = GameScreen.shipDeath.obtain();
				effect.setPosition(body.getWorldCenter().x*GameScreen.BOX2D_TO_SCREEN, body.getWorldCenter().y*GameScreen.BOX2D_TO_SCREEN);
				GameScreen.effects.add(effect);
				effect.setDuration(7);
				effect.start();
				GameScreen.shipGameOver();
			}
			else 
			{
				defenderBar = defenderBars[health];
			}
		}
	}
	public void rotate(int x, int y)
	{
		float angle = (float)(Math.atan2(y, x) + 3*Math.PI/6);
		body.setTransform(body.getPosition().x, body.getPosition().y, angle);
	}
}
