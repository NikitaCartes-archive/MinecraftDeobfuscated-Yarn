package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public record RandomSpreadStructurePlacement(int spacing, int separation, SpreadType spreadType, int salt, Vec3i locateOffset) implements StructurePlacement {
	public static final Codec<RandomSpreadStructurePlacement> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.intRange(0, 4096).fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::spacing),
						Codec.intRange(0, 4096).fieldOf("separation").forGetter(RandomSpreadStructurePlacement::separation),
						SpreadType.CODEC.optionalFieldOf("spread_type", SpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType),
						Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(RandomSpreadStructurePlacement::salt),
						Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(RandomSpreadStructurePlacement::locateOffset)
					)
					.apply(instance, RandomSpreadStructurePlacement::new)
		)
		.<RandomSpreadStructurePlacement>flatXmap(
			placement -> placement.spacing <= placement.separation ? DataResult.error("Spacing has to be larger than separation") : DataResult.success(placement),
			DataResult::success
		)
		.codec();

	public RandomSpreadStructurePlacement(int spacing, int separation, SpreadType spreadType, int salt) {
		this(spacing, separation, spreadType, salt, Vec3i.ZERO);
	}

	public ChunkPos getStartChunk(long seed, int x, int z) {
		int i = this.spacing();
		int j = this.separation();
		int k = Math.floorDiv(x, i);
		int l = Math.floorDiv(z, i);
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(seed, k, l, this.salt());
		int m = i - j;
		int n = this.spreadType().get(chunkRandom, m);
		int o = this.spreadType().get(chunkRandom, m);
		return new ChunkPos(k * i + n, l * i + o);
	}

	@Override
	public boolean isStartChunk(ChunkGenerator chunkGenerator, int x, int z) {
		ChunkPos chunkPos = this.getStartChunk(chunkGenerator.getSeed(), x, z);
		return chunkPos.x == x && chunkPos.z == z;
	}

	@Override
	public StructurePlacementType<?> getType() {
		return StructurePlacementType.RANDOM_SPREAD;
	}
}
