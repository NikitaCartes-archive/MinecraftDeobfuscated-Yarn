package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1131 {
	private final String field_5515;
	private final String field_5517;
	private long field_5516;

	public class_1131(String string, String string2) {
		this.field_5515 = string;
		this.field_5517 = string2;
		this.field_5516 = class_156.method_658();
	}

	public String method_4813() {
		return this.field_5515;
	}

	public String method_4812() {
		return this.field_5517;
	}

	public void method_4814() {
		this.field_5516 = class_156.method_658();
	}
}
