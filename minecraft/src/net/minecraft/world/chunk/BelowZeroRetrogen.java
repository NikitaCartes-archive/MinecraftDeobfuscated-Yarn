package net.minecraft.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;

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

	public ChunkStatus getTargetStatus() {
		return this.targetStatus;
	}

	public boolean hasBedrock(int x, int z) {
		return !this.missingBedrock.get((z & 15) * 16 + (x & 15));
	}
}
