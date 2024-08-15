package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SlimeEntityRenderState extends LivingEntityRenderState {
	public float stretch;
	public int size = 1;
}
