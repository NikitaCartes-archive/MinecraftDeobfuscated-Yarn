package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityRenderState extends BipedEntityRenderState {
	public float yaw;
	public float timeSinceLastHit;
	public boolean marker;
	public boolean small;
	public boolean showArms;
	public boolean hideBasePlate = true;
	public EulerAngle headRotation = ArmorStandEntity.DEFAULT_HEAD_ROTATION;
	public EulerAngle bodyRotation = ArmorStandEntity.DEFAULT_BODY_ROTATION;
	public EulerAngle leftArmRotation = ArmorStandEntity.DEFAULT_LEFT_ARM_ROTATION;
	public EulerAngle rightArmRotation = ArmorStandEntity.DEFAULT_RIGHT_ARM_ROTATION;
	public EulerAngle leftLegRotation = ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION;
	public EulerAngle rightLegRotation = ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION;
}
