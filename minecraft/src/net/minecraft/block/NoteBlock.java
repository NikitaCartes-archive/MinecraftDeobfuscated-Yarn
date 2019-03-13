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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class NoteBlock extends Block {
	public static final EnumProperty<Instrument> field_11325 = Properties.field_12499;
	public static final BooleanProperty field_11326 = Properties.field_12484;
	public static final IntegerProperty field_11324 = Properties.field_12524;

	public NoteBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11325, Instrument.field_12648)
				.method_11657(field_11324, Integer.valueOf(0))
				.method_11657(field_11326, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564()
			.method_11657(field_11325, Instrument.fromBlockState(itemPlacementContext.method_8045().method_8320(itemPlacementContext.method_8037().down())));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.DOWN
			? blockState.method_11657(field_11325, Instrument.fromBlockState(blockState2))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		boolean bl = world.method_8479(blockPos);
		if (bl != (Boolean)blockState.method_11654(field_11326)) {
			if (bl) {
				this.method_10367(world, blockPos);
			}

			world.method_8652(blockPos, blockState.method_11657(field_11326, Boolean.valueOf(bl)), 3);
		}
	}

	private void method_10367(World world, BlockPos blockPos) {
		if (world.method_8320(blockPos.up()).isAir()) {
			world.method_8427(blockPos, this, 0, 0);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			blockState = blockState.method_11572(field_11324);
			world.method_8652(blockPos, blockState, 3);
			this.method_10367(world, blockPos);
			playerEntity.method_7281(Stats.field_15393);
			return true;
		}
	}

	@Override
	public void method_9606(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		if (!world.isClient) {
			this.method_10367(world, blockPos);
			playerEntity.method_7281(Stats.field_15385);
		}
	}

	@Override
	public boolean method_9592(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		int k = (Integer)blockState.method_11654(field_11324);
		float f = (float)Math.pow(2.0, (double)(k - 12) / 12.0);
		world.method_8396(null, blockPos, ((Instrument)blockState.method_11654(field_11325)).method_11886(), SoundCategory.field_15247, 3.0F, f);
		world.method_8406(
			ParticleTypes.field_11224, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + 0.5, (double)k / 24.0, 0.0, 0.0
		);
		return true;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11325, field_11326, field_11324);
	}
}
