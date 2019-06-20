package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.CLIENT)
public class class_3240 implements class_2890 {
	private final MinecraftServer field_14104;
	private final class_2535 field_14103;

	public class_3240(MinecraftServer minecraftServer, class_2535 arg) {
		this.field_14104 = minecraftServer;
		this.field_14103 = arg;
	}

	@Override
	public void method_12576(class_2889 arg) {
		this.field_14103.method_10750(arg.method_12573());
		this.field_14103.method_10763(new class_3248(this.field_14104, this.field_14103));
	}

	@Override
	public void method_10839(class_2561 arg) {
	}
}
