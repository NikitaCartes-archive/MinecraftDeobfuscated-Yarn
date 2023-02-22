/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;

public class MultiNoiseBiomeSourceParameterList {
    public static final Codec<MultiNoiseBiomeSourceParameterList> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Preset.CODEC.fieldOf("preset")).forGetter(multiNoiseBiomeSourceParameterList -> multiNoiseBiomeSourceParameterList.preset), RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME)).apply((Applicative<MultiNoiseBiomeSourceParameterList, ?>)instance, MultiNoiseBiomeSourceParameterList::new));
    public static final Codec<RegistryEntry<MultiNoiseBiomeSourceParameterList>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, CODEC);
    private final Preset preset;
    private final MultiNoiseUtil.Entries<RegistryEntry<Biome>> entries;

    public MultiNoiseBiomeSourceParameterList(Preset preset, RegistryEntryLookup<Biome> biomeLookup) {
        this.preset = preset;
        this.entries = preset.biomeSourceFunction.apply(biomeLookup::getOrThrow);
    }

    public MultiNoiseUtil.Entries<RegistryEntry<Biome>> getEntries() {
        return this.entries;
    }

    public static Map<Preset, MultiNoiseUtil.Entries<RegistryKey<Biome>>> getPresetToEntriesMap() {
        return Preset.BY_IDENTIFIER.values().stream().collect(Collectors.toMap(preset -> preset, preset -> preset.biomeSourceFunction().apply(registryKey -> registryKey)));
    }

    public record Preset(Identifier id, BiomeSourceFunction biomeSourceFunction) {
        public static final Preset NETHER = new Preset(new Identifier("nether"), new BiomeSourceFunction(){

            @Override
            public <T> MultiNoiseUtil.Entries<T> apply(Function<RegistryKey<Biome>, T> function) {
                return new MultiNoiseUtil.Entries<T>(List.of(Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f), function.apply(BiomeKeys.NETHER_WASTES)), Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f), function.apply(BiomeKeys.SOUL_SAND_VALLEY)), Pair.of(MultiNoiseUtil.createNoiseHypercube(0.4f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f), function.apply(BiomeKeys.CRIMSON_FOREST)), Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.375f), function.apply(BiomeKeys.WARPED_FOREST)), Pair.of(MultiNoiseUtil.createNoiseHypercube(-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.175f), function.apply(BiomeKeys.BASALT_DELTAS))));
            }
        });
        public static final Preset OVERWORLD = new Preset(new Identifier("overworld"), new BiomeSourceFunction(){

            @Override
            public <T> MultiNoiseUtil.Entries<T> apply(Function<RegistryKey<Biome>, T> function) {
                return Preset.getOverworldEntries(function, VanillaBiomeParameters.Type.NONE);
            }
        });
        public static final Preset OVERWORLD_UPDATE_1_20 = new Preset(new Identifier("overworld_update_1_20"), new BiomeSourceFunction(){

            @Override
            public <T> MultiNoiseUtil.Entries<T> apply(Function<RegistryKey<Biome>, T> function) {
                return Preset.getOverworldEntries(function, VanillaBiomeParameters.Type.UPDATE_1_20);
            }
        });
        static final Map<Identifier, Preset> BY_IDENTIFIER = Stream.of(NETHER, OVERWORLD, OVERWORLD_UPDATE_1_20).collect(Collectors.toMap(Preset::id, preset -> preset));
        public static final Codec<Preset> CODEC = Identifier.CODEC.flatXmap(identifier -> Optional.ofNullable(BY_IDENTIFIER.get(identifier)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown preset: " + identifier)), preset -> DataResult.success(preset.id));

        static <T> MultiNoiseUtil.Entries<T> getOverworldEntries(Function<RegistryKey<Biome>, T> biomeEntryGetter, VanillaBiomeParameters.Type parametersType) {
            ImmutableList.Builder builder = ImmutableList.builder();
            new VanillaBiomeParameters(parametersType).writeOverworldBiomeParameters(pair -> builder.add(pair.mapSecond(biomeEntryGetter)));
            return new MultiNoiseUtil.Entries(builder.build());
        }

        public Stream<RegistryKey<Biome>> biomeStream() {
            return this.biomeSourceFunction.apply(registryKey -> registryKey).getEntries().stream().map(Pair::getSecond).distinct();
        }

        @FunctionalInterface
        static interface BiomeSourceFunction {
            public <T> MultiNoiseUtil.Entries<T> apply(Function<RegistryKey<Biome>, T> var1);
        }
    }
}

