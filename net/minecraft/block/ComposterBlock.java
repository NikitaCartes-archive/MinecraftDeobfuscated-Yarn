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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
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
    public static final IntProperty LEVEL = Properties.LEVEL_8;
    public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<ItemConvertible>();
    public static final VoxelShape RAY_TRACE_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE = Util.make(new VoxelShape[9], voxelShapes -> {
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

    private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {
        ITEM_TO_LEVEL_INCREASE_CHANCE.put((ItemConvertible)item.asItem(), levelIncreaseChance);
    }

    public ComposterBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, 0));
    }

    @Environment(value=EnvType.CLIENT)
    public static void playEffects(World world, BlockPos pos, boolean fill) {
        BlockState blockState = world.getBlockState(pos);
        world.playSound(pos.getX(), (double)pos.getY(), (double)pos.getZ(), fill ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        double d = blockState.getOutlineShape(world, pos).getEndingCoord(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        double e = 0.13125f;
        double f = 0.7375f;
        Random random = world.getRandom();
        for (int i = 0; i < 10; ++i) {
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.COMPOSTER, (double)pos.getX() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), (double)pos.getY() + d + (double)random.nextFloat() * (1.0 - d), (double)pos.getZ() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), g, h, j);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return LEVEL_TO_COLLISION_SHAPE[state.get(LEVEL)];
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, BlockView view, BlockPos pos) {
        return RAY_TRACE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return LEVEL_TO_COLLISION_SHAPE[0];
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (state.get(LEVEL) == 7) {
            world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(LEVEL);
        ItemStack itemStack = player.getStackInHand(hand);
        if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem())) {
            if (i < 7 && !world.isClient) {
                boolean bl = ComposterBlock.addToComposter(state, world, pos, itemStack);
                world.playLevelEvent(1500, pos, bl ? 1 : 0);
                if (!player.abilities.creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        if (i == 8) {
            if (!world.isClient) {
                float f = 0.7f;
                double d = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
                double e = (double)(world.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
                double g = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, new ItemStack(Items.BONE_MEAL));
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
            ComposterBlock.emptyComposter(state, world, pos);
            world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static void emptyComposter(BlockState state, IWorld world, BlockPos pos) {
        world.setBlockState(pos, (BlockState)state.with(LEVEL, 0), 3);
    }

    private static boolean addToComposter(BlockState state, IWorld world, BlockPos pos, ItemStack item) {
        int i = state.get(LEVEL);
        float f = ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(item.getItem());
        if (i == 0 && f > 0.0f || world.getRandom().nextDouble() < (double)f) {
            int j = i + 1;
            world.setBlockState(pos, (BlockState)state.with(LEVEL, j), 3);
            if (j == 7) {
                world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
            }
            return true;
        }
        return false;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LEVEL) == 7) {
            world.setBlockState(pos, (BlockState)state.cycle(LEVEL), 3);
            world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        int i = state.get(LEVEL);
        if (i == 8) {
            return new FullComposterInventory(state, world, pos, new ItemStack(Items.BONE_MEAL));
        }
        if (i < 7) {
            return new ComposterInventory(state, world, pos);
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

        public ComposterInventory(BlockState state, IWorld world, BlockPos pos) {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        @Override
        public int getInvMaxStackAmount() {
            return 1;
        }

        @Override
        public int[] getInvAvailableSlots(Direction side) {
            int[] nArray;
            if (side == Direction.UP) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir) {
            return !this.dirty && dir == Direction.UP && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
        }

        @Override
        public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
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

        public FullComposterInventory(BlockState state, IWorld world, BlockPos pos, ItemStack outputItem) {
            super(outputItem);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        @Override
        public int getInvMaxStackAmount() {
            return 1;
        }

        @Override
        public int[] getInvAvailableSlots(Direction side) {
            int[] nArray;
            if (side == Direction.DOWN) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir) {
            return false;
        }

        @Override
        public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
            return !this.dirty && dir == Direction.DOWN && stack.getItem() == Items.BONE_MEAL;
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
        public int[] getInvAvailableSlots(Direction side) {
            return new int[0];
        }

        @Override
        public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir) {
            return false;
        }

        @Override
        public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
            return false;
        }
    }
}

