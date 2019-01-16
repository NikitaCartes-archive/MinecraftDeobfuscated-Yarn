package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public class Input {
	public float field_3907;
	public float field_3905;
	public boolean forward;
	public boolean back;
	public boolean left;
	public boolean right;
	public boolean jumping;
	public boolean sneaking;

	public void tick() {
	}

	public Vec2f method_3128() {
		return new Vec2f(this.field_3907, this.field_3905);
	}
}
