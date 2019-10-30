package net.minecraft.block;

import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class NoteBlock extends Block {
	public static final EnumProperty<Instrument> INSTRUMENT = Properties.INSTRUMENT;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final IntProperty NOTE = Properties.NOTE;

	public NoteBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NOTE, Integer.valueOf(0)).with(POWERED, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(INSTRUMENT, Instrument.fromBlockState(ctx.getWorld().getBlockState(ctx.getBlockPos().method_10074())));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return facing == Direction.DOWN
			? state.with(INSTRUMENT, Instrument.fromBlockState(neighborState))
			: super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(POWERED)) {
			if (bl) {
				this.playNote(world, pos);
			}

			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), 3);
		}
	}

	private void playNote(World world, BlockPos pos) {
		if (world.getBlockState(pos.up()).isAir()) {
			world.addBlockAction(pos, this, 0, 0);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			state = state.cycle(NOTE);
			world.setBlockState(pos, state, 3);
			this.playNote(world, pos);
			player.incrementStat(Stats.TUNE_NOTEBLOCK);
			return ActionResult.SUCCESS;
		}
	}

	@Override
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClient) {
			this.playNote(world, pos);
			player.incrementStat(Stats.PLAY_NOTEBLOCK);
		}
	}

	@Override
	public boolean onBlockAction(BlockState state, World world, BlockPos pos, int type, int data) {
		int i = (Integer)state.get(NOTE);
		float f = (float)Math.pow(2.0, (double)(i - 12) / 12.0);
		world.playSound(null, pos, ((Instrument)state.get(INSTRUMENT)).getSound(), SoundCategory.RECORDS, 3.0F, f);
		world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(INSTRUMENT, POWERED, NOTE);
	}
}
