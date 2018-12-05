package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity> {
	private static final Identifier VILLAGER_SKIN = new Identifier("textures/entity/zombie_villager/zombie_villager.png");
	private static final Identifier FARMER_SKIN = new Identifier("textures/entity/zombie_villager/zombie_farmer.png");
	private static final Identifier LIBRARIAN_SKIN = new Identifier("textures/entity/zombie_villager/zombie_librarian.png");
	private static final Identifier PRIEST_SKIN = new Identifier("textures/entity/zombie_villager/zombie_priest.png");
	private static final Identifier SMITH_SKIN = new Identifier("textures/entity/zombie_villager/zombie_smith.png");
	private static final Identifier BUTCHER_SKIN = new Identifier("textures/entity/zombie_villager/zombie_butcher.png");

	public ZombieVillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ZombieVillagerEntityModel(), 0.5F);
		this.addLayer(new ArmorZombieVillagerEntityRenderer(this));
	}

	protected Identifier getTexture(ZombieVillagerEntity zombieVillagerEntity) {
		switch (zombieVillagerEntity.getProfession()) {
			case 0:
				return FARMER_SKIN;
			case 1:
				return LIBRARIAN_SKIN;
			case 2:
				return PRIEST_SKIN;
			case 3:
				return SMITH_SKIN;
			case 4:
				return BUTCHER_SKIN;
			case 5:
			default:
				return VILLAGER_SKIN;
		}
	}

	protected void method_4176(ZombieVillagerEntity zombieVillagerEntity, float f, float g, float h) {
		if (zombieVillagerEntity.isConverting()) {
			g += (float)(Math.cos((double)zombieVillagerEntity.age * 3.25) * Math.PI * 0.25);
		}

		super.method_4058(zombieVillagerEntity, f, g, h);
	}
}
