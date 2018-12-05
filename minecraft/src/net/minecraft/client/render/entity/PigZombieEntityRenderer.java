package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.PigZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigZombieEntityRenderer extends BipedEntityRenderer<PigZombieEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie_pigman.png");

	public PigZombieEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ZombieEntityModel(), 0.5F);
		this.addLayer(new ArmorBipedEntityRenderer(this) {
			@Override
			protected void init() {
				this.modelLeggings = new ZombieEntityModel(0.5F, true);
				this.modelBody = new ZombieEntityModel(1.0F, true);
			}
		});
	}

	protected Identifier getTexture(PigZombieEntity pigZombieEntity) {
		return SKIN;
	}
}
