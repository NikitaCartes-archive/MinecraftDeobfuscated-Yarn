package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3953 implements class_3949 {
	private final class_3951 field_17474;
	private final Long2ObjectOpenHashMap<class_2806> field_17475;
	private class_1923 field_17476 = new class_1923(0, 0);
	private final int field_17477;
	private final int field_17478;
	private final int field_17479;
	private boolean field_17480;

	public class_3953(int i) {
		this.field_17474 = new class_3951(i);
		this.field_17477 = i * 2 + 1;
		this.field_17478 = i + class_2806.method_12155();
		this.field_17479 = this.field_17478 * 2 + 1;
		this.field_17475 = new Long2ObjectOpenHashMap<>();
	}

	@Override
	public void method_17669(class_1923 arg) {
		if (this.field_17480) {
			this.field_17474.method_17669(arg);
			this.field_17476 = arg;
		}
	}

	@Override
	public void method_17670(class_1923 arg, @Nullable class_2806 arg2) {
		if (this.field_17480) {
			this.field_17474.method_17670(arg, arg2);
			if (arg2 == null) {
				this.field_17475.remove(arg.method_8324());
			} else {
				this.field_17475.put(arg.method_8324(), arg2);
			}
		}
	}

	public void method_17675() {
		this.field_17480 = true;
	}

	@Override
	public void method_17671() {
		this.field_17480 = false;
		this.field_17474.method_17671();
		this.field_17475.clear();
	}

	public int method_17677() {
		return this.field_17477;
	}

	public int method_17678() {
		return this.field_17479;
	}

	public int method_17679() {
		return this.field_17474.method_17672();
	}

	@Nullable
	public class_2806 method_17676(int i, int j) {
		return this.field_17475.get(class_1923.method_8331(i + this.field_17476.field_9181 - this.field_17478, j + this.field_17476.field_9180 - this.field_17478));
	}
}
