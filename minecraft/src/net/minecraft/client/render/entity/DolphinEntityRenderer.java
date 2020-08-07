package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DolphinEntityRenderer extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/dolphin.png");

	public DolphinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new DolphinEntityModel<>(), 0.7F);
		this.addFeature(new DolphinHeldItemFeatureRenderer(this));
	}

	public Identifier method_3903(DolphinEntity dolphinEntity) {
		return TEXTURE;
	}
}
