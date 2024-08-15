package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagerEntityRenderer extends MobEntityRenderer<VillagerEntity, VillagerEntityRenderState, VillagerResemblingModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/villager/villager.png");
	public static final HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(-0.1171875F, -0.07421875F, 1.0F);

	public VillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER)), 0.5F);
		this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), HEAD_TRANSFORMATION, context.getItemRenderer()));
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, context.getResourceManager(), "villager"));
		this.addFeature(new VillagerHeldItemFeatureRenderer<>(this, context.getItemRenderer()));
	}

	protected void scale(VillagerEntityRenderState villagerEntityRenderState, MatrixStack matrixStack) {
		super.scale(villagerEntityRenderState, matrixStack);
		float f = villagerEntityRenderState.ageScale;
		matrixStack.scale(f, f, f);
	}

	public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
		return TEXTURE;
	}

	protected float getShadowRadius(VillagerEntityRenderState villagerEntityRenderState) {
		float f = super.getShadowRadius(villagerEntityRenderState);
		return villagerEntityRenderState.baby ? f * 0.5F : f;
	}

	public VillagerEntityRenderState getRenderState() {
		return new VillagerEntityRenderState();
	}

	public void updateRenderState(VillagerEntity villagerEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
		super.updateRenderState(villagerEntity, villagerEntityRenderState, f);
		villagerEntityRenderState.headRolling = villagerEntity.getHeadRollingTimeLeft() > 0;
		villagerEntityRenderState.villagerData = villagerEntity.getVillagerData();
	}
}
