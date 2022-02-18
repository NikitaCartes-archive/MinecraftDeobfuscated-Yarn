/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class JigsawFeature
extends StructureFeature<StructurePoolFeatureConfig> {
    public JigsawFeature(Codec<StructurePoolFeatureConfig> codec, int structureStartY, boolean modifyBoundingBox, boolean surface, Predicate<StructureGeneratorFactory.Context<StructurePoolFeatureConfig>> contextPredicate) {
        super(codec, context -> {
            if (!contextPredicate.test(context)) {
                return Optional.empty();
            }
            BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), structureStartY, context.chunkPos().getStartZ());
            StructurePools.initDefaultPools();
            return StructurePoolBasedGenerator.generate(context, PoolStructurePiece::new, blockPos, modifyBoundingBox, surface);
        });
    }
}

