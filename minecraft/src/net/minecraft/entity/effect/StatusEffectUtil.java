package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.math.MathHelper;

public final class StatusEffectUtil {
	@Environment(EnvType.CLIENT)
	public static String durationToString(StatusEffectInstance statusEffectInstance, float f) {
		if (statusEffectInstance.isPermanent()) {
			return "**:**";
		} else {
			int i = MathHelper.floor((float)statusEffectInstance.getDuration() * f);
			return ChatUtil.ticksToString(i);
		}
	}

	public static boolean hasHaste(LivingEntity livingEntity) {
		return livingEntity.hasStatusEffect(StatusEffects.HASTE) || livingEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
	}

	public static int getHasteAmplifier(LivingEntity livingEntity) {
		int i = 0;
		int j = 0;
		if (livingEntity.hasStatusEffect(StatusEffects.HASTE)) {
			i = livingEntity.getStatusEffect(StatusEffects.HASTE).getAmplifier();
		}

		if (livingEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
			j = livingEntity.getStatusEffect(StatusEffects.CONDUIT_POWER).getAmplifier();
		}

		return Math.max(i, j);
	}

	public static boolean hasWaterBreathing(LivingEntity livingEntity) {
		return livingEntity.hasStatusEffect(StatusEffects.WATER_BREATHING) || livingEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
	}
}
