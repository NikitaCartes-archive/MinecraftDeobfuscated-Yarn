package com.mojang.realmsclient;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeyCombo {
	private final char[] chars;
	private int matchIndex;
	private final Runnable onCompletion;

	public KeyCombo(char[] keys, Runnable task) {
		this.onCompletion = task;
		if (keys.length < 1) {
			throw new IllegalArgumentException("Must have at least one char");
		} else {
			this.chars = keys;
		}
	}

	public boolean keyPressed(char key) {
		if (key == this.chars[this.matchIndex++]) {
			if (this.matchIndex == this.chars.length) {
				this.reset();
				this.onCompletion.run();
				return true;
			}
		} else {
			this.reset();
		}

		return false;
	}

	public void reset() {
		this.matchIndex = 0;
	}

	public String toString() {
		return "KeyCombo{chars=" + Arrays.toString(this.chars) + ", matchIndex=" + this.matchIndex + '}';
	}
}
