package net.minecraft.entity.effect;

import java.util.function.ToIntFunction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

class InfestedStatusEffect extends StatusEffect {
	private final float silverfishChance;
	private final ToIntFunction<Random> silverfishCountFunction;

	protected InfestedStatusEffect(StatusEffectCategory category, int color, float silverfishChance, ToIntFunction<Random> silverfishCountFunction) {
		super(category, color, ParticleTypes.INFESTED);
		this.silverfishChance = silverfishChance;
		this.silverfishCountFunction = silverfishCountFunction;
	}

	@Override
	public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
		if (entity.getRandom().nextFloat() <= this.silverfishChance) {
			int i = this.silverfishCountFunction.applyAsInt(entity.getRandom());

			for (int j = 0; j < i; j++) {
				this.spawnSilverfish(entity.getWorld(), entity.getX(), entity.getY() + 0.5, entity.getZ());
			}
		}
	}

	private void spawnSilverfish(World world, double x, double y, double z) {
		SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world);
		if (silverfishEntity != null) {
			silverfishEntity.refreshPositionAndAngles(x, y, z, world.getRandom().nextFloat() * 360.0F, 0.0F);
			world.spawnEntity(silverfishEntity);
		}
	}
}
