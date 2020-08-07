package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5505;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;

public class VanillaLayeredBiomeSource extends BiomeSource {
	public static final Codec<VanillaLayeredBiomeSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.LONG.fieldOf("seed").stable().forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.field_24728),
					Codec.BOOL
						.optionalFieldOf("legacy_biome_init_layer", Boolean.valueOf(false), Lifecycle.stable())
						.forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.field_24498),
					Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.field_24729),
					class_5505.method_31148(Registry.BIOME_KEY).forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.field_26698)
				)
				.apply(instance, instance.stable(VanillaLayeredBiomeSource::new))
	);
	private final BiomeLayerSampler biomeSampler;
	private static final List<RegistryKey<Biome>> BIOMES = ImmutableList.of(
		Biomes.field_9423,
		Biomes.field_9451,
		Biomes.field_9424,
		Biomes.field_9472,
		Biomes.field_9409,
		Biomes.field_9420,
		Biomes.field_9471,
		Biomes.field_9438,
		Biomes.field_9435,
		Biomes.field_9463,
		Biomes.field_9452,
		Biomes.field_9444,
		Biomes.field_9462,
		Biomes.field_9407,
		Biomes.field_9434,
		Biomes.field_9466,
		Biomes.field_9459,
		Biomes.field_9428,
		Biomes.field_9464,
		Biomes.field_9417,
		Biomes.field_9432,
		Biomes.field_9474,
		Biomes.field_9446,
		Biomes.field_9419,
		Biomes.field_9478,
		Biomes.field_9412,
		Biomes.field_9421,
		Biomes.field_9475,
		Biomes.field_9454,
		Biomes.field_9425,
		Biomes.field_9477,
		Biomes.field_9429,
		Biomes.field_9460,
		Biomes.field_9449,
		Biomes.field_9430,
		Biomes.field_9415,
		Biomes.field_9410,
		Biomes.field_9433,
		Biomes.field_9408,
		Biomes.field_9441,
		Biomes.field_9467,
		Biomes.field_9448,
		Biomes.field_9439,
		Biomes.field_9470,
		Biomes.field_9418,
		Biomes.field_9455,
		Biomes.field_9427,
		Biomes.field_9476,
		Biomes.field_9414,
		Biomes.field_9422,
		Biomes.field_9479,
		Biomes.field_9453,
		Biomes.field_9426,
		Biomes.field_9405,
		Biomes.field_9431,
		Biomes.field_9458,
		Biomes.field_9450,
		Biomes.field_9437,
		Biomes.field_9416,
		Biomes.field_9404,
		Biomes.field_9436,
		Biomes.field_9456,
		Biomes.field_9445,
		Biomes.field_9443,
		Biomes.field_9413,
		Biomes.field_9406
	);
	private final long field_24728;
	private final boolean field_24498;
	private final boolean field_24729;
	private final Registry<Biome> field_26698;

	public VanillaLayeredBiomeSource(long l, boolean bl, boolean bl2, Registry<Biome> registry) {
		super(BIOMES.stream().map(registryKey -> () -> registry.method_31140(registryKey)));
		this.field_24728 = l;
		this.field_24498 = bl;
		this.field_24729 = bl2;
		this.field_26698 = registry;
		this.biomeSampler = BiomeLayers.build(l, bl, bl2 ? 6 : 4, 4);
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource withSeed(long seed) {
		return new VanillaLayeredBiomeSource(seed, this.field_24498, this.field_24729, this.field_26698);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biomeSampler.sample(this.field_26698, biomeX, biomeZ);
	}
}
