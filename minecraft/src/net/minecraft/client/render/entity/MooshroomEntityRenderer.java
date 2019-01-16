package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MooshroomEntityRenderer extends MobEntityRenderer<MooshroomEntity, CowEntityModel<MooshroomEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/cow/mooshroom.png");

	public MooshroomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CowEntityModel<>(), 0.7F);
		this.addFeature(new MooshroomMushroomFeatureRenderer<>(this));
	}

	protected Identifier getTexture(MooshroomEntity mooshroomEntity) {
		return SKIN;
	}
}
