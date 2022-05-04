package net.minecraft.client.render.entity.model;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public abstract class SinglePartEntityModel<E extends Entity> extends EntityModel<E> {
	private static final Vec3f field_39195 = new Vec3f();

	public SinglePartEntityModel() {
		this(RenderLayer::getEntityCutoutNoCull);
	}

	public SinglePartEntityModel(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	public abstract ModelPart getPart();

	public Optional<ModelPart> getChild(String name) {
		return this.getPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));
	}

	protected void updateAnimation(AnimationState animationState, Animation animation) {
		this.updateAnimation(animationState, animation, 1.0F);
	}

	protected void updateAnimation(AnimationState animationState, Animation animation, float f) {
		animationState.update(MinecraftClient.getInstance().isPaused(), f);
		animationState.run(animationStatex -> AnimationHelper.animate(this, animation, animationStatex.getTimeRunning(), 1.0F, field_39195));
	}
}
