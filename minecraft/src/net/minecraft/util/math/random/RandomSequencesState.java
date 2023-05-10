package net.minecraft.util.math.random;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;

public class RandomSequencesState extends PersistentState {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final long seed;
	private final Map<Identifier, RandomSequence> sequences = new Object2ObjectOpenHashMap<>();

	public RandomSequencesState(long seed) {
		this.seed = seed;
	}

	public Random getOrCreate(Identifier id) {
		final Random random = ((RandomSequence)this.sequences.computeIfAbsent(id, idx -> new RandomSequence(this.seed, idx))).getSource();
		return new Random() {
			@Override
			public Random split() {
				RandomSequencesState.this.markDirty();
				return random.split();
			}

			@Override
			public RandomSplitter nextSplitter() {
				RandomSequencesState.this.markDirty();
				return random.nextSplitter();
			}

			@Override
			public void setSeed(long seed) {
				RandomSequencesState.this.markDirty();
				random.setSeed(seed);
			}

			@Override
			public int nextInt() {
				RandomSequencesState.this.markDirty();
				return random.nextInt();
			}

			@Override
			public int nextInt(int bound) {
				RandomSequencesState.this.markDirty();
				return random.nextInt(bound);
			}

			@Override
			public long nextLong() {
				RandomSequencesState.this.markDirty();
				return random.nextLong();
			}

			@Override
			public boolean nextBoolean() {
				RandomSequencesState.this.markDirty();
				return random.nextBoolean();
			}

			@Override
			public float nextFloat() {
				RandomSequencesState.this.markDirty();
				return random.nextFloat();
			}

			@Override
			public double nextDouble() {
				RandomSequencesState.this.markDirty();
				return random.nextDouble();
			}

			@Override
			public double nextGaussian() {
				RandomSequencesState.this.markDirty();
				return random.nextGaussian();
			}
		};
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		this.sequences
			.forEach((id, sequence) -> nbt.put(id.toString(), (NbtElement)RandomSequence.CODEC.encodeStart(NbtOps.INSTANCE, sequence).result().orElseThrow()));
		return nbt;
	}

	public static RandomSequencesState fromNbt(long seed, NbtCompound nbt) {
		RandomSequencesState randomSequencesState = new RandomSequencesState(seed);

		for (String string : nbt.getKeys()) {
			try {
				RandomSequence randomSequence = (RandomSequence)((Pair)RandomSequence.CODEC.decode(NbtOps.INSTANCE, nbt.get(string)).result().get()).getFirst();
				randomSequencesState.sequences.put(new Identifier(string), randomSequence);
			} catch (Exception var8) {
				LOGGER.error("Failed to load random sequence {}", string, var8);
			}
		}

		return randomSequencesState;
	}
}
