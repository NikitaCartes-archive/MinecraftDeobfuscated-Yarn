package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChickenEntityRenderState extends LivingEntityRenderState {
	public float flapProgress;
	public float maxWingDeviation;
}
