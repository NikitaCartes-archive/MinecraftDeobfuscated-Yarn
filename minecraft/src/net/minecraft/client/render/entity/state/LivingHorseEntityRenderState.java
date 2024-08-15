package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LivingHorseEntityRenderState extends LivingEntityRenderState {
	public boolean saddled;
	public boolean hasPassengers;
	public boolean waggingTail;
	public float eatingGrassAnimationProgress;
	public float angryAnimationProgress;
	public float eatingAnimationProgress;
}
