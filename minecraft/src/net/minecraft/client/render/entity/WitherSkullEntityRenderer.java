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
import net.minecraft.client.render.entity.state.WitherSkullEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class WitherSkullEntityRenderer extends EntityRenderer<WitherSkullEntity, WitherSkullEntityRenderState> {
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

	public void render(WitherSkullEntityRenderState witherSkullEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(witherSkullEntityRenderState)));
		this.model.setHeadRotation(0.0F, witherSkullEntityRenderState.yaw, witherSkullEntityRenderState.pitch);
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		super.render(witherSkullEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	private Identifier getTexture(WitherSkullEntityRenderState state) {
		return state.charged ? INVULNERABLE_TEXTURE : TEXTURE;
	}

	public WitherSkullEntityRenderState getRenderState() {
		return new WitherSkullEntityRenderState();
	}

	public void updateRenderState(WitherSkullEntity witherSkullEntity, WitherSkullEntityRenderState witherSkullEntityRenderState, float f) {
		super.updateRenderState(witherSkullEntity, witherSkullEntityRenderState, f);
		witherSkullEntityRenderState.charged = witherSkullEntity.isCharged();
		witherSkullEntityRenderState.yaw = witherSkullEntity.getLerpedYaw(f);
		witherSkullEntityRenderState.pitch = witherSkullEntity.getLerpedPitch(f);
	}
}
