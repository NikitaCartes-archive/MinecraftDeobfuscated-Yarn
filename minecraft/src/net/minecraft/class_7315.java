package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.util.Util;

public class class_7315 {
	private static final long field_38503 = Long.MAX_VALUE;
	private long field_38504 = Long.MAX_VALUE;

	public void method_42776() {
		this.field_38504 = Util.getMeasuringTimeMs();
	}

	public void method_42778() {
		if (!this.method_42781()) {
			this.method_42776();
		}
	}

	public void method_42779() {
		this.field_38504 = Long.MAX_VALUE;
	}

	public long method_42780() {
		return this.field_38504;
	}

	public void method_42777(Consumer<class_7315> consumer) {
		if (this.method_42781()) {
			consumer.accept(this);
		}
	}

	private boolean method_42781() {
		return this.field_38504 != Long.MAX_VALUE;
	}
}
