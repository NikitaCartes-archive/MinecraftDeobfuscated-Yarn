package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2827 implements class_2596<class_2792> {
	private long field_12883;

	public class_2827() {
	}

	@Environment(EnvType.CLIENT)
	public class_2827(long l) {
		this.field_12883 = l;
	}

	public void method_12266(class_2792 arg) {
		arg.method_12082(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12883 = arg.readLong();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeLong(this.field_12883);
	}

	public long method_12267() {
		return this.field_12883;
	}
}
