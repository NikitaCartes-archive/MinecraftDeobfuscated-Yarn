package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StriderEntityRenderState extends LivingEntityRenderState implements SaddleableRenderState {
	public boolean saddled;
	public boolean cold;
	public boolean hasPassengers;

	@Override
	public boolean isSaddled() {
		return this.saddled;
	}
}
