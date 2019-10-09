package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/llama/spit.png");
	private final LlamaSpitEntityModel<LlamaSpitEntity> model = new LlamaSpitEntityModel<>();

	public LlamaSpitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4061(
		LlamaSpitEntity llamaSpitEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		matrixStack.translate(0.0, 0.15F, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.lerp(h, llamaSpitEntity.prevYaw, llamaSpitEntity.yaw) - 90.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.lerp(h, llamaSpitEntity.prevPitch, llamaSpitEntity.pitch)));
		int i = llamaSpitEntity.getLightmapCoordinates();
		this.model.setAngles(llamaSpitEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(this.model.method_23500(SKIN));
		this.model.renderItem(matrixStack, vertexConsumer, i, OverlayTexture.field_21444, 1.0F, 1.0F, 1.0F);
		matrixStack.pop();
		super.render(llamaSpitEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_4062(LlamaSpitEntity llamaSpitEntity) {
		return SKIN;
	}
}
