package net.minecraft;

import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.server.SampleSubscriptionTracker;
import net.minecraft.util.DebugSampleType;

public class class_9193 extends class_9187 {
	private final SampleSubscriptionTracker field_48819;
	private final DebugSampleType field_48820;

	public class_9193(int i, SampleSubscriptionTracker sampleSubscriptionTracker, DebugSampleType debugSampleType) {
		this(i, sampleSubscriptionTracker, debugSampleType, new long[i]);
	}

	public class_9193(int i, SampleSubscriptionTracker sampleSubscriptionTracker, DebugSampleType debugSampleType, long[] ls) {
		super(i, ls);
		this.field_48819 = sampleSubscriptionTracker;
		this.field_48820 = debugSampleType;
	}

	@Override
	protected void method_56649() {
		this.field_48819.sendPacket(new DebugSampleS2CPacket((long[])this.array.clone(), this.field_48820));
	}
}
