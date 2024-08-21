package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class TridentEntityRenderer extends EntityRenderer<TridentEntity, TridentEntityRenderState> {
	public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident.png");
	private final TridentEntityModel model;

	public TridentEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new TridentEntityModel(context.getPart(EntityModelLayers.TRIDENT));
	}

	public void render(TridentEntityRenderState tridentEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(tridentEntityRenderState.yaw - 90.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(tridentEntityRenderState.pitch + 90.0F));
		VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(
			vertexConsumerProvider, this.model.getLayer(this.getTexture(tridentEntityRenderState)), false, tridentEntityRenderState.enchanted
		);
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		super.render(tridentEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(TridentEntityRenderState tridentEntityRenderState) {
		return TEXTURE;
	}

	public TridentEntityRenderState getRenderState() {
		return new TridentEntityRenderState();
	}

	public void updateRenderState(TridentEntity tridentEntity, TridentEntityRenderState tridentEntityRenderState, float f) {
		super.updateRenderState(tridentEntity, tridentEntityRenderState, f);
		tridentEntityRenderState.yaw = tridentEntity.getLerpedYaw(f);
		tridentEntityRenderState.pitch = tridentEntity.getLerpedPitch(f);
		tridentEntityRenderState.enchanted = tridentEntity.isEnchanted();
	}
}
