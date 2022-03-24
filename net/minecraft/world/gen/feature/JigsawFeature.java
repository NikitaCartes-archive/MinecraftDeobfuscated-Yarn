/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureType;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public final class JigsawFeature
extends StructureFeature {
    public static final Codec<JigsawFeature> CODEC = RecordCodecBuilder.create(instance -> JigsawFeature.method_41660(instance).apply((Applicative<RecordCodecBuilder.Mu<JigsawFeature>, ?>)instance, JigsawFeature::new));
    private final RegistryEntry<StructurePool> field_37795;
    private final int field_37796;
    private final HeightProvider field_37797;
    private final boolean field_37798;
    private final Optional<Heightmap.Type> field_37799;
    private final int field_38268;

    public static Products.P10<RecordCodecBuilder.Mu<JigsawFeature>, RegistryEntryList<Biome>, Map<SpawnGroup, StructureSpawns>, GenerationStep.Feature, Boolean, RegistryEntry<StructurePool>, Integer, HeightProvider, Boolean, Optional<Heightmap.Type>, Integer> method_41660(RecordCodecBuilder.Instance<JigsawFeature> instance) {
        Products.P4<RecordCodecBuilder.Mu<JigsawFeature>, RegistryEntryList<Biome>, Map<SpawnGroup, StructureSpawns>, GenerationStep.Feature, Boolean> p4 = JigsawFeature.method_41608(instance);
        Products.P6<JigsawFeature, RegistryEntry, Integer, HeightProvider, Boolean, Optional, Integer> p6 = instance.group(((MapCodec)StructurePool.REGISTRY_CODEC.fieldOf("start_pool")).forGetter(jigsawFeature -> jigsawFeature.field_37795), ((MapCodec)Codec.intRange(0, 7).fieldOf("size")).forGetter(jigsawFeature -> jigsawFeature.field_37796), ((MapCodec)HeightProvider.CODEC.fieldOf("start_height")).forGetter(jigsawFeature -> jigsawFeature.field_37797), ((MapCodec)Codec.BOOL.fieldOf("use_expansion_hack")).forGetter(jigsawFeature -> jigsawFeature.field_37798), Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(jigsawFeature -> jigsawFeature.field_37799), ((MapCodec)Codec.intRange(1, 128).fieldOf("max_distance_from_center")).forGetter(jigsawFeature -> jigsawFeature.field_38268));
        return new Products.P10<JigsawFeature, RegistryEntryList<Biome>, Map<SpawnGroup, StructureSpawns>, GenerationStep.Feature, Boolean, RegistryEntry, Integer, HeightProvider, Boolean, Optional, Integer>(p4.t1(), p4.t2(), p4.t3(), p4.t4(), p6.t1(), p6.t2(), p6.t3(), p6.t4(), p6.t5(), p6.t6());
    }

    public JigsawFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl, RegistryEntry<StructurePool> registryEntry, int i, HeightProvider heightProvider, boolean bl2, Optional<Heightmap.Type> optional, int j) {
        super(registryEntryList, map, feature, bl);
        this.field_37795 = registryEntry;
        this.field_37796 = i;
        this.field_37797 = heightProvider;
        this.field_37798 = bl2;
        this.field_37799 = optional;
        this.field_38268 = j;
    }

    public JigsawFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean surface, RegistryEntry<StructurePool> registryEntry, int i, HeightProvider heightProvider, boolean bl, Heightmap.Type type) {
        this(registryEntryList, map, feature, surface, registryEntry, i, heightProvider, bl, Optional.of(type), 80);
    }

    public JigsawFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl, RegistryEntry<StructurePool> registryEntry, int i, HeightProvider heightProvider, boolean bl2) {
        this(registryEntryList, map, feature, bl, registryEntry, i, heightProvider, bl2, Optional.empty(), 80);
    }

    @Override
    public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
        ChunkPos chunkPos = arg.chunkPos();
        int i = this.field_37797.get(arg.random(), new HeightContext(arg.chunkGenerator(), arg.heightAccessor()));
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());
        StructurePools.initDefaultPools();
        return StructurePoolBasedGenerator.generate(arg, this.field_37795, this.field_37796, PoolStructurePiece::new, blockPos, this.field_37798, this.field_37799, this.field_38268);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.JIGSAW;
    }
}

