package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class class_5308 {
	public static final Codec<class_5308> field_24799 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.DOUBLE.fieldOf("xz_scale").forGetter(class_5308::method_28576),
					Codec.DOUBLE.fieldOf("y_scale").forGetter(class_5308::method_28578),
					Codec.DOUBLE.fieldOf("xz_factor").forGetter(class_5308::method_28579),
					Codec.DOUBLE.fieldOf("y_factor").forGetter(class_5308::method_28580)
				)
				.apply(instance, class_5308::new)
	);
	private final double field_24800;
	private final double field_24801;
	private final double field_24802;
	private final double field_24803;

	public class_5308(double d, double e, double f, double g) {
		this.field_24800 = d;
		this.field_24801 = e;
		this.field_24802 = f;
		this.field_24803 = g;
	}

	public double method_28576() {
		return this.field_24800;
	}

	public double method_28578() {
		return this.field_24801;
	}

	public double method_28579() {
		return this.field_24802;
	}

	public double method_28580() {
		return this.field_24803;
	}
}
