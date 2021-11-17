package net.minecraft.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSupplier;

public final class BelowZeroRetrogen {
	private static final BitSet EMPTY_MISSING_BEDROCK_BIT_SET = new BitSet(0);
	private static final Codec<BitSet> MISSING_BEDROCK_CODEC = Codec.LONG_STREAM
		.xmap(longStream -> BitSet.valueOf(longStream.toArray()), bitSet -> LongStream.of(bitSet.toLongArray()));
	private static final Codec<ChunkStatus> STATUS_CODEC = Registry.CHUNK_STATUS
		.getCodec()
		.comapFlatMap(
			chunkStatus -> chunkStatus == ChunkStatus.EMPTY ? DataResult.error("target_status cannot be empty") : DataResult.success(chunkStatus), Function.identity()
		);
	public static final Codec<BelowZeroRetrogen> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					STATUS_CODEC.fieldOf("target_status").forGetter(BelowZeroRetrogen::getTargetStatus),
					MISSING_BEDROCK_CODEC.optionalFieldOf("missing_bedrock")
						.forGetter(belowZeroRetrogen -> belowZeroRetrogen.missingBedrock.isEmpty() ? Optional.empty() : Optional.of(belowZeroRetrogen.missingBedrock))
				)
				.apply(instance, BelowZeroRetrogen::new)
	);
	private static final Set<RegistryKey<Biome>> CAVE_BIOMES = Set.of(BiomeKeys.LUSH_CAVES, BiomeKeys.DRIPSTONE_CAVES);
	public static final HeightLimitView BELOW_ZERO_VIEW = new HeightLimitView() {
		@Override
		public int getHeight() {
			return 64;
		}

		@Override
		public int getBottomY() {
			return -64;
		}
	};
	private final ChunkStatus targetStatus;
	private final BitSet missingBedrock;

	private BelowZeroRetrogen(ChunkStatus targetStatus, Optional<BitSet> missingBedrock) {
		this.targetStatus = targetStatus;
		this.missingBedrock = (BitSet)missingBedrock.orElse(EMPTY_MISSING_BEDROCK_BIT_SET);
	}

	@Nullable
	public static BelowZeroRetrogen fromNbt(NbtCompound nbt) {
		ChunkStatus chunkStatus = ChunkStatus.byId(nbt.getString("target_status"));
		return chunkStatus == ChunkStatus.EMPTY ? null : new BelowZeroRetrogen(chunkStatus, Optional.of(BitSet.valueOf(nbt.getLongArray("missing_bedrock"))));
	}

	public static void replaceOldBedrock(ProtoChunk chunk) {
		int i = 4;
		BlockPos.iterate(0, 0, 0, 15, 4, 15).forEach(pos -> {
			if (chunk.getBlockState(pos).isOf(Blocks.BEDROCK)) {
				chunk.setBlockState(pos, Blocks.DEEPSLATE.getDefaultState(), false);
			}
		});
	}

	public static void fillChunkWithAir(ProtoChunk chunk) {
		HeightLimitView heightLimitView = chunk.getHeightLimitView();
		int i = heightLimitView.getBottomY();
		int j = heightLimitView.getTopY() - 1;
		BlockPos.iterate(0, i, 0, 15, j, 15).forEach(pos -> chunk.setBlockState(pos, Blocks.AIR.getDefaultState(), false));
	}

	public ChunkStatus getTargetStatus() {
		return this.targetStatus;
	}

	public boolean hasNoBedrock() {
		int i = this.missingBedrock.size();
		return i == 256 && i == this.missingBedrock.cardinality();
	}

	public static BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier, Registry<Biome> biomeRegistry, Chunk chunk) {
		if (!chunk.hasBelowZeroRetrogen()) {
			return biomeSupplier;
		} else {
			Set<Biome> set = (Set<Biome>)CAVE_BIOMES.stream().map(biomeRegistry::get).collect(Collectors.toSet());
			return (x, y, z, noise) -> {
				Biome biome = biomeSupplier.getBiome(x, y, z, noise);
				return set.contains(biome) ? biome : chunk.getBiomeForNoiseGen(x, 0, z);
			};
		}
	}
}
