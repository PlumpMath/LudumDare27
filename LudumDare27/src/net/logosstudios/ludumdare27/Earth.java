package net.logosstudios.ludumdare27;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class Earth {
	public Body body;
	public int health;
	public Sprite sprite, currentHealth;
	public Sprite[] earthBar;
	public long lastDamageTime;
	
	public Earth(World world)
	{
		health = 6;
		earthBar = LudumDare27.earthBar;
		currentHealth = earthBar[health];
		sprite = LudumDare27.earthSprite;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0.0f, 0.0f);
		body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(sprite.getHeight()/GameScreen.BOX2D_TO_SCREEN/2);
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.6f;
		fixtureDef.filter.groupIndex = -10;
		Fixture fix = body.createFixture(fixtureDef);
		fix.setUserData(this);
	}
	
	public void draw(SpriteBatch batch)
	{
		Vector2 pos = body.getPosition().mul(GameScreen.BOX2D_TO_SCREEN);
		sprite.setPosition(pos.x-sprite.getWidth()/2, pos.y-sprite.getHeight()/2);
		sprite.draw(batch);
		
		currentHealth.setPosition(pos.x - currentHealth.getWidth()/2, pos.y + sprite.getHeight()/2 + 30);
		currentHealth.setOrigin(currentHealth.getWidth()/2, currentHealth.getHeight()/2);
		currentHealth.setScale(2.0f);
		currentHealth.draw(batch);
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
				if(!GameScreen.earthGameOver)
				{
					PooledEffect effect = GameScreen.shipDeath.obtain();
					effect.setPosition(body.getWorldCenter().x*GameScreen.BOX2D_TO_SCREEN, body.getWorldCenter().y*GameScreen.BOX2D_TO_SCREEN);
					effect.getEmitters().get(0).getScale().setHigh(200, 400);
					GameScreen.effects.add(effect);
					effect.setDuration(5);
					effect.start();
					GameScreen.earthGameOver();
				}
			}
			else 
			{
				currentHealth = earthBar[health];
			}
		}
	}
}
