package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public final class class_5363 {
	public static final Codec<class_5363> field_25411 = RecordCodecBuilder.create(
		instance -> instance.group(
					DimensionType.field_24756.fieldOf("type").forGetter(class_5363::method_29566),
					ChunkGenerator.field_24746.fieldOf("generator").forGetter(class_5363::method_29571)
				)
				.apply(instance, instance.stable(class_5363::new))
	);
	public static final RegistryKey<class_5363> field_25412 = RegistryKey.of(Registry.field_25490, new Identifier("overworld"));
	public static final RegistryKey<class_5363> field_25413 = RegistryKey.of(Registry.field_25490, new Identifier("the_nether"));
	public static final RegistryKey<class_5363> field_25414 = RegistryKey.of(Registry.field_25490, new Identifier("the_end"));
	private static final LinkedHashSet<RegistryKey<class_5363>> field_25415 = Sets.newLinkedHashSet(ImmutableList.of(field_25412, field_25413, field_25414));
	private final Supplier<DimensionType> field_25416;
	private final ChunkGenerator field_25417;

	public class_5363(Supplier<DimensionType> supplier, ChunkGenerator chunkGenerator) {
		this.field_25416 = supplier;
		this.field_25417 = chunkGenerator;
	}

	public Supplier<DimensionType> method_29566() {
		return this.field_25416;
	}

	public DimensionType method_29570() {
		return (DimensionType)this.field_25416.get();
	}

	public ChunkGenerator method_29571() {
		return this.field_25417;
	}

	public static SimpleRegistry<class_5363> method_29569(SimpleRegistry<class_5363> simpleRegistry) {
		SimpleRegistry<class_5363> simpleRegistry2 = new SimpleRegistry<>(Registry.field_25490, Lifecycle.experimental());

		for (RegistryKey<class_5363> registryKey : field_25415) {
			class_5363 lv = simpleRegistry.get(registryKey);
			if (lv != null) {
				simpleRegistry2.add(registryKey, lv);
				if (simpleRegistry.method_29723(registryKey)) {
					simpleRegistry2.method_29725(registryKey);
				}
			}
		}

		for (Entry<RegistryKey<class_5363>, class_5363> entry : simpleRegistry.method_29722()) {
			RegistryKey<class_5363> registryKey2 = (RegistryKey<class_5363>)entry.getKey();
			if (!field_25415.contains(registryKey2)) {
				simpleRegistry2.add(registryKey2, entry.getValue());
				if (simpleRegistry.method_29723(registryKey2)) {
					simpleRegistry2.method_29725(registryKey2);
				}
			}
		}

		return simpleRegistry2;
	}

	public static boolean method_29567(long l, SimpleRegistry<class_5363> simpleRegistry) {
		List<Entry<RegistryKey<class_5363>, class_5363>> list = Lists.<Entry<RegistryKey<class_5363>, class_5363>>newArrayList(simpleRegistry.method_29722());
		if (list.size() != field_25415.size()) {
			return false;
		} else {
			Entry<RegistryKey<class_5363>, class_5363> entry = (Entry<RegistryKey<class_5363>, class_5363>)list.get(0);
			Entry<RegistryKey<class_5363>, class_5363> entry2 = (Entry<RegistryKey<class_5363>, class_5363>)list.get(1);
			Entry<RegistryKey<class_5363>, class_5363> entry3 = (Entry<RegistryKey<class_5363>, class_5363>)list.get(2);
			if (entry.getKey() != field_25412 || entry2.getKey() != field_25413 || entry3.getKey() != field_25414) {
				return false;
			} else if (!((class_5363)entry.getValue()).method_29570().isOverworld()
				|| !((class_5363)entry2.getValue()).method_29570().isNether()
				|| !((class_5363)entry3.getValue()).method_29570().isEnd()) {
				return false;
			} else if (((class_5363)entry2.getValue()).method_29571() instanceof SurfaceChunkGenerator
				&& ((class_5363)entry3.getValue()).method_29571() instanceof SurfaceChunkGenerator) {
				SurfaceChunkGenerator surfaceChunkGenerator = (SurfaceChunkGenerator)((class_5363)entry2.getValue()).method_29571();
				SurfaceChunkGenerator surfaceChunkGenerator2 = (SurfaceChunkGenerator)((class_5363)entry3.getValue()).method_29571();
				if (!surfaceChunkGenerator.method_28548(l, ChunkGeneratorType.Preset.NETHER)) {
					return false;
				} else if (!surfaceChunkGenerator2.method_28548(l, ChunkGeneratorType.Preset.END)) {
					return false;
				} else if (!(surfaceChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
					return false;
				} else {
					MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)surfaceChunkGenerator.getBiomeSource();
					if (!multiNoiseBiomeSource.method_28462(l)) {
						return false;
					} else if (!(surfaceChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
						return false;
					} else {
						TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)surfaceChunkGenerator2.getBiomeSource();
						return theEndBiomeSource.method_28479(l);
					}
				}
			} else {
				return false;
			}
		}
	}
}
