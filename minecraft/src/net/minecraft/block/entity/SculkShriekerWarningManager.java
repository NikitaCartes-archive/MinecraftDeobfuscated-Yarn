package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

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
	private static final double field_38738 = 16.0;
	private static final int field_38186 = 48;
	private static final int field_38187 = 12000;
	private static final int field_38188 = 200;
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
		if (this.cooldownTicks <= 0 && world.getDifficulty() != Difficulty.PEACEFUL) {
			Box box = Box.of(Vec3d.ofCenter(pos), 48.0, 48.0, 48.0);
			return world.getNonSpectatingEntities(WardenEntity.class, box).isEmpty();
		} else {
			return false;
		}
	}

	private static List<ServerPlayerEntity> getPlayersInRange(ServerWorld world, BlockPos pos) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		Predicate<ServerPlayerEntity> predicate = player -> player.getPos().isInRange(vec3d, 16.0);
		return world.getPlayers(predicate.and(LivingEntity::isAlive));
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

	public int getWarningLevel() {
		return this.warningLevel;
	}

	private void copy(SculkShriekerWarningManager other) {
		this.ticksSinceLastWarning = other.ticksSinceLastWarning;
		this.warningLevel = other.warningLevel;
		this.cooldownTicks = other.cooldownTicks;
	}
}
