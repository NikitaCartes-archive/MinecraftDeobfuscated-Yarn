package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ZombieEntityRenderState extends BipedEntityRenderState {
	public boolean attacking;
	public boolean convertingInWater;
}
