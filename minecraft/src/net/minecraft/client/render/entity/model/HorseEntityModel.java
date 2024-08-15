package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;

@Environment(EnvType.CLIENT)
public class HorseEntityModel extends AbstractHorseEntityModel<LivingHorseEntityRenderState> {
	public HorseEntityModel(ModelPart modelPart) {
		super(modelPart);
	}
}
