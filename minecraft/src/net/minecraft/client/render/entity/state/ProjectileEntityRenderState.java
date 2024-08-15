package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ProjectileEntityRenderState extends EntityRenderState {
	public float pitch;
	public float yaw;
	public float shake;
}
