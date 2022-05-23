package net.minecraft.entity.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class StatusEffectUtil {
	public static String durationToString(StatusEffectInstance effect, float multiplier) {
		if (effect.isPermanent()) {
			return "**:**";
		} else {
			int i = MathHelper.floor((float)effect.getDuration() * multiplier);
			return StringHelper.formatTicks(i);
		}
	}

	public static boolean hasHaste(LivingEntity entity) {
		return entity.hasStatusEffect(StatusEffects.HASTE) || entity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
	}

	public static int getHasteAmplifier(LivingEntity entity) {
		int i = 0;
		int j = 0;
		if (entity.hasStatusEffect(StatusEffects.HASTE)) {
			i = entity.getStatusEffect(StatusEffects.HASTE).getAmplifier();
		}

		if (entity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
			j = entity.getStatusEffect(StatusEffects.CONDUIT_POWER).getAmplifier();
		}

		return Math.max(i, j);
	}

	public static boolean hasWaterBreathing(LivingEntity entity) {
		return entity.hasStatusEffect(StatusEffects.WATER_BREATHING) || entity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
	}

	public static List<ServerPlayerEntity> addEffectToPlayersWithinDistance(
		ServerWorld world, @Nullable Entity entity, Vec3d origin, double range, StatusEffectInstance statusEffectInstance, int duration
	) {
		StatusEffect statusEffect = statusEffectInstance.getEffectType();
		List<ServerPlayerEntity> list = world.getPlayers(
			player -> player.interactionManager.isSurvivalLike()
					&& (entity == null || !entity.isTeammate(player))
					&& origin.isInRange(player.getPos(), range)
					&& (
						!player.hasStatusEffect(statusEffect)
							|| player.getStatusEffect(statusEffect).getAmplifier() < statusEffectInstance.getAmplifier()
							|| player.getStatusEffect(statusEffect).getDuration() < duration
					)
		);
		list.forEach(player -> player.addStatusEffect(new StatusEffectInstance(statusEffectInstance), entity));
		return list;
	}
}
