package net.logosstudios.ludumdare27;

import java.util.ArrayList;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
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

public class Enemy {
	public Body body;
	public Vector2 origin;
	public float angle;
	public static final float maxVelocity = 100.0f, friction = 0.87f, speed = 10.0f;;
	public Sprite currentSprite;
	public Sprite[] enemyColors;
	public int health;
	public long lastDamageTime;
	public PointLight light;
	
	public Enemy(Vector2 p, Vector2 v, World world)
	{
		health = 2;
		enemyColors = LudumDare27.enemyColors;
		currentSprite = enemyColors[health];
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(0, 0);
		body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.6f;
		LudumDare27.enemyLoader.attachFixture(body, "enemyBody", fixtureDef, currentSprite.getWidth()/GameScreen.BOX2D_TO_SCREEN);
		ArrayList<Fixture> fixtures = body.getFixtureList();
		for(int i = 0; i < fixtures.size(); i++)
		{
			fixtures.get(i).setUserData(this);
		}
		origin = LudumDare27.enemyLoader.getOrigin("enemyBody", currentSprite.getWidth()/GameScreen.BOX2D_TO_SCREEN);
		body.setTransform(p, body.getAngle());
		
		light = new PointLight(GameScreen.rayHandler, 50, new Color(0,1,0,1), 10, 0.0f, 0.0f);
		Vector2 pos = body.getWorldCenter().sub(body.getPosition());
		light.attachToBody(body, pos.x, pos.y);
		light.setXray(true);
	}
	public void logic()
	{
		if(Math.abs(body.getLinearVelocity().x) >= maxVelocity)
			body.setLinearVelocity(new Vector2(Math.signum(body.getLinearVelocity().x) * maxVelocity, 0.0f));
		if(Math.abs(body.getLinearVelocity().y) >= maxVelocity)
			body.setLinearVelocity(new Vector2(0.0f, Math.signum(body.getLinearVelocity().y) * maxVelocity));
		
		//rotate(0, 0);
		
		Vector2 direction = new Vector2(-body.getWorldCenter().x, -body.getWorldCenter().y).nor();
		
		body.setLinearVelocity(direction.mul(speed));
	}
	public void damage(int amount, boolean score)
	{
		long time = TimeUtils.millis();
		if(time - lastDamageTime > 200)
		{
			lastDamageTime = time;
			health-=amount;
			if(health < 0)
			{
				GameScreen.enemies.remove(this);
				if(!GameScreen.bodiesToRemove.contains(body))
					GameScreen.bodiesToRemove.add(body);
				GameScreen.rayHandler.lightList.removeValue(light, true);
				if(score)
				{
					GameScreen.score += 100;
					if(time - GameScreen.lastScoreTime < 1000)
					{
						GameScreen.score += 100;
					}
					GameScreen.lastScoreTime = time;
				}
				PooledEffect effect = GameScreen.enemyDeath.obtain();
				effect.setPosition(body.getWorldCenter().x*GameScreen.BOX2D_TO_SCREEN, body.getWorldCenter().y*GameScreen.BOX2D_TO_SCREEN);
				GameScreen.effects.add(effect);
				effect.setDuration(5);
				effect.start();
				LudumDare27.ufoDeath.play(1.0f);
			}
			else 
			{
				currentSprite = enemyColors[health];
				if(health == 1)
				{
					light.setColor(new Color(0.5f, 0.5f, 0, 1));
				}
				if(health == 0)
				{
					light.setColor(new Color(1, 0, 0, 1));
				}
			}
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
	public void rotate(int x, int y)
	{
		float angle = (float)Math.atan2(y-body.getPosition().y, x-body.getPosition().x);
		body.setTransform(body.getPosition().x, body.getPosition().y, angle);
	}
}
