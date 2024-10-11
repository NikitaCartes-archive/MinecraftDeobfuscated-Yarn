package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WanderingTraderEntityRenderer extends MobEntityRenderer<WanderingTraderEntity, VillagerEntityRenderState, VillagerResemblingModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wandering_trader.png");

	public WanderingTraderEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.WANDERING_TRADER)), 0.5F);
		this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getItemRenderer()));
		this.addFeature(new VillagerHeldItemFeatureRenderer<>(this, context.getItemRenderer()));
	}

	public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
		return TEXTURE;
	}

	protected void scale(VillagerEntityRenderState villagerEntityRenderState, MatrixStack matrixStack) {
		float f = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}

	public VillagerEntityRenderState createRenderState() {
		return new VillagerEntityRenderState();
	}

	public void updateRenderState(WanderingTraderEntity wanderingTraderEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
		super.updateRenderState(wanderingTraderEntity, villagerEntityRenderState, f);
		villagerEntityRenderState.headRolling = wanderingTraderEntity.getHeadRollingTimeLeft() > 0;
	}
}
