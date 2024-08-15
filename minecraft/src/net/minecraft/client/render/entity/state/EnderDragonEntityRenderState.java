package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.dragon.EnderDragonFrameTracker;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderState extends EntityRenderState {
	public float wingPosition;
	public float ticksSinceDeath;
	public boolean hurt;
	@Nullable
	public Vec3d crystalBeamPos;
	public boolean inLandingOrTakeoffPhase;
	public boolean sittingOrHovering;
	public double squaredDistanceFromOrigin;
	public float tickDelta;
	public final EnderDragonFrameTracker frameTracker = new EnderDragonFrameTracker();

	public EnderDragonFrameTracker.Frame getLerpedFrame(int age) {
		return this.frameTracker.getLerpedFrame(age, this.tickDelta);
	}

	public float getNeckPartPitchOffset(int id, EnderDragonFrameTracker.Frame bodyFrame, EnderDragonFrameTracker.Frame neckFrame) {
		double d;
		if (this.inLandingOrTakeoffPhase) {
			d = (double)id / Math.max(this.squaredDistanceFromOrigin / 4.0, 1.0);
		} else if (this.sittingOrHovering) {
			d = (double)id;
		} else if (id == 6) {
			d = 0.0;
		} else {
			d = neckFrame.y() - bodyFrame.y();
		}

		return (float)d;
	}
}
