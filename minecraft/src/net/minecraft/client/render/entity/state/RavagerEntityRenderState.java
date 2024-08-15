package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RavagerEntityRenderState extends LivingEntityRenderState {
	public float stunTick;
	public float attackTick;
	public float roarTick;
}
