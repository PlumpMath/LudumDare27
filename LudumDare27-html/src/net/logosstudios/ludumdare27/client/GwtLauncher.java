package net.logosstudios.ludumdare27.client;

import net.logosstudios.ludumdare27.LudumDare27;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader.PreloaderCallback;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(600, 600);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new LudumDare27();
	}
	
	long loadStart = TimeUtils.nanoTime();
	public PreloaderCallback getPreloaderCallback () {
		final Canvas canvas = Canvas.createIfSupported();
		canvas.setWidth("" + (int)(getConfig().width * 0.7f) + "px");
		canvas.setHeight("70px");
		getRootPanel().add(canvas);
		final Context2d context = canvas.getContext2d();
		context.setTextAlign(TextAlign.CENTER);
		context.setTextBaseline(TextBaseline.MIDDLE);
		context.setFont("18pt Calibri");

		return new PreloaderCallback() {
			@Override
			public void done () {
				context.fillRect(0, 0, 300, 40);
			}

			@Override
			public void loaded (String file, int loaded, int total) {
				System.out.println("loaded " + file + "," + loaded + "/" + total);
				CssColor color = CssColor.make(30, 30, 30);
				context.setFillStyle(color);
				context.setStrokeStyle(color);
				context.fillRect(0, 0, 300, 70);
				int value = (int)((((TimeUtils.nanoTime() - loadStart) % 1000000000) / 1000000000f) * 255);
				color = CssColor.make(value, value, value);
				context.setFillStyle(color);
				context.setStrokeStyle(color);
				context.fillRect(0, 0, 300 * (loaded / (float)total) * 0.97f, 70);

				context.setFillStyle(CssColor.make(50, 50, 50));
				context.fillText("loading", 300 / 2, 70 / 2);
			}

			@Override
			public void error (String file) {
				System.out.println("error: " + file);
			}
		};
	}
}