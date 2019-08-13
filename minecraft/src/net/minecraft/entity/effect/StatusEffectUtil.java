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
		return livingEntity.hasStatusEffect(StatusEffects.field_5917) || livingEntity.hasStatusEffect(StatusEffects.field_5927);
	}

	public static int getHasteAmplifier(LivingEntity livingEntity) {
		int i = 0;
		int j = 0;
		if (livingEntity.hasStatusEffect(StatusEffects.field_5917)) {
			i = livingEntity.getStatusEffect(StatusEffects.field_5917).getAmplifier();
		}

		if (livingEntity.hasStatusEffect(StatusEffects.field_5927)) {
			j = livingEntity.getStatusEffect(StatusEffects.field_5927).getAmplifier();
		}

		return Math.max(i, j);
	}

	public static boolean hasWaterBreathing(LivingEntity livingEntity) {
		return livingEntity.hasStatusEffect(StatusEffects.field_5923) || livingEntity.hasStatusEffect(StatusEffects.field_5927);
	}
}
