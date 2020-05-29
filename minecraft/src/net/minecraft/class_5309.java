package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class class_5309 {
	public static final Codec<class_5309> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					class_5324.method_29229(0, 256).fieldOf("height").forGetter(class_5309::method_28581),
					class_5308.field_24799.fieldOf("sampling").forGetter(class_5309::method_28583),
					class_5310.CODEC.fieldOf("top_slide").forGetter(class_5309::method_28584),
					class_5310.CODEC.fieldOf("bottom_slide").forGetter(class_5309::method_28585),
					class_5324.method_29229(1, 4).fieldOf("size_horizontal").forGetter(class_5309::method_28586),
					class_5324.method_29229(1, 4).fieldOf("size_vertical").forGetter(class_5309::method_28587),
					Codec.DOUBLE.fieldOf("density_factor").forGetter(class_5309::method_28588),
					Codec.DOUBLE.fieldOf("density_offset").forGetter(class_5309::method_28589),
					Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(class_5309::method_28590),
					Codec.BOOL.optionalFieldOf("random_density_offset", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(class_5309::method_28591),
					Codec.BOOL.optionalFieldOf("island_noise_override", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(class_5309::method_28592),
					Codec.BOOL.optionalFieldOf("amplified", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(class_5309::method_28593)
				)
				.apply(instance, class_5309::new)
	);
	private final int field_24805;
	private final class_5308 field_24806;
	private final class_5310 field_24807;
	private final class_5310 field_24808;
	private final int field_24809;
	private final int field_24810;
	private final double field_24811;
	private final double field_24812;
	private final boolean field_24813;
	private final boolean field_24814;
	private final boolean field_24815;
	private final boolean field_24816;

	public class_5309(int i, class_5308 arg, class_5310 arg2, class_5310 arg3, int j, int k, double d, double e, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		this.field_24805 = i;
		this.field_24806 = arg;
		this.field_24807 = arg2;
		this.field_24808 = arg3;
		this.field_24809 = j;
		this.field_24810 = k;
		this.field_24811 = d;
		this.field_24812 = e;
		this.field_24813 = bl;
		this.field_24814 = bl2;
		this.field_24815 = bl3;
		this.field_24816 = bl4;
	}

	public int method_28581() {
		return this.field_24805;
	}

	public class_5308 method_28583() {
		return this.field_24806;
	}

	public class_5310 method_28584() {
		return this.field_24807;
	}

	public class_5310 method_28585() {
		return this.field_24808;
	}

	public int method_28586() {
		return this.field_24809;
	}

	public int method_28587() {
		return this.field_24810;
	}

	public double method_28588() {
		return this.field_24811;
	}

	public double method_28589() {
		return this.field_24812;
	}

	@Deprecated
	public boolean method_28590() {
		return this.field_24813;
	}

	@Deprecated
	public boolean method_28591() {
		return this.field_24814;
	}

	@Deprecated
	public boolean method_28592() {
		return this.field_24815;
	}

	@Deprecated
	public boolean method_28593() {
		return this.field_24816;
	}
}
