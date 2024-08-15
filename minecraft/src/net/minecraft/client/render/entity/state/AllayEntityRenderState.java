package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AllayEntityRenderState extends LivingEntityRenderState {
	public boolean dancing;
	public boolean spinning;
	public float spinningAnimationTicks;
	public float itemHoldAnimationTicks;
}
