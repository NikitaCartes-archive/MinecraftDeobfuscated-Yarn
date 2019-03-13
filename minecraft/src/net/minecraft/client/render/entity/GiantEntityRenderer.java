package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GiantEntityRenderer extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>> {
	private static final Identifier field_4710 = new Identifier("textures/entity/zombie/zombie.png");
	private final float field_4711;

	public GiantEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher, new GiantEntityModel(), 0.5F * f);
		this.field_4711 = f;
		this.method_4046(new HeldItemFeatureRenderer<>(this));
		this.method_4046(new ArmorBipedFeatureRenderer<>(this, new GiantEntityModel(0.5F, true), new GiantEntityModel(1.0F, true)));
	}

	protected void method_3980(GiantEntity giantEntity, float f) {
		GlStateManager.scalef(this.field_4711, this.field_4711, this.field_4711);
	}

	protected Identifier method_3981(GiantEntity giantEntity) {
		return field_4710;
	}
}
