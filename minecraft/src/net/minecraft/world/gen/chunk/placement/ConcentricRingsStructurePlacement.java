package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.Products.P4;
import com.mojang.datafixers.Products.P5;
import com.mojang.datafixers.Products.P9;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

public class ConcentricRingsStructurePlacement extends StructurePlacement {
	public static final Codec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.create(
		instance -> method_41629(instance).apply(instance, ConcentricRingsStructurePlacement::new)
	);
	private final int field_37768;
	private final int field_37769;
	private final int structureCount;
	private final RegistryEntryList<Biome> biasedToBiomes;

	private static P9<Mu<ConcentricRingsStructurePlacement>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.class_7152>, Integer, Integer, Integer, RegistryEntryList<Biome>> method_41629(
		Instance<ConcentricRingsStructurePlacement> instance
	) {
		P5<Mu<ConcentricRingsStructurePlacement>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.class_7152>> p5 = method_41637(
			instance
		);
		P4<Mu<ConcentricRingsStructurePlacement>, Integer, Integer, Integer, RegistryEntryList<Biome>> p4 = instance.group(
			Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::method_41627),
			Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::method_41628),
			Codec.intRange(1, 4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::getStructureCount),
			RegistryCodecs.entryList(Registry.BIOME_KEY).fieldOf("preferred_biomes").forGetter(ConcentricRingsStructurePlacement::getBiasedToBiomes)
		);
		return new P9<>(p5.t1(), p5.t2(), p5.t3(), p5.t4(), p5.t5(), p4.t1(), p4.t2(), p4.t3(), p4.t4());
	}

	public ConcentricRingsStructurePlacement(
		Vec3i locateOffset,
		StructurePlacement.FrequencyReductionMethod generationPredicateType,
		float f,
		int i,
		Optional<StructurePlacement.class_7152> optional,
		int j,
		int k,
		int structureCount,
		RegistryEntryList<Biome> biasedToBiomes
	) {
		super(locateOffset, generationPredicateType, f, i, optional);
		this.field_37768 = j;
		this.field_37769 = k;
		this.structureCount = structureCount;
		this.biasedToBiomes = biasedToBiomes;
	}

	public ConcentricRingsStructurePlacement(int i, int j, int structureCount, RegistryEntryList<Biome> biasedToBiomes) {
		this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, 0, Optional.empty(), i, j, structureCount, biasedToBiomes);
	}

	public int method_41627() {
		return this.field_37768;
	}

	public int method_41628() {
		return this.field_37769;
	}

	public int getStructureCount() {
		return this.structureCount;
	}

	public RegistryEntryList<Biome> getBiasedToBiomes() {
		return this.biasedToBiomes;
	}

	@Override
	protected boolean isStartChunk(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long l, int i, int j) {
		List<ChunkPos> list = chunkGenerator.getConcentricRingsStartChunks(this, noiseConfig);
		return list == null ? false : list.contains(new ChunkPos(i, j));
	}

	@Override
	public StructurePlacementType<?> getType() {
		return StructurePlacementType.CONCENTRIC_RINGS;
	}
}
