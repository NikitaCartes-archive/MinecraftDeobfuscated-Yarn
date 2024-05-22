package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DolphinEntityRenderer extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/dolphin.png");

	public DolphinEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new DolphinEntityModel<>(context.getPart(EntityModelLayers.DOLPHIN)), 0.7F);
		this.addFeature(new DolphinHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
	}

	public Identifier getTexture(DolphinEntity dolphinEntity) {
		return TEXTURE;
	}
}
