package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LightningRodBlock extends RodBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public LightningRodBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP).with(POWERED, Boolean.valueOf(false)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getSide());
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	public void method_31648(BlockState blockState, World world, BlockPos blockPos) {
		world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(true)), 3);
		world.getBlockTickScheduler().schedule(blockPos, this, 8);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 3);
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (world.isThundering() && projectile instanceof TridentEntity && ((TridentEntity)projectile).method_31551()) {
			BlockPos blockPos = hit.getBlockPos();
			if (world.isSkyVisible(blockPos)) {
				LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
				lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
				Entity entity = projectile.getOwner();
				lightningEntity.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null);
				world.spawnEntity(lightningEntity);
				world.playSound(null, blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.WEATHER, 5.0F, 1.0F);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}
}