package net.minecraft.test;

import javax.annotation.Nullable;

class TimedTask {
	@Nullable
	public final Long duration;
	public final Runnable task;
}
