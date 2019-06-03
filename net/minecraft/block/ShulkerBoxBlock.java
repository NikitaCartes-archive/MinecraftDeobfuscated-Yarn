/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.container.Container;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import org.jetbrains.annotations.Nullable;

public class ShulkerBoxBlock
extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    public static final Identifier CONTENTS = new Identifier("contents");
    @Nullable
    private final DyeColor color;

    public ShulkerBoxBlock(@Nullable DyeColor dyeColor, Block.Settings settings) {
        super(settings);
        this.color = dyeColor;
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(FACING, Direction.UP));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new ShulkerBoxBlockEntity(this.color);
    }

    @Override
    public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
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
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        if (playerEntity.isSpectator()) {
            return true;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            boolean bl;
            Direction direction = blockState.get(FACING);
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
            if (shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                Box box = VoxelShapes.fullCube().getBoundingBox().stretch(0.5f * (float)direction.getOffsetX(), 0.5f * (float)direction.getOffsetY(), 0.5f * (float)direction.getOffsetZ()).shrink(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
                bl = world.doesNotCollide(box.offset(blockPos.offset(direction)));
            } else {
                bl = true;
            }
            if (bl) {
                playerEntity.openContainer(shulkerBoxBlockEntity);
                playerEntity.incrementStat(Stats.OPEN_SHULKER_BOX);
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)this.getDefaultState().with(FACING, itemPlacementContext.getSide());
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
            if (!world.isClient && playerEntity.isCreative() && !shulkerBoxBlockEntity.isInvEmpty()) {
                ItemStack itemStack = ShulkerBoxBlock.getItemStack(this.getColor());
                CompoundTag compoundTag = shulkerBoxBlockEntity.serializeInventory(new CompoundTag());
                if (!compoundTag.isEmpty()) {
                    itemStack.putSubTag("BlockEntityTag", compoundTag);
                }
                if (shulkerBoxBlockEntity.hasCustomName()) {
                    itemStack.setCustomName(shulkerBoxBlockEntity.getCustomName());
                }
                ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                shulkerBoxBlockEntity.checkLootInteraction(playerEntity);
            }
        }
        super.onBreak(world, blockPos, blockState, playerEntity);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
            builder = builder.putDrop(CONTENTS, (lootContext, consumer) -> {
                for (int i = 0; i < shulkerBoxBlockEntity.getInvSize(); ++i) {
                    consumer.accept(shulkerBoxBlockEntity.getInvStack(i));
                }
            });
        }
        return super.getDroppedStacks(blockState, builder);
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (itemStack.hasCustomName() && (blockEntity = world.getBlockEntity(blockPos)) instanceof ShulkerBoxBlockEntity) {
            ((ShulkerBoxBlockEntity)blockEntity).setCustomName(itemStack.getName());
        }
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            world.updateHorizontalAdjacent(blockPos, blockState.getBlock());
        }
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
        super.buildTooltip(itemStack, blockView, list, tooltipContext);
        CompoundTag compoundTag = itemStack.getSubTag("BlockEntityTag");
        if (compoundTag != null) {
            if (compoundTag.containsKey("LootTable", 8)) {
                list.add(new LiteralText("???????"));
            }
            if (compoundTag.containsKey("Items", 9)) {
                DefaultedList<ItemStack> defaultedList = DefaultedList.create(27, ItemStack.EMPTY);
                Inventories.fromTag(compoundTag, defaultedList);
                int i = 0;
                int j = 0;
                for (ItemStack itemStack2 : defaultedList) {
                    if (itemStack2.isEmpty()) continue;
                    ++j;
                    if (i > 4) continue;
                    ++i;
                    Text text = itemStack2.getName().deepCopy();
                    text.append(" x").append(String.valueOf(itemStack2.getCount()));
                    list.add(text);
                }
                if (j - i > 0) {
                    list.add(new TranslatableText("container.shulkerBox.more", j - i).formatted(Formatting.ITALIC));
                }
            }
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            return VoxelShapes.cuboid(((ShulkerBoxBlockEntity)blockEntity).getBoundingBox(blockState));
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean isOpaque(BlockState blockState) {
        return false;
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        return Container.calculateComparatorOutput((Inventory)((Object)world.getBlockEntity(blockPos)));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        ItemStack itemStack = super.getPickStack(blockView, blockPos, blockState);
        ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockView.getBlockEntity(blockPos);
        CompoundTag compoundTag = shulkerBoxBlockEntity.serializeInventory(new CompoundTag());
        if (!compoundTag.isEmpty()) {
            itemStack.putSubTag("BlockEntityTag", compoundTag);
        }
        return itemStack;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static DyeColor getColor(Item item) {
        return ShulkerBoxBlock.getColor(Block.getBlockFromItem(item));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static DyeColor getColor(Block block) {
        if (block instanceof ShulkerBoxBlock) {
            return ((ShulkerBoxBlock)block).getColor();
        }
        return null;
    }

    public static Block get(@Nullable DyeColor dyeColor) {
        if (dyeColor == null) {
            return Blocks.SHULKER_BOX;
        }
        switch (dyeColor) {
            case WHITE: {
                return Blocks.WHITE_SHULKER_BOX;
            }
            case ORANGE: {
                return Blocks.ORANGE_SHULKER_BOX;
            }
            case MAGENTA: {
                return Blocks.MAGENTA_SHULKER_BOX;
            }
            case LIGHT_BLUE: {
                return Blocks.LIGHT_BLUE_SHULKER_BOX;
            }
            case YELLOW: {
                return Blocks.YELLOW_SHULKER_BOX;
            }
            case LIME: {
                return Blocks.LIME_SHULKER_BOX;
            }
            case PINK: {
                return Blocks.PINK_SHULKER_BOX;
            }
            case GRAY: {
                return Blocks.GRAY_SHULKER_BOX;
            }
            case LIGHT_GRAY: {
                return Blocks.LIGHT_GRAY_SHULKER_BOX;
            }
            case CYAN: {
                return Blocks.CYAN_SHULKER_BOX;
            }
            default: {
                return Blocks.PURPLE_SHULKER_BOX;
            }
            case BLUE: {
                return Blocks.BLUE_SHULKER_BOX;
            }
            case BROWN: {
                return Blocks.BROWN_SHULKER_BOX;
            }
            case GREEN: {
                return Blocks.GREEN_SHULKER_BOX;
            }
            case RED: {
                return Blocks.RED_SHULKER_BOX;
            }
            case BLACK: 
        }
        return Blocks.BLACK_SHULKER_BOX;
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getItemStack(@Nullable DyeColor dyeColor) {
        return new ItemStack(ShulkerBoxBlock.get(dyeColor));
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }
}

