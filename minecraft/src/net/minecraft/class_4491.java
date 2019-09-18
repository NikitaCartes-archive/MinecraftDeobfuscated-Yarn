package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4491 {
	private final List<ConcurrentLinkedQueue<class_4573>> field_20453 = ImmutableList.of(
		new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue()
	);
	private volatile int field_20454;
	private volatile int field_20455;
	private volatile int field_20456;

	public class_4491() {
		this.field_20454 = this.field_20455 = this.field_20456 + 1;
	}
}
