package net.minecraft.client.model;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

/**
 * Represents a dynamic model which has its own render layers and custom rendering.
 */
@Environment(EnvType.CLIENT)
public abstract class Model {
	private static final Vector3f ANIMATION_VEC = new Vector3f();
	protected final Function<Identifier, RenderLayer> layerFactory;

	public Model(Function<Identifier, RenderLayer> layerFactory) {
		this.layerFactory = layerFactory;
	}

	/**
	 * {@return the render layer for the corresponding texture}
	 * 
	 * @param texture the texture used for the render layer
	 */
	public final RenderLayer getLayer(Identifier texture) {
		return (RenderLayer)this.layerFactory.apply(texture);
	}

	public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.getPart().render(matrices, vertices, light, overlay, color);
	}

	public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.render(matrices, vertices, light, overlay, -1);
	}

	public abstract ModelPart getPart();

	public Optional<ModelPart> getPart(String name) {
		return name.equals("root")
			? Optional.of(this.getPart())
			: this.getPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(partx -> partx.getChild(name));
	}

	protected void animate(AnimationState animationState, Animation animation, float age) {
		this.animate(animationState, animation, age, 1.0F);
	}

	protected void animateWalking(Animation animation, float limbFrequency, float limbAmplitudeModifier, float f, float g) {
		long l = (long)(limbFrequency * 50.0F * f);
		float h = Math.min(limbAmplitudeModifier * g, 1.0F);
		AnimationHelper.animate(this, animation, l, h, ANIMATION_VEC);
	}

	protected void animate(AnimationState animationState, Animation animation, float age, float speedMultiplier) {
		animationState.run(state -> AnimationHelper.animate(this, animation, (long)((float)state.getTimeInMilliseconds(age) * speedMultiplier), 1.0F, ANIMATION_VEC));
	}

	protected void animate(Animation animation) {
		AnimationHelper.animate(this, animation, 0L, 1.0F, ANIMATION_VEC);
	}

	@Environment(EnvType.CLIENT)
	public static class SinglePartModel extends Model {
		private final ModelPart part;

		public SinglePartModel(ModelPart part, Function<Identifier, RenderLayer> layerFactory) {
			super(layerFactory);
			this.part = part;
		}

		@Override
		public ModelPart getPart() {
			return this.part;
		}
	}
}
