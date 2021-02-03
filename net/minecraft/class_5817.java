/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructureFeature;

public class class_5817 {
    public static final class_5817 field_28740 = new class_5817();
    private static final float[] field_28741 = Util.make(new float[13824], fs -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    fs[i * 24 * 24 + j * 24 + k] = (float)class_5817.method_33642(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private final ObjectList<StructurePiece> field_28742;
    private final ObjectList<JigsawJunction> field_28743;
    private final ObjectListIterator<StructurePiece> field_28744;
    private final ObjectListIterator<JigsawJunction> field_28745;

    protected class_5817(StructureAccessor structureAccessor, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x;
        int j = chunkPos.z;
        int k = chunkPos.getStartX();
        int l = chunkPos.getStartZ();
        this.field_28743 = new ObjectArrayList<JigsawJunction>(32);
        this.field_28742 = new ObjectArrayList<StructurePiece>(10);
        for (StructureFeature<?> structureFeature : StructureFeature.JIGSAW_STRUCTURES) {
            structureAccessor.getStructuresWithChildren(ChunkSectionPos.method_33705(chunk), structureFeature).forEach(structureStart -> {
                for (StructurePiece structurePiece : structureStart.getChildren()) {
                    if (!structurePiece.intersectsChunk(chunkPos, 12)) continue;
                    if (structurePiece instanceof PoolStructurePiece) {
                        PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
                        StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                        if (projection == StructurePool.Projection.RIGID) {
                            this.field_28742.add(poolStructurePiece);
                        }
                        for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
                            int k = jigsawJunction.getSourceX();
                            int l = jigsawJunction.getSourceZ();
                            if (k <= k - 12 || l <= l - 12 || k >= k + 15 + 12 || l >= l + 15 + 12) continue;
                            this.field_28743.add(jigsawJunction);
                        }
                        continue;
                    }
                    this.field_28742.add(structurePiece);
                }
            });
        }
        this.field_28744 = this.field_28742.iterator();
        this.field_28745 = this.field_28743.iterator();
    }

    private class_5817() {
        this.field_28743 = new ObjectArrayList<JigsawJunction>();
        this.field_28742 = new ObjectArrayList<StructurePiece>();
        this.field_28744 = this.field_28742.iterator();
        this.field_28745 = this.field_28743.iterator();
    }

    protected double method_33638(int i, int j, int k) {
        int m;
        int l;
        double d = 0.0;
        while (this.field_28744.hasNext()) {
            StructurePiece structurePiece = (StructurePiece)this.field_28744.next();
            BlockBox blockBox = structurePiece.getBoundingBox();
            l = Math.max(0, Math.max(blockBox.minX - i, i - blockBox.maxX));
            m = j - (blockBox.minY + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece)structurePiece).getGroundLevelDelta() : 0));
            int n = Math.max(0, Math.max(blockBox.minZ - k, k - blockBox.maxZ));
            d += class_5817.method_33641(l, m, n) * 0.8;
        }
        this.field_28744.back(this.field_28742.size());
        while (this.field_28745.hasNext()) {
            JigsawJunction jigsawJunction = (JigsawJunction)this.field_28745.next();
            int o = i - jigsawJunction.getSourceX();
            l = j - jigsawJunction.getSourceGroundY();
            m = k - jigsawJunction.getSourceZ();
            d += class_5817.method_33641(o, l, m) * 0.4;
        }
        this.field_28745.back(this.field_28743.size());
        return d;
    }

    private static double method_33641(int i, int j, int k) {
        int l = i + 12;
        int m = j + 12;
        int n = k + 12;
        if (l < 0 || l >= 24) {
            return 0.0;
        }
        if (m < 0 || m >= 24) {
            return 0.0;
        }
        if (n < 0 || n >= 24) {
            return 0.0;
        }
        return field_28741[n * 24 * 24 + l * 24 + m];
    }

    private static double method_33642(int i, int j, int k) {
        double d = i * i + k * k;
        double e = (double)j + 0.5;
        double f = e * e;
        double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
        double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
        return h * g;
    }
}

