/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.ChunkRandom;

public class MineshaftFeature
extends StructureFeature<MineshaftFeatureConfig> {
    public MineshaftFeature(Codec<MineshaftFeatureConfig> codec) {
        super(codec, MineshaftFeature::method_38678);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, ChunkPos chunkPos, ChunkPos chunkPos2, MineshaftFeatureConfig mineshaftFeatureConfig, HeightLimitView heightLimitView) {
        chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
        double d = mineshaftFeatureConfig.probability;
        return chunkRandom.nextDouble() < d;
    }

    private static void method_38678(class_6626 arg, MineshaftFeatureConfig mineshaftFeatureConfig, class_6622.class_6623 arg2) {
        if (!arg2.validBiome().test(arg2.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(arg2.chunkPos().getCenterX()), BiomeCoords.fromBlock(50), BiomeCoords.fromBlock(arg2.chunkPos().getCenterZ())))) {
            return;
        }
        MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(0, arg2.random(), arg2.chunkPos().getOffsetX(2), arg2.chunkPos().getOffsetZ(2), mineshaftFeatureConfig.type);
        arg.addPiece(mineshaftRoom);
        mineshaftRoom.fillOpenings(mineshaftRoom, arg, arg2.random());
        if (mineshaftFeatureConfig.type == Type.MESA) {
            int i = -5;
            BlockBox blockBox = arg.method_38721();
            int j = arg2.chunkGenerator().getSeaLevel() - blockBox.getMaxY() + blockBox.getBlockCountY() / 2 - -5;
            arg.method_38715(j);
        } else {
            arg.method_38716(arg2.chunkGenerator().getSeaLevel(), arg2.chunkGenerator().getMinimumY(), arg2.random(), 10);
        }
    }

    public static enum Type implements StringIdentifiable
    {
        NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
        MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

        public static final Codec<Type> CODEC;
        private static final Map<String, Type> BY_NAME;
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

        private static Type byName(String name) {
            return BY_NAME.get(name);
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
            CODEC = StringIdentifiable.createCodec(Type::values, Type::byName);
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, type -> type));
        }
    }
}

