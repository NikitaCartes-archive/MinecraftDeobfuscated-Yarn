package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AllayEntityRenderState;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class AllayEntityRenderer extends MobEntityRenderer<AllayEntity, AllayEntityRenderState, AllayEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/allay/allay.png");

	public AllayEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new AllayEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4F);
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getItemRenderer()));
	}

	public Identifier getTexture(AllayEntityRenderState allayEntityRenderState) {
		return TEXTURE;
	}

	public AllayEntityRenderState getRenderState() {
		return new AllayEntityRenderState();
	}

	public void updateRenderState(AllayEntity allayEntity, AllayEntityRenderState allayEntityRenderState, float f) {
		super.updateRenderState(allayEntity, allayEntityRenderState, f);
		allayEntityRenderState.dancing = allayEntity.isDancing();
		allayEntityRenderState.spinning = allayEntity.isSpinning();
		allayEntityRenderState.spinningAnimationTicks = allayEntity.getSpinningAnimationTicks(f);
		allayEntityRenderState.itemHoldAnimationTicks = allayEntity.getItemHoldAnimationTicks(f);
	}

	protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
		return 15;
	}
}
