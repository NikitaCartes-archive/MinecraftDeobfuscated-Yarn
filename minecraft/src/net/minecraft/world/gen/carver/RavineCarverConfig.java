package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_6122;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;

public class RavineCarverConfig extends CarverConfig {
	public static final Codec<RavineCarverConfig> RAVINE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CarverConfig.CONFIG_CODEC.forGetter(ravineCarverConfig -> ravineCarverConfig),
					FloatProvider.VALUE_CODEC.fieldOf("vertical_rotation").forGetter(ravineCarverConfig -> ravineCarverConfig.field_31479),
					RavineCarverConfig.class_6107.field_31481.fieldOf("shape").forGetter(ravineCarverConfig -> ravineCarverConfig.field_31480)
				)
				.apply(instance, RavineCarverConfig::new)
	);
	public final FloatProvider field_31479;
	public final RavineCarverConfig.class_6107 field_31480;

	public RavineCarverConfig(
		float f,
		class_6122 arg,
		FloatProvider floatProvider,
		YOffset yOffset,
		CarverDebugConfig carverDebugConfig,
		FloatProvider floatProvider2,
		RavineCarverConfig.class_6107 arg2
	) {
		super(f, arg, floatProvider, yOffset, carverDebugConfig);
		this.field_31479 = floatProvider2;
		this.field_31480 = arg2;
	}

	public RavineCarverConfig(CarverConfig carverConfig, FloatProvider floatProvider, RavineCarverConfig.class_6107 arg) {
		this(carverConfig.probability, carverConfig.field_31488, carverConfig.field_31489, carverConfig.field_31490, carverConfig.debugConfig, floatProvider, arg);
	}

	public static class class_6107 {
		public static final Codec<RavineCarverConfig.class_6107> field_31481 = RecordCodecBuilder.create(
			instance -> instance.group(
						FloatProvider.VALUE_CODEC.fieldOf("distance_factor").forGetter(arg -> arg.field_31482),
						FloatProvider.VALUE_CODEC.fieldOf("thickness").forGetter(arg -> arg.field_31483),
						Codec.intRange(0, Integer.MAX_VALUE).fieldOf("width_smoothness").forGetter(arg -> arg.field_31484),
						FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_factor").forGetter(arg -> arg.field_31485),
						Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter(arg -> arg.field_31486),
						Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter(arg -> arg.field_31487)
					)
					.apply(instance, RavineCarverConfig.class_6107::new)
		);
		public final FloatProvider field_31482;
		public final FloatProvider field_31483;
		public final int field_31484;
		public final FloatProvider field_31485;
		public final float field_31486;
		public final float field_31487;

		public class_6107(FloatProvider floatProvider, FloatProvider floatProvider2, int i, FloatProvider floatProvider3, float f, float g) {
			this.field_31484 = i;
			this.field_31485 = floatProvider3;
			this.field_31486 = f;
			this.field_31487 = g;
			this.field_31482 = floatProvider;
			this.field_31483 = floatProvider2;
		}
	}
}
