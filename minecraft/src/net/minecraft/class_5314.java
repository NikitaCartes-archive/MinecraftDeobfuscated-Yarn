package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class class_5314 {
	public static final Codec<class_5314> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						class_5324.method_29229(0, 4096).fieldOf("spacing").forGetter(arg -> arg.field_24918),
						class_5324.method_29229(0, 4096).fieldOf("separation").forGetter(arg -> arg.field_24919),
						class_5324.method_29229(0, Integer.MAX_VALUE).fieldOf("salt").forGetter(arg -> arg.field_24920)
					)
					.apply(instance, class_5314::new)
		)
		.comapFlatMap(
			arg -> arg.field_24918 <= arg.field_24919 ? DataResult.error("Spacing has to be smaller than separation") : DataResult.success(arg), Function.identity()
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
