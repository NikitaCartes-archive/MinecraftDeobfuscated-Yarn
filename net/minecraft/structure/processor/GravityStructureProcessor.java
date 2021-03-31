/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class GravityStructureProcessor
extends StructureProcessor {
    public static final Codec<GravityStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Heightmap.Type.CODEC.fieldOf("heightmap")).orElse(Heightmap.Type.WORLD_SURFACE_WG).forGetter(gravityStructureProcessor -> gravityStructureProcessor.heightmap), ((MapCodec)Codec.INT.fieldOf("offset")).orElse(0).forGetter(gravityStructureProcessor -> gravityStructureProcessor.offset)).apply((Applicative<GravityStructureProcessor, ?>)instance, GravityStructureProcessor::new));
    private final Heightmap.Type heightmap;
    private final int offset;

    public GravityStructureProcessor(Heightmap.Type heightmap, int offset) {
        this.heightmap = heightmap;
        this.offset = offset;
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos blockPos, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        Heightmap.Type type = world instanceof ServerWorld ? (this.heightmap == Heightmap.Type.WORLD_SURFACE_WG ? Heightmap.Type.WORLD_SURFACE : (this.heightmap == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : this.heightmap)) : this.heightmap;
        int i = world.getTopY(type, structureBlockInfo2.pos.getX(), structureBlockInfo2.pos.getZ()) + this.offset;
        int j = structureBlockInfo.pos.getY();
        return new Structure.StructureBlockInfo(new BlockPos(structureBlockInfo2.pos.getX(), i + j, structureBlockInfo2.pos.getZ()), structureBlockInfo2.state, structureBlockInfo2.nbt);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.GRAVITY;
    }
}

