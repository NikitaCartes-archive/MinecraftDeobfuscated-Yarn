package net.minecraft.block;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignChangingItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public abstract class AbstractSignBlock extends BlockWithEntity implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final float field_31243 = 4.0F;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
	private final WoodType type;

	protected AbstractSignBlock(AbstractBlock.Settings settings, WoodType type) {
		super(settings);
		this.type = type;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SignBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		SignChangingItem signChangingItem2 = itemStack.getItem() instanceof SignChangingItem signChangingItem ? signChangingItem : null;
		boolean bl = signChangingItem2 != null && player.getAbilities().allowModifyWorld;
		if (world.isClient) {
			return bl ? ActionResult.SUCCESS : ActionResult.CONSUME;
		} else if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
			boolean bl2 = signBlockEntity.isPlayerFacingFront(player);
			SignText signText = signBlockEntity.getText(bl2);
			if (signBlockEntity.isWaxed()) {
				boolean bl3 = signText.runCommandClickEvent((ServerPlayerEntity)player, (ServerWorld)world, pos);
				if (!bl3) {
					world.playSound(null, signBlockEntity.getPos(), SoundEvents.BLOCK_SIGN_WAXED_INTERACT_FAIL, SoundCategory.BLOCKS);
				}

				return ActionResult.SUCCESS;
			} else if (bl
				&& !this.isOtherPlayerEditing(player, signBlockEntity)
				&& signChangingItem2.canUseOnSignText(signText, player)
				&& signChangingItem2.useOnSign(world, signBlockEntity, bl2, player)) {
				if (!player.isCreative()) {
					itemStack.decrement(1);
				}

				if (player instanceof ServerPlayerEntity serverPlayerEntity) {
					Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, pos, itemStack);
				}

				world.emitGameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getPos(), GameEvent.Emitter.of(player, signBlockEntity.getCachedState()));
				player.incrementStat(Stats.USED.getOrCreateStat(item));
				return ActionResult.SUCCESS;
			} else if (!this.isOtherPlayerEditing(player, signBlockEntity)) {
				this.openEditScreen(player, signBlockEntity, bl2);
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		} else {
			return ActionResult.PASS;
		}
	}

	public abstract float getRotationDegrees(BlockState state);

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	public WoodType getWoodType() {
		return this.type;
	}

	public static WoodType getWoodType(Block block) {
		WoodType woodType;
		if (block instanceof AbstractSignBlock) {
			woodType = ((AbstractSignBlock)block).getWoodType();
		} else {
			woodType = WoodType.OAK;
		}

		return woodType;
	}

	public void openEditScreen(PlayerEntity player, SignBlockEntity blockEntity, boolean front) {
		blockEntity.setEditor(player.getUuid());
		player.openEditSignScreen(blockEntity, front);
	}

	private boolean isOtherPlayerEditing(PlayerEntity player, SignBlockEntity blockEntity) {
		UUID uUID = blockEntity.getEditor();
		return uUID != null && !uUID.equals(player.getUuid());
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, BlockEntityType.SIGN, SignBlockEntity::tick);
	}
}
