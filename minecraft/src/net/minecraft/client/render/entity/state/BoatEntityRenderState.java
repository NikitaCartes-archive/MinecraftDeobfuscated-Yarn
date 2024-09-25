package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderState extends EntityRenderState {
	public float yaw;
	public int damageWobbleSide;
	public float damageWobbleTicks;
	public float damageWobbleStrength;
	public float bubbleWobble;
	public boolean submergedInWater;
	public float leftPaddleAngle;
	public float rightPaddleAngle;
}
