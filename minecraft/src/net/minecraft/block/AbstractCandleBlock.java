package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public abstract class AbstractCandleBlock extends Block {
	public static final BooleanProperty LIT = Properties.LIT;

	protected AbstractCandleBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	protected abstract Iterable<Vec3d> getParticleOffsets(BlockState state);

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (!world.isClient && projectile.isOnFire() && !(Boolean)state.get(LIT)) {
			setLit(world, state, hit.getBlockPos(), true);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(LIT)) {
			this.getParticleOffsets(state)
				.forEach(offset -> spawnCandleParticles(world, offset.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), random));
		}
	}

	@Environment(EnvType.CLIENT)
	private static void spawnCandleParticles(World world, Vec3d vec3d, Random random) {
		float f = random.nextFloat();
		if (f < 0.3F) {
			world.addParticle(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
			if (f < 0.17F) {
				world.playSound(
					vec3d.x + 0.5,
					vec3d.y + 0.5,
					vec3d.z + 0.5,
					SoundEvents.BLOCK_CANDLE_AMBIENT,
					SoundCategory.BLOCKS,
					1.0F + random.nextFloat(),
					random.nextFloat() * 0.7F + 0.3F,
					false
				);
			}
		}

		world.addParticle(ParticleTypes.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
	}

	protected static void extinguish(@Nullable PlayerEntity playerEntity, BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		setLit(worldAccess, blockState, blockPos, false);
		worldAccess.addParticle(ParticleTypes.SMOKE, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 0.0, 0.1F, 0.0);
		worldAccess.playSound(null, blockPos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
		worldAccess.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
	}

	private static void setLit(WorldAccess world, BlockState state, BlockPos pos, boolean lit) {
		world.setBlockState(pos, state.with(LIT, Boolean.valueOf(lit)), 11);
	}
}
