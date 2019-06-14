package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeverBlock extends WallMountedBlock {
	public static final BooleanProperty field_11265 = Properties.field_12484;
	protected static final VoxelShape field_11267 = Block.method_9541(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
	protected static final VoxelShape field_11263 = Block.method_9541(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
	protected static final VoxelShape field_11260 = Block.method_9541(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
	protected static final VoxelShape field_11262 = Block.method_9541(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
	protected static final VoxelShape field_11264 = Block.method_9541(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
	protected static final VoxelShape field_11261 = Block.method_9541(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
	protected static final VoxelShape field_11268 = Block.method_9541(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
	protected static final VoxelShape field_11266 = Block.method_9541(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

	protected LeverBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, Direction.field_11043)
				.method_11657(field_11265, Boolean.valueOf(false))
				.method_11657(field_11007, WallMountLocation.field_12471)
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((WallMountLocation)blockState.method_11654(field_11007)) {
			case field_12475:
				switch (((Direction)blockState.method_11654(field_11177)).getAxis()) {
					case X:
						return field_11261;
					case Z:
					default:
						return field_11264;
				}
			case field_12471:
				switch ((Direction)blockState.method_11654(field_11177)) {
					case field_11034:
						return field_11262;
					case field_11039:
						return field_11260;
					case field_11035:
						return field_11263;
					case field_11043:
					default:
						return field_11267;
				}
			case field_12473:
			default:
				switch (((Direction)blockState.method_11654(field_11177)).getAxis()) {
					case X:
						return field_11266;
					case Z:
					default:
						return field_11268;
				}
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		blockState = blockState.method_11572(field_11265);
		boolean bl = (Boolean)blockState.method_11654(field_11265);
		if (world.isClient) {
			if (bl) {
				method_10308(blockState, world, blockPos, 1.0F);
			}

			return true;
		} else {
			world.method_8652(blockPos, blockState, 3);
			float f = bl ? 0.6F : 0.5F;
			world.playSound(null, blockPos, SoundEvents.field_14962, SoundCategory.field_15245, 0.3F, f);
			this.method_10309(blockState, world, blockPos);
			return true;
		}
	}

	private static void method_10308(BlockState blockState, IWorld iWorld, BlockPos blockPos, float f) {
		Direction direction = ((Direction)blockState.method_11654(field_11177)).getOpposite();
		Direction direction2 = method_10119(blockState).getOpposite();
		double d = (double)blockPos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
		double e = (double)blockPos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
		double g = (double)blockPos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
		iWorld.addParticle(new DustParticleEffect(1.0F, 0.0F, 0.0F, f), d, e, g, 0.0, 0.0, 0.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11265) && random.nextFloat() < 0.25F) {
			method_10308(blockState, world, blockPos, 0.5F);
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.method_11654(field_11265)) {
				this.method_10309(blockState, world, blockPos);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11265) ? 15 : 0;
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11265) && method_10119(blockState) == direction ? 15 : 0;
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	private void method_10309(BlockState blockState, World world, BlockPos blockPos) {
		world.method_8452(blockPos, this);
		world.method_8452(blockPos.offset(method_10119(blockState).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11007, field_11177, field_11265);
	}
}
