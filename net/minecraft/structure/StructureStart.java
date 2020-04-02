/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class StructureStart {
    public static final StructureStart DEFAULT = new StructureStart((StructureFeature)Feature.MINESHAFT, 0, 0, BlockBox.empty(), 0, 0L){

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
        }
    };
    private final StructureFeature<?> feature;
    protected final List<StructurePiece> children = Lists.newArrayList();
    protected BlockBox boundingBox;
    private final int chunkX;
    private final int chunkZ;
    private int references;
    protected final ChunkRandom random;

    public StructureStart(StructureFeature<?> feature, int chunkX, int chunkZ, BlockBox box, int references, long seed) {
        this.feature = feature;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.references = references;
        this.random = new ChunkRandom();
        this.random.setCarverSeed(seed, chunkX, chunkZ);
        this.boundingBox = box;
    }

    public abstract void initialize(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5);

    public BlockBox getBoundingBox() {
        return this.boundingBox;
    }

    public List<StructurePiece> getChildren() {
        return this.children;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generateStructure(IWorld world, StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
        List<StructurePiece> list = this.children;
        synchronized (list) {
            if (this.children.isEmpty()) {
                return;
            }
            BlockBox blockBox2 = this.children.get((int)0).boundingBox;
            Vec3i vec3i = blockBox2.getCenter();
            BlockPos blockPos = new BlockPos(vec3i.getX(), blockBox2.minY, vec3i.getZ());
            Iterator<StructurePiece> iterator = this.children.iterator();
            while (iterator.hasNext()) {
                StructurePiece structurePiece = iterator.next();
                if (!structurePiece.getBoundingBox().intersects(blockBox) || structurePiece.generate(world, structureAccessor, chunkGenerator, random, blockBox, chunkPos, blockPos)) continue;
                iterator.remove();
            }
            this.setBoundingBoxFromChildren();
        }
    }

    protected void setBoundingBoxFromChildren() {
        this.boundingBox = BlockBox.empty();
        for (StructurePiece structurePiece : this.children) {
            this.boundingBox.encompass(structurePiece.getBoundingBox());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompoundTag toTag(int chunkX, int chunkZ) {
        CompoundTag compoundTag = new CompoundTag();
        if (!this.hasChildren()) {
            compoundTag.putString("id", "INVALID");
            return compoundTag;
        }
        compoundTag.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
        compoundTag.putInt("ChunkX", chunkX);
        compoundTag.putInt("ChunkZ", chunkZ);
        compoundTag.putInt("references", this.references);
        compoundTag.put("BB", this.boundingBox.toNbt());
        ListTag listTag = new ListTag();
        List<StructurePiece> list = this.children;
        synchronized (list) {
            for (StructurePiece structurePiece : this.children) {
                listTag.add(structurePiece.getTag());
            }
        }
        compoundTag.put("Children", listTag);
        return compoundTag;
    }

    protected void method_14978(int i, Random random, int j) {
        int k = i - j;
        int l = this.boundingBox.getBlockCountY() + 1;
        if (l < k) {
            l += random.nextInt(k - l);
        }
        int m = l - this.boundingBox.maxY;
        this.boundingBox.offset(0, m, 0);
        for (StructurePiece structurePiece : this.children) {
            structurePiece.translate(0, m, 0);
        }
    }

    protected void method_14976(Random random, int i, int j) {
        int k = j - i + 1 - this.boundingBox.getBlockCountY();
        int l = k > 1 ? i + random.nextInt(k) : i;
        int m = l - this.boundingBox.minY;
        this.boundingBox.offset(0, m, 0);
        for (StructurePiece structurePiece : this.children) {
            structurePiece.translate(0, m, 0);
        }
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public BlockPos getPos() {
        return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
    }

    public boolean isInExistingChunk() {
        return this.references < this.getReferenceCountToBeInExistingChunk();
    }

    public void incrementReferences() {
        ++this.references;
    }

    public int getReferences() {
        return this.references;
    }

    protected int getReferenceCountToBeInExistingChunk() {
        return 1;
    }

    public StructureFeature<?> getFeature() {
        return this.feature;
    }
}

