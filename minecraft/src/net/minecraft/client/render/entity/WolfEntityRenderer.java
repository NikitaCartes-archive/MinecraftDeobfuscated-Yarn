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

	protected float method_4167(WolfEntity wolfEntity, float f) {
		return wolfEntity.method_6714();
	}

	public void method_4166(WolfEntity wolfEntity, double d, double e, double f, float g, float h) {
		if (wolfEntity.method_6711()) {
			float i = wolfEntity.method_5718() * wolfEntity.method_6707(h);
			GlStateManager.color3f(i, i, i);
		}

		super.method_4072(wolfEntity, d, e, f, g, h);
	}

	protected Identifier method_4165(WolfEntity wolfEntity) {
		if (wolfEntity.isTamed()) {
			return TAMED_SKIN;
		} else {
			return wolfEntity.isAngry() ? ANGRY_SKIN : WILD_SKIN;
		}
	}
}
