package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class DaylightDetectorBlock extends BlockWithEntity {
	public static final MapCodec<DaylightDetectorBlock> CODEC = createCodec(DaylightDetectorBlock::new);
	public static final IntProperty POWER = Properties.POWER;
	public static final BooleanProperty INVERTED = Properties.INVERTED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

	@Override
	public MapCodec<DaylightDetectorBlock> getCodec() {
		return CODEC;
	}

	public DaylightDetectorBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, Integer.valueOf(0)).with(INVERTED, Boolean.valueOf(false)));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Integer)state.get(POWER);
	}

	private static void updateState(BlockState state, World world, BlockPos pos) {
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
			world.setBlockState(pos, state.with(POWER, Integer.valueOf(i)), Block.NOTIFY_ALL);
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (player.canModifyBlocks()) {
			if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				BlockState blockState = state.cycle(INVERTED);
				world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
				updateState(blockState, world, pos);
				return ActionResult.CONSUME;
			}
		} else {
			return super.onUse(state, world, pos, player, hit);
		}
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new DaylightDetectorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return !world.isClient && world.getDimension().hasSkyLight() ? validateTicker(type, BlockEntityType.DAYLIGHT_DETECTOR, DaylightDetectorBlock::tick) : null;
	}

	private static void tick(World world, BlockPos pos, BlockState state, DaylightDetectorBlockEntity blockEntity) {
		if (world.getTime() % 20L == 0L) {
			updateState(state, world, pos);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER, INVERTED);
	}
}
