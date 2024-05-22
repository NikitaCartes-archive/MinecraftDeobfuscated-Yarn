package net.minecraft.client.render.entity.model;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public abstract class SinglePartEntityModel<E extends Entity> extends EntityModel<E> {
	private static final Vector3f TEMP = new Vector3f();

	public SinglePartEntityModel() {
		this(RenderLayer::getEntityCutoutNoCull);
	}

	public SinglePartEntityModel(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.getPart().render(matrices, vertices, light, overlay, color);
	}

	public abstract ModelPart getPart();

	public Optional<ModelPart> getChild(String name) {
		return name.equals("root")
			? Optional.of(this.getPart())
			: this.getPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));
	}

	protected void updateAnimation(AnimationState animationState, Animation animation, float animationProgress) {
		this.updateAnimation(animationState, animation, animationProgress, 1.0F);
	}

	protected void animateMovement(Animation animation, float limbAngle, float limbDistance, float limbAngleScale, float limbDistanceScale) {
		long l = (long)(limbAngle * 50.0F * limbAngleScale);
		float f = Math.min(limbDistance * limbDistanceScale, 1.0F);
		AnimationHelper.animate(this, animation, l, f, TEMP);
	}

	protected void updateAnimation(AnimationState animationState, Animation animation, float animationProgress, float speedMultiplier) {
		animationState.update(animationProgress, speedMultiplier);
		animationState.run(state -> AnimationHelper.animate(this, animation, state.getTimeRunning(), 1.0F, TEMP));
	}

	protected void animate(Animation animation) {
		AnimationHelper.animate(this, animation, 0L, 1.0F, TEMP);
	}
}
