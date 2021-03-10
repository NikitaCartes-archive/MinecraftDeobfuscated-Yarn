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
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class RuinedPortalFeature
extends StructureFeature<RuinedPortalFeatureConfig> {
    private static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

    public RuinedPortalFeature(Codec<RuinedPortalFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<RuinedPortalFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    private static boolean isColdAt(BlockPos pos, Biome biome) {
        return biome.getTemperature(pos) < 0.15f;
    }

    private static int method_27211(Random random, ChunkGenerator chunkGenerator, RuinedPortalStructurePiece.VerticalPlacement verticalPlacement, boolean bl, int i, int j, BlockBox blockBox, HeightLimitView heightLimitView) {
        int m;
        int l;
        int k;
        if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER) {
            k = bl ? MathHelper.nextBetween(random, 32, 100) : (random.nextFloat() < 0.5f ? MathHelper.nextBetween(random, 27, 29) : MathHelper.nextBetween(random, 29, 100));
        } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN) {
            l = i - j;
            k = RuinedPortalFeature.choosePlacementHeight(random, 70, l);
        } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND) {
            l = i - j;
            k = RuinedPortalFeature.choosePlacementHeight(random, 15, l);
        } else {
            k = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED ? i - j + MathHelper.nextBetween(random, 2, 8) : i;
        }
        ImmutableList<BlockPos> list = ImmutableList.of(new BlockPos(blockBox.minX, 0, blockBox.minZ), new BlockPos(blockBox.maxX, 0, blockBox.minZ), new BlockPos(blockBox.minX, 0, blockBox.maxZ), new BlockPos(blockBox.maxX, 0, blockBox.maxZ));
        List list2 = list.stream().map(blockPos -> chunkGenerator.getColumnSample(blockPos.getX(), blockPos.getZ(), heightLimitView)).collect(Collectors.toList());
        Heightmap.Type type = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        block0: for (m = k; m > 15; --m) {
            int n = 0;
            mutable.set(0, m, 0);
            for (VerticalBlockSample verticalBlockSample : list2) {
                BlockState blockState = verticalBlockSample.getState(mutable);
                if (!type.getBlockPredicate().test(blockState) || ++n != 3) continue;
                break block0;
            }
        }
        return m;
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

    public static class Start
    extends StructureStart<RuinedPortalFeatureConfig> {
        protected Start(StructureFeature<RuinedPortalFeatureConfig> structureFeature, ChunkPos chunkPos, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkPos, blockBox, i, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, RuinedPortalFeatureConfig ruinedPortalFeatureConfig, HeightLimitView heightLimitView) {
            boolean bl;
            RuinedPortalStructurePiece.VerticalPlacement verticalPlacement;
            RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
            if (ruinedPortalFeatureConfig.portalType == Type.DESERT) {
                verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED;
                properties.airPocket = false;
                properties.mossiness = 0.0f;
            } else if (ruinedPortalFeatureConfig.portalType == Type.JUNGLE) {
                verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
                properties.airPocket = this.random.nextFloat() < 0.5f;
                properties.mossiness = 0.8f;
                properties.overgrown = true;
                properties.vines = true;
            } else if (ruinedPortalFeatureConfig.portalType == Type.SWAMP) {
                verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
                properties.airPocket = false;
                properties.mossiness = 0.5f;
                properties.vines = true;
            } else if (ruinedPortalFeatureConfig.portalType == Type.MOUNTAIN) {
                bl = this.random.nextFloat() < 0.5f;
                verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
                properties.airPocket = bl || this.random.nextFloat() < 0.5f;
            } else if (ruinedPortalFeatureConfig.portalType == Type.OCEAN) {
                verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
                properties.airPocket = false;
                properties.mossiness = 0.8f;
            } else if (ruinedPortalFeatureConfig.portalType == Type.NETHER) {
                verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER;
                properties.airPocket = this.random.nextFloat() < 0.5f;
                properties.mossiness = 0.0f;
                properties.replaceWithBlackstone = true;
            } else {
                bl = this.random.nextFloat() < 0.5f;
                verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
                properties.airPocket = bl || this.random.nextFloat() < 0.5f;
            }
            Identifier identifier = this.random.nextFloat() < 0.05f ? new Identifier(RARE_PORTAL_STRUCTURE_IDS[this.random.nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]) : new Identifier(COMMON_PORTAL_STRUCTURE_IDS[this.random.nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
            Structure structure = structureManager.getStructureOrBlank(identifier);
            BlockRotation blockRotation = Util.getRandom(BlockRotation.values(), (Random)this.random);
            BlockMirror blockMirror = this.random.nextFloat() < 0.5f ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
            BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
            BlockPos blockPos2 = chunkPos.getStartPos();
            BlockBox blockBox = structure.calculateBoundingBox(blockPos2, blockRotation, blockPos, blockMirror);
            BlockPos blockPos3 = blockBox.getCenter();
            int i = blockPos3.getX();
            int j = blockPos3.getZ();
            int k = chunkGenerator.getHeight(i, j, RuinedPortalStructurePiece.getHeightmapType(verticalPlacement), heightLimitView) - 1;
            int l = RuinedPortalFeature.method_27211(this.random, chunkGenerator, verticalPlacement, properties.airPocket, k, blockBox.getBlockCountY(), blockBox, heightLimitView);
            BlockPos blockPos4 = new BlockPos(blockPos2.getX(), l, blockPos2.getZ());
            if (ruinedPortalFeatureConfig.portalType == Type.MOUNTAIN || ruinedPortalFeatureConfig.portalType == Type.OCEAN || ruinedPortalFeatureConfig.portalType == Type.STANDARD) {
                properties.cold = RuinedPortalFeature.isColdAt(blockPos4, biome);
            }
            this.children.add(new RuinedPortalStructurePiece(blockPos4, verticalPlacement, properties, identifier, structure, blockRotation, blockMirror, blockPos));
            this.setBoundingBoxFromChildren();
        }
    }
}

