package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CowEntityRenderer extends MobEntityRenderer<CowEntity, CowEntityModel<CowEntity>> {
	private static final Identifier field_4651 = new Identifier("textures/entity/cow/cow.png");

	public CowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CowEntityModel<>(), 0.7F);
	}

	protected Identifier method_3895(CowEntity cowEntity) {
		return field_4651;
	}
}
