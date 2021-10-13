/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.EightWayDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final UpgradeData NO_UPGRADE_DATA = new UpgradeData(EmptyBlockView.INSTANCE);
    private static final String INDICES_KEY = "Indices";
    private static final EightWayDirection[] EIGHT_WAYS = EightWayDirection.values();
    private final EnumSet<EightWayDirection> sidesToUpgrade = EnumSet.noneOf(EightWayDirection.class);
    private final int[][] centerIndicesToUpgrade;
    static final Map<Block, Logic> BLOCK_TO_LOGIC = new IdentityHashMap<Block, Logic>();
    static final Set<Logic> CALLBACK_LOGICS = Sets.newHashSet();

    private UpgradeData(HeightLimitView world) {
        this.centerIndicesToUpgrade = new int[world.countVerticalSections()][];
    }

    public UpgradeData(NbtCompound nbt, HeightLimitView world) {
        this(world);
        if (nbt.contains(INDICES_KEY, 10)) {
            NbtCompound nbtCompound = nbt.getCompound(INDICES_KEY);
            for (int i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
                String string = String.valueOf(i);
                if (!nbtCompound.contains(string, 11)) continue;
                this.centerIndicesToUpgrade[i] = nbtCompound.getIntArray(string);
            }
        }
        int j = nbt.getInt("Sides");
        for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
            if ((j & 1 << eightWayDirection.ordinal()) == 0) continue;
            this.sidesToUpgrade.add(eightWayDirection);
        }
    }

    public void upgrade(WorldChunk chunk) {
        this.upgradeCenter(chunk);
        for (EightWayDirection eightWayDirection : EIGHT_WAYS) {
            UpgradeData.upgradeSide(chunk, eightWayDirection);
        }
        World world = chunk.getWorld();
        CALLBACK_LOGICS.forEach(logic -> logic.postUpdate(world));
    }

    private static void upgradeSide(WorldChunk chunk, EightWayDirection side) {
        World world = chunk.getWorld();
        if (!chunk.getUpgradeData().sidesToUpgrade.remove((Object)side)) {
            return;
        }
        Set<Direction> set = side.getDirections();
        boolean i = false;
        int j = 15;
        boolean bl = set.contains(Direction.EAST);
        boolean bl2 = set.contains(Direction.WEST);
        boolean bl3 = set.contains(Direction.SOUTH);
        boolean bl4 = set.contains(Direction.NORTH);
        boolean bl5 = set.size() == 1;
        ChunkPos chunkPos = chunk.getPos();
        int k = chunkPos.getStartX() + (bl5 && (bl4 || bl3) ? 1 : (bl2 ? 0 : 15));
        int l = chunkPos.getStartX() + (bl5 && (bl4 || bl3) ? 14 : (bl2 ? 0 : 15));
        int m = chunkPos.getStartZ() + (bl5 && (bl || bl2) ? 1 : (bl4 ? 0 : 15));
        int n = chunkPos.getStartZ() + (bl5 && (bl || bl2) ? 14 : (bl4 ? 0 : 15));
        Direction[] directions = Direction.values();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos : BlockPos.iterate(k, world.getBottomY(), m, l, world.getTopY() - 1, n)) {
            BlockState blockState;
            BlockState blockState2 = blockState = world.getBlockState(blockPos);
            for (Direction direction : directions) {
                mutable.set((Vec3i)blockPos, direction);
                blockState2 = UpgradeData.applyAdjacentBlock(blockState2, direction, world, blockPos, mutable);
            }
            Block.replace(blockState, blockState2, world, blockPos, 18);
        }
    }

    private static BlockState applyAdjacentBlock(BlockState oldState, Direction dir, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
        return BLOCK_TO_LOGIC.getOrDefault(oldState.getBlock(), BuiltinLogic.DEFAULT).getUpdatedState(oldState, dir, world.getBlockState(otherPos), world, currentPos, otherPos);
    }

    private void upgradeCenter(WorldChunk chunk) {
        int i;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        ChunkPos chunkPos = chunk.getPos();
        World worldAccess = chunk.getWorld();
        for (i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            ChunkSection chunkSection = chunk.getSection(i);
            int[] is = this.centerIndicesToUpgrade[i];
            this.centerIndicesToUpgrade[i] = null;
            if (is == null || is.length <= 0) continue;
            Direction[] directions = Direction.values();
            PalettedContainer<BlockState> palettedContainer = chunkSection.getBlockStateContainer();
            for (int j : is) {
                BlockState blockState;
                int k = j & 0xF;
                int l = j >> 8 & 0xF;
                int m = j >> 4 & 0xF;
                mutable.set(chunkPos.getStartX() + k, chunkSection.getYOffset() + l, chunkPos.getStartZ() + m);
                BlockState blockState2 = blockState = palettedContainer.get(j);
                for (Direction direction : directions) {
                    mutable2.set((Vec3i)mutable, direction);
                    if (ChunkSectionPos.getSectionCoord(mutable.getX()) != chunkPos.x || ChunkSectionPos.getSectionCoord(mutable.getZ()) != chunkPos.z) continue;
                    blockState2 = UpgradeData.applyAdjacentBlock(blockState2, direction, worldAccess, mutable, mutable2);
                }
                Block.replace(blockState, blockState2, worldAccess, mutable, 18);
            }
        }
        for (i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            if (this.centerIndicesToUpgrade[i] != null) {
                LOGGER.warn("Discarding update data for section {} for chunk ({} {})", (Object)worldAccess.sectionIndexToCoord(i), (Object)chunkPos.x, (Object)chunkPos.z);
            }
            this.centerIndicesToUpgrade[i] = null;
        }
    }

    public boolean isDone() {
        for (int[] is : this.centerIndicesToUpgrade) {
            if (is == null) continue;
            return false;
        }
        return this.sidesToUpgrade.isEmpty();
    }

    public NbtCompound toNbt() {
        int i;
        NbtCompound nbtCompound = new NbtCompound();
        NbtCompound nbtCompound2 = new NbtCompound();
        for (i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            String string = String.valueOf(i);
            if (this.centerIndicesToUpgrade[i] == null || this.centerIndicesToUpgrade[i].length == 0) continue;
            nbtCompound2.putIntArray(string, this.centerIndicesToUpgrade[i]);
        }
        if (!nbtCompound2.isEmpty()) {
            nbtCompound.put(INDICES_KEY, nbtCompound2);
        }
        i = 0;
        for (EightWayDirection eightWayDirection : this.sidesToUpgrade) {
            i |= 1 << eightWayDirection.ordinal();
        }
        nbtCompound.putByte("Sides", (byte)i);
        return nbtCompound;
    }

    static enum BuiltinLogic implements Logic
    {
        BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}){

            @Override
            public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess world, BlockPos blockPos, BlockPos blockPos2) {
                return blockState;
            }
        }
        ,
        DEFAULT(new Block[0]){

            @Override
            public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess world, BlockPos blockPos, BlockPos blockPos2) {
                return blockState.getStateForNeighborUpdate(direction, world.getBlockState(blockPos2), world, blockPos, blockPos2);
            }
        }
        ,
        CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}){

            @Override
            public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess world, BlockPos blockPos, BlockPos blockPos2) {
                if (blockState2.isOf(blockState.getBlock()) && direction.getAxis().isHorizontal() && blockState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE && blockState2.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
                    Direction direction2 = blockState.get(ChestBlock.FACING);
                    if (direction.getAxis() != direction2.getAxis() && direction2 == blockState2.get(ChestBlock.FACING)) {
                        ChestType chestType = direction == direction2.rotateYClockwise() ? ChestType.LEFT : ChestType.RIGHT;
                        world.setBlockState(blockPos2, (BlockState)blockState2.with(ChestBlock.CHEST_TYPE, chestType.getOpposite()), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                        if (direction2 == Direction.NORTH || direction2 == Direction.EAST) {
                            BlockEntity blockEntity = world.getBlockEntity(blockPos);
                            BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
                            if (blockEntity instanceof ChestBlockEntity && blockEntity2 instanceof ChestBlockEntity) {
                                ChestBlockEntity.copyInventory((ChestBlockEntity)blockEntity, (ChestBlockEntity)blockEntity2);
                            }
                        }
                        return (BlockState)blockState.with(ChestBlock.CHEST_TYPE, chestType);
                    }
                }
                return blockState;
            }
        }
        ,
        LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}){
            private final ThreadLocal<List<ObjectSet<BlockPos>>> distanceToPositions = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));

            @Override
            public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess world, BlockPos blockPos, BlockPos blockPos2) {
                BlockState blockState3 = blockState.getStateForNeighborUpdate(direction, world.getBlockState(blockPos2), world, blockPos, blockPos2);
                if (blockState != blockState3) {
                    int i = blockState3.get(Properties.DISTANCE_1_7);
                    List<ObjectSet<BlockPos>> list = this.distanceToPositions.get();
                    if (list.isEmpty()) {
                        for (int j = 0; j < 7; ++j) {
                            list.add(new ObjectOpenHashSet());
                        }
                    }
                    list.get(i).add(blockPos.toImmutable());
                }
                return blockState;
            }

            @Override
            public void postUpdate(WorldAccess world) {
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                List<ObjectSet<BlockPos>> list = this.distanceToPositions.get();
                for (int i = 2; i < list.size(); ++i) {
                    int j = i - 1;
                    ObjectSet<BlockPos> objectSet = list.get(j);
                    ObjectSet<BlockPos> objectSet2 = list.get(i);
                    for (BlockPos blockPos : objectSet) {
                        BlockState blockState = world.getBlockState(blockPos);
                        if (blockState.get(Properties.DISTANCE_1_7) < j) continue;
                        world.setBlockState(blockPos, (BlockState)blockState.with(Properties.DISTANCE_1_7, j), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                        if (i == 7) continue;
                        for (Direction direction : DIRECTIONS) {
                            mutable.set((Vec3i)blockPos, direction);
                            BlockState blockState2 = world.getBlockState(mutable);
                            if (!blockState2.contains(Properties.DISTANCE_1_7) || blockState.get(Properties.DISTANCE_1_7) <= i) continue;
                            objectSet2.add(mutable.toImmutable());
                        }
                    }
                }
                list.clear();
            }
        }
        ,
        STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}){

            @Override
            public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess world, BlockPos blockPos, BlockPos blockPos2) {
                GourdBlock gourdBlock;
                if (blockState.get(StemBlock.AGE) == 7 && blockState2.isOf(gourdBlock = ((StemBlock)blockState.getBlock()).getGourdBlock())) {
                    return (BlockState)gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction);
                }
                return blockState;
            }
        };

        public static final Direction[] DIRECTIONS;

        BuiltinLogic(Block ... blocks) {
            this(false, blocks);
        }

        BuiltinLogic(boolean bl, Block ... blocks) {
            for (Block block : blocks) {
                BLOCK_TO_LOGIC.put(block, this);
            }
            if (bl) {
                CALLBACK_LOGICS.add(this);
            }
        }

        static {
            DIRECTIONS = Direction.values();
        }
    }

    public static interface Logic {
        public BlockState getUpdatedState(BlockState var1, Direction var2, BlockState var3, WorldAccess var4, BlockPos var5, BlockPos var6);

        default public void postUpdate(WorldAccess world) {
        }
    }
}

