package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3969;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GiantEntityRenderer extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/zombie.png");
	private final float field_4711;

	public GiantEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher, new class_3969(), 0.5F * f);
		this.field_4711 = f;
		this.addFeature(new HeldItemFeatureRenderer<>(this));
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, new class_3969(0.5F, true), new class_3969(1.0F, true)));
	}

	protected void method_3980(GiantEntity giantEntity, float f) {
		GlStateManager.scalef(this.field_4711, this.field_4711, this.field_4711);
	}

	protected Identifier getTexture(GiantEntity giantEntity) {
		return SKIN;
	}
}
