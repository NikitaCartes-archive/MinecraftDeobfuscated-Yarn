package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class WardenEntityRenderState extends LivingEntityRenderState {
	public float tendrilPitch;
	public float heartPitch;
	public final AnimationState roaringAnimationState = new AnimationState();
	public final AnimationState sniffingAnimationState = new AnimationState();
	public final AnimationState emergingAnimationState = new AnimationState();
	public final AnimationState diggingAnimationState = new AnimationState();
	public final AnimationState attackingAnimationState = new AnimationState();
	public final AnimationState chargingSonicBoomAnimationState = new AnimationState();
}
