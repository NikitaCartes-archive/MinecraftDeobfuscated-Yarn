package net.minecraft.util.math.random;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;

public class RandomSequencesState extends PersistentState {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final long seed;
	private int salt;
	private boolean includeWorldSeed = true;
	private boolean includeSequenceId = true;
	private final Map<Identifier, RandomSequence> sequences = new Object2ObjectOpenHashMap<>();

	public static PersistentState.Type<RandomSequencesState> getPersistentStateType(long seed) {
		return new PersistentState.Type<>(() -> new RandomSequencesState(seed), (nbt, registryLookup) -> fromNbt(seed, nbt), DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);
	}

	public RandomSequencesState(long seed) {
		this.seed = seed;
	}

	public Random getOrCreate(Identifier id) {
		Random random = ((RandomSequence)this.sequences.computeIfAbsent(id, this::createSequence)).getSource();
		return new RandomSequencesState.WrappedRandom(random);
	}

	private RandomSequence createSequence(Identifier id) {
		return this.createSequence(id, this.salt, this.includeWorldSeed, this.includeSequenceId);
	}

	private RandomSequence createSequence(Identifier id, int salt, boolean includeWorldSeed, boolean includeSequenceId) {
		long l = (includeWorldSeed ? this.seed : 0L) ^ (long)salt;
		return new RandomSequence(l, includeSequenceId ? Optional.of(id) : Optional.empty());
	}

	public void forEachSequence(BiConsumer<Identifier, RandomSequence> consumer) {
		this.sequences.forEach(consumer);
	}

	public void setDefaultParameters(int salt, boolean includeWorldSeed, boolean includeSequenceId) {
		this.salt = salt;
		this.includeWorldSeed = includeWorldSeed;
		this.includeSequenceId = includeSequenceId;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		nbt.putInt("salt", this.salt);
		nbt.putBoolean("include_world_seed", this.includeWorldSeed);
		nbt.putBoolean("include_sequence_id", this.includeSequenceId);
		NbtCompound nbtCompound = new NbtCompound();
		this.sequences
			.forEach((id, sequence) -> nbtCompound.put(id.toString(), (NbtElement)RandomSequence.CODEC.encodeStart(NbtOps.INSTANCE, sequence).result().orElseThrow()));
		nbt.put("sequences", nbtCompound);
		return nbt;
	}

	private static boolean getBooleanFromNbtOrFallback(NbtCompound nbt, String key, boolean fallback) {
		return nbt.contains(key, NbtElement.BYTE_TYPE) ? nbt.getBoolean(key) : fallback;
	}

	public static RandomSequencesState fromNbt(long seed, NbtCompound nbt) {
		RandomSequencesState randomSequencesState = new RandomSequencesState(seed);
		randomSequencesState.setDefaultParameters(
			nbt.getInt("salt"), getBooleanFromNbtOrFallback(nbt, "include_world_seed", true), getBooleanFromNbtOrFallback(nbt, "include_sequence_id", true)
		);
		NbtCompound nbtCompound = nbt.getCompound("sequences");

		for (String string : nbtCompound.getKeys()) {
			try {
				RandomSequence randomSequence = (RandomSequence)((Pair)RandomSequence.CODEC.decode(NbtOps.INSTANCE, nbtCompound.get(string)).result().get()).getFirst();
				randomSequencesState.sequences.put(Identifier.of(string), randomSequence);
			} catch (Exception var9) {
				LOGGER.error("Failed to load random sequence {}", string, var9);
			}
		}

		return randomSequencesState;
	}

	public int resetAll() {
		int i = this.sequences.size();
		this.sequences.clear();
		return i;
	}

	public void reset(Identifier id) {
		this.sequences.put(id, this.createSequence(id));
	}

	public void reset(Identifier id, int salt, boolean includeWorldSeed, boolean includeSequenceId) {
		this.sequences.put(id, this.createSequence(id, salt, includeWorldSeed, includeSequenceId));
	}

	class WrappedRandom implements Random {
		private final Random random;

		WrappedRandom(final Random random) {
			this.random = random;
		}

		@Override
		public Random split() {
			RandomSequencesState.this.markDirty();
			return this.random.split();
		}

		@Override
		public RandomSplitter nextSplitter() {
			RandomSequencesState.this.markDirty();
			return this.random.nextSplitter();
		}

		@Override
		public void setSeed(long seed) {
			RandomSequencesState.this.markDirty();
			this.random.setSeed(seed);
		}

		@Override
		public int nextInt() {
			RandomSequencesState.this.markDirty();
			return this.random.nextInt();
		}

		@Override
		public int nextInt(int bound) {
			RandomSequencesState.this.markDirty();
			return this.random.nextInt(bound);
		}

		@Override
		public long nextLong() {
			RandomSequencesState.this.markDirty();
			return this.random.nextLong();
		}

		@Override
		public boolean nextBoolean() {
			RandomSequencesState.this.markDirty();
			return this.random.nextBoolean();
		}

		@Override
		public float nextFloat() {
			RandomSequencesState.this.markDirty();
			return this.random.nextFloat();
		}

		@Override
		public double nextDouble() {
			RandomSequencesState.this.markDirty();
			return this.random.nextDouble();
		}

		@Override
		public double nextGaussian() {
			RandomSequencesState.this.markDirty();
			return this.random.nextGaussian();
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				return o instanceof RandomSequencesState.WrappedRandom wrappedRandom ? this.random.equals(wrappedRandom.random) : false;
			}
		}
	}
}
