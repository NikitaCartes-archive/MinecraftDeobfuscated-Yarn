/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class SwampHutFeature
extends AbstractTempleFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList(new Biome.SpawnEntry(EntityType.WITCH, 1, 1, 1));
    private static final List<Biome.SpawnEntry> CREATURE_SPAWNS = Lists.newArrayList(new Biome.SpawnEntry(EntityType.CAT, 1, 1, 1));

    public SwampHutFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public String getName() {
        return "Swamp_Hut";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier() {
        return 14357620;
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    @Override
    public List<Biome.SpawnEntry> getCreatureSpawns() {
        return CREATURE_SPAWNS;
    }

    public boolean method_14029(IWorld iWorld, BlockPos blockPos) {
        StructureStart structureStart = this.isInsideStructure(iWorld, blockPos, true);
        if (structureStart == StructureStart.DEFAULT || !(structureStart instanceof Start) || structureStart.getChildren().isEmpty()) {
            return false;
        }
        StructurePiece structurePiece = structureStart.getChildren().get(0);
        return structurePiece instanceof SwampHutGenerator;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkX, chunkZ, blockBox, i, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            SwampHutGenerator swampHutGenerator = new SwampHutGenerator(this.random, x * 16, z * 16);
            this.children.add(swampHutGenerator);
            this.setBoundingBoxFromChildren();
        }
    }
}

