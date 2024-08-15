package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SquidEntityRenderState extends LivingEntityRenderState {
	public float tentacleAngle;
	public float tiltAngle;
	public float rollAngle;
}
