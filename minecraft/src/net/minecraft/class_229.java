package net.minecraft;

import net.minecraft.server.MinecraftServer;

public class class_229 implements class_234<MinecraftServer> {
	private final class_2960 field_1303;

	public class_229(class_2960 arg) {
		this.field_1303 = arg;
	}

	public void method_962(MinecraftServer minecraftServer, class_236<MinecraftServer> arg, long l) {
		class_2991 lv = minecraftServer.method_3740();
		class_3494<class_2158> lv2 = lv.method_12901().method_15188(this.field_1303);

		for (class_2158 lv3 : lv2.method_15138()) {
			lv.method_12904(lv3, lv.method_12899());
		}
	}

	public static class class_230 extends class_234.class_235<MinecraftServer, class_229> {
		public class_230() {
			super(new class_2960("function_tag"), class_229.class);
		}

		public void method_964(class_2487 arg, class_229 arg2) {
			arg.method_10582("Name", arg2.field_1303.toString());
		}

		public class_229 method_965(class_2487 arg) {
			class_2960 lv = new class_2960(arg.method_10558("Name"));
			return new class_229(lv);
		}
	}
}
