package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EndermiteEntityRenderer extends MobEntityRenderer<EndermiteEntity, EndermiteEntityModel<EndermiteEntity>> {
	private static final Identifier field_4671 = new Identifier("textures/entity/endermite.png");

	public EndermiteEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EndermiteEntityModel<>(), 0.3F);
	}

	protected float method_3919(EndermiteEntity endermiteEntity) {
		return 180.0F;
	}

	protected Identifier method_3920(EndermiteEntity endermiteEntity) {
		return field_4671;
	}
}
