package net.minecraft;

import net.minecraft.server.MinecraftServer;

public class class_3350 implements class_2165 {
	private final StringBuffer field_14404 = new StringBuffer();
	private final MinecraftServer field_14405;

	public class_3350(MinecraftServer minecraftServer) {
		this.field_14405 = minecraftServer;
	}

	public void method_14702() {
		this.field_14404.setLength(0);
	}

	public String method_14701() {
		return this.field_14404.toString();
	}

	public class_2168 method_14700() {
		class_3218 lv = this.field_14405.method_3847(class_2874.field_13072);
		return new class_2168(this, new class_243(lv.method_8395()), class_241.field_1340, lv, 4, "Recon", new class_2585("Rcon"), this.field_14405, null);
	}

	@Override
	public void method_9203(class_2561 arg) {
		this.field_14404.append(arg.getString());
	}

	@Override
	public boolean method_9200() {
		return true;
	}

	@Override
	public boolean method_9202() {
		return true;
	}

	@Override
	public boolean method_9201() {
		return this.field_14405.method_3732();
	}
}
