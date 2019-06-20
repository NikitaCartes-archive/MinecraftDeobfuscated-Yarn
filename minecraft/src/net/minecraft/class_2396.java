package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2396<T extends class_2394> {
	private final boolean field_11196;
	private final class_2394.class_2395<T> field_11197;

	protected class_2396(boolean bl, class_2394.class_2395<T> arg) {
		this.field_11196 = bl;
		this.field_11197 = arg;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_10299() {
		return this.field_11196;
	}

	public class_2394.class_2395<T> method_10298() {
		return this.field_11197;
	}
}
