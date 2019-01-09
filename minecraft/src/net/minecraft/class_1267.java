package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;

public enum class_1267 {
	field_5801(0, "peaceful"),
	field_5805(1, "easy"),
	field_5802(2, "normal"),
	field_5807(3, "hard");

	private static final class_1267[] field_5800 = (class_1267[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_1267::method_5461))
		.toArray(class_1267[]::new);
	private final int field_5803;
	private final String field_5806;

	private class_1267(int j, String string2) {
		this.field_5803 = j;
		this.field_5806 = string2;
	}

	public int method_5461() {
		return this.field_5803;
	}

	public class_2561 method_5463() {
		return new class_2588("options.difficulty." + this.field_5806);
	}

	public static class_1267 method_5462(int i) {
		return field_5800[i % field_5800.length];
	}

	@Nullable
	public static class_1267 method_16691(String string) {
		for (class_1267 lv : values()) {
			if (lv.field_5806.equals(string)) {
				return lv;
			}
		}

		return null;
	}

	public String method_5460() {
		return this.field_5806;
	}
}
