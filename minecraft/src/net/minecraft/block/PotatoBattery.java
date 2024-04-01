package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PotatoBattery extends Block {
	public static final MapCodec<PotatoBattery> CODEC = createCodec(PotatoBattery::new);
	public static final BooleanProperty INVERTED = Properties.INVERTED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

	@Override
	public MapCodec<PotatoBattery> getCodec() {
		return CODEC;
	}

	public PotatoBattery(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(INVERTED, Boolean.valueOf(false)));
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
		return state.get(INVERTED) ? 15 : 0;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (player.canModifyBlocks()) {
			if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				BlockState blockState = state.cycle(INVERTED);
				world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
				return ActionResult.CONSUME;
			}
		} else {
			return super.onUse(state, world, pos, player, hit);
		}
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity livingEntity && (Boolean)state.get(INVERTED) && world instanceof ServerWorld serverWorld) {
			livingEntity.damage(world.getDamageSources().potatoMagic(), 0.5F);

			for (float f = 0.2F; f < 0.8F; f += 0.1F) {
				serverWorld.spawnParticles(
					ParticleTypes.ELECTRIC_SPARK, (double)((float)pos.getX() + f), (double)pos.getY() + 0.35, (double)((float)pos.getZ() + f), 1, 0.05, 0.05, 0.05, 0.1
				);
			}

			serverWorld.playSound(null, pos, SoundEvents.BLOCK_POTATO_BATTERY_ZAP, SoundCategory.BLOCKS, 0.5F, 2.0F);
		}

		super.onEntityCollision(state, world, pos, entity);
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(INVERTED);
	}
}
