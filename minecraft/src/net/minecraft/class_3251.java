package net.minecraft;

import net.minecraft.server.MinecraftServer;

public class class_3251 implements class_2933 {
	private static final class_2561 field_14179 = new class_2588("multiplayer.status.request_handled");
	private final MinecraftServer field_14180;
	private final class_2535 field_14178;
	private boolean field_14177;

	public class_3251(MinecraftServer minecraftServer, class_2535 arg) {
		this.field_14180 = minecraftServer;
		this.field_14178 = arg;
	}

	@Override
	public void method_10839(class_2561 arg) {
	}

	@Override
	public void method_12698(class_2937 arg) {
		if (this.field_14177) {
			this.field_14178.method_10747(field_14179);
		} else {
			this.field_14177 = true;
			this.field_14178.method_10743(new class_2924(this.field_14180.method_3765()));
		}
	}

	@Override
	public void method_12697(class_2935 arg) {
		this.field_14178.method_10743(new class_2923(arg.method_12700()));
		this.field_14178.method_10747(field_14179);
	}
}
