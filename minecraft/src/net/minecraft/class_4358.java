package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_4358 implements Runnable {
	protected class_4398 field_19638;

	public void method_21066(class_4398 arg) {
		this.field_19638 = arg;
	}

	public void method_21067(String string) {
		this.field_19638.method_21290(string);
	}

	public void method_21069(String string) {
		this.field_19638.method_21292(string);
	}

	public boolean method_21065() {
		return this.field_19638.method_21291();
	}

	public void method_21068() {
	}

	public void method_21070() {
	}

	public void method_21071() {
	}
}
