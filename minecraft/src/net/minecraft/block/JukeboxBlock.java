package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class JukeboxBlock extends BlockWithEntity {
	public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

	protected JukeboxBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HAS_RECORD, Boolean.valueOf(false)));
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
		if (nbtCompound != null && nbtCompound.contains("RecordItem")) {
			world.setBlockState(pos, state.with(HAS_RECORD, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if ((Boolean)state.get(HAS_RECORD)) {
			this.removeRecord(world, pos);
			state = state.with(HAS_RECORD, Boolean.valueOf(false));
			world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Emitter.of(state));
			world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	public void setRecord(@Nullable Entity user, WorldAccess world, BlockPos pos, BlockState state, ItemStack stack) {
		if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity) {
			jukeboxBlockEntity.setRecord(stack.copy());
			jukeboxBlockEntity.startPlaying();
			world.setBlockState(pos, state.with(HAS_RECORD, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, state));
		}
	}

	private void removeRecord(World world, BlockPos pos) {
		if (!world.isClient) {
			if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity) {
				ItemStack itemStack = jukeboxBlockEntity.getRecord();
				if (!itemStack.isEmpty()) {
					world.syncWorldEvent(WorldEvents.MUSIC_DISC_PLAYED, pos, 0);
					jukeboxBlockEntity.clear();
					float f = 0.7F;
					double d = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
					double e = (double)(world.random.nextFloat() * 0.7F) + 0.060000002F + 0.6;
					double g = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
					ItemStack itemStack2 = itemStack.copy();
					ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, itemStack2);
					itemEntity.setToDefaultPickupDelay();
					world.spawnEntity(itemEntity);
				}
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			this.removeRecord(world, pos);
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new JukeboxBlockEntity(pos, state);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof JukeboxBlockEntity) {
			Item item = ((JukeboxBlockEntity)blockEntity).getRecord().getItem();
			if (item instanceof MusicDiscItem) {
				return ((MusicDiscItem)item).getComparatorOutput();
			}
		}

		return 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HAS_RECORD);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return state.get(HAS_RECORD) ? checkType(type, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick) : null;
	}
}
