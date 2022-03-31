/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.StructureFeature;

public class SwampHutFeature
extends StructureFeature {
    public static final Codec<SwampHutFeature> CODEC = SwampHutFeature.createCodec(SwampHutFeature::new);

    public SwampHutFeature(StructureFeature.Config config) {
        super(config);
    }

    @Override
    public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
        return SwampHutFeature.getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> SwampHutFeature.addPieces(structurePiecesCollector, context));
    }

    private static void addPieces(StructurePiecesCollector collector, StructureFeature.Context context) {
        collector.addPiece(new SwampHutGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.SWAMP_HUT;
    }
}

