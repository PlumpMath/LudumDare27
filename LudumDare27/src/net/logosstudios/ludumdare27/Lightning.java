package net.logosstudios.ludumdare27;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;


public class Lightning implements RayCastCallback, QueryCallback{
	public Fixture foundFixture;
	public ArrayList<Fixture> adjacentFixtures;
	public ArrayList<Body> bodyList;
	
	public Lightning(World world, Vector2 start, float direction)
	{
		adjacentFixtures = new ArrayList<Fixture>();
		bodyList = new ArrayList<Body>();
		Vector2 finish = new Vector2((float)(start.x+60*Math.cos(direction)), (float)(start.y+60*Math.sin(direction)));
		GameScreen.lightningToDraw.add(new Vector2[]{start, finish});
		float dist = new Vector2(finish.x-start.x, finish.y-start.y).len();
		world.rayCast(this, start, finish);
		if(foundFixture!=null)
		{
			Vector2 pos = foundFixture.getBody().getWorldCenter();
			world.QueryAABB(this, pos.x-30, pos.y-30, pos.x+30, pos.y+30);
			if(adjacentFixtures.size()>0)
			{
				for(int i = 0; i < adjacentFixtures.size(); i++)
				{
					Fixture fixture = adjacentFixtures.get(i);
					if(!fixture.equals(foundFixture) && !fixture.getBody().equals(foundFixture.getBody()))
					{
						if(!bodyList.contains(fixture.getBody()))
						{
							bodyList.add(fixture.getBody());
						}
					}
				}
				for(int i = 0; i < bodyList.size() && i < 6; i++)
				{
					((Enemy)bodyList.get(i).getFixtureList().get(0).getUserData()).damage(1, true);
					GameScreen.lightningToDraw.add(new Vector2[]{foundFixture.getBody().getWorldCenter(), bodyList.get(i).getWorldCenter()});
				}
			}
		}
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		if(fixture.getUserData() instanceof Enemy)
		{
			((Enemy)fixture.getUserData()).damage(1, true);
			this.foundFixture = fixture;
			return 0;
		}
		return 1.0f;
	}

	@Override
	public boolean reportFixture(Fixture fixture) {
		if(fixture.getUserData() instanceof Enemy && !fixture.getBody().equals(foundFixture.getBody()))
			adjacentFixtures.add(fixture);
		return true;
	}
}
