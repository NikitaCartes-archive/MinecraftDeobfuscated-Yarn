package net.minecraft.util;

public enum TaskPriority {
	EXTREMELY_HIGH(-3),
	VERY_HIGH(-2),
	HIGH(-1),
	NORMAL(0),
	LOW(1),
	VERY_LOW(2),
	EXTREMELY_LOW(3);

	private final int priorityIndex;

	private TaskPriority(int j) {
		this.priorityIndex = j;
	}

	public static TaskPriority getByIndex(int i) {
		for (TaskPriority taskPriority : values()) {
			if (taskPriority.priorityIndex == i) {
				return taskPriority;
			}
		}

		return i < EXTREMELY_HIGH.priorityIndex ? EXTREMELY_HIGH : EXTREMELY_LOW;
	}

	public int getPriorityIndex() {
		return this.priorityIndex;
	}
}
