package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnowmanEntityRenderer extends EntityMobRenderer<SnowmanEntity> {
	private static final Identifier field_4788 = new Identifier("textures/entity/snow_golem.png");

	public SnowmanEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SnowmanEntityModel(), 0.5F);
		this.addLayer(new SnowmanPumpkinEntityRenderer(this));
	}

	protected Identifier getTexture(SnowmanEntity snowmanEntity) {
		return field_4788;
	}

	public SnowmanEntityModel method_4121() {
		return (SnowmanEntityModel)super.method_4038();
	}
}
