package net.minecraft.util;

public enum TaskPriority {
	field_9315(-3),
	field_9313(-2),
	field_9310(-1),
	field_9314(0),
	field_9316(1),
	field_9309(2),
	field_9311(3);

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

		return i < field_9315.priorityIndex ? field_9315 : field_9311;
	}

	public int getPriorityIndex() {
		return this.priorityIndex;
	}
}
