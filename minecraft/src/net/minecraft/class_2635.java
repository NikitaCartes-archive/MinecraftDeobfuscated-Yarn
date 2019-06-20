package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2635 implements class_2596<class_2602> {
	private class_2561 field_12112;
	private class_2556 field_12113;

	public class_2635() {
	}

	public class_2635(class_2561 arg) {
		this(arg, class_2556.field_11735);
	}

	public class_2635(class_2561 arg, class_2556 arg2) {
		this.field_12112 = arg;
		this.field_12113 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12112 = arg.method_10808();
		this.field_12113 = class_2556.method_10842(arg.readByte());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10805(this.field_12112);
		arg.writeByte(this.field_12113.method_10843());
	}

	public void method_11386(class_2602 arg) {
		arg.method_11121(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11388() {
		return this.field_12112;
	}

	public boolean method_11387() {
		return this.field_12113 == class_2556.field_11735 || this.field_12113 == class_2556.field_11733;
	}

	public class_2556 method_11389() {
		return this.field_12113;
	}

	@Override
	public boolean method_11051() {
		return true;
	}
}
