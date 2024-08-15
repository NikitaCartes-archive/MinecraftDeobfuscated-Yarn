package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class BreezeEntityRenderState extends LivingEntityRenderState {
	public final AnimationState shootingAnimationState = new AnimationState();
	public final AnimationState slidingAnimationState = new AnimationState();
	public final AnimationState slidingBackAnimationState = new AnimationState();
	public final AnimationState inhalingAnimationState = new AnimationState();
	public final AnimationState longJumpingAnimationState = new AnimationState();
}
