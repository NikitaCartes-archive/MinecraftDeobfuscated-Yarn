package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TurtleEntityRenderState extends LivingEntityRenderState {
	public boolean onLand;
	public boolean diggingSand;
	public boolean hasEgg;
}
