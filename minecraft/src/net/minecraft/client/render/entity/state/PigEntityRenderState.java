package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PigEntityRenderState extends LivingEntityRenderState implements SaddleableRenderState {
	public boolean saddled;

	@Override
	public boolean isSaddled() {
		return this.saddled;
	}
}
