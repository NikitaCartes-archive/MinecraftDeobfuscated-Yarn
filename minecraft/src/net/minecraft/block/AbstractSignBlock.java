package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignChangingItem;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.PlainTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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

	protected AbstractSignBlock(WoodType type, AbstractBlock.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	protected abstract MapCodec<? extends AbstractSignBlock> getCodec();

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public boolean canMobSpawnInside(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SignBlockEntity(pos, state);
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
			SignChangingItem signChangingItem2 = stack.getItem() instanceof SignChangingItem signChangingItem ? signChangingItem : null;
			boolean bl = signChangingItem2 != null && player.canModifyBlocks();
			if (!world.isClient) {
				if (bl && !signBlockEntity.isWaxed() && !this.isOtherPlayerEditing(player, signBlockEntity)) {
					boolean bl2 = signBlockEntity.isPlayerFacingFront(player);
					if (signChangingItem2.canUseOnSignText(signBlockEntity.getText(bl2), player) && signChangingItem2.useOnSign(world, signBlockEntity, bl2, player)) {
						signBlockEntity.runCommandClickEvent(player, world, pos, bl2);
						player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
						world.emitGameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getPos(), GameEvent.Emitter.of(player, signBlockEntity.getCachedState()));
						if (!player.isCreative()) {
							stack.decrement(1);
						}

						return ItemActionResult.SUCCESS;
					} else {
						return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
					}
				} else {
					return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				}
			} else {
				return !bl && !signBlockEntity.isWaxed() ? ItemActionResult.CONSUME : ItemActionResult.SUCCESS;
			}
		} else {
			return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
			if (world.isClient) {
				Util.throwOrPause(new IllegalStateException("Expected to only call this on server"));
			}

			boolean bl = signBlockEntity.isPlayerFacingFront(player);
			boolean bl2 = signBlockEntity.runCommandClickEvent(player, world, pos, bl);
			if (signBlockEntity.isWaxed()) {
				world.playSound(null, signBlockEntity.getPos(), signBlockEntity.getInteractionFailSound(), SoundCategory.BLOCKS);
				return ActionResult.SUCCESS;
			} else if (bl2) {
				return ActionResult.SUCCESS;
			} else if (!this.isOtherPlayerEditing(player, signBlockEntity) && player.canModifyBlocks() && this.isTextLiteralOrEmpty(player, signBlockEntity, bl)) {
				this.openEditScreen(player, signBlockEntity, bl);
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		} else {
			return ActionResult.PASS;
		}
	}

	private boolean isTextLiteralOrEmpty(PlayerEntity player, SignBlockEntity blockEntity, boolean front) {
		SignText signText = blockEntity.getText(front);
		return Arrays.stream(signText.getMessages(player.shouldFilterText()))
			.allMatch(message -> message.equals(ScreenTexts.EMPTY) || message.getContent() instanceof PlainTextContent);
	}

	public abstract float getRotationDegrees(BlockState state);

	public Vec3d getCenter(BlockState state) {
		return new Vec3d(0.5, 0.5, 0.5);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
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
		return validateTicker(type, BlockEntityType.SIGN, SignBlockEntity::tick);
	}
}
