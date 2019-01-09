package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_1664 {
	field_7559(0, "cape"),
	field_7564(1, "jacket"),
	field_7568(2, "left_sleeve"),
	field_7570(3, "right_sleeve"),
	field_7566(4, "left_pants_leg"),
	field_7565(5, "right_pants_leg"),
	field_7563(6, "hat");

	private final int field_7561;
	private final int field_7560;
	private final String field_7569;
	private final class_2561 field_7567;

	private class_1664(int j, String string2) {
		this.field_7561 = j;
		this.field_7560 = 1 << j;
		this.field_7569 = string2;
		this.field_7567 = new class_2588("options.modelPart." + string2);
	}

	public int method_7430() {
		return this.field_7560;
	}

	public int method_7431() {
		return this.field_7561;
	}

	public String method_7429() {
		return this.field_7569;
	}

	public class_2561 method_7428() {
		return this.field_7567;
	}
}
