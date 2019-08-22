package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RavagerEntityRenderer extends MobEntityRenderer<RavagerEntity, RavagerEntityModel> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/ravager.png");

	public RavagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new RavagerEntityModel(), 1.1F);
	}

	protected Identifier method_3984(RavagerEntity ravagerEntity) {
		return SKIN;
	}
}
