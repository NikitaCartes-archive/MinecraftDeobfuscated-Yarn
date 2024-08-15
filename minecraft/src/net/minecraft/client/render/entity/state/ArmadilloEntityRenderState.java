package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class ArmadilloEntityRenderState extends LivingEntityRenderState {
	public boolean rolledUp;
	public final AnimationState unrollingAnimationState = new AnimationState();
	public final AnimationState rollingAnimationState = new AnimationState();
	public final AnimationState scaredAnimationState = new AnimationState();
}
