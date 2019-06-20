package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_2190 extends class_2237 {
	private final class_2484.class_2485 field_9867;

	public class_2190(class_2484.class_2485 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_9867 = arg;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(class_2680 arg) {
		return true;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2631();
	}

	@Environment(EnvType.CLIENT)
	public class_2484.class_2485 method_9327() {
		return this.field_9867;
	}
}
