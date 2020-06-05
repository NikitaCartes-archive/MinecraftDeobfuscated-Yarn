package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.dynamic.NumberCodecs;

public class StructureConfig {
	public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						NumberCodecs.rangedInt(0, 4096).fieldOf("spacing").forGetter(structureConfig -> structureConfig.spacing),
						NumberCodecs.rangedInt(0, 4096).fieldOf("separation").forGetter(structureConfig -> structureConfig.separation),
						NumberCodecs.rangedInt(0, Integer.MAX_VALUE).fieldOf("salt").forGetter(structureConfig -> structureConfig.salt)
					)
					.apply(instance, StructureConfig::new)
		)
		.comapFlatMap(
			structureConfig -> structureConfig.spacing <= structureConfig.separation
					? DataResult.error("Spacing has to be smaller than separation")
					: DataResult.success(structureConfig),
			Function.identity()
		);
	private final int spacing;
	private final int separation;
	private final int salt;

	public StructureConfig(int spacing, int separation, int salt) {
		this.spacing = spacing;
		this.separation = separation;
		this.salt = salt;
	}

	public int getSpacing() {
		return this.spacing;
	}

	public int getSeparation() {
		return this.separation;
	}

	public int getSalt() {
		return this.salt;
	}
}
