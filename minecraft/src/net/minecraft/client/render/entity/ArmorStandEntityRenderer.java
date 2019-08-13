package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityRenderer extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
	public static final Identifier SKIN = new Identifier("textures/entity/armorstand/wood.png");

	public ArmorStandEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ArmorStandEntityModel(), 0.0F);
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, new ArmorStandArmorEntityModel(0.5F), new ArmorStandArmorEntityModel(1.0F)));
		this.addFeature(new HeldItemFeatureRenderer<>(this));
		this.addFeature(new ElytraFeatureRenderer<>(this));
		this.addFeature(new HeadFeatureRenderer<>(this));
	}

	protected Identifier method_3880(ArmorStandEntity armorStandEntity) {
		return SKIN;
	}

	protected void method_3877(ArmorStandEntity armorStandEntity, float f, float g, float h) {
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		float i = (float)(armorStandEntity.world.getTime() - armorStandEntity.field_7112) + h;
		if (i < 5.0F) {
			GlStateManager.rotatef(MathHelper.sin(i / 1.5F * (float) Math.PI) * 3.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	protected boolean method_3878(ArmorStandEntity armorStandEntity) {
		return armorStandEntity.isCustomNameVisible();
	}

	public void method_3876(ArmorStandEntity armorStandEntity, double d, double e, double f, float g, float h) {
		if (armorStandEntity.isMarker()) {
			this.disableOutlineRender = true;
		}

		super.method_4054(armorStandEntity, d, e, f, g, h);
		if (armorStandEntity.isMarker()) {
			this.disableOutlineRender = false;
		}
	}
}
