/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.StructureType;

public class SwampHutStructure
extends StructureType {
    public static final Codec<SwampHutStructure> CODEC = SwampHutStructure.createCodec(SwampHutStructure::new);

    public SwampHutStructure(StructureType.Config config) {
        super(config);
    }

    @Override
    public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
        return SwampHutStructure.getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, collector -> SwampHutStructure.addPieces(collector, context));
    }

    private static void addPieces(StructurePiecesCollector collector, StructureType.Context context) {
        collector.addPiece(new SwampHutGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
    }

    @Override
    public net.minecraft.structure.StructureType<?> getType() {
        return net.minecraft.structure.StructureType.SWAMP_HUT;
    }
}

