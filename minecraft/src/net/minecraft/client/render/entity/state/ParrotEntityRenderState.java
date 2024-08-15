package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.passive.ParrotEntity;

@Environment(EnvType.CLIENT)
public class ParrotEntityRenderState extends LivingEntityRenderState {
	public ParrotEntity.Variant variant = ParrotEntity.Variant.RED_BLUE;
	public float flapAngle;
	public ParrotEntityModel.Pose parrotPose = ParrotEntityModel.Pose.FLYING;
}
