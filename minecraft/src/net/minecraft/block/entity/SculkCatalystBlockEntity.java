package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkCatalystBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener.Holder<SculkCatalystBlockEntity.Listener> {
	private final SculkCatalystBlockEntity.Listener eventListener;

	public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SCULK_CATALYST, pos, state);
		this.eventListener = new SculkCatalystBlockEntity.Listener(state, new BlockPositionSource(pos));
	}

	public static void tick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity) {
		blockEntity.eventListener.getSpreadManager().tick(world, pos, world.getRandom(), true);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.eventListener.spreadManager.readNbt(nbt);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		this.eventListener.spreadManager.writeNbt(nbt);
		super.writeNbt(nbt, registryLookup);
	}

	public SculkCatalystBlockEntity.Listener getEventListener() {
		return this.eventListener;
	}

	public static class Listener implements GameEventListener {
		public static final int RANGE = 8;
		final SculkSpreadManager spreadManager;
		private final BlockState state;
		private final PositionSource positionSource;

		public Listener(BlockState state, PositionSource positionSource) {
			this.state = state;
			this.positionSource = positionSource;
			this.spreadManager = SculkSpreadManager.create();
		}

		@Override
		public PositionSource getPositionSource() {
			return this.positionSource;
		}

		@Override
		public int getRange() {
			return 8;
		}

		@Override
		public GameEventListener.TriggerOrder getTriggerOrder() {
			return GameEventListener.TriggerOrder.BY_DISTANCE;
		}

		@Override
		public boolean listen(ServerWorld world, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, Vec3d emitterPos) {
			if (event.matches(GameEvent.ENTITY_DIE) && emitter.sourceEntity() instanceof LivingEntity livingEntity) {
				if (!livingEntity.isExperienceDroppingDisabled()) {
					DamageSource damageSource = livingEntity.getRecentDamageSource();
					int i = livingEntity.getXpToDrop(world, Nullables.map(damageSource, DamageSource::getAttacker));
					if (livingEntity.shouldDropXp() && i > 0) {
						this.spreadManager.spread(BlockPos.ofFloored(emitterPos.offset(Direction.UP, 0.5)), i);
						this.triggerCriteria(world, livingEntity);
					}

					livingEntity.disableExperienceDropping();
					this.positionSource.getPos(world).ifPresent(pos -> this.bloom(world, BlockPos.ofFloored(pos), this.state, world.getRandom()));
				}

				return true;
			} else {
				return false;
			}
		}

		@VisibleForTesting
		public SculkSpreadManager getSpreadManager() {
			return this.spreadManager;
		}

		private void bloom(ServerWorld world, BlockPos pos, BlockState state, Random random) {
			world.setBlockState(pos, state.with(SculkCatalystBlock.BLOOM, Boolean.valueOf(true)), Block.NOTIFY_ALL);
			world.scheduleBlockTick(pos, state.getBlock(), 8);
			world.spawnParticles(ParticleTypes.SCULK_SOUL, (double)pos.getX() + 0.5, (double)pos.getY() + 1.15, (double)pos.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
			world.playSound(null, pos, SoundEvents.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F);
		}

		private void triggerCriteria(World world, LivingEntity deadEntity) {
			if (deadEntity.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity) {
				DamageSource damageSource = deadEntity.getRecentDamageSource() == null
					? world.getDamageSources().playerAttack(serverPlayerEntity)
					: deadEntity.getRecentDamageSource();
				Criteria.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayerEntity, deadEntity, damageSource);
			}
		}
	}
}
