package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.LargeEntitySpawnHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SculkShriekerWarningManager {
	public static final Codec<SculkShriekerWarningManager> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.NONNEGATIVE_INT.fieldOf("ticks_since_last_warning").orElse(0).forGetter(manager -> manager.ticksSinceLastWarning),
					Codecs.NONNEGATIVE_INT.fieldOf("warning_level").orElse(0).forGetter(manager -> manager.warningLevel),
					Codecs.NONNEGATIVE_INT.fieldOf("cooldown_ticks").orElse(0).forGetter(manager -> manager.cooldownTicks)
				)
				.apply(instance, SculkShriekerWarningManager::new)
	);
	public static final int field_38184 = 3;
	private static final int field_38185 = 10;
	private static final int field_38186 = 48;
	private static final int field_38187 = 12000;
	private static final int field_38188 = 200;
	private static final int field_38189 = 20;
	private static final int field_38190 = 5;
	private static final int field_38191 = 6;
	private static final int field_38192 = 40;
	private static final Int2ObjectMap<SoundEvent> WARNING_SOUNDS = Util.make(new Int2ObjectOpenHashMap<>(), map -> {
		map.put(1, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE);
		map.put(2, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSER);
		map.put(3, SoundEvents.ENTITY_WARDEN_NEARBY_CLOSEST);
	});
	private int ticksSinceLastWarning;
	private int warningLevel;
	private int cooldownTicks;

	public SculkShriekerWarningManager(int ticksSinceLastWarning, int warningLevel, int cooldownTicks) {
		this.ticksSinceLastWarning = ticksSinceLastWarning;
		this.warningLevel = warningLevel;
		this.cooldownTicks = cooldownTicks;
	}

	public void tick() {
		if (this.ticksSinceLastWarning >= 12000) {
			this.decreaseWarningLevel();
			this.ticksSinceLastWarning = 0;
		} else {
			this.ticksSinceLastWarning++;
		}

		if (this.cooldownTicks > 0) {
			this.cooldownTicks--;
		}
	}

	public void reset() {
		this.ticksSinceLastWarning = 0;
		this.warningLevel = 0;
		this.cooldownTicks = 0;
	}

	public boolean warnNearbyPlayers(ServerWorld world, BlockPos pos) {
		if (!this.canIncreaseWarningLevel(world, pos)) {
			return false;
		} else {
			List<ServerPlayerEntity> list = getPlayersInRange(world, pos);
			if (list.isEmpty()) {
				return false;
			} else {
				Optional<SculkShriekerWarningManager> optional = list.stream()
					.map(PlayerEntity::getSculkShriekerWarningManager)
					.max(Comparator.comparingInt(manager -> manager.warningLevel));
				optional.ifPresent(manager -> {
					manager.increaseWarningLevel();
					list.forEach(player -> player.getSculkShriekerWarningManager().copy(manager));
				});
				return true;
			}
		}
	}

	public boolean canIncreaseWarningLevel(ServerWorld world, BlockPos pos) {
		if (this.cooldownTicks > 0) {
			return false;
		} else {
			Box box = Box.of(Vec3d.ofCenter(pos), 48.0, 48.0, 48.0);
			return world.getNonSpectatingEntities(WardenEntity.class, box).isEmpty();
		}
	}

	private static List<ServerPlayerEntity> getPlayersInRange(ServerWorld world, BlockPos pos) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		double d = 16.0;
		Predicate<ServerPlayerEntity> predicate = player -> player.getPos().isInRange(vec3d, 16.0);
		return world.getPlayers(predicate.and(LivingEntity::isAlive));
	}

	public void warn(ServerWorld world, BlockPos pos) {
		if (this.getWarningLevel() < 3) {
			WardenEntity.addDarknessToClosePlayers(world, Vec3d.ofCenter(pos), null, 40);
			playWarningSound(world, pos, this.warningLevel);
		} else {
			trySpawnWarden(world, pos);
		}
	}

	private static void playWarningSound(ServerWorld world, BlockPos pos, int warningCount) {
		SoundEvent soundEvent = WARNING_SOUNDS.get(warningCount);
		if (soundEvent != null) {
			int i = pos.getX() + MathHelper.nextBetween(world.random, -10, 10);
			int j = pos.getY() + MathHelper.nextBetween(world.random, -10, 10);
			int k = pos.getZ() + MathHelper.nextBetween(world.random, -10, 10);
			world.playSound(null, (double)i, (double)j, (double)k, soundEvent, SoundCategory.HOSTILE, 5.0F, 1.0F);
		}
	}

	private static void trySpawnWarden(ServerWorld world, BlockPos pos) {
		Optional<WardenEntity> optional = LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, world, pos, 20, 5, 6);
		optional.ifPresent(warden -> {
			warden.getBrain().remember(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, (long)WardenBrain.EMERGE_DURATION);
			world.playSound(null, warden.getX(), warden.getY(), warden.getZ(), SoundEvents.ENTITY_WARDEN_AGITATED, SoundCategory.BLOCKS, 5.0F, 1.0F);
		});
	}

	private void increaseWarningLevel() {
		if (this.cooldownTicks <= 0) {
			this.ticksSinceLastWarning = 0;
			this.cooldownTicks = 200;
			this.setWarningLevel(this.getWarningLevel() + 1);
		}
	}

	private void decreaseWarningLevel() {
		this.setWarningLevel(this.getWarningLevel() - 1);
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = MathHelper.clamp(warningLevel, 0, 3);
	}

	private int getWarningLevel() {
		return this.warningLevel;
	}

	private void copy(SculkShriekerWarningManager other) {
		this.ticksSinceLastWarning = other.ticksSinceLastWarning;
		this.warningLevel = other.warningLevel;
		this.cooldownTicks = other.cooldownTicks;
	}
}
