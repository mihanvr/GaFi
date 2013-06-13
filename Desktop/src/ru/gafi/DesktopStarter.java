package ru.gafi;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 14:05
 */
public class DesktopStarter {

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "GaFi";
		cfg.width = 800;
		cfg.height = 480;
		cfg.useGL20 = true;

		packImagesToAtlas();

		new LwjglApplication(new Main(),cfg);
	}

	private static void packImagesToAtlas() {
		TexturePacker2.Settings settings = new TexturePacker2.Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 512;
		TexturePacker2.process(settings, "../raw/800", ".", "800");
		TexturePacker2.process(settings, "../raw/1280", ".", "1280");
		TexturePacker2.process(settings, "../raw/2048", ".", "2048");
	}

}
