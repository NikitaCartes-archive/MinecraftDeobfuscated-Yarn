package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.VillagerEntityModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagerEntityRenderer extends EntityMobRenderer<VillagerEntity> {
	private static final Identifier VILLAGER_SKIN = new Identifier("textures/entity/villager/villager.png");
	private static final Identifier FARMER_SKIN = new Identifier("textures/entity/villager/farmer.png");
	private static final Identifier LIBRARIAN_SKIN = new Identifier("textures/entity/villager/librarian.png");
	private static final Identifier PRIEST_SKIN = new Identifier("textures/entity/villager/priest.png");
	private static final Identifier BLACKSMITH_SKIN = new Identifier("textures/entity/villager/smith.png");
	private static final Identifier BUTCHER_SKIN = new Identifier("textures/entity/villager/butcher.png");

	public VillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VillagerEntityModel(0.0F), 0.5F);
		this.addLayer(new HeadEntityRenderer(this.method_4150().method_2838()));
	}

	public VillagerEntityModel method_4150() {
		return (VillagerEntityModel)super.method_4038();
	}

	protected Identifier getTexture(VillagerEntity villagerEntity) {
		switch (villagerEntity.getVillagerType()) {
			case 0:
				return FARMER_SKIN;
			case 1:
				return LIBRARIAN_SKIN;
			case 2:
				return PRIEST_SKIN;
			case 3:
				return BLACKSMITH_SKIN;
			case 4:
				return BUTCHER_SKIN;
			case 5:
			default:
				return VILLAGER_SKIN;
		}
	}

	protected void method_4149(VillagerEntity villagerEntity, float f) {
		float g = 0.9375F;
		if (villagerEntity.getBreedingAge() < 0) {
			g = (float)((double)g * 0.5);
			this.field_4673 = 0.25F;
		} else {
			this.field_4673 = 0.5F;
		}

		GlStateManager.scalef(g, g, g);
	}
}
