package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WolfEntityRenderer extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private static final Identifier WILD_SKIN = new Identifier("textures/entity/wolf/wolf.png");
	private static final Identifier TAMED_SKIN = new Identifier("textures/entity/wolf/wolf_tame.png");
	private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/wolf/wolf_angry.png");

	public WolfEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new WolfEntityModel<>(), 0.5F);
		this.addFeature(new WolfCollarFeatureRenderer(this));
	}

	protected float getAnimationProgress(WolfEntity wolfEntity, float f) {
		return wolfEntity.method_6714();
	}

	public void render(WolfEntity wolfEntity, double d, double e, double f, float g, float h) {
		if (wolfEntity.isFurWet()) {
			float i = wolfEntity.getBrightnessAtEyes() * wolfEntity.getFurWetBrightnessMultiplier(h);
			GlStateManager.color3f(i, i, i);
		}

		super.render(wolfEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(WolfEntity wolfEntity) {
		if (wolfEntity.isTamed()) {
			return TAMED_SKIN;
		} else {
			return wolfEntity.isAngry() ? ANGRY_SKIN : WILD_SKIN;
		}
	}
}
