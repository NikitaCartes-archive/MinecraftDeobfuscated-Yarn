package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonEntityRenderer extends BipedEntityRenderer<AbstractSkeletonEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new StrayEntityModel(), 0.5F);
		this.addLayer(new HeldItemEntityRenderer(this));
		this.addLayer(new ArmorBipedEntityRenderer(this) {
			@Override
			protected void init() {
				this.modelLeggings = new StrayEntityModel(0.5F, true);
				this.modelBody = new StrayEntityModel(1.0F, true);
			}
		});
	}

	protected Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return SKIN;
	}
}
