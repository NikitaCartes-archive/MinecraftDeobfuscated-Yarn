package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkCatalystBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
	private final BlockPositionSource positionSource = new BlockPositionSource(this.pos);
	private final SculkSpreadManager spreadManager = SculkSpreadManager.create();

	public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SCULK_CATALYST, pos, state);
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
	public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
		if (event == GameEvent.ENTITY_DIE) {
			Entity i = emitter.sourceEntity();
			if (i instanceof LivingEntity livingEntity) {
				if (!livingEntity.isExperienceDroppingDisabled()) {
					int ix = livingEntity.getXpToDrop();
					if (livingEntity.shouldDropXp() && ix > 0) {
						this.spreadManager.spread(new BlockPos(emitterPos.withBias(Direction.UP, 0.5)), ix);
						this.triggerCriteria(livingEntity);
					}

					livingEntity.disableExperienceDropping();
					SculkCatalystBlock.bloom(world, this.pos, this.getCachedState(), world.getRandom());
				}

				return true;
			}
		}

		return false;
	}

	private void triggerCriteria(LivingEntity deadEntity) {
		LivingEntity livingEntity = deadEntity.getAttacker();
		if (livingEntity instanceof ServerPlayerEntity serverPlayerEntity) {
			DamageSource damageSource = deadEntity.getRecentDamageSource() == null ? DamageSource.player(serverPlayerEntity) : deadEntity.getRecentDamageSource();
			Criteria.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayerEntity, deadEntity, damageSource);
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity) {
		blockEntity.spreadManager.tick(world, pos, world.getRandom(), true);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.spreadManager.readNbt(nbt);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		this.spreadManager.writeNbt(nbt);
		super.writeNbt(nbt);
	}

	@VisibleForTesting
	public SculkSpreadManager getSpreadManager() {
		return this.spreadManager;
	}
}
