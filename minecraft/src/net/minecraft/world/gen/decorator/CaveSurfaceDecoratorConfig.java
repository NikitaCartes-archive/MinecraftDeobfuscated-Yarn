package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.VerticalSurfaceType;

public class CaveSurfaceDecoratorConfig implements DecoratorConfig {
	public static final Codec<CaveSurfaceDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					VerticalSurfaceType.CODEC.fieldOf("surface").forGetter(caveSurfaceDecoratorConfig -> caveSurfaceDecoratorConfig.surface),
					Codec.INT.fieldOf("floor_to_ceiling_search_range").forGetter(caveSurfaceDecoratorConfig -> caveSurfaceDecoratorConfig.searchRange),
					Codec.BOOL.fieldOf("allow_water").forGetter(caveSurfaceDecoratorConfig -> caveSurfaceDecoratorConfig.field_35422)
				)
				.apply(instance, CaveSurfaceDecoratorConfig::new)
	);
	public final VerticalSurfaceType surface;
	public final int searchRange;
	public final boolean field_35422;

	public CaveSurfaceDecoratorConfig(VerticalSurfaceType surface, int searchRange, boolean bl) {
		this.surface = surface;
		this.searchRange = searchRange;
		this.field_35422 = bl;
	}
}
