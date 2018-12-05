package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OcelotEntityRenderer extends EntityMobRenderer<OcelotEntity> {
	private static final Identifier field_16259 = new Identifier("textures/entity/cat/ocelot.png");

	public OcelotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new OcelotEntityModel(0.0F), 0.4F);
	}

	protected Identifier method_16046(OcelotEntity ocelotEntity) {
		return field_16259;
	}
}
