/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.ChunkRandom;

public class MineshaftFeature
extends StructureFeature {
    public static final Codec<MineshaftFeature> CODEC = RecordCodecBuilder.create(instance -> instance.group(MineshaftFeature.configCodecBuilder(instance), ((MapCodec)Type.CODEC.fieldOf("mineshaft_type")).forGetter(mineshaftFeature -> mineshaftFeature.field_37802)).apply((Applicative<MineshaftFeature, ?>)instance, MineshaftFeature::new));
    private final Type field_37802;

    public MineshaftFeature(StructureFeature.Config config, Type type) {
        super(config);
        this.field_37802 = type;
    }

    @Override
    public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
        context.random().nextDouble();
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getCenterX(), 50, chunkPos.getStartZ());
        return Optional.of(new StructureFeature.StructurePosition(blockPos, structurePiecesCollector -> this.addPieces((StructurePiecesCollector)structurePiecesCollector, blockPos, context)));
    }

    private void addPieces(StructurePiecesCollector structurePiecesCollector, BlockPos blockPos, StructureFeature.Context context) {
        ChunkPos chunkPos = context.chunkPos();
        ChunkRandom chunkRandom = context.random();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(0, chunkRandom, chunkPos.getOffsetX(2), chunkPos.getOffsetZ(2), this.field_37802);
        structurePiecesCollector.addPiece(mineshaftRoom);
        mineshaftRoom.fillOpenings(mineshaftRoom, structurePiecesCollector, chunkRandom);
        int i = chunkGenerator.getSeaLevel();
        if (this.field_37802 == Type.MESA) {
            BlockPos blockPos2 = structurePiecesCollector.getBoundingBox().getCenter();
            int j = chunkGenerator.getHeight(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world(), context.noiseConfig());
            int k = j <= i ? i : MathHelper.nextBetween((Random)chunkRandom, i, j);
            int l = k - blockPos2.getY();
            structurePiecesCollector.shift(l);
        } else {
            structurePiecesCollector.shiftInto(i, chunkGenerator.getMinimumY(), chunkRandom, 10);
        }
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.MINESHAFT;
    }

    public static enum Type implements StringIdentifiable
    {
        NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
        MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

        public static final Codec<Type> CODEC;
        private final String name;
        private final BlockState log;
        private final BlockState planks;
        private final BlockState fence;

        private Type(String name, Block log, Block planks, Block fence) {
            this.name = name;
            this.log = log.getDefaultState();
            this.planks = planks.getDefaultState();
            this.fence = fence.getDefaultState();
        }

        public String getName() {
            return this.name;
        }

        public static Type byIndex(int index) {
            if (index < 0 || index >= Type.values().length) {
                return NORMAL;
            }
            return Type.values()[index];
        }

        public BlockState getLog() {
            return this.log;
        }

        public BlockState getPlanks() {
            return this.planks;
        }

        public BlockState getFence() {
            return this.fence;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Type::values);
        }
    }
}

