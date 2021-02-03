/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;

public class class_5821<FC extends FeatureConfig> {
    private final StructureWorldAccess field_28769;
    private final ChunkGenerator field_28770;
    private final Random field_28771;
    private final BlockPos field_28772;
    private final FC field_28773;

    public class_5821(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, FC featureConfig) {
        this.field_28769 = structureWorldAccess;
        this.field_28770 = chunkGenerator;
        this.field_28771 = random;
        this.field_28772 = blockPos;
        this.field_28773 = featureConfig;
    }

    public StructureWorldAccess method_33652() {
        return this.field_28769;
    }

    public ChunkGenerator method_33653() {
        return this.field_28770;
    }

    public Random method_33654() {
        return this.field_28771;
    }

    public BlockPos method_33655() {
        return this.field_28772;
    }

    public FC method_33656() {
        return this.field_28773;
    }
}

