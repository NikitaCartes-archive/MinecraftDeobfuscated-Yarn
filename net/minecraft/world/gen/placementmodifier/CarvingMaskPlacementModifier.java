/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class CarvingMaskPlacementModifier
extends PlacementModifier {
    public static final Codec<CarvingMaskPlacementModifier> MODIFIER_CODEC = ((MapCodec)GenerationStep.Carver.CODEC.fieldOf("step")).xmap(CarvingMaskPlacementModifier::new, config -> config.step).codec();
    private final GenerationStep.Carver step;

    private CarvingMaskPlacementModifier(GenerationStep.Carver step) {
        this.step = step;
    }

    public static CarvingMaskPlacementModifier of(GenerationStep.Carver step) {
        return new CarvingMaskPlacementModifier(step);
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, AbstractRandom abstractRandom, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return context.getOrCreateCarvingMask(chunkPos, this.step).streamBlockPos(chunkPos);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.CARVING_MASK;
    }
}

