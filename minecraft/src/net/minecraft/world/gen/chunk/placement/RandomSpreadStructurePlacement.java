package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.AtomicSimpleRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

public class RandomSpreadStructurePlacement extends StructurePlacement {
	public static final Codec<RandomSpreadStructurePlacement> CODEC = RecordCodecBuilder.mapCodec(
			instance -> method_41637(instance)
					.<int, int, SpreadType>and(
						instance.group(
							Codec.intRange(0, 4096).fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::method_41632),
							Codec.intRange(0, 4096).fieldOf("separation").forGetter(RandomSpreadStructurePlacement::method_41633),
							SpreadType.CODEC.optionalFieldOf("spread_type", SpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::method_41634)
						)
					)
					.apply(instance, RandomSpreadStructurePlacement::new)
		)
		.<RandomSpreadStructurePlacement>flatXmap(
			placement -> placement.field_37772 <= placement.field_37773 ? DataResult.error("Spacing has to be larger than separation") : DataResult.success(placement),
			DataResult::success
		)
		.codec();
	private final int field_37772;
	private final int field_37773;
	private final SpreadType field_37774;

	public RandomSpreadStructurePlacement(
		Vec3i vec3i,
		StructurePlacement.FrequencyReductionMethod frequencyReductionMethod,
		float f,
		int i,
		Optional<StructurePlacement.ExclusionZone> optional,
		int j,
		int k,
		SpreadType spreadType
	) {
		super(vec3i, frequencyReductionMethod, f, i, optional);
		this.field_37772 = j;
		this.field_37773 = k;
		this.field_37774 = spreadType;
	}

	public RandomSpreadStructurePlacement(int spacing, int i, SpreadType spreadType, int j) {
		this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, j, Optional.empty(), spacing, i, spreadType);
	}

	public int method_41632() {
		return this.field_37772;
	}

	public int method_41633() {
		return this.field_37773;
	}

	public SpreadType method_41634() {
		return this.field_37774;
	}

	public ChunkPos getStartChunk(long seed, int x, int z) {
		int i = Math.floorDiv(x, this.field_37772);
		int j = Math.floorDiv(z, this.field_37772);
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(seed, i, j, this.getSalt());
		int k = this.field_37772 - this.field_37773;
		int l = this.field_37774.get(chunkRandom, k);
		int m = this.field_37774.get(chunkRandom, k);
		return new ChunkPos(i * this.field_37772 + l, j * this.field_37772 + m);
	}

	@Override
	protected boolean isStartChunk(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long seed, int x, int z) {
		ChunkPos chunkPos = this.getStartChunk(seed, x, z);
		return chunkPos.x == x && chunkPos.z == z;
	}

	@Override
	public StructurePlacementType<?> getType() {
		return StructurePlacementType.RANDOM_SPREAD;
	}
}
