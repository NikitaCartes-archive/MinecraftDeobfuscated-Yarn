package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class CreakingEntityRenderState extends LivingEntityRenderState {
	public AnimationState invulnerableAnimationState = new AnimationState();
	public AnimationState attackAnimationState = new AnimationState();
	public boolean active;
	public boolean unrooted;
}
