package net.minecraft.entity.damage;

import javax.annotation.Nullable;

public record DamageRecord(DamageSource damageSource, float damage, @Nullable FallLocation fallLocation, float fallDistance) {
}
