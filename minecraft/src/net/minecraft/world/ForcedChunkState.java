package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class ForcedChunkState extends PersistentState {
	public static final String CHUNKS_KEY = "chunks";
	private static final String FORCED_KEY = "Forced";
	private final LongSet chunks;

	public static PersistentState.Type<ForcedChunkState> getPersistentStateType() {
		return new PersistentState.Type<>(ForcedChunkState::new, ForcedChunkState::fromNbt, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
	}

	private ForcedChunkState(LongSet chunks) {
		this.chunks = chunks;
	}

	public ForcedChunkState() {
		this(new LongOpenHashSet());
	}

	public static ForcedChunkState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		return new ForcedChunkState(new LongOpenHashSet(nbt.getLongArray("Forced")));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		nbt.putLongArray("Forced", this.chunks.toLongArray());
		return nbt;
	}

	public LongSet getChunks() {
		return this.chunks;
	}
}
