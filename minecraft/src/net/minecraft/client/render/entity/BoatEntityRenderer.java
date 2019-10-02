package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderer extends EntityRenderer<BoatEntity> {
	private static final Identifier[] SKIN = new Identifier[]{
		new Identifier("textures/entity/boat/oak.png"),
		new Identifier("textures/entity/boat/spruce.png"),
		new Identifier("textures/entity/boat/birch.png"),
		new Identifier("textures/entity/boat/jungle.png"),
		new Identifier("textures/entity/boat/acacia.png"),
		new Identifier("textures/entity/boat/dark_oak.png")
	};
	protected final BoatEntityModel model = new BoatEntityModel();

	public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.8F;
	}

	public void method_3888(
		BoatEntity boatEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		matrixStack.translate(0.0, 0.375, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - g, true));
		float i = (float)boatEntity.getDamageWobbleTicks() - h;
		float j = boatEntity.getDamageWobbleStrength() - h;
		if (j < 0.0F) {
			j = 0.0F;
		}

		if (i > 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(MathHelper.sin(i) * i * j / 10.0F * (float)boatEntity.getDamageWobbleSide(), true));
		}

		float k = boatEntity.interpolateBubbleWobble(h);
		if (!MathHelper.approximatelyEquals(k, 0.0F)) {
			matrixStack.multiply(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), boatEntity.interpolateBubbleWobble(h), true));
		}

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		int l = boatEntity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(this.method_3891(boatEntity)));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0F, true));
		this.model.method_22952(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		this.model.method_22957(matrixStack, vertexConsumer, l);
		VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.WATER_MASK);
		this.model.method_22954().render(matrixStack, vertexConsumer2, 0.0625F, l, null);
		matrixStack.pop();
		vertexConsumer.clearDefaultOverlay();
		super.render(boatEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_3891(BoatEntity boatEntity) {
		return SKIN[boatEntity.getBoatType().ordinal()];
	}
}
