package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.GiantEntity;

@Environment(EnvType.CLIENT)
public class GiantEntityModel extends AbstractZombieModel<GiantEntity> {
	public GiantEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public boolean isAttacking(GiantEntity giantEntity) {
		return false;
	}
}
