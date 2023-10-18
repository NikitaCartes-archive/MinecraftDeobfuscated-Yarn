package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeCache;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class CrafterBlock extends BlockWithEntity {
	public static final MapCodec<CrafterBlock> CODEC = createCodec(CrafterBlock::new);
	public static final BooleanProperty CRAFTING = Properties.CRAFTING;
	public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
	private static final EnumProperty<JigsawOrientation> ORIENTATION = Properties.ORIENTATION;
	private static final int field_46802 = 6;
	private static final RecipeCache recipeCache = new RecipeCache(10);

	public CrafterBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(ORIENTATION, JigsawOrientation.NORTH_UP)
				.with(TRIGGERED, Boolean.valueOf(false))
				.with(CRAFTING, Boolean.valueOf(false))
		);
	}

	@Override
	protected MapCodec<CrafterBlock> getCodec() {
		return CODEC;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return world.getBlockEntity(pos) instanceof CrafterBlockEntity crafterBlockEntity ? crafterBlockEntity.getComparatorOutput() : 0;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos);
		boolean bl2 = (Boolean)state.get(TRIGGERED);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (bl && !bl2) {
			world.scheduleBlockTick(pos, this, 1);
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
			this.setTriggered(blockEntity, true);
		} else if (!bl && bl2) {
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)).with(CRAFTING, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
			this.setTriggered(blockEntity, false);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.craft(state, world, pos);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : validateTicker(type, BlockEntityType.CRAFTER, CrafterBlockEntity::tickCrafting);
	}

	private void setTriggered(@Nullable BlockEntity blockEntity, boolean triggered) {
		if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
			crafterBlockEntity.setTriggered(triggered);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		CrafterBlockEntity crafterBlockEntity = new CrafterBlockEntity(pos, state);
		crafterBlockEntity.setTriggered(state.contains(TRIGGERED) && (Boolean)state.get(TRIGGERED));
		return crafterBlockEntity;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getPlayerLookDirection().getOpposite();

		Direction direction2 = switch (direction) {
			case DOWN -> ctx.getHorizontalPlayerFacing().getOpposite();
			case UP -> ctx.getHorizontalPlayerFacing();
			case NORTH, SOUTH, WEST, EAST -> Direction.UP;
		};
		return this.getDefaultState()
			.with(ORIENTATION, JigsawOrientation.byDirections(direction, direction2))
			.with(TRIGGERED, Boolean.valueOf(ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())));
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof CrafterBlockEntity crafterBlockEntity) {
			crafterBlockEntity.setCustomName(itemStack.getName());
		}

		if ((Boolean)state.get(TRIGGERED)) {
			world.scheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		ItemScatterer.onStateReplaced(state, newState, world, pos);
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CrafterBlockEntity) {
				player.openHandledScreen((CrafterBlockEntity)blockEntity);
			}

			return ActionResult.CONSUME;
		}
	}

	protected void craft(BlockState state, ServerWorld world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof CrafterBlockEntity crafterBlockEntity) {
			Optional<CraftingRecipe> optional = getCraftingRecipe(world, crafterBlockEntity);
			if (optional.isEmpty()) {
				world.syncWorldEvent(WorldEvents.CRAFTER_FAILS, pos, 0);
			} else {
				crafterBlockEntity.setCraftingTicksRemaining(6);
				world.setBlockState(pos, state.with(CRAFTING, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
				CraftingRecipe craftingRecipe = (CraftingRecipe)optional.get();
				ItemStack itemStack = craftingRecipe.craft(crafterBlockEntity, world.getRegistryManager());
				itemStack.onCraftByCrafter(world);
				this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack, state);
				craftingRecipe.getRemainder(crafterBlockEntity).forEach(stack -> this.transferOrSpawnStack(world, pos, crafterBlockEntity, stack, state));
				crafterBlockEntity.method_11282().forEach(stack -> {
					if (!stack.isEmpty()) {
						stack.decrement(1);
					}
				});
			}
		}
	}

	public static Optional<CraftingRecipe> getCraftingRecipe(World world, RecipeInputInventory inputInventory) {
		return recipeCache.getRecipe(world, inputInventory);
	}

	private void transferOrSpawnStack(World world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack stack, BlockState state) {
		Direction direction = ((JigsawOrientation)state.get(ORIENTATION)).getFacing();
		Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
		ItemStack itemStack = stack.copy();
		if (inventory instanceof CrafterBlockEntity) {
			while (!itemStack.isEmpty()) {
				ItemStack itemStack2 = itemStack.copyWithCount(1);
				ItemStack itemStack3 = HopperBlockEntity.transfer(blockEntity, inventory, itemStack2, direction.getOpposite());
				if (!itemStack3.isEmpty()) {
					break;
				}

				itemStack.decrement(1);
			}
		} else if (inventory != null) {
			while (!itemStack.isEmpty()) {
				int i = itemStack.getCount();
				itemStack = HopperBlockEntity.transfer(blockEntity, inventory, itemStack, direction.getOpposite());
				if (i == itemStack.getCount()) {
					break;
				}
			}
		}

		if (!itemStack.isEmpty()) {
			Vec3d vec3d = Vec3d.ofCenter(pos).offset(direction, 0.7);
			ItemDispenserBehavior.spawnItem(world, itemStack, 6, direction, vec3d);
			world.syncWorldEvent(WorldEvents.CRAFTER_CRAFTS, pos, 0);
			world.syncWorldEvent(WorldEvents.CRAFTER_SHOOTS, pos, direction.getId());
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION, TRIGGERED, CRAFTING);
	}
}
