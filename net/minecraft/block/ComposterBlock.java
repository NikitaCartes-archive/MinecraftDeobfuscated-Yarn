/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ComposterBlock
extends Block
implements InventoryProvider {
    public static final IntegerProperty LEVEL = Properties.COMPOSTER_LEVEL;
    public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<ItemConvertible>();
    public static final VoxelShape RAY_TRACE_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE = SystemUtil.consume(new VoxelShape[9], voxelShapes -> {
        for (int i = 0; i < 8; ++i) {
            voxelShapes[i] = VoxelShapes.combineAndSimplify(RAY_TRACE_SHAPE, Block.createCuboidShape(2.0, Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);
        }
        voxelShapes[8] = voxelShapes[7];
    });

    public static void registerDefaultCompostableItems() {
        ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue(-1.0f);
        float f = 0.3f;
        float g = 0.5f;
        float h = 0.65f;
        float i = 0.85f;
        float j = 1.0f;
        ComposterBlock.registerCompostableItem(0.3f, Items.JUNGLE_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.OAK_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.SPRUCE_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.DARK_OAK_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.ACACIA_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.BIRCH_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.OAK_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.SPRUCE_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.BIRCH_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.JUNGLE_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.ACACIA_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.DARK_OAK_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.BEETROOT_SEEDS);
        ComposterBlock.registerCompostableItem(0.3f, Items.DRIED_KELP);
        ComposterBlock.registerCompostableItem(0.3f, Items.GRASS);
        ComposterBlock.registerCompostableItem(0.3f, Items.KELP);
        ComposterBlock.registerCompostableItem(0.3f, Items.MELON_SEEDS);
        ComposterBlock.registerCompostableItem(0.3f, Items.PUMPKIN_SEEDS);
        ComposterBlock.registerCompostableItem(0.3f, Items.SEAGRASS);
        ComposterBlock.registerCompostableItem(0.3f, Items.SWEET_BERRIES);
        ComposterBlock.registerCompostableItem(0.3f, Items.WHEAT_SEEDS);
        ComposterBlock.registerCompostableItem(0.5f, Items.DRIED_KELP_BLOCK);
        ComposterBlock.registerCompostableItem(0.5f, Items.TALL_GRASS);
        ComposterBlock.registerCompostableItem(0.5f, Items.CACTUS);
        ComposterBlock.registerCompostableItem(0.5f, Items.SUGAR_CANE);
        ComposterBlock.registerCompostableItem(0.5f, Items.VINE);
        ComposterBlock.registerCompostableItem(0.5f, Items.MELON_SLICE);
        ComposterBlock.registerCompostableItem(0.65f, Items.SEA_PICKLE);
        ComposterBlock.registerCompostableItem(0.65f, Items.LILY_PAD);
        ComposterBlock.registerCompostableItem(0.65f, Items.PUMPKIN);
        ComposterBlock.registerCompostableItem(0.65f, Items.CARVED_PUMPKIN);
        ComposterBlock.registerCompostableItem(0.65f, Items.MELON);
        ComposterBlock.registerCompostableItem(0.65f, Items.APPLE);
        ComposterBlock.registerCompostableItem(0.65f, Items.BEETROOT);
        ComposterBlock.registerCompostableItem(0.65f, Items.CARROT);
        ComposterBlock.registerCompostableItem(0.65f, Items.COCOA_BEANS);
        ComposterBlock.registerCompostableItem(0.65f, Items.POTATO);
        ComposterBlock.registerCompostableItem(0.65f, Items.WHEAT);
        ComposterBlock.registerCompostableItem(0.65f, Items.BROWN_MUSHROOM);
        ComposterBlock.registerCompostableItem(0.65f, Items.RED_MUSHROOM);
        ComposterBlock.registerCompostableItem(0.65f, Items.MUSHROOM_STEM);
        ComposterBlock.registerCompostableItem(0.65f, Items.DANDELION);
        ComposterBlock.registerCompostableItem(0.65f, Items.POPPY);
        ComposterBlock.registerCompostableItem(0.65f, Items.BLUE_ORCHID);
        ComposterBlock.registerCompostableItem(0.65f, Items.ALLIUM);
        ComposterBlock.registerCompostableItem(0.65f, Items.AZURE_BLUET);
        ComposterBlock.registerCompostableItem(0.65f, Items.RED_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.ORANGE_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.WHITE_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.PINK_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.OXEYE_DAISY);
        ComposterBlock.registerCompostableItem(0.65f, Items.CORNFLOWER);
        ComposterBlock.registerCompostableItem(0.65f, Items.LILY_OF_THE_VALLEY);
        ComposterBlock.registerCompostableItem(0.65f, Items.WITHER_ROSE);
        ComposterBlock.registerCompostableItem(0.65f, Items.FERN);
        ComposterBlock.registerCompostableItem(0.65f, Items.SUNFLOWER);
        ComposterBlock.registerCompostableItem(0.65f, Items.LILAC);
        ComposterBlock.registerCompostableItem(0.65f, Items.ROSE_BUSH);
        ComposterBlock.registerCompostableItem(0.65f, Items.PEONY);
        ComposterBlock.registerCompostableItem(0.65f, Items.LARGE_FERN);
        ComposterBlock.registerCompostableItem(0.85f, Items.HAY_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.BROWN_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.RED_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.BREAD);
        ComposterBlock.registerCompostableItem(0.85f, Items.BAKED_POTATO);
        ComposterBlock.registerCompostableItem(0.85f, Items.COOKIE);
        ComposterBlock.registerCompostableItem(1.0f, Items.CAKE);
        ComposterBlock.registerCompostableItem(1.0f, Items.PUMPKIN_PIE);
    }

    private static void registerCompostableItem(float f, ItemConvertible itemConvertible) {
        ITEM_TO_LEVEL_INCREASE_CHANCE.put((ItemConvertible)itemConvertible.asItem(), f);
    }

    public ComposterBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(LEVEL, 0));
    }

    @Environment(value=EnvType.CLIENT)
    public static void playEffects(World world, BlockPos blockPos, boolean bl) {
        BlockState blockState = world.getBlockState(blockPos);
        world.playSound(blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), bl ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        double d = blockState.getOutlineShape(world, blockPos).method_1102(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        double e = 0.13125f;
        double f = 0.7375f;
        Random random = world.getRandom();
        for (int i = 0; i < 10; ++i) {
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.COMPOSTER, (double)blockPos.getX() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), (double)blockPos.getY() + d + (double)random.nextFloat() * (1.0 - d), (double)blockPos.getZ() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), g, h, j);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return LEVEL_TO_COLLISION_SHAPE[blockState.get(LEVEL)];
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return RAY_TRACE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return LEVEL_TO_COLLISION_SHAPE[0];
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.get(LEVEL) == 7) {
            world.getBlockTickScheduler().schedule(blockPos, blockState.getBlock(), 20);
        }
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        int i = blockState.get(LEVEL);
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem())) {
            if (i < 7 && !world.isClient) {
                boolean bl = ComposterBlock.addToComposter(blockState, world, blockPos, itemStack);
                world.playLevelEvent(1500, blockPos, bl ? 1 : 0);
                if (!playerEntity.abilities.creativeMode) {
                    itemStack.subtractAmount(1);
                }
            }
            return true;
        }
        if (i == 8) {
            if (!world.isClient) {
                float f = 0.7f;
                double d = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
                double e = (double)(world.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
                double g = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
                ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, new ItemStack(Items.BONE_MEAL));
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
            ComposterBlock.emptyComposter(blockState, world, blockPos);
            world.playSound(null, blockPos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    private static void emptyComposter(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
        iWorld.setBlockState(blockPos, (BlockState)blockState.with(LEVEL, 0), 3);
    }

    private static boolean addToComposter(BlockState blockState, IWorld iWorld, BlockPos blockPos, ItemStack itemStack) {
        int i = blockState.get(LEVEL);
        float f = ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(itemStack.getItem());
        if (i == 0 && f > 0.0f || iWorld.getRandom().nextDouble() < (double)f) {
            int j = i + 1;
            iWorld.setBlockState(blockPos, (BlockState)blockState.with(LEVEL, j), 3);
            if (j == 7) {
                iWorld.getBlockTickScheduler().schedule(blockPos, blockState.getBlock(), 20);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (blockState.get(LEVEL) == 7) {
            world.setBlockState(blockPos, (BlockState)blockState.cycle(LEVEL), 3);
            world.playSound(null, blockPos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.onScheduledTick(blockState, world, blockPos, random);
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        return blockState.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }

    @Override
    public SidedInventory getInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
        int i = blockState.get(LEVEL);
        if (i == 8) {
            return new FullComposterInventory(blockState, iWorld, blockPos, new ItemStack(Items.BONE_MEAL));
        }
        if (i < 7) {
            return new ComposterInventory(blockState, iWorld, blockPos);
        }
        return new DummyInventory();
    }

    static class ComposterInventory
    extends BasicInventory
    implements SidedInventory {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean dirty;

        public ComposterInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
            super(1);
            this.state = blockState;
            this.world = iWorld;
            this.pos = blockPos;
        }

        @Override
        public int getInvMaxStackAmount() {
            return 1;
        }

        @Override
        public int[] getInvAvailableSlots(Direction direction) {
            int[] nArray;
            if (direction == Direction.UP) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
            return !this.dirty && direction == Direction.UP && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem());
        }

        @Override
        public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
            return false;
        }

        @Override
        public void markDirty() {
            ItemStack itemStack = this.getInvStack(0);
            if (!itemStack.isEmpty()) {
                this.dirty = true;
                ComposterBlock.addToComposter(this.state, this.world, this.pos, itemStack);
                this.removeInvStack(0);
            }
        }
    }

    static class FullComposterInventory
    extends BasicInventory
    implements SidedInventory {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean dirty;

        public FullComposterInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos, ItemStack itemStack) {
            super(itemStack);
            this.state = blockState;
            this.world = iWorld;
            this.pos = blockPos;
        }

        @Override
        public int getInvMaxStackAmount() {
            return 1;
        }

        @Override
        public int[] getInvAvailableSlots(Direction direction) {
            int[] nArray;
            if (direction == Direction.DOWN) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        @Override
        public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
            return !this.dirty && direction == Direction.DOWN && itemStack.getItem() == Items.BONE_MEAL;
        }

        @Override
        public void markDirty() {
            ComposterBlock.emptyComposter(this.state, this.world, this.pos);
            this.dirty = true;
        }
    }

    static class DummyInventory
    extends BasicInventory
    implements SidedInventory {
        public DummyInventory() {
            super(0);
        }

        @Override
        public int[] getInvAvailableSlots(Direction direction) {
            return new int[0];
        }

        @Override
        public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        @Override
        public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
            return false;
        }
    }
}

