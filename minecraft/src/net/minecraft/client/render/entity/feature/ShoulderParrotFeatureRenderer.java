package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

@Environment(EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	private final ParrotEntityModel model = new ParrotEntityModel();

	public ShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context) {
		super(context);
	}

	public void method_4185(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T playerEntity, float f, float g, float h, float j, float k, float l, float m
	) {
		this.renderShoulderParrot(matrixStack, vertexConsumerProvider, i, playerEntity, f, g, h, k, l, m, true);
		this.renderShoulderParrot(matrixStack, vertexConsumerProvider, i, playerEntity, f, g, h, k, l, m, false);
	}

	private void renderShoulderParrot(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		T playerEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		boolean bl
	) {
		CompoundTag compoundTag = bl ? playerEntity.getShoulderEntityLeft() : playerEntity.getShoulderEntityRight();
		EntityType.get(compoundTag.getString("id")).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> {
			matrixStack.push();
			matrixStack.translate(bl ? 0.4F : -0.4F, playerEntity.isInSneakingPose() ? -1.3F : -1.5, 0.0);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(ParrotEntityRenderer.SKINS[compoundTag.getInt("Variant")]));
			this.model.method_17106(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, f, g, j, k, l, playerEntity.age);
			matrixStack.pop();
		});
	}
}
