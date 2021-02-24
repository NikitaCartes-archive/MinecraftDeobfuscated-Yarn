package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.YOffset;

public class class_5869 extends class_5871 {
	public static final Codec<class_5869> field_29041 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(arg -> arg.probability),
					class_5872.field_29056.optionalFieldOf("debug_settings", class_5872.field_29055).forGetter(class_5871::method_33969),
					YOffset.OFFSET_CODEC.fieldOf("bottom_inclusive").forGetter(class_5869::method_33947),
					YOffset.OFFSET_CODEC.fieldOf("top_inclusive").forGetter(class_5869::method_33950),
					UniformIntDistribution.CODEC.fieldOf("y_scale").forGetter(class_5869::method_33951),
					class_5863.method_33916(0.0F, 1.0F).fieldOf("distanceFactor").forGetter(class_5869::method_33952),
					class_5863.field_29007.fieldOf("vertical_rotation").forGetter(class_5869::method_33953),
					class_5863.field_29007.fieldOf("thickness").forGetter(class_5869::method_33954),
					Codec.intRange(0, Integer.MAX_VALUE).fieldOf("width_smoothness").forGetter(class_5869::method_33955),
					class_5863.field_29007.fieldOf("horizontal_radius_factor").forGetter(class_5869::method_33956),
					Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter(class_5869::method_33957),
					Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter(class_5869::method_33958)
				)
				.apply(instance, class_5869::new)
	);
	private final YOffset field_29042;
	private final YOffset field_29043;
	private final UniformIntDistribution field_29044;
	private final class_5863 field_29045;
	private final class_5863 field_29046;
	private final class_5863 field_29047;
	private final int field_29048;
	private final class_5863 field_29049;
	private final float field_29050;
	private final float field_29051;

	public class_5869(
		float f,
		class_5872 arg,
		YOffset yOffset,
		YOffset yOffset2,
		UniformIntDistribution uniformIntDistribution,
		class_5863 arg2,
		class_5863 arg3,
		class_5863 arg4,
		int i,
		class_5863 arg5,
		float g,
		float h
	) {
		super(f, arg);
		this.field_29042 = yOffset;
		this.field_29043 = yOffset2;
		this.field_29044 = uniformIntDistribution;
		this.field_29045 = arg2;
		this.field_29046 = arg3;
		this.field_29047 = arg4;
		this.field_29048 = i;
		this.field_29049 = arg5;
		this.field_29050 = g;
		this.field_29051 = h;
	}

	public YOffset method_33947() {
		return this.field_29042;
	}

	public YOffset method_33950() {
		return this.field_29043;
	}

	public UniformIntDistribution method_33951() {
		return this.field_29044;
	}

	public class_5863 method_33952() {
		return this.field_29045;
	}

	public class_5863 method_33953() {
		return this.field_29046;
	}

	public class_5863 method_33954() {
		return this.field_29047;
	}

	public int method_33955() {
		return this.field_29048;
	}

	public class_5863 method_33956() {
		return this.field_29049;
	}

	public float method_33957() {
		return this.field_29050;
	}

	public float method_33958() {
		return this.field_29051;
	}
}
