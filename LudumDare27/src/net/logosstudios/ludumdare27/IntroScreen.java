package net.logosstudios.ludumdare27;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IntroScreen implements Screen
{
	private LudumDare27 game;
	private SpriteBatch batch;
	private Camera camera;
	private BitmapFont font;
	private Sprite currentSprite;
	private Sprite[] introScreen;
	private float animationTick;
	
	public IntroScreen(LudumDare27 game)
	{
		this.game = game;
		batch = game.getBatch();
		camera= game.getCamera();
		font = LudumDare27.terminalFont;
		introScreen = LudumDare27.introScreen;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        animationTick += 0.05f;
		currentSprite = introScreen[(int)(animationTick%introScreen.length)];
		//currentSprite.setPosition(-currentSprite.getWidth()/2, - currentSprite.getHeight()/2);
		currentSprite.draw(batch);
        batch.end();

        if (Gdx.input.isButtonPressed(0)) {
                game.setScreen(new GameScreen(game));
                dispose();
        }
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
		
	}
	

}
