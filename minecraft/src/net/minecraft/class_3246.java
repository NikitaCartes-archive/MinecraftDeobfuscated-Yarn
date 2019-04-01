package net.minecraft;

import net.minecraft.server.MinecraftServer;

public class class_3246 implements class_2890 {
	private final MinecraftServer field_14154;
	private final class_2535 field_14153;

	public class_3246(MinecraftServer minecraftServer, class_2535 arg) {
		this.field_14154 = minecraftServer;
		this.field_14153 = arg;
	}

	@Override
	public void method_12576(class_2889 arg) {
		switch (arg.method_12573()) {
			case field_11688:
				this.field_14153.method_10750(class_2539.field_11688);
				if (arg.method_12574() > class_155.method_16673().getProtocolVersion()) {
					class_2561 lv = new class_2588("multiplayer.disconnect.outdated_server", class_155.method_16673().getName());
					this.field_14153.method_10743(new class_2909(lv));
					this.field_14153.method_10747(lv);
				} else if (arg.method_12574() < class_155.method_16673().getProtocolVersion()) {
					class_2561 lv = new class_2588("multiplayer.disconnect.outdated_client", class_155.method_16673().getName());
					this.field_14153.method_10743(new class_2909(lv));
					this.field_14153.method_10747(lv);
				} else {
					this.field_14153.method_10763(new class_3248(this.field_14154, this.field_14153));
				}
				break;
			case field_11691:
				this.field_14153.method_10750(class_2539.field_11691);
				this.field_14153.method_10763(new class_3251(this.field_14154, this.field_14153));
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + arg.method_12573());
		}
	}

	@Override
	public void method_10839(class_2561 arg) {
	}
}
