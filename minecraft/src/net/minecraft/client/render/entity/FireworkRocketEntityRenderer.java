package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.FireworkRocketEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class FireworkRocketEntityRenderer extends EntityRenderer<FireworkRocketEntity, FireworkRocketEntityRenderState> {
	private final ItemRenderer itemRenderer;

	public FireworkRocketEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	public void render(
		FireworkRocketEntityRenderState fireworkRocketEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		matrixStack.push();
		matrixStack.multiply(this.dispatcher.getRotation());
		if (fireworkRocketEntityRenderState.shotAtAngle) {
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
		}

		this.itemRenderer
			.renderItem(
				fireworkRocketEntityRenderState.stack,
				ModelTransformationMode.GROUND,
				false,
				matrixStack,
				vertexConsumerProvider,
				i,
				OverlayTexture.DEFAULT_UV,
				fireworkRocketEntityRenderState.model
			);
		matrixStack.pop();
		super.render(fireworkRocketEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(FireworkRocketEntityRenderState fireworkRocketEntityRenderState) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public FireworkRocketEntityRenderState getRenderState() {
		return new FireworkRocketEntityRenderState();
	}

	public void updateRenderState(FireworkRocketEntity fireworkRocketEntity, FireworkRocketEntityRenderState fireworkRocketEntityRenderState, float f) {
		super.updateRenderState(fireworkRocketEntity, fireworkRocketEntityRenderState, f);
		fireworkRocketEntityRenderState.shotAtAngle = fireworkRocketEntity.wasShotAtAngle();
		fireworkRocketEntityRenderState.stack = fireworkRocketEntity.getStack();
		fireworkRocketEntityRenderState.model = !fireworkRocketEntityRenderState.stack.isEmpty()
			? this.itemRenderer.getModel(fireworkRocketEntityRenderState.stack, fireworkRocketEntity.getWorld(), null, fireworkRocketEntity.getId())
			: null;
	}
}
