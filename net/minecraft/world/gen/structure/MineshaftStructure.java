/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class MineshaftStructure
extends Structure {
    public static final Codec<MineshaftStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(MineshaftStructure.configCodecBuilder(instance), ((MapCodec)Type.CODEC.fieldOf("mineshaft_type")).forGetter(mineshaftStructure -> mineshaftStructure.type)).apply((Applicative<MineshaftStructure, ?>)instance, MineshaftStructure::new));
    private final Type type;

    public MineshaftStructure(Structure.Config config, Type type) {
        super(config);
        this.type = type;
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        context.random().nextDouble();
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getCenterX(), 50, chunkPos.getStartZ());
        StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
        int i = this.addPieces(structurePiecesCollector, context);
        return Optional.of(new Structure.StructurePosition(blockPos.add(0, i, 0), Either.right(structurePiecesCollector)));
    }

    private int addPieces(StructurePiecesCollector collector, Structure.Context context) {
        ChunkPos chunkPos = context.chunkPos();
        ChunkRandom chunkRandom = context.random();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(0, chunkRandom, chunkPos.getOffsetX(2), chunkPos.getOffsetZ(2), this.type);
        collector.addPiece(mineshaftRoom);
        mineshaftRoom.fillOpenings(mineshaftRoom, collector, chunkRandom);
        int i = chunkGenerator.getSeaLevel();
        if (this.type == Type.MESA) {
            BlockPos blockPos = collector.getBoundingBox().getCenter();
            int j = chunkGenerator.getHeight(blockPos.getX(), blockPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world(), context.noiseConfig());
            int k = j <= i ? i : MathHelper.nextBetween((Random)chunkRandom, i, j);
            int l = k - blockPos.getY();
            collector.shift(l);
            return l;
        }
        return collector.shiftInto(i, chunkGenerator.getMinimumY(), chunkRandom, 10);
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
        private static final IntFunction<Type> BY_ID;
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

        public static Type byId(int id) {
            return BY_ID.apply(id);
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
            BY_ID = ValueLists.createIdToValueFunction(Enum::ordinal, Type.values(), ValueLists.OutOfBoundsHandling.ZERO);
        }
    }
}

