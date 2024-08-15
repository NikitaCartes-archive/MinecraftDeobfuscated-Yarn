package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitherSkullEntityRenderState extends EntityRenderState {
	public boolean charged;
	public float pitch;
	public float yaw;
}
