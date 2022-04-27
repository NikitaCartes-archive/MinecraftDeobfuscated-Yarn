package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.LargeEntitySpawnHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;
import org.slf4j.Logger;

public class SculkShriekerBlockEntity extends BlockEntity implements VibrationListener.Callback {
	private static final Logger field_38237 = LogUtils.getLogger();
	private static final int RANGE = 8;
	private static final int field_38750 = 10;
	private static final int field_38751 = 20;
	private static final int field_38752 = 5;
	private static final int field_38753 = 6;
	private static final int field_38754 = 40;
	private static final Int2ObjectMap<SoundEvent> WARNING_SOUNDS = Util.make(new Int2ObjectOpenHashMap<>(), warningSounds -> {
		warningSounds.put(1, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE);
		warningSounds.put(2, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSER);
		warningSounds.put(3, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSEST);
	});
	private static final int field_38756 = 90;
	private int warningLevel;
	private VibrationListener vibrationListener = new VibrationListener(new BlockPositionSource(this.pos), 8, this, null, 0, 0);

	public SculkShriekerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SCULK_SHRIEKER, pos, state);
	}

	public VibrationListener getVibrationListener() {
		return this.vibrationListener;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("warning_level", NbtElement.NUMBER_TYPE)) {
			this.warningLevel = nbt.getInt("warning_level");
		}

		if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
			VibrationListener.createCodec(this)
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
				.resultOrPartial(field_38237::error)
				.ifPresent(vibrationListener -> this.vibrationListener = vibrationListener);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("warning_level", this.warningLevel);
		VibrationListener.createCodec(this)
			.encodeStart(NbtOps.INSTANCE, this.vibrationListener)
			.resultOrPartial(field_38237::error)
			.ifPresent(nbtElement -> nbt.put("listener", nbtElement));
	}

	@Override
	public TagKey<GameEvent> getTag() {
		return GameEventTags.SHRIEKER_CAN_LISTEN;
	}

	@Override
	public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable GameEvent.Emitter emitter) {
		return this.canWarn(world);
	}

	@Override
	public void accept(
		ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, int delay
	) {
		this.shriek(world, sourceEntity != null ? sourceEntity : entity);
	}

	private boolean canWarn(ServerWorld world) {
		BlockState blockState = this.getCachedState();
		if ((Boolean)blockState.get(SculkShriekerBlock.SHRIEKING)) {
			return false;
		} else if (!(Boolean)blockState.get(SculkShriekerBlock.CAN_SUMMON)) {
			return true;
		} else {
			BlockPos blockPos = this.getPos();
			return (Boolean)getClosestPlayerWarningManager(world, blockPos).map(warningManager -> warningManager.canIncreaseWarningLevel(world, blockPos)).orElse(false);
		}
	}

	public void shriek(ServerWorld world, @Nullable Entity entity) {
		BlockState blockState = this.getCachedState();
		if (this.canWarn(world) && this.trySyncWarningLevel(world, blockState)) {
			BlockPos blockPos = this.getPos();
			world.setBlockState(blockPos, blockState.with(SculkShriekerBlock.SHRIEKING, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
			world.createAndScheduleBlockTick(blockPos, blockState.getBlock(), 90);
			world.syncWorldEvent(WorldEvents.SCULK_SHRIEKS, blockPos, 0);
			world.emitGameEvent(GameEvent.SHRIEK, blockPos, GameEvent.Emitter.of(entity));
		}
	}

	private boolean trySyncWarningLevel(ServerWorld world, BlockState state) {
		if ((Boolean)state.get(SculkShriekerBlock.CAN_SUMMON)) {
			BlockPos blockPos = this.getPos();
			Optional<SculkShriekerWarningManager> optional = getClosestPlayerWarningManager(world, blockPos)
				.filter(warningManager -> warningManager.warnNearbyPlayers(world, blockPos));
			if (optional.isEmpty()) {
				return false;
			}

			this.warningLevel = ((SculkShriekerWarningManager)optional.get()).getWarningLevel();
		}

		return true;
	}

	private static Optional<SculkShriekerWarningManager> getClosestPlayerWarningManager(ServerWorld world, BlockPos pos) {
		PlayerEntity playerEntity = world.getClosestPlayer(
			(double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 16.0, EntityPredicates.EXCEPT_SPECTATOR.and(Entity::isAlive)
		);
		return playerEntity == null ? Optional.empty() : Optional.of(playerEntity.getSculkShriekerWarningManager());
	}

	public void warn(ServerWorld world) {
		if ((Boolean)this.getCachedState().get(SculkShriekerBlock.CAN_SUMMON)) {
			WardenEntity.addDarknessToClosePlayers(world, Vec3d.ofCenter(this.getPos()), null, 40);
			if (this.warningLevel >= 3) {
				trySpawnWarden(world, this.getPos());
				return;
			}
		}

		this.playWarningSound();
	}

	private void playWarningSound() {
		SoundEvent soundEvent = WARNING_SOUNDS.get(this.warningLevel);
		if (soundEvent != null) {
			BlockPos blockPos = this.getPos();
			int i = blockPos.getX() + MathHelper.nextBetween(this.world.random, -10, 10);
			int j = blockPos.getY() + MathHelper.nextBetween(this.world.random, -10, 10);
			int k = blockPos.getZ() + MathHelper.nextBetween(this.world.random, -10, 10);
			this.world.playSound(null, (double)i, (double)j, (double)k, soundEvent, SoundCategory.HOSTILE, 5.0F, 1.0F);
		}
	}

	private static void trySpawnWarden(ServerWorld world, BlockPos pos) {
		if (world.getGameRules().getBoolean(GameRules.DO_WARDEN_SPAWNING)) {
			LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, SpawnReason.TRIGGERED, world, pos, 20, 5, 6)
				.ifPresent(entity -> entity.playSound(SoundEvents.ENTITY_WARDEN_AGITATED, 5.0F, 1.0F));
		}
	}

	@Override
	public void onListen() {
		this.markDirty();
	}
}
