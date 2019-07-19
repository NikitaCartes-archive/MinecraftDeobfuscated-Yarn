package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VindicatorEntityRenderer extends IllagerEntityRenderer<VindicatorEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/vindicator.png");

	public VindicatorEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IllagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<VindicatorEntity, IllagerEntityModel<VindicatorEntity>>(this) {
			public void render(VindicatorEntity vindicatorEntity, float f, float g, float h, float i, float j, float k, float l) {
				if (vindicatorEntity.isAttacking()) {
					super.render(vindicatorEntity, f, g, h, i, j, k, l);
				}
			}
		});
	}

	protected Identifier getTexture(VindicatorEntity vindicatorEntity) {
		return SKIN;
	}
}
