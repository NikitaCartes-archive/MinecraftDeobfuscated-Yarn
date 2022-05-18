/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.StructureType;

public class RuinedPortalStructure
extends StructureType {
    private static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
    private static final float field_31512 = 0.05f;
    private static final int field_31511 = 15;
    private final List<Setup> setups;
    public static final Codec<RuinedPortalStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(RuinedPortalStructure.configCodecBuilder(instance), ((MapCodec)Codecs.nonEmptyList(Setup.field_37814.listOf()).fieldOf("setups")).forGetter(structure -> structure.setups)).apply((Applicative<RuinedPortalStructure, ?>)instance, RuinedPortalStructure::new));

    public RuinedPortalStructure(StructureType.Config config, List<Setup> setups) {
        super(config);
        this.setups = setups;
    }

    public RuinedPortalStructure(StructureType.Config config, Setup setup) {
        this(config, List.of(setup));
    }

    @Override
    public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
        RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
        ChunkRandom chunkRandom = context.random();
        Setup setup = null;
        if (this.setups.size() > 1) {
            float f = 0.0f;
            for (Setup setup2 : this.setups) {
                f += setup2.weight();
            }
            float g = chunkRandom.nextFloat();
            for (Setup setup3 : this.setups) {
                if (!((g -= setup3.weight() / f) < 0.0f)) continue;
                setup = setup3;
                break;
            }
        } else {
            setup = this.setups.get(0);
        }
        if (setup == null) {
            throw new IllegalStateException();
        }
        Setup setup4 = setup;
        properties.airPocket = RuinedPortalStructure.method_41682(chunkRandom, setup4.airPocketProbability());
        properties.mossiness = setup4.mossiness();
        properties.overgrown = setup4.overgrown();
        properties.vines = setup4.vines();
        properties.replaceWithBlackstone = setup4.replaceWithBlackstone();
        Identifier identifier = chunkRandom.nextFloat() < 0.05f ? new Identifier(RARE_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]) : new Identifier(COMMON_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
        Structure structure = context.structureManager().getStructureOrBlank(identifier);
        BlockRotation blockRotation = Util.getRandom(BlockRotation.values(), (Random)chunkRandom);
        BlockMirror blockMirror = chunkRandom.nextFloat() < 0.5f ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
        BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        HeightLimitView heightLimitView = context.world();
        NoiseConfig noiseConfig = context.noiseConfig();
        BlockPos blockPos2 = context.chunkPos().getStartPos();
        BlockBox blockBox = structure.calculateBoundingBox(blockPos2, blockRotation, blockPos, blockMirror);
        BlockPos blockPos3 = blockBox.getCenter();
        int i = chunkGenerator.getHeight(blockPos3.getX(), blockPos3.getZ(), RuinedPortalStructurePiece.getHeightmapType(setup4.placement()), heightLimitView, noiseConfig) - 1;
        int j = RuinedPortalStructure.getFloorHeight(chunkRandom, chunkGenerator, setup4.placement(), properties.airPocket, i, blockBox.getBlockCountY(), blockBox, heightLimitView, noiseConfig);
        BlockPos blockPos4 = new BlockPos(blockPos2.getX(), j, blockPos2.getZ());
        return Optional.of(new StructureType.StructurePosition(blockPos4, structurePiecesCollector -> {
            if (setup4.canBeCold()) {
                properties.cold = RuinedPortalStructure.isColdAt(blockPos4, context.chunkGenerator().getBiomeSource().getBiome(BiomeCoords.fromBlock(blockPos4.getX()), BiomeCoords.fromBlock(blockPos4.getY()), BiomeCoords.fromBlock(blockPos4.getZ()), noiseConfig.getMultiNoiseSampler()));
            }
            structurePiecesCollector.addPiece(new RuinedPortalStructurePiece(context.structureManager(), blockPos4, setup4.placement(), properties, identifier, structure, blockRotation, blockMirror, blockPos));
        }));
    }

    private static boolean method_41682(ChunkRandom chunkRandom, float f) {
        if (f == 0.0f) {
            return false;
        }
        if (f == 1.0f) {
            return true;
        }
        return chunkRandom.nextFloat() < f;
    }

    private static boolean isColdAt(BlockPos pos, RegistryEntry<Biome> biome) {
        return biome.value().isCold(pos);
    }

    private static int getFloorHeight(Random random, ChunkGenerator chunkGenerator, RuinedPortalStructurePiece.VerticalPlacement verticalPlacement, boolean airPocket, int height, int blockCountY, BlockBox box, HeightLimitView world, NoiseConfig noiseConfig) {
        int l;
        int i = world.getBottomY() + 15;
        if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER) {
            j = airPocket ? MathHelper.nextBetween(random, 32, 100) : (random.nextFloat() < 0.5f ? MathHelper.nextBetween(random, 27, 29) : MathHelper.nextBetween(random, 29, 100));
        } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN) {
            k = height - blockCountY;
            j = RuinedPortalStructure.choosePlacementHeight(random, 70, k);
        } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND) {
            k = height - blockCountY;
            j = RuinedPortalStructure.choosePlacementHeight(random, i, k);
        } else {
            j = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED ? height - blockCountY + MathHelper.nextBetween(random, 2, 8) : height;
        }
        ImmutableList<BlockPos> list = ImmutableList.of(new BlockPos(box.getMinX(), 0, box.getMinZ()), new BlockPos(box.getMaxX(), 0, box.getMinZ()), new BlockPos(box.getMinX(), 0, box.getMaxZ()), new BlockPos(box.getMaxX(), 0, box.getMaxZ()));
        List list2 = list.stream().map(blockPos -> chunkGenerator.getColumnSample(blockPos.getX(), blockPos.getZ(), world, noiseConfig)).collect(Collectors.toList());
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

    @Override
    public net.minecraft.structure.StructureType<?> getType() {
        return net.minecraft.structure.StructureType.RUINED_PORTAL;
    }

    public record Setup(RuinedPortalStructurePiece.VerticalPlacement placement, float airPocketProbability, float mossiness, boolean overgrown, boolean vines, boolean canBeCold, boolean replaceWithBlackstone, float weight) {
        public static final Codec<Setup> field_37814 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RuinedPortalStructurePiece.VerticalPlacement.field_37811.fieldOf("placement")).forGetter(Setup::placement), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("air_pocket_probability")).forGetter(Setup::airPocketProbability), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("mossiness")).forGetter(Setup::mossiness), ((MapCodec)Codec.BOOL.fieldOf("overgrown")).forGetter(Setup::overgrown), ((MapCodec)Codec.BOOL.fieldOf("vines")).forGetter(Setup::vines), ((MapCodec)Codec.BOOL.fieldOf("can_be_cold")).forGetter(Setup::canBeCold), ((MapCodec)Codec.BOOL.fieldOf("replace_with_blackstone")).forGetter(Setup::replaceWithBlackstone), ((MapCodec)Codecs.POSITIVE_FLOAT.fieldOf("weight")).forGetter(Setup::weight)).apply((Applicative<Setup, ?>)instance, Setup::new));
    }
}

