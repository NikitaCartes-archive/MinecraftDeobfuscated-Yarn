package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.math.MathHelper;

public final class StatusEffectUtil {
	@Environment(EnvType.CLIENT)
	public static String durationToString(StatusEffectInstance effect, float multiplier) {
		if (effect.isPermanent()) {
			return "**:**";
		} else {
			int i = MathHelper.floor((float)effect.getDuration() * multiplier);
			return ChatUtil.ticksToString(i);
		}
	}

	public static boolean hasHaste(LivingEntity entity) {
		return entity.hasStatusEffect(StatusEffects.field_5917) || entity.hasStatusEffect(StatusEffects.field_5927);
	}

	public static int getHasteAmplifier(LivingEntity entity) {
		int i = 0;
		int j = 0;
		if (entity.hasStatusEffect(StatusEffects.field_5917)) {
			i = entity.getStatusEffect(StatusEffects.field_5917).getAmplifier();
		}

		if (entity.hasStatusEffect(StatusEffects.field_5927)) {
			j = entity.getStatusEffect(StatusEffects.field_5927).getAmplifier();
		}

		return Math.max(i, j);
	}

	public static boolean hasWaterBreathing(LivingEntity entity) {
		return entity.hasStatusEffect(StatusEffects.field_5923) || entity.hasStatusEffect(StatusEffects.field_5927);
	}
}
