package net.minecraft.enchantment;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;

public class DamageEnchantment extends Enchantment {
	private final Optional<TagKey<EntityType<?>>> applicableEntities;

	public DamageEnchantment(Enchantment.Properties properties, Optional<TagKey<EntityType<?>>> applicableEntities) {
		super(properties);
		this.applicableEntities = applicableEntities;
	}

	@Override
	public float getAttackDamage(int level, @Nullable EntityType<?> entityType) {
		if (this.applicableEntities.isEmpty()) {
			return 1.0F + (float)Math.max(0, level - 1) * 0.5F;
		} else {
			return entityType != null && entityType.isIn((TagKey<EntityType<?>>)this.applicableEntities.get()) ? (float)level * 2.5F : 0.0F;
		}
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return !(other instanceof DamageEnchantment);
	}

	@Override
	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
		if (this.applicableEntities.isPresent()
			&& target instanceof LivingEntity livingEntity
			&& this.applicableEntities.get() == EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS
			&& level > 0
			&& livingEntity.getType().isIn((TagKey<EntityType<?>>)this.applicableEntities.get())) {
			int i = 20 + user.getRandom().nextInt(10 * level);
			livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, i, 3));
		}
	}
}
