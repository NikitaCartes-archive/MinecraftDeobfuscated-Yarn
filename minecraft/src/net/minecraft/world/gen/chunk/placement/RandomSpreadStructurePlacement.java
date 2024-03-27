package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;

public class RandomSpreadStructurePlacement extends StructurePlacement {
	public static final MapCodec<RandomSpreadStructurePlacement> CODEC = RecordCodecBuilder.<RandomSpreadStructurePlacement>mapCodec(
			instance -> buildCodec(instance)
					.<int, int, SpreadType>and(
						instance.group(
							Codec.intRange(0, 4096).fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::getSpacing),
							Codec.intRange(0, 4096).fieldOf("separation").forGetter(RandomSpreadStructurePlacement::getSeparation),
							SpreadType.CODEC.optionalFieldOf("spread_type", SpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::getSpreadType)
						)
					)
					.apply(instance, RandomSpreadStructurePlacement::new)
		)
		.validate(RandomSpreadStructurePlacement::validate);
	private final int spacing;
	private final int separation;
	private final SpreadType spreadType;

	private static DataResult<RandomSpreadStructurePlacement> validate(RandomSpreadStructurePlacement structurePlacement) {
		return structurePlacement.spacing <= structurePlacement.separation
			? DataResult.error(() -> "Spacing has to be larger than separation")
			: DataResult.success(structurePlacement);
	}

	public RandomSpreadStructurePlacement(
		Vec3i locateOffset,
		StructurePlacement.FrequencyReductionMethod frequencyReductionMethod,
		float frequency,
		int salt,
		Optional<StructurePlacement.ExclusionZone> exclusionZone,
		int spacing,
		int separation,
		SpreadType spreadType
	) {
		super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
		this.spacing = spacing;
		this.separation = separation;
		this.spreadType = spreadType;
	}

	public RandomSpreadStructurePlacement(int spacing, int separation, SpreadType spreadType, int salt) {
		this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, salt, Optional.empty(), spacing, separation, spreadType);
	}

	public int getSpacing() {
		return this.spacing;
	}

	public int getSeparation() {
		return this.separation;
	}

	public SpreadType getSpreadType() {
		return this.spreadType;
	}

	public ChunkPos getStartChunk(long seed, int chunkX, int chunkZ) {
		int i = Math.floorDiv(chunkX, this.spacing);
		int j = Math.floorDiv(chunkZ, this.spacing);
		ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
		chunkRandom.setRegionSeed(seed, i, j, this.getSalt());
		int k = this.spacing - this.separation;
		int l = this.spreadType.get(chunkRandom, k);
		int m = this.spreadType.get(chunkRandom, k);
		return new ChunkPos(i * this.spacing + l, j * this.spacing + m);
	}

	@Override
	protected boolean isStartChunk(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
		ChunkPos chunkPos = this.getStartChunk(calculator.getStructureSeed(), chunkX, chunkZ);
		return chunkPos.x == chunkX && chunkPos.z == chunkZ;
	}

	@Override
	public StructurePlacementType<?> getType() {
		return StructurePlacementType.RANDOM_SPREAD;
	}
}
