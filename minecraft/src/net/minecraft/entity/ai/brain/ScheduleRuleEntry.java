package net.minecraft.entity.ai.brain;

public class ScheduleRuleEntry {
	private final int startTime;
	private final float priority;

	public ScheduleRuleEntry(int i, float f) {
		this.startTime = i;
		this.priority = f;
	}

	public int getStartTime() {
		return this.startTime;
	}

	public float getPriority() {
		return this.priority;
	}
}
