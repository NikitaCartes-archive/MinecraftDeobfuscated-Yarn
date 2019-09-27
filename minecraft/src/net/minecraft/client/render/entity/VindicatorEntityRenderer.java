package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VindicatorEntityRenderer extends IllagerEntityRenderer<VindicatorEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/vindicator.png");

	public VindicatorEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EvilVillagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<VindicatorEntity, EvilVillagerEntityModel<VindicatorEntity>>(this) {
				public void method_17156(
					class_4587 arg, class_4597 arg2, int i, VindicatorEntity vindicatorEntity, float f, float g, float h, float j, float k, float l, float m
				) {
					if (vindicatorEntity.isAttacking()) {
						super.method_17162(arg, arg2, i, vindicatorEntity, f, g, h, j, k, l, m);
					}
				}
			}
		);
	}

	public Identifier method_4147(VindicatorEntity vindicatorEntity) {
		return SKIN;
	}
}
