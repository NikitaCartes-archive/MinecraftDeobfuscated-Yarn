package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_750 {
	private final class_287[] field_3951 = new class_287[class_1921.values().length];

	public class_750() {
		this.field_3951[class_1921.field_9178.ordinal()] = new class_287(2097152);
		this.field_3951[class_1921.field_9174.ordinal()] = new class_287(131072);
		this.field_3951[class_1921.field_9175.ordinal()] = new class_287(131072);
		this.field_3951[class_1921.field_9179.ordinal()] = new class_287(262144);
	}

	public class_287 method_3154(class_1921 arg) {
		return this.field_3951[arg.ordinal()];
	}

	public class_287 method_3155(int i) {
		return this.field_3951[i];
	}
}
