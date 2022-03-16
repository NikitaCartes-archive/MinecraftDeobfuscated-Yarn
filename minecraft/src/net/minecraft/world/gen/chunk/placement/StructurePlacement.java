package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.Products.P5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class StructurePlacement {
	public static final Codec<StructurePlacement> TYPE_CODEC = Registry.STRUCTURE_PLACEMENT
		.getCodec()
		.dispatch(StructurePlacement::getType, StructurePlacementType::codec);
	private static final int field_37775 = 10387320;
	private final Vec3i locateOffset;
	private final StructurePlacement.FrequencyReductionMethod frequencyReductionMethod;
	private final float frequency;
	private final int salt;
	private final Optional<StructurePlacement.class_7152> exclusionZone;

	protected static <S extends StructurePlacement> P5<Mu<S>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.class_7152>> method_41637(
		Instance<S> instance
	) {
		return instance.group(
			Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(StructurePlacement::getLocateOffset),
			StructurePlacement.FrequencyReductionMethod.CODEC
				.optionalFieldOf("frequency_reduction_method", StructurePlacement.FrequencyReductionMethod.DEFAULT)
				.forGetter(StructurePlacement::getFrequencyReductionMethod),
			Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(StructurePlacement::getFrequency),
			Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(StructurePlacement::getSalt),
			StructurePlacement.class_7152.field_37781.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::getExclusionZone)
		);
	}

	protected StructurePlacement(
		Vec3i locateOffset,
		StructurePlacement.FrequencyReductionMethod frequencyReductionMethod,
		float frequency,
		int salt,
		Optional<StructurePlacement.class_7152> exclusionZone
	) {
		this.locateOffset = locateOffset;
		this.frequencyReductionMethod = frequencyReductionMethod;
		this.frequency = frequency;
		this.salt = salt;
		this.exclusionZone = exclusionZone;
	}

	protected Vec3i getLocateOffset() {
		return this.locateOffset;
	}

	protected StructurePlacement.FrequencyReductionMethod getFrequencyReductionMethod() {
		return this.frequencyReductionMethod;
	}

	protected float getFrequency() {
		return this.frequency;
	}

	protected int getSalt() {
		return this.salt;
	}

	protected Optional<StructurePlacement.class_7152> getExclusionZone() {
		return this.exclusionZone;
	}

	public boolean method_41639(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long l, int i, int j) {
		if (!this.isStartChunk(chunkGenerator, noiseConfig, l, i, j)) {
			return false;
		} else {
			return this.frequency < 1.0F && !this.frequencyReductionMethod.shouldGenerate(l, this.salt, i, j, this.frequency)
				? false
				: !this.exclusionZone.isPresent() || !((StructurePlacement.class_7152)this.exclusionZone.get()).method_41648(chunkGenerator, noiseConfig, l, i, j);
		}
	}

	protected abstract boolean isStartChunk(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long l, int i, int j);

	public BlockPos method_41636(ChunkPos chunkPos) {
		return new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ()).add(this.getLocateOffset());
	}

	public abstract StructurePlacementType<?> getType();

	private static boolean defaultShouldGenerate(long seed, int regionX, int regionZ, int salt, float frequency) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(seed, regionX, regionZ, salt);
		return chunkRandom.nextFloat() < frequency;
	}

	private static boolean legacyType3ShouldGenerate(long seed, int i, int chunkX, int chunkZ, float frequency) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(seed, chunkX, chunkZ);
		return chunkRandom.nextDouble() < (double)frequency;
	}

	private static boolean legacyType2ShouldGenerate(long seed, int i, int regionX, int regionZ, float frequency) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(seed, regionX, regionZ, 10387320);
		return chunkRandom.nextFloat() < frequency;
	}

	private static boolean legacyType1ShouldGenerate(long seed, int i, int j, int k, float frequency) {
		int l = j >> 4;
		int m = k >> 4;
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setSeed((long)(l ^ m << 4) ^ seed);
		chunkRandom.nextInt();
		return chunkRandom.nextInt((int)(1.0F / frequency)) == 0;
	}

	public static enum FrequencyReductionMethod implements StringIdentifiable {
		DEFAULT("default", StructurePlacement::defaultShouldGenerate),
		LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyType1ShouldGenerate),
		LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyType2ShouldGenerate),
		LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyType3ShouldGenerate);

		public static final Codec<StructurePlacement.FrequencyReductionMethod> CODEC = StringIdentifiable.createCodec(
			StructurePlacement.FrequencyReductionMethod::values, StructurePlacement.FrequencyReductionMethod::get
		);
		private final String name;
		private final StructurePlacement.GenerationPredicate generationPredicate;

		private FrequencyReductionMethod(String name, StructurePlacement.GenerationPredicate generationPredicate) {
			this.name = name;
			this.generationPredicate = generationPredicate;
		}

		public boolean shouldGenerate(long seed, int i, int j, int k, float chance) {
			return this.generationPredicate.shouldGenerate(seed, i, j, k, chance);
		}

		@Nullable
		public static StructurePlacement.FrequencyReductionMethod get(String name) {
			for (StructurePlacement.FrequencyReductionMethod frequencyReductionMethod : values()) {
				if (frequencyReductionMethod.name.equals(name)) {
					return frequencyReductionMethod;
				}
			}

			return null;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	@FunctionalInterface
	public interface GenerationPredicate {
		boolean shouldGenerate(long seed, int i, int j, int k, float chance);
	}

	@Deprecated
	public static record class_7152(RegistryEntry<StructureSet> otherSet, int chunkCount) {
		public static final Codec<StructurePlacement.class_7152> field_37781 = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryElementCodec.of(Registry.STRUCTURE_SET_KEY, StructureSet.CODEC, false).fieldOf("other_set").forGetter(StructurePlacement.class_7152::otherSet),
						Codec.intRange(1, 16).fieldOf("chunk_count").forGetter(StructurePlacement.class_7152::chunkCount)
					)
					.apply(instance, StructurePlacement.class_7152::new)
		);

		boolean method_41648(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long l, int i, int j) {
			return chunkGenerator.method_41053(this.otherSet, noiseConfig, l, i, j, this.chunkCount);
		}
	}
}
