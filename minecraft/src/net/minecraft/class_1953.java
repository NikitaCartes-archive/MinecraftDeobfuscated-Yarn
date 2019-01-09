package net.minecraft;

public enum class_1953 {
	field_9315(-3),
	field_9313(-2),
	field_9310(-1),
	field_9314(0),
	field_9316(1),
	field_9309(2),
	field_9311(3);

	private final int field_9308;

	private class_1953(int j) {
		this.field_9308 = j;
	}

	public static class_1953 method_8680(int i) {
		for (class_1953 lv : values()) {
			if (lv.field_9308 == i) {
				return lv;
			}
		}

		return i < field_9315.field_9308 ? field_9315 : field_9311;
	}

	public int method_8681() {
		return this.field_9308;
	}
}
