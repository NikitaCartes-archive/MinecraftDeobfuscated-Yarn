package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityRenderer extends EntityRenderer<EndCrystalEntity, EndCrystalEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
	private static final RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
	private final EndCrystalEntityModel model;

	public EndCrystalEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.model = new EndCrystalEntityModel(context.getPart(EntityModelLayers.END_CRYSTAL));
	}

	public void render(EndCrystalEntityRenderState endCrystalEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		matrixStack.translate(0.0F, -0.5F, 0.0F);
		this.model.setAngles(endCrystalEntityRenderState);
		this.model.render(matrixStack, vertexConsumerProvider.getBuffer(END_CRYSTAL), i, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		Vec3d vec3d = endCrystalEntityRenderState.beamOffset;
		if (vec3d != null) {
			float f = getYOffset(endCrystalEntityRenderState.age);
			float g = (float)vec3d.x;
			float h = (float)vec3d.y;
			float j = (float)vec3d.z;
			matrixStack.translate(vec3d);
			EnderDragonEntityRenderer.renderCrystalBeam(-g, -h + f, -j, endCrystalEntityRenderState.age, matrixStack, vertexConsumerProvider, i);
		}

		super.render(endCrystalEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public static float getYOffset(float f) {
		float g = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
		g = (g * g + g) * 0.4F;
		return g - 1.4F;
	}

	public EndCrystalEntityRenderState createRenderState() {
		return new EndCrystalEntityRenderState();
	}

	public void updateRenderState(EndCrystalEntity endCrystalEntity, EndCrystalEntityRenderState endCrystalEntityRenderState, float f) {
		super.updateRenderState(endCrystalEntity, endCrystalEntityRenderState, f);
		endCrystalEntityRenderState.age = (float)endCrystalEntity.endCrystalAge + f;
		endCrystalEntityRenderState.baseVisible = endCrystalEntity.shouldShowBottom();
		BlockPos blockPos = endCrystalEntity.getBeamTarget();
		if (blockPos != null) {
			endCrystalEntityRenderState.beamOffset = Vec3d.ofCenter(blockPos).subtract(endCrystalEntity.getLerpedPos(f));
		} else {
			endCrystalEntityRenderState.beamOffset = null;
		}
	}

	public boolean shouldRender(EndCrystalEntity endCrystalEntity, Frustum frustum, double d, double e, double f) {
		return super.shouldRender(endCrystalEntity, frustum, d, e, f) || endCrystalEntity.getBeamTarget() != null;
	}
}
