/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class NetherFossilStructure
extends Structure {
    public static final Codec<NetherFossilStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(NetherFossilStructure.configCodecBuilder(instance), ((MapCodec)HeightProvider.CODEC.fieldOf("height")).forGetter(structure -> structure.height)).apply((Applicative<NetherFossilStructure, ?>)instance, NetherFossilStructure::new));
    public final HeightProvider height;

    public NetherFossilStructure(Structure.Config config, HeightProvider height) {
        super(config);
        this.height = height;
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        ChunkRandom chunkRandom = context.random();
        int i = context.chunkPos().getStartX() + chunkRandom.nextInt(16);
        int j = context.chunkPos().getStartZ() + chunkRandom.nextInt(16);
        int k = context.chunkGenerator().getSeaLevel();
        HeightContext heightContext = new HeightContext(context.chunkGenerator(), context.world());
        int l = this.height.get(chunkRandom, heightContext);
        VerticalBlockSample verticalBlockSample = context.chunkGenerator().getColumnSample(i, j, context.world(), context.noiseConfig());
        BlockPos.Mutable mutable = new BlockPos.Mutable(i, l, j);
        while (l > k) {
            BlockState blockState = verticalBlockSample.getState(l);
            BlockState blockState2 = verticalBlockSample.getState(--l);
            if (!blockState.isAir() || !blockState2.isOf(Blocks.SOUL_SAND) && !blockState2.isSideSolidFullSquare(EmptyBlockView.INSTANCE, mutable.setY(l), Direction.UP)) continue;
            break;
        }
        if (l <= k) {
            return Optional.empty();
        }
        BlockPos blockPos = new BlockPos(i, l, j);
        return Optional.of(new Structure.StructurePosition(blockPos, structurePiecesCollector -> NetherFossilGenerator.addPieces(context.structureTemplateManager(), structurePiecesCollector, chunkRandom, blockPos)));
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.NETHER_FOSSIL;
    }
}

