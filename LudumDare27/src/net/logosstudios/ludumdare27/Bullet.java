package net.logosstudios.ludumdare27;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Bullet {
	public Body body;
	public Sprite sprite;
	public float angle;
	public int damage;
	
	public Bullet(Sprite sprite, World world, Vector2 position, Vector2 velocity, float direction, float speed, int damage)
	{
		this.damage = damage;
		angle = direction * MathUtils.radiansToDegrees - 90;
		this.sprite = sprite;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(0.0f, 0.0f);
		body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.001f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		CircleShape shape = new CircleShape();
		shape.setRadius(1.0f);
		fixtureDef.shape = shape;
		fixtureDef.filter.groupIndex = -10;
		Fixture fix = body.createFixture(fixtureDef);
		fix.setUserData(this);
		
		body.setLinearVelocity(new Vector2(velocity.x+(float)(speed*Math.cos(direction)), velocity.y+(float)(speed*Math.sin(direction))));
		body.setTransform(position, 0.0f);
	}
	public void draw(SpriteBatch batch)
	{
		sprite.setRotation(angle);
		sprite.setPosition(body.getWorldCenter().x*GameScreen.BOX2D_TO_SCREEN-sprite.getWidth()/2, body.getWorldCenter().y*GameScreen.BOX2D_TO_SCREEN-sprite.getHeight()/2);
		sprite.draw(batch);
	}
}
