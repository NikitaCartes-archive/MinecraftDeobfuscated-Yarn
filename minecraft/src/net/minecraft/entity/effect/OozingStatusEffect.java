package net.minecraft.entity.effect;

import java.util.function.ToIntFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

class OozingStatusEffect extends StatusEffect {
	private final ToIntFunction<Random> slimeCountFunction;

	protected OozingStatusEffect(StatusEffectCategory category, int color, ToIntFunction<Random> slimeCountFunction) {
		super(category, color, ParticleTypes.ITEM_SLIME);
		this.slimeCountFunction = slimeCountFunction;
	}

	@Override
	public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
		if (reason == Entity.RemovalReason.KILLED) {
			int i = this.slimeCountFunction.applyAsInt(entity.getRandom());

			for (int j = 0; j < i; j++) {
				this.spawnSlime(entity.getWorld(), entity.getX(), entity.getY() + 0.5, entity.getZ());
			}
		}
	}

	private void spawnSlime(World world, double x, double y, double z) {
		SlimeEntity slimeEntity = EntityType.SLIME.create(world);
		if (slimeEntity != null) {
			slimeEntity.setSize(2, true);
			slimeEntity.refreshPositionAndAngles(x, y, z, world.getRandom().nextFloat() * 360.0F, 0.0F);
			world.spawnEntity(slimeEntity);
		}
	}
}
