package net.minecraft;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3951 implements class_3949 {
	private static final Logger field_17467 = LogManager.getLogger();
	private final int field_17468;
	private int field_17469;
	private long field_17470;
	private long field_17471 = Long.MAX_VALUE;

	public class_3951(int i) {
		int j = i * 2 + 1;
		this.field_17468 = j * j;
	}

	@Override
	public void method_17669(class_1923 arg) {
		this.field_17471 = class_156.method_658();
		this.field_17470 = this.field_17471;
	}

	@Override
	public void method_17670(class_1923 arg, @Nullable class_2806 arg2) {
		if (arg2 == class_2806.field_12803) {
			this.field_17469++;
		}

		int i = this.method_17672();
		if (class_156.method_658() > this.field_17471) {
			this.field_17471 += 500L;
			field_17467.info(new class_2588("menu.preparingSpawn", class_3532.method_15340(i, 0, 100)).getString());
		}
	}

	@Override
	public void method_17671() {
		field_17467.info("Time elapsed: {} ms", class_156.method_658() - this.field_17470);
		this.field_17471 = Long.MAX_VALUE;
	}

	public int method_17672() {
		return class_3532.method_15375((float)this.field_17469 * 100.0F / (float)this.field_17468);
	}
}
