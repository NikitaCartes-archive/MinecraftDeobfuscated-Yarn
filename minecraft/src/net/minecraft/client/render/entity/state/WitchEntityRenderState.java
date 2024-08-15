package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitchEntityRenderState extends LivingEntityRenderState {
	public int id;
	public boolean holdingItem;
}
