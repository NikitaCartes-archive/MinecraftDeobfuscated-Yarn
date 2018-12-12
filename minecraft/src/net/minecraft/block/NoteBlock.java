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
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class NoteBlock extends Block {
	public static final EnumProperty<Instrument> field_11325 = Properties.INSTRUMENT;
	public static final BooleanProperty field_11326 = Properties.POWERED;
	public static final IntegerProperty field_11324 = Properties.NOTE;

	public NoteBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11325, Instrument.field_12648)
				.with(field_11324, Integer.valueOf(0))
				.with(field_11326, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState()
			.with(field_11325, Instrument.fromBlockState(itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos().down())));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == Direction.DOWN
			? blockState.with(field_11325, Instrument.fromBlockState(blockState2))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		boolean bl = world.isReceivingRedstonePower(blockPos);
		if (bl != (Boolean)blockState.get(field_11326)) {
			if (bl) {
				this.method_10367(world, blockPos);
			}

			world.setBlockState(blockPos, blockState.with(field_11326, Boolean.valueOf(bl)), 3);
		}
	}

	private void method_10367(World world, BlockPos blockPos) {
		if (world.getBlockState(blockPos.up()).isAir()) {
			world.addBlockAction(blockPos, this, 0, 0);
		}
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return true;
		} else {
			blockState = blockState.method_11572(field_11324);
			world.setBlockState(blockPos, blockState, 3);
			this.method_10367(world, blockPos);
			playerEntity.increaseStat(Stats.field_15393);
			return true;
		}
	}

	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		if (!world.isClient) {
			this.method_10367(world, blockPos);
			playerEntity.increaseStat(Stats.field_15385);
		}
	}

	@Override
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		int k = (Integer)blockState.get(field_11324);
		float f = (float)Math.pow(2.0, (double)(k - 12) / 12.0);
		world.playSound(null, blockPos, ((Instrument)blockState.get(field_11325)).getSound(), SoundCategory.field_15247, 3.0F, f);
		world.method_8406(
			ParticleTypes.field_11224, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + 0.5, (double)k / 24.0, 0.0, 0.0
		);
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11325, field_11326, field_11324);
	}
}
