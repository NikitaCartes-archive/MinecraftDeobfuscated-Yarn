package net.minecraft.util.profiler.log;

import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.server.SampleSubscriptionTracker;

public class SubscribableDebugSampleLog extends ArrayDebugSampleLog {
	private final SampleSubscriptionTracker tracker;
	private final DebugSampleType type;

	public SubscribableDebugSampleLog(int size, SampleSubscriptionTracker tracker, DebugSampleType type) {
		this(size, tracker, type, new long[size]);
	}

	public SubscribableDebugSampleLog(int size, SampleSubscriptionTracker tracker, DebugSampleType type, long[] defaults) {
		super(size, defaults);
		this.tracker = tracker;
		this.type = type;
	}

	@Override
	protected void onPush() {
		this.tracker.sendPacket(new DebugSampleS2CPacket((long[])this.values.clone(), this.type));
	}
}
