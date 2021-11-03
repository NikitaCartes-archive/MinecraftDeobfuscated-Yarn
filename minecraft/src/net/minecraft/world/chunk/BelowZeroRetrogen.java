package net.minecraft.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;

public final class BelowZeroRetrogen {
	private static final BitSet EMPTY_MISSING_BEDROCK_BIT_SET = new BitSet(0);
	private static final Codec<BitSet> MISSING_BEDROCK_CODEC = Codec.LONG_STREAM
		.xmap(longStream -> BitSet.valueOf(longStream.toArray()), bitSet -> LongStream.of(bitSet.toLongArray()));
	private static final Codec<ChunkStatus> STATUS_CODEC = Registry.CHUNK_STATUS
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

	public void fillColumnWithAirIfMissingBedrock(ProtoChunk chunk) {
		HeightLimitView heightLimitView = chunk.getHeightLimitView();
		int i = heightLimitView.getBottomY();
		int j = heightLimitView.getTopY() - 1;

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				if (this.isMissingBedrockAt(k, l)) {
					BlockPos.iterate(k, i, l, k, j, l).forEach(pos -> chunk.setBlockState(pos, Blocks.AIR.getDefaultState(), false));
				}
			}
		}
	}

	public ChunkStatus getTargetStatus() {
		return this.targetStatus;
	}

	public boolean hasMissingBedrock() {
		return !this.missingBedrock.isEmpty();
	}

	public boolean isMissingBedrockAt(int x, int z) {
		return this.missingBedrock.get((z & 15) * 16 + (x & 15));
	}
}
