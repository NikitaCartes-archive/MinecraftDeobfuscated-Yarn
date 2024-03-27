package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class WindBurstEnchantment extends Enchantment {
	private static final WindBurstEnchantment.ExplosionBehaviour[] EXPLOSION_BEHAVIOURS = new WindBurstEnchantment.ExplosionBehaviour[]{
		new WindBurstEnchantment.ExplosionBehaviour(0.5F), new WindBurstEnchantment.ExplosionBehaviour(0.75F), new WindBurstEnchantment.ExplosionBehaviour(1.0F)
	};

	public WindBurstEnchantment() {
		super(
			Enchantment.properties(
				ItemTags.MACE_ENCHANTABLE,
				2,
				3,
				Enchantment.leveledCost(15, 9),
				Enchantment.leveledCost(65, 9),
				4,
				FeatureSet.of(FeatureFlags.UPDATE_1_21),
				EquipmentSlot.MAINHAND
			)
		);
	}

	@Override
	public void onAttack(LivingEntity attacket, Entity target, int level) {
		attacket.getWorld()
			.createExplosion(
				null,
				null,
				EXPLOSION_BEHAVIOURS[level - 1],
				attacket.getX(),
				attacket.getY(),
				attacket.getZ(),
				3.5F,
				false,
				World.ExplosionSourceType.BLOW,
				ParticleTypes.GUST_EMITTER_SMALL,
				ParticleTypes.GUST_EMITTER_LARGE,
				SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
			);
	}

	static final class ExplosionBehaviour extends AbstractWindChargeEntity.WindChargeExplosionBehavior {
		private final float knockbackModifier;

		public ExplosionBehaviour(float knockbackModifier) {
			this.knockbackModifier = knockbackModifier;
		}

		@Override
		public float getKnockbackModifier() {
			return this.knockbackModifier;
		}
	}
}
