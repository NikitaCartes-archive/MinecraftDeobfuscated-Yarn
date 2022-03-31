/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.LavaSubmergedBlockStructureProcessor;
import net.minecraft.structure.processor.ProtectedBlocksStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.slf4j.Logger;

public class RuinedPortalStructurePiece
extends SimpleStructurePiece {
    private static final Logger field_24992 = LogUtils.getLogger();
    private static final float field_31620 = 0.3f;
    private static final float field_31621 = 0.07f;
    private static final float field_31622 = 0.2f;
    private final VerticalPlacement verticalPlacement;
    private final Properties properties;

    public RuinedPortalStructurePiece(StructureManager manager, BlockPos pos, VerticalPlacement verticalPlacement, Properties properties, Identifier id, Structure structure, BlockRotation rotation, BlockMirror mirror, BlockPos blockPos) {
        super(StructurePieceType.RUINED_PORTAL, 0, manager, id, id.toString(), RuinedPortalStructurePiece.createPlacementData(mirror, rotation, verticalPlacement, blockPos, properties), pos);
        this.verticalPlacement = verticalPlacement;
        this.properties = properties;
    }

    public RuinedPortalStructurePiece(StructureManager manager, NbtCompound nbt) {
        super(StructurePieceType.RUINED_PORTAL, nbt, manager, id -> RuinedPortalStructurePiece.createPlacementData(manager, nbt, id));
        this.verticalPlacement = VerticalPlacement.getFromId(nbt.getString("VerticalPlacement"));
        this.properties = (Properties)Properties.CODEC.parse(new Dynamic<NbtElement>(NbtOps.INSTANCE, nbt.get("Properties"))).getOrThrow(true, field_24992::error);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putString("Rotation", this.placementData.getRotation().name());
        nbt.putString("Mirror", this.placementData.getMirror().name());
        nbt.putString("VerticalPlacement", this.verticalPlacement.getId());
        Properties.CODEC.encodeStart(NbtOps.INSTANCE, this.properties).resultOrPartial(field_24992::error).ifPresent(nbtElement -> nbt.put("Properties", (NbtElement)nbtElement));
    }

    private static StructurePlacementData createPlacementData(StructureManager manager, NbtCompound nbt, Identifier id) {
        Structure structure = manager.getStructureOrBlank(id);
        BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
        return RuinedPortalStructurePiece.createPlacementData(BlockMirror.valueOf(nbt.getString("Mirror")), BlockRotation.valueOf(nbt.getString("Rotation")), VerticalPlacement.getFromId(nbt.getString("VerticalPlacement")), blockPos, (Properties)Properties.CODEC.parse(new Dynamic<NbtElement>(NbtOps.INSTANCE, nbt.get("Properties"))).getOrThrow(true, field_24992::error));
    }

    private static StructurePlacementData createPlacementData(BlockMirror mirror, BlockRotation rotation, VerticalPlacement verticalPlacement, BlockPos pos, Properties properties) {
        BlockIgnoreStructureProcessor blockIgnoreStructureProcessor = properties.airPocket ? BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS : BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS;
        ArrayList<StructureProcessorRule> list = Lists.newArrayList();
        list.add(RuinedPortalStructurePiece.createReplacementRule(Blocks.GOLD_BLOCK, 0.3f, Blocks.AIR));
        list.add(RuinedPortalStructurePiece.createLavaReplacementRule(verticalPlacement, properties));
        if (!properties.cold) {
            list.add(RuinedPortalStructurePiece.createReplacementRule(Blocks.NETHERRACK, 0.07f, Blocks.MAGMA_BLOCK));
        }
        StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(rotation).setMirror(mirror).setPosition(pos).addProcessor(blockIgnoreStructureProcessor).addProcessor(new RuleStructureProcessor(list)).addProcessor(new BlockAgeStructureProcessor(properties.mossiness)).addProcessor(new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)).addProcessor(new LavaSubmergedBlockStructureProcessor());
        if (properties.replaceWithBlackstone) {
            structurePlacementData.addProcessor(BlackstoneReplacementStructureProcessor.INSTANCE);
        }
        return structurePlacementData;
    }

    private static StructureProcessorRule createLavaReplacementRule(VerticalPlacement verticalPlacement, Properties properties) {
        if (verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR) {
            return RuinedPortalStructurePiece.createReplacementRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
        }
        if (properties.cold) {
            return RuinedPortalStructurePiece.createReplacementRule(Blocks.LAVA, Blocks.NETHERRACK);
        }
        return RuinedPortalStructurePiece.createReplacementRule(Blocks.LAVA, 0.2f, Blocks.MAGMA_BLOCK);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos2) {
        BlockBox blockBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
        if (!chunkBox.contains(blockBox.getCenter())) {
            return;
        }
        chunkBox.encompass(blockBox);
        super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pos2);
        this.placeNetherrackBase(random, world);
        this.updateNetherracksInBound(random, world);
        if (this.properties.vines || this.properties.overgrown) {
            BlockPos.stream(this.getBoundingBox()).forEach(pos -> {
                if (this.properties.vines) {
                    this.generateVines(random, world, (BlockPos)pos);
                }
                if (this.properties.overgrown) {
                    this.generateOvergrownLeaves(random, world, (BlockPos)pos);
                }
            });
        }
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
    }

    private void generateVines(Random random, WorldAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir() || blockState.isOf(Blocks.VINE)) {
            return;
        }
        Direction direction = RuinedPortalStructurePiece.getRandomHorizontalDirection(random);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState2 = world.getBlockState(blockPos);
        if (!blockState2.isAir()) {
            return;
        }
        if (!Block.isFaceFullSquare(blockState.getCollisionShape(world, pos), direction)) {
            return;
        }
        BooleanProperty booleanProperty = VineBlock.getFacingProperty(direction.getOpposite());
        world.setBlockState(blockPos, (BlockState)Blocks.VINE.getDefaultState().with(booleanProperty, true), Block.NOTIFY_ALL);
    }

    private void generateOvergrownLeaves(Random random, WorldAccess world, BlockPos pos) {
        if (random.nextFloat() < 0.5f && world.getBlockState(pos).isOf(Blocks.NETHERRACK) && world.getBlockState(pos.up()).isAir()) {
            world.setBlockState(pos.up(), (BlockState)Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), Block.NOTIFY_ALL);
        }
    }

    private void updateNetherracksInBound(Random random, WorldAccess world) {
        for (int i = this.boundingBox.getMinX() + 1; i < this.boundingBox.getMaxX(); ++i) {
            for (int j = this.boundingBox.getMinZ() + 1; j < this.boundingBox.getMaxZ(); ++j) {
                BlockPos blockPos = new BlockPos(i, this.boundingBox.getMinY(), j);
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
        BlockPos blockPos = this.boundingBox.getCenter();
        int i = blockPos.getX();
        int j = blockPos.getZ();
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
                int s = RuinedPortalStructurePiece.getBaseHeight(world, o, p, this.verticalPlacement);
                int t = bl ? s : Math.min(this.boundingBox.getMinY(), s);
                mutable.set(o, t, p);
                if (Math.abs(t - this.boundingBox.getMinY()) > 3 || !this.canFillNetherrack(world, mutable)) continue;
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
        return !blockState.isOf(Blocks.AIR) && !blockState.isOf(Blocks.OBSIDIAN) && !blockState.isIn(BlockTags.FEATURES_CANNOT_REPLACE) && (this.verticalPlacement == VerticalPlacement.IN_NETHER || !blockState.isOf(Blocks.LAVA));
    }

    private void placeNetherrackBottom(Random random, WorldAccess world, BlockPos pos) {
        if (!this.properties.cold && random.nextFloat() < 0.07f) {
            world.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
        } else {
            world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    private static int getBaseHeight(WorldAccess world, int x, int y, VerticalPlacement verticalPlacement) {
        return world.getTopY(RuinedPortalStructurePiece.getHeightmapType(verticalPlacement), x, y) - 1;
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

    public static enum VerticalPlacement implements StringIdentifiable
    {
        ON_LAND_SURFACE("on_land_surface"),
        PARTLY_BURIED("partly_buried"),
        ON_OCEAN_FLOOR("on_ocean_floor"),
        IN_MOUNTAIN("in_mountain"),
        UNDERGROUND("underground"),
        IN_NETHER("in_nether");

        public static final StringIdentifiable.Codec<VerticalPlacement> field_37811;
        private final String id;

        private VerticalPlacement(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public static VerticalPlacement getFromId(String id) {
            return field_37811.byId(id);
        }

        @Override
        public String asString() {
            return this.id;
        }

        static {
            field_37811 = StringIdentifiable.createCodec(VerticalPlacement::values);
        }
    }

    public static class Properties {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.BOOL.fieldOf("cold")).forGetter(properties -> properties.cold), ((MapCodec)Codec.FLOAT.fieldOf("mossiness")).forGetter(properties -> Float.valueOf(properties.mossiness)), ((MapCodec)Codec.BOOL.fieldOf("air_pocket")).forGetter(properties -> properties.airPocket), ((MapCodec)Codec.BOOL.fieldOf("overgrown")).forGetter(properties -> properties.overgrown), ((MapCodec)Codec.BOOL.fieldOf("vines")).forGetter(properties -> properties.vines), ((MapCodec)Codec.BOOL.fieldOf("replace_with_blackstone")).forGetter(properties -> properties.replaceWithBlackstone)).apply((Applicative<Properties, ?>)instance, Properties::new));
        public boolean cold;
        public float mossiness;
        public boolean airPocket;
        public boolean overgrown;
        public boolean vines;
        public boolean replaceWithBlackstone;

        public Properties() {
        }

        public Properties(boolean cold, float mossiness, boolean airPocket, boolean overgrown, boolean vines, boolean replaceWithBlackstone) {
            this.cold = cold;
            this.mossiness = mossiness;
            this.airPocket = airPocket;
            this.overgrown = overgrown;
            this.vines = vines;
            this.replaceWithBlackstone = replaceWithBlackstone;
        }
    }
}

