package net.minecraft.block;

import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
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
			this.stateFactory.getDefaultState().with(INSTRUMENT, Instrument.field_12648).with(NOTE, Integer.valueOf(0)).with(POWERED, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState()
			.with(INSTRUMENT, Instrument.fromBlockState(itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos().down())));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == Direction.field_11033
			? blockState.with(INSTRUMENT, Instrument.fromBlockState(blockState2))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		boolean bl2 = world.isReceivingRedstonePower(blockPos);
		if (bl2 != (Boolean)blockState.get(POWERED)) {
			if (bl2) {
				this.playNote(world, blockPos);
			}

			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl2)), 3);
		}
	}

	private void playNote(World world, BlockPos blockPos) {
		if (world.getBlockState(blockPos.up()).isAir()) {
			world.addBlockAction(blockPos, this, 0, 0);
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			blockState = blockState.cycle(NOTE);
			world.setBlockState(blockPos, blockState, 3);
			this.playNote(world, blockPos);
			playerEntity.incrementStat(Stats.field_15393);
			return true;
		}
	}

	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		if (!world.isClient) {
			this.playNote(world, blockPos);
			playerEntity.incrementStat(Stats.field_15385);
		}
	}

	@Override
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		int k = (Integer)blockState.get(NOTE);
		float f = (float)Math.pow(2.0, (double)(k - 12) / 12.0);
		world.playSound(null, blockPos, ((Instrument)blockState.get(INSTRUMENT)).getSound(), SoundCategory.field_15247, 3.0F, f);
		world.addParticle(
			ParticleTypes.field_11224, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + 0.5, (double)k / 24.0, 0.0, 0.0
		);
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(INSTRUMENT, POWERED, NOTE);
	}
}
