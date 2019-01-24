package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VindicatorEntityRenderer extends IllagerEntityRenderer<VindicatorEntity> {
	private static final Identifier field_4804 = new Identifier("textures/entity/illager/vindicator.png");

	public VindicatorEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EvilVillagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<VindicatorEntity, EvilVillagerEntityModel<VindicatorEntity>>(this) {
			public void method_17156(VindicatorEntity vindicatorEntity, float f, float g, float h, float i, float j, float k, float l) {
				if (vindicatorEntity.method_7169()) {
					super.method_17162(vindicatorEntity, f, g, h, i, j, k, l);
				}
			}
		});
	}

	protected Identifier method_4147(VindicatorEntity vindicatorEntity) {
		return field_4804;
	}
}
