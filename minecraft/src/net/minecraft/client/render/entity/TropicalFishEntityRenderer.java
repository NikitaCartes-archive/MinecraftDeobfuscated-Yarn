package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.TropicalFishSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends MobEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
	private final TropicalFishEntityModelA<TropicalFishEntity> field_4800 = new TropicalFishEntityModelA<>();
	private final TropicalFishEntityModelB<TropicalFishEntity> field_4799 = new TropicalFishEntityModelB<>();

	public TropicalFishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new TropicalFishEntityModelA<>(), 0.15F);
		this.addFeature(new TropicalFishSomethingFeatureRenderer(this));
	}

	@Nullable
	protected Identifier getTexture(TropicalFishEntity tropicalFishEntity) {
		return tropicalFishEntity.getShapeId();
	}

	public void render(TropicalFishEntity tropicalFishEntity, double d, double e, double f, float g, float h) {
		this.model = (EntityModel<TropicalFishEntity>)(tropicalFishEntity.getShape() == 0 ? this.field_4800 : this.field_4799);
		float[] fs = tropicalFishEntity.getBaseColorComponents();
		GlStateManager.color3f(fs[0], fs[1], fs[2]);
		super.render(tropicalFishEntity, d, e, f, g, h);
	}

	protected void setupTransforms(TropicalFishEntity tropicalFishEntity, float f, float g, float h) {
		super.setupTransforms(tropicalFishEntity, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		GlStateManager.rotatef(i, 0.0F, 1.0F, 0.0F);
		if (!tropicalFishEntity.isTouchingWater()) {
			GlStateManager.translatef(0.2F, 0.1F, 0.0F);
			GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
