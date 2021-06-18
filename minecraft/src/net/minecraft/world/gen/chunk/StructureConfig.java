package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.dynamic.Codecs;

/**
 * Contains parameters for placement of a single type of {@link net.minecraft.world.gen.feature.StructureFeature} during chunk
 * generation.
 */
public class StructureConfig {
	public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.intRange(0, 4096).fieldOf("spacing").forGetter(config -> config.spacing),
						Codec.intRange(0, 4096).fieldOf("separation").forGetter(config -> config.separation),
						Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(config -> config.salt)
					)
					.apply(instance, StructureConfig::new)
		)
		.comapFlatMap(
			config -> config.spacing <= config.separation ? DataResult.error("Spacing has to be smaller than separation") : DataResult.success(config),
			Function.identity()
		);
	/**
	 * Defines the width and height of a cell in the structure placement grid in chunks.
	 * <p>
	 * For each cell in the grid, the chunk generator will attempt to place the start of a structure.
	 * <p>
	 * Minimum is 1, which means the structure will potentially be placed in every chunk.
	 * 2 leads to one structure per 2x2 chunks, and so on.
	 */
	private final int spacing;
	/**
	 * Defines the margin of each cell in the placement grid, which leads to guaranteeing
	 * a certain minimum distance between each placed structure.
	 * <p>
	 * The margin is effectively subtracted from the width and height of a cell when
	 * trying to determine the actual starting chunk within the cell.
	 * <p>
	 * A value of 0 means that the structure can be placed in any chunk within a grid cell,
	 * which also means two structures from adjacent grid cells could be placed directly
	 * next to each other.
	 * <p>
	 * A value that equals {@link #spacing} - 1 will restrict placement of the structure to the
	 * chunk at 0,0 of the grid cell.
	 * <p>
	 * This value must be between 0 and {@link #spacing} - 1, and is expressed in chunks.
	 */
	private final int separation;
	/**
	 * Used together with the world seed and a grid cell's x,y coordinates to seed the RNG when deciding
	 * whether a structure should really be placed in a grid cell or not.
	 * This is used to avoid that two types of structures that have the same spacing always occur
	 * in the same chunks of their placement grid, and thus in the same world chunk.
	 */
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
