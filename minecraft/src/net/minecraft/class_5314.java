package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class class_5314 {
	public static final Codec<class_5314> field_24917 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("spacing").forGetter(arg -> arg.field_24918),
					Codec.INT.fieldOf("separation").forGetter(arg -> arg.field_24919),
					Codec.INT.fieldOf("salt").forGetter(arg -> arg.field_24920)
				)
				.apply(instance, class_5314::new)
	);
	private final int field_24918;
	private final int field_24919;
	private final int field_24920;

	public class_5314(int i, int j, int k) {
		this.field_24918 = i;
		this.field_24919 = j;
		this.field_24920 = k;
	}

	public int method_28803() {
		return this.field_24918;
	}

	public int method_28806() {
		return this.field_24919;
	}

	public int method_28808() {
		return this.field_24920;
	}
}
