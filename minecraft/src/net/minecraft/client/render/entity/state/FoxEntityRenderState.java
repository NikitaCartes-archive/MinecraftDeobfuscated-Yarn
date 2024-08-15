package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.FoxEntity;

@Environment(EnvType.CLIENT)
public class FoxEntityRenderState extends LivingEntityRenderState {
	public float headRoll;
	public float bodyRotationHeightOffset;
	public boolean inSneakingPose;
	public boolean sleeping;
	public boolean sitting;
	public boolean walking;
	public boolean chasing;
	public FoxEntity.Type type = FoxEntity.Type.RED;
}
