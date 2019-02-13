package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_766 {
	private final MinecraftClient field_4139;
	private final class_751 field_4141;
	private float field_4140;

	public class_766(class_751 arg) {
		this.field_4141 = arg;
		this.field_4139 = MinecraftClient.getInstance();
	}

	public void method_3317(float f, float g) {
		this.field_4140 += f;
		this.field_4141.method_3156(this.field_4139, MathHelper.sin(this.field_4140 * 0.001F) * 5.0F + 25.0F, -this.field_4140 * 0.1F, g);
		this.field_4139.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
	}
}
