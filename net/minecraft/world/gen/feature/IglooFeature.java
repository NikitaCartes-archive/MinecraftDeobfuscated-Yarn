/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class IglooFeature
extends StructureFeature<DefaultFeatureConfig> {
    public IglooFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, IglooFeature::addPieces);
    }

    private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
        if (!context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG)) {
            return;
        }
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(context.random());
        IglooGenerator.addPieces(context.structureManager(), blockPos, blockRotation, collector, context.random());
    }
}

