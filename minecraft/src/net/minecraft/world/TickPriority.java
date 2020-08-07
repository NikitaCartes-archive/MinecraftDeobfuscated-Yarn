package net.minecraft.world;

public enum TickPriority {
	field_9315(-3),
	field_9313(-2),
	field_9310(-1),
	field_9314(0),
	field_9316(1),
	field_9309(2),
	field_9311(3);

	private final int index;

	private TickPriority(int index) {
		this.index = index;
	}

	public static TickPriority byIndex(int index) {
		for (TickPriority tickPriority : values()) {
			if (tickPriority.index == index) {
				return tickPriority;
			}
		}

		return index < field_9315.index ? field_9315 : field_9311;
	}

	public int getIndex() {
		return this.index;
	}
}
