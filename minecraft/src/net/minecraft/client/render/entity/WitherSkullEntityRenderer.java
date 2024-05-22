package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherSkullEntityRenderer extends EntityRenderer<WitherSkullEntity> {
	private static final Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither.png");
	private final SkullEntityModel model;

	public WitherSkullEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new SkullEntityModel(context.getPart(EntityModelLayers.WITHER_SKULL));
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 35).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 64);
	}

	protected int getBlockLight(WitherSkullEntity witherSkullEntity, BlockPos blockPos) {
		return 15;
	}

	public void render(WitherSkullEntity witherSkullEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		float h = MathHelper.lerpAngleDegrees(g, witherSkullEntity.prevYaw, witherSkullEntity.getYaw());
		float j = MathHelper.lerp(g, witherSkullEntity.prevPitch, witherSkullEntity.getPitch());
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(witherSkullEntity)));
		this.model.setHeadRotation(0.0F, h, j);
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		super.render(witherSkullEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(WitherSkullEntity witherSkullEntity) {
		return witherSkullEntity.isCharged() ? INVULNERABLE_TEXTURE : TEXTURE;
	}
}
