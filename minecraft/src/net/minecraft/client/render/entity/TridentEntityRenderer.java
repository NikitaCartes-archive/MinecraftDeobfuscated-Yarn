package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TridentEntityRenderer extends EntityRenderer<TridentEntity> {
	public static final Identifier SKIN = new Identifier("textures/entity/trident.png");
	private final TridentEntityModel model = new TridentEntityModel();

	public TridentEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4133(
		TridentEntity tridentEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider
	) {
		matrixStack.push();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.lerp(h, tridentEntity.prevYaw, tridentEntity.yaw) - 90.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.lerp(h, tridentEntity.prevPitch, tridentEntity.pitch) + 90.0F));
		int i = tridentEntity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.method_4134(tridentEntity)));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F);
		matrixStack.pop();
		super.render(tridentEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
	}

	public Identifier method_4134(TridentEntity tridentEntity) {
		return SKIN;
	}
}
