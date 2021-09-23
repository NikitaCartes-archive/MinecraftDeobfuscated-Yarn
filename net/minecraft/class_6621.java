/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Random;
import net.minecraft.class_6624;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@FunctionalInterface
public interface class_6621 {
    public static final class_6621 field_34938 = (structureWorldAccess, structureAccessor, chunkGenerator, random, blockBox, chunkPos, arg) -> {};

    public void afterPlace(StructureWorldAccess var1, StructureAccessor var2, ChunkGenerator var3, Random var4, BlockBox var5, ChunkPos var6, class_6624 var7);
}

