package net.minecraft.entity.boss.dragon;

import java.util.Arrays;
import net.minecraft.util.math.MathHelper;

public class EnderDragonFrameTracker {
	public static final int field_52489 = 64;
	private static final int field_52490 = 63;
	private final EnderDragonFrameTracker.Frame[] frames = new EnderDragonFrameTracker.Frame[64];
	private int currentIndex = -1;

	public EnderDragonFrameTracker() {
		Arrays.fill(this.frames, new EnderDragonFrameTracker.Frame(0.0, 0.0F));
	}

	public void copyFrom(EnderDragonFrameTracker other) {
		System.arraycopy(other.frames, 0, this.frames, 0, 64);
		this.currentIndex = other.currentIndex;
	}

	public void tick(double y, float yaw) {
		EnderDragonFrameTracker.Frame frame = new EnderDragonFrameTracker.Frame(y, yaw);
		if (this.currentIndex < 0) {
			Arrays.fill(this.frames, frame);
		}

		if (++this.currentIndex == 64) {
			this.currentIndex = 0;
		}

		this.frames[this.currentIndex] = frame;
	}

	public EnderDragonFrameTracker.Frame getFrame(int age) {
		return this.frames[this.currentIndex - age & 63];
	}

	public EnderDragonFrameTracker.Frame getLerpedFrame(int age, float tickDelta) {
		EnderDragonFrameTracker.Frame frame = this.getFrame(age);
		EnderDragonFrameTracker.Frame frame2 = this.getFrame(age + 1);
		return new EnderDragonFrameTracker.Frame(
			MathHelper.lerp((double)tickDelta, frame2.y, frame.y), MathHelper.lerpAngleDegrees(tickDelta, frame2.yRot, frame.yRot)
		);
	}

	public static record Frame(double y, float yRot) {
	}
}
