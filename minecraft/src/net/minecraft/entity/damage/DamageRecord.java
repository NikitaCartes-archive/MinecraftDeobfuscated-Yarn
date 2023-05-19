package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.class_8572;

public record DamageRecord(DamageSource damageSource, float damage, @Nullable class_8572 fallLocation, float fallDistance) {
}
