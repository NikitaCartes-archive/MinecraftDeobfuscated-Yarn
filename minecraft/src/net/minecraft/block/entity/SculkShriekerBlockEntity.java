package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
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
		warningSounds.put(4, SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY);
	});
	private static final int field_38756 = 90;
	private int warningLevel;
	private VibrationListener vibrationListener = new VibrationListener(new BlockPositionSource(this.pos), 8, this, null, 0.0F, 0);

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
	public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
		return !this.isRemoved()
			&& !(Boolean)this.getCachedState().get(SculkShriekerBlock.SHRIEKING)
			&& findResponsiblePlayerFromEntity(emitter.sourceEntity()) != null;
	}

	@Nullable
	public static ServerPlayerEntity findResponsiblePlayerFromEntity(@Nullable Entity entity) {
		if (entity instanceof ServerPlayerEntity) {
			return (ServerPlayerEntity)entity;
		} else {
			if (entity != null) {
				Entity serverPlayerEntity2 = entity.getPrimaryPassenger();
				if (serverPlayerEntity2 instanceof ServerPlayerEntity) {
					return (ServerPlayerEntity)serverPlayerEntity2;
				}
			}

			if (entity instanceof ProjectileEntity projectileEntity) {
				Entity var3 = projectileEntity.getOwner();
				if (var3 instanceof ServerPlayerEntity) {
					return (ServerPlayerEntity)var3;
				}
			}

			return null;
		}
	}

	@Override
	public void accept(
		ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance
	) {
		this.shriek(world, findResponsiblePlayerFromEntity(sourceEntity != null ? sourceEntity : entity));
	}

	public void shriek(ServerWorld world, @Nullable ServerPlayerEntity player) {
		if (player != null) {
			BlockState blockState = this.getCachedState();
			if (!(Boolean)blockState.get(SculkShriekerBlock.SHRIEKING)) {
				this.warningLevel = 0;
				if (!this.canWarn(world) || this.trySyncWarningLevel(world, player)) {
					this.shriek(world, (Entity)player);
				}
			}
		}
	}

	private boolean trySyncWarningLevel(ServerWorld world, ServerPlayerEntity player) {
		OptionalInt optionalInt = SculkShriekerWarningManager.warnNearbyPlayers(world, this.getPos(), player);
		optionalInt.ifPresent(warningLevel -> this.warningLevel = warningLevel);
		return optionalInt.isPresent();
	}

	private void shriek(ServerWorld world, @Nullable Entity entity) {
		BlockPos blockPos = this.getPos();
		BlockState blockState = this.getCachedState();
		world.setBlockState(blockPos, blockState.with(SculkShriekerBlock.SHRIEKING, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
		world.createAndScheduleBlockTick(blockPos, blockState.getBlock(), 90);
		world.syncWorldEvent(WorldEvents.SCULK_SHRIEKS, blockPos, 0);
		world.emitGameEvent(GameEvent.SHRIEK, blockPos, GameEvent.Emitter.of(entity));
	}

	private boolean canWarn(ServerWorld world) {
		return (Boolean)this.getCachedState().get(SculkShriekerBlock.CAN_SUMMON)
			&& world.getDifficulty() != Difficulty.PEACEFUL
			&& world.getGameRules().getBoolean(GameRules.DO_WARDEN_SPAWNING);
	}

	public void warn(ServerWorld world) {
		if (this.canWarn(world) && this.warningLevel > 0) {
			if (!this.trySpawnWarden(world)) {
				this.playWarningSound();
			}

			WardenEntity.addDarknessToClosePlayers(world, Vec3d.ofCenter(this.getPos()), null, 40);
		}
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

	private boolean trySpawnWarden(ServerWorld world) {
		return this.warningLevel < 4
			? false
			: LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, SpawnReason.TRIGGERED, world, this.getPos(), 20, 5, 6, LargeEntitySpawnHelper.Requirements.WARDEN)
				.isPresent();
	}

	@Override
	public void onListen() {
		this.markDirty();
	}
}
