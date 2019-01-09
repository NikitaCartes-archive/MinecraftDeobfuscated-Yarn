package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1942 {
	public static final class_1942[] field_9279 = new class_1942[16];
	public static final class_1942 field_9265 = new class_1942(0, "default", 1).method_8634();
	public static final class_1942 field_9277 = new class_1942(1, "flat").method_8631(true);
	public static final class_1942 field_9276 = new class_1942(2, "largeBiomes");
	public static final class_1942 field_9267 = new class_1942(3, "amplified").method_8629();
	public static final class_1942 field_9278 = new class_1942(4, "customized", "normal", 0).method_8631(true).method_8633(false);
	public static final class_1942 field_9275 = new class_1942(5, "buffet").method_8631(true);
	public static final class_1942 field_9266 = new class_1942(6, "debug_all_block_states");
	public static final class_1942 field_9268 = new class_1942(8, "default_1_1", 0).method_8633(false);
	private final int field_9281;
	private final String field_9269;
	private final String field_9272;
	private final int field_9280;
	private boolean field_9274;
	private boolean field_9273;
	private boolean field_9271;
	private boolean field_9270;

	private class_1942(int i, String string) {
		this(i, string, string, 0);
	}

	private class_1942(int i, String string, int j) {
		this(i, string, string, j);
	}

	private class_1942(int i, String string, String string2, int j) {
		this.field_9269 = string;
		this.field_9272 = string2;
		this.field_9280 = j;
		this.field_9274 = true;
		this.field_9281 = i;
		field_9279[i] = this;
	}

	public String method_8635() {
		return this.field_9269;
	}

	public String method_8638() {
		return this.field_9272;
	}

	@Environment(EnvType.CLIENT)
	public String method_8640() {
		return "generator." + this.field_9269;
	}

	@Environment(EnvType.CLIENT)
	public String method_8630() {
		return this.method_8640() + ".info";
	}

	public int method_8636() {
		return this.field_9280;
	}

	public class_1942 method_8632(int i) {
		return this == field_9265 && i == 0 ? field_9268 : this;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_8641() {
		return this.field_9270;
	}

	public class_1942 method_8631(boolean bl) {
		this.field_9270 = bl;
		return this;
	}

	private class_1942 method_8633(boolean bl) {
		this.field_9274 = bl;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_8642() {
		return this.field_9274;
	}

	private class_1942 method_8634() {
		this.field_9273 = true;
		return this;
	}

	public boolean method_8643() {
		return this.field_9273;
	}

	@Nullable
	public static class_1942 method_8639(String string) {
		for (class_1942 lv : field_9279) {
			if (lv != null && lv.field_9269.equalsIgnoreCase(string)) {
				return lv;
			}
		}

		return null;
	}

	public int method_8637() {
		return this.field_9281;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_8644() {
		return this.field_9271;
	}

	private class_1942 method_8629() {
		this.field_9271 = true;
		return this;
	}
}
