package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FrogEntityRenderState extends LivingEntityRenderState {
	private static final Identifier DEFAULT_TEXTURE = Identifier.ofVanilla("textures/entity/frog/temperate_frog.png");
	public boolean insideWaterOrBubbleColumn;
	public final AnimationState longJumpingAnimationState = new AnimationState();
	public final AnimationState croakingAnimationState = new AnimationState();
	public final AnimationState usingTongueAnimationState = new AnimationState();
	public final AnimationState idlingInWaterAnimationState = new AnimationState();
	public Identifier texture = DEFAULT_TEXTURE;
}
