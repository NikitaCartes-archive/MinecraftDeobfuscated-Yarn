package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderer extends EntityMobRenderer<SheepEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SheepWoolEntityModel(), 0.7F);
		this.addLayer(new SheepWoolEntityRenderer(this));
	}

	protected Identifier getTexture(SheepEntity sheepEntity) {
		return SKIN;
	}
}
