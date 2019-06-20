package net.minecraft;

import net.minecraft.server.MinecraftServer;

public class class_231 implements class_234<MinecraftServer> {
	private final class_2960 field_1304;

	public class_231(class_2960 arg) {
		this.field_1304 = arg;
	}

	public void method_967(MinecraftServer minecraftServer, class_236<MinecraftServer> arg, long l) {
		class_2991 lv = minecraftServer.method_3740();
		lv.method_12905(this.field_1304).ifPresent(arg2 -> lv.method_12904(arg2, lv.method_12899()));
	}

	public static class class_232 extends class_234.class_235<MinecraftServer, class_231> {
		public class_232() {
			super(new class_2960("function"), class_231.class);
		}

		public void method_968(class_2487 arg, class_231 arg2) {
			arg.method_10582("Name", arg2.field_1304.toString());
		}

		public class_231 method_969(class_2487 arg) {
			class_2960 lv = new class_2960(arg.method_10558("Name"));
			return new class_231(lv);
		}
	}
}
