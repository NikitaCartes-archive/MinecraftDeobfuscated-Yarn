/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuinedPortalFeaturePiece
extends SimpleStructurePiece {
    private static final Logger field_24992 = LogManager.getLogger();
    private final Identifier template;
    private final BlockRotation rotation;
    private final BlockMirror mirror;
    private final VerticalPlacement verticalPlacement;
    private final Properties properties;

    public RuinedPortalFeaturePiece(BlockPos pos, VerticalPlacement verticalPlacement, Properties properties, Identifier template, Structure structure, BlockRotation rotation, BlockMirror mirror, BlockPos center) {
        super(StructurePieceType.RUINED_PORTAL, 0);
        this.pos = pos;
        this.template = template;
        this.rotation = rotation;
        this.mirror = mirror;
        this.verticalPlacement = verticalPlacement;
        this.properties = properties;
        this.processProperties(structure, center);
    }

    public RuinedPortalFeaturePiece(StructureManager manager, CompoundTag tag) {
        super(StructurePieceType.RUINED_PORTAL, tag);
        this.template = new Identifier(tag.getString("Template"));
        this.rotation = BlockRotation.valueOf(tag.getString("Rotation"));
        this.mirror = BlockMirror.valueOf(tag.getString("Mirror"));
        this.verticalPlacement = VerticalPlacement.getFromId(tag.getString("VerticalPlacement"));
        this.properties = (Properties)Properties.CODEC.parse(new Dynamic<Tag>(NbtOps.INSTANCE, tag.get("Properties"))).getOrThrow(true, field_24992::error);
        Structure structure = manager.getStructureOrBlank(this.template);
        this.processProperties(structure, new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2));
    }

    @Override
    protected void toNbt(CompoundTag tag2) {
        super.toNbt(tag2);
        tag2.putString("Template", this.template.toString());
        tag2.putString("Rotation", this.rotation.name());
        tag2.putString("Mirror", this.mirror.name());
        tag2.putString("VerticalPlacement", this.verticalPlacement.getId());
        Properties.CODEC.encodeStart(NbtOps.INSTANCE, this.properties).resultOrPartial(field_24992::error).ifPresent(tag -> tag2.put("Properties", (Tag)tag));
    }

    private void processProperties(Structure structure, BlockPos center) {
        BlockIgnoreStructureProcessor blockIgnoreStructureProcessor = this.properties.airPocket ? BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS : BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS;
        ArrayList<StructureProcessorRule> list = Lists.newArrayList();
        list.add(RuinedPortalFeaturePiece.createReplacementRule(Blocks.GOLD_BLOCK, 0.3f, Blocks.AIR));
        list.add(this.createLavaReplacementRule());
        if (!this.properties.cold) {
            list.add(RuinedPortalFeaturePiece.createReplacementRule(Blocks.NETHERRACK, 0.07f, Blocks.MAGMA_BLOCK));
        }
        StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(this.rotation).setMirror(this.mirror).setPosition(center).addProcessor(blockIgnoreStructureProcessor).addProcessor(new RuleStructureProcessor(list)).addProcessor(new BlockAgeStructureProcessor(this.properties.mossiness));
        if (this.properties.replaceWithBlackstone) {
            structurePlacementData.addProcessor(BlackstoneReplacementStructureProcessor.INSTANCE);
        }
        this.setStructureData(structure, this.pos, structurePlacementData);
    }

    private StructureProcessorRule createLavaReplacementRule() {
        if (this.verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR) {
            return RuinedPortalFeaturePiece.createReplacementRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
        }
        if (this.properties.cold) {
            return RuinedPortalFeaturePiece.createReplacementRule(Blocks.LAVA, Blocks.NETHERRACK);
        }
        return RuinedPortalFeaturePiece.createReplacementRule(Blocks.LAVA, 0.2f, Blocks.MAGMA_BLOCK);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos2) {
        if (!boundingBox.contains(this.pos)) {
            return true;
        }
        boundingBox.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
        boolean bl = super.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos2);
        this.placeNetherrackBase(random, serverWorldAccess);
        this.updateNetherracksInBound(random, serverWorldAccess);
        if (this.properties.vines || this.properties.overgrown) {
            BlockPos.stream(this.getBoundingBox()).forEach(blockPos -> {
                if (this.properties.vines) {
                    this.generateVines(random, serverWorldAccess, (BlockPos)blockPos);
                }
                if (this.properties.overgrown) {
                    this.generateOvergrownLeaves(random, serverWorldAccess, (BlockPos)blockPos);
                }
            });
        }
        return bl;
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, WorldAccess world, Random random, BlockBox boundingBox) {
    }

    private void generateVines(Random random, WorldAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir() || blockState.isOf(Blocks.VINE)) {
            return;
        }
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState2 = world.getBlockState(blockPos);
        if (!blockState2.isAir()) {
            return;
        }
        if (!Block.isFaceFullSquare(blockState.getCollisionShape(world, pos), direction)) {
            return;
        }
        BooleanProperty booleanProperty = VineBlock.getFacingProperty(direction.getOpposite());
        world.setBlockState(blockPos, (BlockState)Blocks.VINE.getDefaultState().with(booleanProperty, true), 3);
    }

    private void generateOvergrownLeaves(Random random, WorldAccess world, BlockPos pos) {
        if (random.nextFloat() < 0.5f && world.getBlockState(pos).isOf(Blocks.NETHERRACK) && world.getBlockState(pos.up()).isAir()) {
            world.setBlockState(pos.up(), (BlockState)Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), 3);
        }
    }

    private void updateNetherracksInBound(Random random, WorldAccess world) {
        for (int i = this.boundingBox.minX + 1; i < this.boundingBox.maxX; ++i) {
            for (int j = this.boundingBox.minZ + 1; j < this.boundingBox.maxZ; ++j) {
                BlockPos blockPos = new BlockPos(i, this.boundingBox.minY, j);
                if (!world.getBlockState(blockPos).isOf(Blocks.NETHERRACK)) continue;
                this.updateNetherracks(random, world, blockPos.down());
            }
        }
    }

    private void updateNetherracks(Random random, WorldAccess world, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        this.placeNetherrackBottom(random, world, mutable);
        for (int i = 8; i > 0 && random.nextFloat() < 0.5f; --i) {
            mutable.move(Direction.DOWN);
            this.placeNetherrackBottom(random, world, mutable);
        }
    }

    private void placeNetherrackBase(Random random, WorldAccess world) {
        boolean bl = this.verticalPlacement == VerticalPlacement.ON_LAND_SURFACE || this.verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR;
        Vec3i vec3i = this.boundingBox.getCenter();
        int i = vec3i.getX();
        int j = vec3i.getZ();
        float[] fs = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.9f, 0.9f, 0.8f, 0.7f, 0.6f, 0.4f, 0.2f};
        int k = fs.length;
        int l = (this.boundingBox.getBlockCountX() + this.boundingBox.getBlockCountZ()) / 2;
        int m = random.nextInt(Math.max(1, 8 - l / 2));
        int n = 3;
        BlockPos.Mutable mutable = BlockPos.ORIGIN.mutableCopy();
        for (int o = i - k; o <= i + k; ++o) {
            for (int p = j - k; p <= j + k; ++p) {
                int q = Math.abs(o - i) + Math.abs(p - j);
                int r = Math.max(0, q + m);
                if (r >= k) continue;
                float f = fs[r];
                if (!(random.nextDouble() < (double)f)) continue;
                int s = RuinedPortalFeaturePiece.getBaseHeight(world, o, p, this.verticalPlacement);
                int t = bl ? s : Math.min(this.boundingBox.minY, s);
                mutable.set(o, t, p);
                if (Math.abs(t - this.boundingBox.minY) > 3 || !this.canFillNetherrack(world, mutable)) continue;
                this.placeNetherrackBottom(random, world, mutable);
                if (this.properties.overgrown) {
                    this.generateOvergrownLeaves(random, world, mutable);
                }
                this.updateNetherracks(random, world, (BlockPos)mutable.down());
            }
        }
    }

    private boolean canFillNetherrack(WorldAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return !blockState.isOf(Blocks.AIR) && !blockState.isOf(Blocks.OBSIDIAN) && (this.verticalPlacement == VerticalPlacement.IN_NETHER || !blockState.isOf(Blocks.LAVA));
    }

    private void placeNetherrackBottom(Random random, WorldAccess world, BlockPos pos) {
        if (!this.properties.cold && random.nextFloat() < 0.07f) {
            world.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), 3);
        } else {
            world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 3);
        }
    }

    private static int getBaseHeight(WorldAccess world, int x, int y, VerticalPlacement verticalPlacement) {
        return world.getTopY(RuinedPortalFeaturePiece.getHeightmapType(verticalPlacement), x, y) - 1;
    }

    public static Heightmap.Type getHeightmapType(VerticalPlacement verticalPlacement) {
        return verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
    }

    private static StructureProcessorRule createReplacementRule(Block old, float chance, Block updated) {
        return new StructureProcessorRule(new RandomBlockMatchRuleTest(old, chance), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
    }

    private static StructureProcessorRule createReplacementRule(Block old, Block updated) {
        return new StructureProcessorRule(new BlockMatchRuleTest(old), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
    }

    public static enum VerticalPlacement {
        ON_LAND_SURFACE("on_land_surface"),
        PARTLY_BURIED("partly_buried"),
        ON_OCEAN_FLOOR("on_ocean_floor"),
        IN_MOUNTAIN("in_mountain"),
        UNDERGROUND("underground"),
        IN_NETHER("in_nether");

        private static final Map<String, VerticalPlacement> VERTICAL_PLACEMENTS;
        private final String id;

        private VerticalPlacement(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public static VerticalPlacement getFromId(String id) {
            return VERTICAL_PLACEMENTS.get(id);
        }

        static {
            VERTICAL_PLACEMENTS = Arrays.stream(VerticalPlacement.values()).collect(Collectors.toMap(VerticalPlacement::getId, verticalPlacement -> verticalPlacement));
        }
    }

    public static class Properties {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.BOOL.fieldOf("cold")).forGetter(properties -> properties.cold), ((MapCodec)Codec.FLOAT.fieldOf("mossiness")).forGetter(properties -> Float.valueOf(properties.mossiness)), ((MapCodec)Codec.BOOL.fieldOf("air_pocket")).forGetter(properties -> properties.airPocket), ((MapCodec)Codec.BOOL.fieldOf("overgrown")).forGetter(properties -> properties.overgrown), ((MapCodec)Codec.BOOL.fieldOf("vines")).forGetter(properties -> properties.vines), ((MapCodec)Codec.BOOL.fieldOf("replace_with_blackstone")).forGetter(properties -> properties.replaceWithBlackstone)).apply((Applicative<Properties, ?>)instance, Properties::new));
        public boolean cold;
        public float mossiness = 0.2f;
        public boolean airPocket;
        public boolean overgrown;
        public boolean vines;
        public boolean replaceWithBlackstone;

        public Properties() {
        }

        public <T> Properties(boolean bl, float f, boolean bl2, boolean bl3, boolean bl4, boolean bl5) {
            this.cold = bl;
            this.mossiness = f;
            this.airPocket = bl2;
            this.overgrown = bl3;
            this.vines = bl4;
            this.replaceWithBlackstone = bl5;
        }
    }
}

