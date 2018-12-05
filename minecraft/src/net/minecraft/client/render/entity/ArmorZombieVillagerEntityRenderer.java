package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;

@Environment(EnvType.CLIENT)
public class ArmorZombieVillagerEntityRenderer extends ArmorBipedEntityRenderer {
	public ArmorZombieVillagerEntityRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected void init() {
		this.modelLeggings = new ZombieVillagerEntityModel(0.5F, 0.0F, true);
		this.modelBody = new ZombieVillagerEntityModel(1.0F, 0.0F, true);
	}
}
