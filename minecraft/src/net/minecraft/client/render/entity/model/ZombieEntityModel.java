package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;

@Environment(EnvType.CLIENT)
public class ZombieEntityModel<S extends ZombieEntityRenderState> extends AbstractZombieModel<S> {
	public ZombieEntityModel(ModelPart modelPart) {
		super(modelPart);
	}
}
