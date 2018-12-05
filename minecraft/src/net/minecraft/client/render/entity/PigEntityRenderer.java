package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigEntityRenderer extends EntityMobRenderer<PigEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PigEntityModel(), 0.7F);
		this.addLayer(new PigSaddleEntityRenderer(this));
	}

	protected Identifier getTexture(PigEntity pigEntity) {
		return SKIN;
	}
}
