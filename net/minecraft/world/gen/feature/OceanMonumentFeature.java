/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanMonumentFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList(new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4));

    public OceanMonumentFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
        int m = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanMonumentSpacing();
        int n = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanMonumentSeparation();
        int o = i + m * k;
        int p = j + m * l;
        int q = o < 0 ? o - m + 1 : o;
        int r = p < 0 ? p - m + 1 : p;
        int s = q / m;
        int t = r / m;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387313);
        s *= m;
        t *= m;
        return new ChunkPos(s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2, t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2);
    }

    @Override
    public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
        ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
        if (i == chunkPos.x && j == chunkPos.z) {
            Set<Biome> set = chunkGenerator.getBiomeSource().getBiomesInArea(i * 16 + 9, j * 16 + 9, 16);
            for (Biome biome : set) {
                if (chunkGenerator.hasStructure(biome, Feature.OCEAN_MONUMENT)) continue;
                return false;
            }
            Set<Biome> set2 = chunkGenerator.getBiomeSource().getBiomesInArea(i * 16 + 9, j * 16 + 9, 29);
            for (Biome biome2 : set2) {
                if (biome2.getCategory() == Biome.Category.OCEAN || biome2.getCategory() == Biome.Category.RIVER) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Monument";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    public static class Start
    extends StructureStart {
        private boolean field_13717;

        public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
            super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
            this.method_16588(i, j);
        }

        private void method_16588(int i, int j) {
            int k = i * 16 - 29;
            int l = j * 16 - 29;
            Direction direction = Direction.Type.HORIZONTAL.random(this.random);
            this.children.add(new OceanMonumentGenerator.Base(this.random, k, l, direction));
            this.setBoundingBoxFromChildren();
            this.field_13717 = true;
        }

        @Override
        public void generateStructure(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            if (!this.field_13717) {
                this.children.clear();
                this.method_16588(this.getChunkX(), this.getChunkZ());
            }
            super.generateStructure(iWorld, random, mutableIntBoundingBox, chunkPos);
        }
    }
}

