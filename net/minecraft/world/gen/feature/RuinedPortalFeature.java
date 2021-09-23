/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class RuinedPortalFeature
extends StructureFeature<RuinedPortalFeatureConfig> {
    private static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
    private static final float field_31512 = 0.05f;
    private static final float field_31513 = 0.5f;
    private static final float field_31514 = 0.5f;
    private static final float field_31508 = 0.8f;
    private static final float field_31509 = 0.8f;
    private static final float field_31510 = 0.5f;
    private static final int field_31511 = 15;

    public RuinedPortalFeature(Codec<RuinedPortalFeatureConfig> codec) {
        super(codec, RuinedPortalFeature::method_38684);
    }

    private static void method_38684(class_6626 arg, RuinedPortalFeatureConfig ruinedPortalFeatureConfig, class_6622.class_6623 arg2) {
        RuinedPortalStructurePiece.VerticalPlacement verticalPlacement;
        RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
        if (ruinedPortalFeatureConfig.portalType == Type.DESERT) {
            verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED;
            properties.airPocket = false;
            properties.mossiness = 0.0f;
        } else if (ruinedPortalFeatureConfig.portalType == Type.JUNGLE) {
            verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
            properties.airPocket = arg2.random().nextFloat() < 0.5f;
            properties.mossiness = 0.8f;
            properties.overgrown = true;
            properties.vines = true;
        } else if (ruinedPortalFeatureConfig.portalType == Type.SWAMP) {
            verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
            properties.airPocket = false;
            properties.mossiness = 0.5f;
            properties.vines = true;
        } else if (ruinedPortalFeatureConfig.portalType == Type.MOUNTAIN) {
            bl = arg2.random().nextFloat() < 0.5f;
            verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
            properties.airPocket = bl || arg2.random().nextFloat() < 0.5f;
        } else if (ruinedPortalFeatureConfig.portalType == Type.OCEAN) {
            verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
            properties.airPocket = false;
            properties.mossiness = 0.8f;
        } else if (ruinedPortalFeatureConfig.portalType == Type.NETHER) {
            verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER;
            properties.airPocket = arg2.random().nextFloat() < 0.5f;
            properties.mossiness = 0.0f;
            properties.replaceWithBlackstone = true;
        } else {
            bl = arg2.random().nextFloat() < 0.5f;
            verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
            properties.airPocket = bl || arg2.random().nextFloat() < 0.5f;
        }
        Identifier identifier = arg2.random().nextFloat() < 0.05f ? new Identifier(RARE_PORTAL_STRUCTURE_IDS[arg2.random().nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]) : new Identifier(COMMON_PORTAL_STRUCTURE_IDS[arg2.random().nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
        Structure structure = arg2.structureManager().getStructureOrBlank(identifier);
        BlockRotation blockRotation = Util.getRandom(BlockRotation.values(), (Random)arg2.random());
        BlockMirror blockMirror = arg2.random().nextFloat() < 0.5f ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
        BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
        BlockPos blockPos2 = arg2.chunkPos().getStartPos();
        BlockBox blockBox = structure.calculateBoundingBox(blockPos2, blockRotation, blockPos, blockMirror);
        BlockPos blockPos3 = blockBox.getCenter();
        int i = blockPos3.getX();
        int j = blockPos3.getZ();
        int k = arg2.chunkGenerator().getHeight(i, j, RuinedPortalStructurePiece.getHeightmapType(verticalPlacement), arg2.heightAccessor()) - 1;
        int l = RuinedPortalFeature.getFloorHeight(arg2.random(), arg2.chunkGenerator(), verticalPlacement, properties.airPocket, k, blockBox.getBlockCountY(), blockBox, arg2.heightAccessor());
        BlockPos blockPos4 = new BlockPos(blockPos2.getX(), l, blockPos2.getZ());
        if (!arg2.validBiome().test(arg2.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos4.getX()), BiomeCoords.fromBlock(blockPos4.getY()), BiomeCoords.fromBlock(blockPos4.getZ())))) {
            return;
        }
        if (ruinedPortalFeatureConfig.portalType == Type.MOUNTAIN || ruinedPortalFeatureConfig.portalType == Type.OCEAN || ruinedPortalFeatureConfig.portalType == Type.STANDARD) {
            properties.cold = RuinedPortalFeature.isColdAt(blockPos4, arg2.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos4.getX()), BiomeCoords.fromBlock(blockPos4.getY()), BiomeCoords.fromBlock(blockPos4.getZ())));
        }
        arg.addPiece(new RuinedPortalStructurePiece(arg2.structureManager(), blockPos4, verticalPlacement, properties, identifier, structure, blockRotation, blockMirror, blockPos));
    }

    private static boolean isColdAt(BlockPos pos, Biome biome) {
        return biome.getTemperature(pos) < 0.15f;
    }

    private static int getFloorHeight(Random random, ChunkGenerator chunkGenerator, RuinedPortalStructurePiece.VerticalPlacement verticalPlacement, boolean airPocket, int height, int blockCountY, BlockBox box, HeightLimitView world) {
        int l;
        int i = world.getBottomY() + 15;
        if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER) {
            j = airPocket ? MathHelper.nextBetween(random, 32, 100) : (random.nextFloat() < 0.5f ? MathHelper.nextBetween(random, 27, 29) : MathHelper.nextBetween(random, 29, 100));
        } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN) {
            k = height - blockCountY;
            j = RuinedPortalFeature.choosePlacementHeight(random, 70, k);
        } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND) {
            k = height - blockCountY;
            j = RuinedPortalFeature.choosePlacementHeight(random, i, k);
        } else {
            j = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED ? height - blockCountY + MathHelper.nextBetween(random, 2, 8) : height;
        }
        ImmutableList<BlockPos> list = ImmutableList.of(new BlockPos(box.getMinX(), 0, box.getMinZ()), new BlockPos(box.getMaxX(), 0, box.getMinZ()), new BlockPos(box.getMinX(), 0, box.getMaxZ()), new BlockPos(box.getMaxX(), 0, box.getMaxZ()));
        List list2 = list.stream().map(blockPos -> chunkGenerator.getColumnSample(blockPos.getX(), blockPos.getZ(), world)).collect(Collectors.toList());
        Heightmap.Type type = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
        block0: for (l = j; l > i; --l) {
            int m = 0;
            for (VerticalBlockSample verticalBlockSample : list2) {
                BlockState blockState = verticalBlockSample.getState(l);
                if (!type.getBlockPredicate().test(blockState) || ++m != 3) continue;
                break block0;
            }
        }
        return l;
    }

    private static int choosePlacementHeight(Random random, int min, int max) {
        if (min < max) {
            return MathHelper.nextBetween(random, min, max);
        }
        return max;
    }

    public static enum Type implements StringIdentifiable
    {
        STANDARD("standard"),
        DESERT("desert"),
        JUNGLE("jungle"),
        SWAMP("swamp"),
        MOUNTAIN("mountain"),
        OCEAN("ocean"),
        NETHER("nether");

        public static final Codec<Type> CODEC;
        private static final Map<String, Type> BY_NAME;
        private final String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Type byName(String name) {
            return BY_NAME.get(name);
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

