package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JukeboxBlock extends BlockWithEntity {
	public static final MapCodec<JukeboxBlock> CODEC = createCodec(JukeboxBlock::new);
	public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

	@Override
	public MapCodec<JukeboxBlock> getCodec() {
		return CODEC;
	}

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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if ((Boolean)state.get(HAS_RECORD) && world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity) {
			jukeboxBlockEntity.dropRecord();
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity) {
				jukeboxBlockEntity.dropRecord();
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new JukeboxBlockEntity(pos, state);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity && jukeboxBlockEntity.isPlayingRecord()) {
			return 15;
		}

		return 0;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity
			&& jukeboxBlockEntity.getStack().getItem() instanceof MusicDiscItem musicDiscItem) {
			return musicDiscItem.getComparatorOutput();
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
		return state.get(HAS_RECORD) ? validateTicker(type, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick) : null;
	}
}
