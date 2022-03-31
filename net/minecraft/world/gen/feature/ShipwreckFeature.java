/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.StructureFeature;

public class ShipwreckFeature
extends StructureFeature {
    public static final Codec<ShipwreckFeature> CODEC = RecordCodecBuilder.create(instance -> instance.group(ShipwreckFeature.configCodecBuilder(instance), ((MapCodec)Codec.BOOL.fieldOf("is_beached")).forGetter(shipwreckFeature -> shipwreckFeature.field_37816)).apply((Applicative<ShipwreckFeature, ?>)instance, ShipwreckFeature::new));
    public final boolean field_37816;

    public ShipwreckFeature(StructureFeature.Config config, boolean bl) {
        super(config);
        this.field_37816 = bl;
    }

    @Override
    public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
        Heightmap.Type type = this.field_37816 ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
        return ShipwreckFeature.getStructurePosition(context, type, structurePiecesCollector -> this.addPieces((StructurePiecesCollector)structurePiecesCollector, context));
    }

    private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        ShipwreckGenerator.addParts(context.structureManager(), blockPos, blockRotation, structurePiecesCollector, context.random(), this.field_37816);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.SHIPWRECK;
    }
}

