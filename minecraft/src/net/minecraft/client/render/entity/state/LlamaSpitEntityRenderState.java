package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderState extends EntityRenderState {
	public float yaw;
	public float pitch;
}
