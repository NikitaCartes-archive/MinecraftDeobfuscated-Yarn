package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class WitherSkullEntityRenderer extends EntityRenderer<WitherSkullEntity> {
	private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");
	private final SkullEntityModel model = new SkullEntityModel();

	public WitherSkullEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4159(
		WitherSkullEntity witherSkullEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		float i = 0.0625F;
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		float j = MathHelper.method_22859(witherSkullEntity.prevYaw, witherSkullEntity.yaw, h);
		float k = MathHelper.lerp(h, witherSkullEntity.prevPitch, witherSkullEntity.pitch);
		int l = witherSkullEntity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(this.method_4160(witherSkullEntity)));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		this.model.render(matrixStack, vertexConsumer, 0.0F, j, k, 0.0625F, l);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.pop();
		super.render(witherSkullEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_4160(WitherSkullEntity witherSkullEntity) {
		return witherSkullEntity.isCharged() ? INVINCIBLE_SKIN : SKIN;
	}
}
