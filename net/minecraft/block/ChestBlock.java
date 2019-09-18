/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChestBlock
extends BlockWithEntity
implements Waterloggable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
    protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
    protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final PropertyRetriever<Inventory> INVENTORY_RETRIEVER = new PropertyRetriever<Inventory>(){

        public Inventory method_17461(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
            return new DoubleInventory(chestBlockEntity, chestBlockEntity2);
        }

        public Inventory method_17460(ChestBlockEntity chestBlockEntity) {
            return chestBlockEntity;
        }

        @Override
        public /* synthetic */ Object getFromSingleChest(ChestBlockEntity chestBlockEntity) {
            return this.method_17460(chestBlockEntity);
        }

        @Override
        public /* synthetic */ Object getFromDoubleChest(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
            return this.method_17461(chestBlockEntity, chestBlockEntity2);
        }
    };
    private static final PropertyRetriever<NameableContainerProvider> NAME_RETRIEVER = new PropertyRetriever<NameableContainerProvider>(){

        public NameableContainerProvider method_17463(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
            final DoubleInventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
            return new NameableContainerProvider(){

                @Override
                @Nullable
                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                        chestBlockEntity.checkLootInteraction(playerInventory.player);
                        chestBlockEntity2.checkLootInteraction(playerInventory.player);
                        return GenericContainer.createGeneric9x6(i, playerInventory, inventory);
                    }
                    return null;
                }

                @Override
                public Text getDisplayName() {
                    if (chestBlockEntity.hasCustomName()) {
                        return chestBlockEntity.getDisplayName();
                    }
                    if (chestBlockEntity2.hasCustomName()) {
                        return chestBlockEntity2.getDisplayName();
                    }
                    return new TranslatableText("container.chestDouble", new Object[0]);
                }
            };
        }

        public NameableContainerProvider method_17462(ChestBlockEntity chestBlockEntity) {
            return chestBlockEntity;
        }

        @Override
        public /* synthetic */ Object getFromSingleChest(ChestBlockEntity chestBlockEntity) {
            return this.method_17462(chestBlockEntity);
        }

        @Override
        public /* synthetic */ Object getFromDoubleChest(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
            return this.method_17463(chestBlockEntity, chestBlockEntity2);
        }
    };

    protected ChestBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(FACING, Direction.NORTH)).with(CHEST_TYPE, ChestType.SINGLE)).with(WATERLOGGED, false));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(BlockState blockState) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        }
        if (blockState2.getBlock() == this && direction.getAxis().isHorizontal()) {
            ChestType chestType = blockState2.get(CHEST_TYPE);
            if (blockState.get(CHEST_TYPE) == ChestType.SINGLE && chestType != ChestType.SINGLE && blockState.get(FACING) == blockState2.get(FACING) && ChestBlock.getFacing(blockState2) == direction.getOpposite()) {
                return (BlockState)blockState.with(CHEST_TYPE, chestType.getOpposite());
            }
        } else if (ChestBlock.getFacing(blockState) == direction) {
            return (BlockState)blockState.with(CHEST_TYPE, ChestType.SINGLE);
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        if (blockState.get(CHEST_TYPE) == ChestType.SINGLE) {
            return SINGLE_SHAPE;
        }
        switch (ChestBlock.getFacing(blockState)) {
            default: {
                return DOUBLE_NORTH_SHAPE;
            }
            case SOUTH: {
                return DOUBLE_SOUTH_SHAPE;
            }
            case WEST: {
                return DOUBLE_WEST_SHAPE;
            }
            case EAST: 
        }
        return DOUBLE_EAST_SHAPE;
    }

    public static Direction getFacing(BlockState blockState) {
        Direction direction = blockState.get(FACING);
        return blockState.get(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        Direction direction3;
        ChestType chestType = ChestType.SINGLE;
        Direction direction = itemPlacementContext.getPlayerFacing().getOpposite();
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
        boolean bl = itemPlacementContext.shouldCancelInteraction();
        Direction direction2 = itemPlacementContext.getSide();
        if (direction2.getAxis().isHorizontal() && bl && (direction3 = this.getNeighborChestDirection(itemPlacementContext, direction2.getOpposite())) != null && direction3.getAxis() != direction2.getAxis()) {
            direction = direction3;
            ChestType chestType2 = chestType = direction.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
        }
        if (chestType == ChestType.SINGLE && !bl) {
            if (direction == this.getNeighborChestDirection(itemPlacementContext, direction.rotateYClockwise())) {
                chestType = ChestType.LEFT;
            } else if (direction == this.getNeighborChestDirection(itemPlacementContext, direction.rotateYCounterclockwise())) {
                chestType = ChestType.RIGHT;
            }
        }
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, direction)).with(CHEST_TYPE, chestType)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(blockState);
    }

    @Nullable
    private Direction getNeighborChestDirection(ItemPlacementContext itemPlacementContext, Direction direction) {
        BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos().offset(direction));
        return blockState.getBlock() == this && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? blockState.get(FACING) : null;
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (itemStack.hasCustomName() && (blockEntity = world.getBlockEntity(blockPos)) instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity).setCustomName(itemStack.getName());
        }
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, blockPos, (Inventory)((Object)blockEntity));
            world.updateHorizontalAdjacent(blockPos, this);
        }
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        NameableContainerProvider nameableContainerProvider = this.createContainerProvider(blockState, world, blockPos);
        if (nameableContainerProvider != null) {
            playerEntity.openContainer(nameableContainerProvider);
            playerEntity.incrementStat(this.getOpenStat());
        }
        return true;
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
    }

    @Nullable
    public static <T> T retrieve(BlockState blockState, IWorld iWorld, BlockPos blockPos, boolean bl, PropertyRetriever<T> propertyRetriever) {
        ChestType chestType2;
        BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
        if (!(blockEntity instanceof ChestBlockEntity)) {
            return null;
        }
        if (!bl && ChestBlock.isChestBlocked(iWorld, blockPos)) {
            return null;
        }
        ChestBlockEntity chestBlockEntity = (ChestBlockEntity)blockEntity;
        ChestType chestType = blockState.get(CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return propertyRetriever.getFromSingleChest(chestBlockEntity);
        }
        BlockPos blockPos2 = blockPos.offset(ChestBlock.getFacing(blockState));
        BlockState blockState2 = iWorld.getBlockState(blockPos2);
        if (blockState2.getBlock() == blockState.getBlock() && (chestType2 = blockState2.get(CHEST_TYPE)) != ChestType.SINGLE && chestType != chestType2 && blockState2.get(FACING) == blockState.get(FACING)) {
            if (!bl && ChestBlock.isChestBlocked(iWorld, blockPos2)) {
                return null;
            }
            BlockEntity blockEntity2 = iWorld.getBlockEntity(blockPos2);
            if (blockEntity2 instanceof ChestBlockEntity) {
                ChestBlockEntity chestBlockEntity2 = chestType == ChestType.RIGHT ? chestBlockEntity : (ChestBlockEntity)blockEntity2;
                ChestBlockEntity chestBlockEntity3 = chestType == ChestType.RIGHT ? (ChestBlockEntity)blockEntity2 : chestBlockEntity;
                return propertyRetriever.getFromDoubleChest(chestBlockEntity2, chestBlockEntity3);
            }
        }
        return propertyRetriever.getFromSingleChest(chestBlockEntity);
    }

    @Nullable
    public static Inventory getInventory(BlockState blockState, World world, BlockPos blockPos, boolean bl) {
        return ChestBlock.retrieve(blockState, world, blockPos, bl, INVENTORY_RETRIEVER);
    }

    @Override
    @Nullable
    public NameableContainerProvider createContainerProvider(BlockState blockState, World world, BlockPos blockPos) {
        return ChestBlock.retrieve(blockState, world, blockPos, false, NAME_RETRIEVER);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new ChestBlockEntity();
    }

    private static boolean isChestBlocked(IWorld iWorld, BlockPos blockPos) {
        return ChestBlock.hasBlockOnTop(iWorld, blockPos) || ChestBlock.hasOcelotOnTop(iWorld, blockPos);
    }

    private static boolean hasBlockOnTop(BlockView blockView, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.up();
        return blockView.getBlockState(blockPos2).isSimpleFullBlock(blockView, blockPos2);
    }

    private static boolean hasOcelotOnTop(IWorld iWorld, BlockPos blockPos) {
        List<CatEntity> list = iWorld.getNonSpectatingEntities(CatEntity.class, new Box(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 2, blockPos.getZ() + 1));
        if (!list.isEmpty()) {
            for (CatEntity catEntity : list) {
                if (!catEntity.isSitting()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        return Container.calculateComparatorOutput(ChestBlock.getInventory(blockState, world, blockPos, false));
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHEST_TYPE, WATERLOGGED);
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }

    static interface PropertyRetriever<T> {
        public T getFromDoubleChest(ChestBlockEntity var1, ChestBlockEntity var2);

        public T getFromSingleChest(ChestBlockEntity var1);
    }
}

