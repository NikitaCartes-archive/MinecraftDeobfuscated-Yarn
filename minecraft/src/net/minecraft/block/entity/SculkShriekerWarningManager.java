package net.minecraft.block.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.LargeEntitySpawnHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SculkShriekerWarningManager {
	private static final int field_36796 = 10;
	private static final int field_36797 = 48;
	private static final int field_36798 = 3;
	private static final int field_36799 = 12000;
	private static final int field_36800 = 200;
	private static final int field_36801 = 20;
	private static final int field_36802 = 5;
	private static final int field_36803 = 6;
	private static final int field_36804 = 40;
	private static final int field_36805 = 260;
	private static final Int2ObjectMap<SoundEvent> WARNING_SOUNDS = Util.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
		int2ObjectOpenHashMap.put(1, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE);
		int2ObjectOpenHashMap.put(2, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSER);
		int2ObjectOpenHashMap.put(3, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSEST);
	});
	private int ticksSinceLastWarning;
	private int warningCount;
	private int shriekerCooldownTicks;

	public void tick() {
		if (this.ticksSinceLastWarning >= 12000) {
			this.decreaseWarningCount();
			this.ticksSinceLastWarning = 0;
		} else {
			this.ticksSinceLastWarning++;
		}

		if (this.shriekerCooldownTicks > 0) {
			this.shriekerCooldownTicks--;
		}
	}

	public void reset() {
		this.ticksSinceLastWarning = 0;
		this.warningCount = 0;
		this.shriekerCooldownTicks = 0;
	}

	public boolean method_40723(ServerWorld serverWorld, BlockPos blockPos, List<ServerPlayerEntity> list) {
		if (!this.method_40721(serverWorld, blockPos)) {
			return false;
		} else {
			Optional<SculkShriekerWarningManager> optional = list.stream()
				.max(Comparator.comparing(serverPlayerEntity -> serverPlayerEntity.getSculkShriekerWarningManager().warningCount))
				.map(PlayerEntity::getSculkShriekerWarningManager);
			if (optional.isPresent()) {
				SculkShriekerWarningManager sculkShriekerWarningManager = (SculkShriekerWarningManager)optional.get();
				sculkShriekerWarningManager.method_40732();
				list.stream()
					.map(PlayerEntity::getSculkShriekerWarningManager)
					.forEach(sculkShriekerWarningManager2 -> sculkShriekerWarningManager2.copy(sculkShriekerWarningManager));
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean method_40721(ServerWorld serverWorld, BlockPos blockPos) {
		return this.shriekerCooldownTicks <= 0 && !this.isWardenInRange(serverWorld, Vec3d.ofCenter(blockPos));
	}

	private boolean isWardenInRange(ServerWorld world, Vec3d pos) {
		return !world.getNonSpectatingEntities(WardenEntity.class, Box.of(pos, 48.0, 48.0, 48.0)).isEmpty();
	}

	public boolean method_40730(ServerWorld serverWorld, BlockPos blockPos) {
		if (!this.method_40735()) {
			int i = MathHelper.nextBetween(serverWorld.random, -10, 10);
			int j = MathHelper.nextBetween(serverWorld.random, -10, 10);
			int k = MathHelper.nextBetween(serverWorld.random, -10, 10);
			return method_40722(serverWorld, blockPos, blockPos.add(i, j, k), WARNING_SOUNDS.get(this.warningCount), 5.0F);
		} else {
			return method_40733(serverWorld, blockPos);
		}
	}

	private void method_40732() {
		if (this.shriekerCooldownTicks <= 0) {
			this.ticksSinceLastWarning = 0;
			this.shriekerCooldownTicks = 200;
			this.setWarningCount(this.getWarningCount() + 1);
		}
	}

	private void decreaseWarningCount() {
		this.setWarningCount(this.getWarningCount() - 1);
	}

	private boolean method_40735() {
		return this.getWarningCount() >= 3;
	}

	public void setWarningCount(int warningCount) {
		this.warningCount = MathHelper.clamp(warningCount, 0, 3);
	}

	private int getWarningCount() {
		return this.warningCount;
	}

	private void copy(SculkShriekerWarningManager manager) {
		this.ticksSinceLastWarning = manager.ticksSinceLastWarning;
		this.warningCount = manager.warningCount;
		this.shriekerCooldownTicks = manager.shriekerCooldownTicks;
	}

	public void readNbt(NbtCompound nbt) {
		if (nbt.contains("ticksSinceLastWarning", NbtElement.NUMBER_TYPE)) {
			this.ticksSinceLastWarning = nbt.getInt("ticksSinceLastWarning");
			this.warningCount = nbt.getInt("warningCount");
			this.shriekerCooldownTicks = nbt.getInt("shriekerCooldownTicks");
		}
	}

	public void writeNbt(NbtCompound nbt) {
		nbt.putInt("ticksSinceLastWarning", this.ticksSinceLastWarning);
		nbt.putInt("warningCount", this.warningCount);
		nbt.putInt("shriekerCooldownTicks", this.shriekerCooldownTicks);
	}

	public static boolean method_40722(ServerWorld serverWorld, BlockPos blockPos, BlockPos blockPos2, SoundEvent soundEvent, float f) {
		StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.DARKNESS, 260, 0, false, false);
		StatusEffectUtil.addEffectToPlayersWithinDistance(serverWorld, null, Vec3d.ofCenter(blockPos), 40.0, statusEffectInstance, 200);
		serverWorld.playSound(null, (double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ(), soundEvent, SoundCategory.HOSTILE, f, 1.0F);
		return true;
	}

	public static boolean method_40733(ServerWorld serverWorld, BlockPos blockPos) {
		Optional<WardenEntity> optional = LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, serverWorld, blockPos, 20, 5, 6);
		if (optional.isPresent()) {
			WardenEntity wardenEntity = (WardenEntity)optional.get();
			WardenBrain.lookAtDisturbance(wardenEntity, blockPos);
			wardenEntity.getBrain().remember(MemoryModuleType.IS_EMERGING, true, (long)WardenBrain.EMERGE_DURATION);
			serverWorld.playSound(
				null, wardenEntity.getX(), wardenEntity.getY(), wardenEntity.getZ(), SoundEvents.ENTITY_WARDEN_AGITATED, SoundCategory.BLOCKS, 5.0F, 1.0F
			);
			return true;
		} else {
			return false;
		}
	}
}
