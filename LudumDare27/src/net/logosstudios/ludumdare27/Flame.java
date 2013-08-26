package net.logosstudios.ludumdare27;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Flame extends Bullet{
	public PointLight light;
	public int duration = 20;
	
	public Flame(Sprite sprite, World world, Vector2 position,
			Vector2 velocity, float direction, float speed, int damage, boolean playEffect) {
		super(sprite, world, position, velocity, direction, speed, damage);
		if(playEffect)
		{
			PooledEffect effect = GameScreen.flameThrower.obtain();
			effect.setPosition(body.getWorldCenter().x*GameScreen.BOX2D_TO_SCREEN, body.getWorldCenter().y*GameScreen.BOX2D_TO_SCREEN);
			GameScreen.effects.add(effect);
			effect.setDuration(5);
			effect.start();
		}
		
		light = new PointLight(GameScreen.rayHandler, 10, new Color(1,0.5f,0,1), 5, 0.0f, 0.0f);
		Vector2 pos = body.getWorldCenter().sub(body.getPosition());
		light.attachToBody(body, pos.x, pos.y);
		light.setXray(true);
	}
	public void draw(SpriteBatch batch)
	{
		duration--;
		if(duration<0)
		{
			light.setActive(false);
			GameScreen.rayHandler.lightList.removeValue(light, false);
			GameScreen.rayHandler.disabledLights.add(light);
			GameScreen.bullets.remove(this);
			if(!GameScreen.bodiesToRemove.contains(body))
				GameScreen.bodiesToRemove.add(body);
			duration = 1;
		}
	}
	
}
