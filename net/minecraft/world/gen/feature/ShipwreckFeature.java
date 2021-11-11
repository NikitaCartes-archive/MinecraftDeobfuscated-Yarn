/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ShipwreckFeature
extends StructureFeature<ShipwreckFeatureConfig> {
    public ShipwreckFeature(Codec<ShipwreckFeatureConfig> configCodec) {
        super(configCodec, ShipwreckFeature::addPieces);
    }

    private static void addPieces(StructurePiecesCollector collector, ShipwreckFeatureConfig config, StructurePiecesGenerator.Context context) {
        Heightmap.Type type;
        Heightmap.Type type2 = type = config.isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
        if (!context.isBiomeValid(type)) {
            return;
        }
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        ShipwreckGenerator.addParts(context.structureManager(), blockPos, blockRotation, collector, context.random(), config);
    }
}

