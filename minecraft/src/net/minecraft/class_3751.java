package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3751 extends class_2586 {
	private class_2960 field_16550 = new class_2960("empty");
	private class_2960 field_16552 = new class_2960("empty");
	private String field_16551 = "minecraft:air";

	public class_3751(class_2591<?> arg) {
		super(arg);
	}

	public class_3751() {
		this(class_2591.field_16549);
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_16381() {
		return this.field_16550;
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_16382() {
		return this.field_16552;
	}

	@Environment(EnvType.CLIENT)
	public String method_16380() {
		return this.field_16551;
	}

	public void method_16379(class_2960 arg) {
		this.field_16550 = arg;
	}

	public void method_16378(class_2960 arg) {
		this.field_16552 = arg;
	}

	public void method_16377(String string) {
		this.field_16551 = string;
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10582("attachement_type", this.field_16550.toString());
		arg.method_10582("target_pool", this.field_16552.toString());
		arg.method_10582("final_state", this.field_16551);
		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_16550 = new class_2960(arg.method_10558("attachement_type"));
		this.field_16552 = new class_2960(arg.method_10558("target_pool"));
		this.field_16551 = arg.method_10558("final_state");
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 12, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}
}
