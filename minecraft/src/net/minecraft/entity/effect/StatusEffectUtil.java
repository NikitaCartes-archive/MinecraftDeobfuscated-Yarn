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

	public static boolean method_5576(LivingEntity livingEntity) {
		return livingEntity.hasPotionEffect(StatusEffects.field_5917) || livingEntity.hasPotionEffect(StatusEffects.field_5927);
	}

	public static int method_5575(LivingEntity livingEntity) {
		int i = 0;
		int j = 0;
		if (livingEntity.hasPotionEffect(StatusEffects.field_5917)) {
			i = livingEntity.getPotionEffect(StatusEffects.field_5917).getAmplifier();
		}

		if (livingEntity.hasPotionEffect(StatusEffects.field_5927)) {
			j = livingEntity.getPotionEffect(StatusEffects.field_5927).getAmplifier();
		}

		return Math.max(i, j);
	}

	public static boolean method_5574(LivingEntity livingEntity) {
		return livingEntity.hasPotionEffect(StatusEffects.field_5923) || livingEntity.hasPotionEffect(StatusEffects.field_5927);
	}
}
