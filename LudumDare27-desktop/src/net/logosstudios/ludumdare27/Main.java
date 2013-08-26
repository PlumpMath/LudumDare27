package net.logosstudios.ludumdare27;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LudumDare27";
		cfg.useGL20 = false;
		cfg.width = 600;
		cfg.height = 600;
		cfg.resizable = false;
		
		new LwjglApplication(new LudumDare27(), cfg);
	}
}