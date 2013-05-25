package ru.gafi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * User: Michael
 * Date: 22.05.13
 * Time: 10:33
 */
public class Settings {

	private static final String PREF_STRETCH_TABLE = "stretch";
	private static final String PREFS = "prefs";
	private Preferences preferences;
	private SnapshotArray<SettingsListener> listeners = new SnapshotArray<>(SettingsListener.class);

	public Settings() {
		preferences = Gdx.app.getPreferences(PREFS);
	}

	public void setStretch(boolean value) {
		preferences.putBoolean(PREF_STRETCH_TABLE, value);
		fireChange();
	}

	public boolean isStretch() {
		return preferences.getBoolean(PREF_STRETCH_TABLE, false);
	}

	public void flush() {
		preferences.flush();
	}

	private void fireChange() {
		preferences.flush();
		SettingsListener[] array = listeners.begin();
		for (int i = 0; i < listeners.size; i++) {
			array[i].onChange(this);
		}
		listeners.end();
	}

	public void addChangeListener(SettingsListener listener) {
		listeners.add(listener);
	}

	public static interface SettingsListener {
		public void onChange(Settings settings);
	}

}
