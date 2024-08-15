package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityRenderer extends AgeableMobEntityRenderer<WolfEntity, WolfEntityRenderState, WolfEntityModel> {
	public WolfEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WolfEntityModel(context.getPart(EntityModelLayers.WOLF)), new WolfEntityModel(context.getPart(EntityModelLayers.WOLF_BABY)), 0.5F);
		this.addFeature(new WolfArmorFeatureRenderer(this, context.getModelLoader()));
		this.addFeature(new WolfCollarFeatureRenderer(this));
	}

	protected int getMixColor(WolfEntityRenderState wolfEntityRenderState) {
		float f = wolfEntityRenderState.furWetBrightnessMultiplier;
		return f == 1.0F ? -1 : ColorHelper.fromFloats(1.0F, f, f, f);
	}

	public Identifier getTexture(WolfEntityRenderState wolfEntityRenderState) {
		return wolfEntityRenderState.texture;
	}

	public WolfEntityRenderState getRenderState() {
		return new WolfEntityRenderState();
	}

	public void updateRenderState(WolfEntity wolfEntity, WolfEntityRenderState wolfEntityRenderState, float f) {
		super.updateRenderState(wolfEntity, wolfEntityRenderState, f);
		wolfEntityRenderState.angerTime = wolfEntity.hasAngerTime();
		wolfEntityRenderState.inSittingPose = wolfEntity.isInSittingPose();
		wolfEntityRenderState.tailAngle = wolfEntity.getTailAngle();
		wolfEntityRenderState.begAnimationProgress = wolfEntity.getBegAnimationProgress(f);
		wolfEntityRenderState.shakeProgress = wolfEntity.getShakeProgress(f);
		wolfEntityRenderState.texture = wolfEntity.getTextureId();
		wolfEntityRenderState.furWetBrightnessMultiplier = wolfEntity.getFurWetBrightnessMultiplier(f);
		wolfEntityRenderState.collarColor = wolfEntity.isTamed() ? wolfEntity.getCollarColor() : null;
		wolfEntityRenderState.bodyArmor = wolfEntity.getBodyArmor().copy();
	}
}
