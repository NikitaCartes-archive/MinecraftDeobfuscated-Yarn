package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class IllagerEntityRenderState extends LivingEntityRenderState {
	public boolean hasVehicle;
	public boolean attacking;
	public Arm illagerMainArm = Arm.RIGHT;
	public IllagerEntity.State illagerState = IllagerEntity.State.NEUTRAL;
	public int crossbowPullTime;
	public int itemUseTime;
	public float handSwingProgress;
}
