package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;

/**
 * Represents the model of a {@linkplain GiantEntity}.
 * 
 * <p>Inherits the model of {@link AbstractZombieModel}.
 */
@Environment(EnvType.CLIENT)
public class GiantEntityModel extends AbstractZombieModel<ZombieEntityRenderState> {
	public GiantEntityModel(ModelPart modelPart) {
		super(modelPart);
	}
}
