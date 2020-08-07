package net.minecraft.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum Perspective {
	field_26664(true, false),
	field_26665(false, false),
	field_26666(false, true);

	private static final Perspective[] VALUES = values();
	private boolean firstPerson;
	private boolean frontView;

	private Perspective(boolean firstPerson, boolean frontView) {
		this.firstPerson = firstPerson;
		this.frontView = frontView;
	}

	public boolean isFirstPerson() {
		return this.firstPerson;
	}

	public boolean isFrontView() {
		return this.frontView;
	}

	public Perspective next() {
		return VALUES[(this.ordinal() + 1) % VALUES.length];
	}
}
