package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityRenderState extends LivingEntityRenderState {
	public float spikesExtension;
	public float tailAngle;
	public Vec3d cameraPosVec = Vec3d.ZERO;
	@Nullable
	public Vec3d rotationVec;
	@Nullable
	public Vec3d lookAtPos;
	@Nullable
	public Vec3d beamTargetPos;
	public float beamTicks;
	public float beamProgress;
}
