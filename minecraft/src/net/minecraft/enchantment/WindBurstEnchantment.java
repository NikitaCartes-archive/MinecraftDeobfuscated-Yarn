package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class WindBurstEnchantment extends Enchantment {
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
		float f = 0.25F + 0.25F * (float)level;
		attacket.getWorld()
			.createExplosion(
				null,
				null,
				new WindBurstEnchantment.ExplosionBehaviour(f),
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

	@Override
	public boolean isAvailableForEnchantedBookOffer() {
		return false;
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		return false;
	}

	static final class ExplosionBehaviour extends AbstractWindChargeEntity.WindChargeExplosionBehavior {
		private final float knockbackModifier;

		public ExplosionBehaviour(float knockbackModifier) {
			this.knockbackModifier = knockbackModifier;
		}

		@Override
		public float getKnockbackModifier(Entity entity) {
			boolean var10000;
			label17: {
				if (entity instanceof PlayerEntity playerEntity && playerEntity.getAbilities().flying) {
					var10000 = true;
					break label17;
				}

				var10000 = false;
			}

			boolean bl = var10000;
			return !bl ? this.knockbackModifier : 0.0F;
		}
	}
}
