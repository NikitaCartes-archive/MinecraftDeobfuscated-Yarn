package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends BlockWithEntity {
	public static final IntProperty POWER = Properties.POWER;
	public static final BooleanProperty INVERTED = Properties.INVERTED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

	public DaylightDetectorBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, Integer.valueOf(0)).with(INVERTED, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return SHAPE;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return (Integer)state.get(POWER);
	}

	public static void updateState(BlockState state, World world, BlockPos pos) {
		if (world.dimension.hasSkyLight()) {
			int i = world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness();
			float f = world.getSkyAngleRadians(1.0F);
			boolean bl = (Boolean)state.get(INVERTED);
			if (bl) {
				i = 15 - i;
			} else if (i > 0) {
				float g = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
				f += (g - f) * 0.2F;
				i = Math.round((float)i * MathHelper.cos(f));
			}

			i = MathHelper.clamp(i, 0, 15);
			if ((Integer)state.get(POWER) != i) {
				world.setBlockState(pos, state.with(POWER, Integer.valueOf(i)), 3);
			}
		}
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.canModifyWorld()) {
			if (world.isClient) {
				return true;
			} else {
				BlockState blockState = state.cycle(INVERTED);
				world.setBlockState(pos, blockState, 4);
				updateState(blockState, world, pos);
				return true;
			}
		} else {
			return super.activate(state, world, pos, player, hand, hit);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new DaylightDetectorBlockEntity();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER, INVERTED);
	}
}
