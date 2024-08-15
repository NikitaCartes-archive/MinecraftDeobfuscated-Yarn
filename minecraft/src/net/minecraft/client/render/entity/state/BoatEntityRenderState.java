package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.BoatEntity;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderState extends EntityRenderState {
	public float yaw;
	public int damageWobbleSide;
	public float damageWobbleTicks;
	public float damageWobbleStrength;
	public float bubbleWobble;
	public boolean submergedInWater;
	public BoatEntity.Type variant = BoatEntity.Type.OAK;
	public float leftPaddleAngle;
	public float rightPaddleAngle;
}
