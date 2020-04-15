/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.BastionBridgeData;
import net.minecraft.structure.BastionData;
import net.minecraft.structure.BastionTreasureData;
import net.minecraft.structure.BastionUnitsData;
import net.minecraft.structure.HoglinStableData;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class BastionRemnantGenerator {
    public static final ImmutableMap<String, Integer> START_POOLS_TO_SIZES = ImmutableMap.builder().put("bastion/units/base", 60).put("bastion/hoglin_stable/origin", 60).put("bastion/treasure/starters", 60).put("bastion/bridge/start", 60).build();

    public static void init() {
        BastionUnitsData.init();
        HoglinStableData.init();
        BastionTreasureData.init();
        BastionBridgeData.init();
        BastionData.init();
    }

    public static void addPieces(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BlockPos pos, List<StructurePiece> pieces, ChunkRandom random, BastionRemnantFeatureConfig config) {
        BastionRemnantGenerator.init();
        StructurePoolFeatureConfig structurePoolFeatureConfig = config.getRandom(random);
        StructurePoolBasedGenerator.addPieces(structurePoolFeatureConfig.startPool, structurePoolFeatureConfig.size, Piece::new, chunkGenerator, structureManager, pos, pieces, random, false, false);
    }

    public static class Piece
    extends PoolStructurePiece {
        public Piece(StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos pos, int groundLevelDelta, BlockRotation rotation, BlockBox boundingBox) {
            super(StructurePieceType.BASTION_REMNANT, structureManager, structurePoolElement, pos, groundLevelDelta, rotation, boundingBox);
        }

        public Piece(StructureManager structureManager, CompoundTag tag) {
            super(structureManager, tag, StructurePieceType.BASTION_REMNANT);
        }
    }
}

