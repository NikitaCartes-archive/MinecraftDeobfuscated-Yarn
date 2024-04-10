package net.minecraft.entity.effect;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

class OozingStatusEffect extends StatusEffect {
	private static final int field_51373 = 2;
	public static final int field_51372 = 2;
	private final ToIntFunction<Random> slimeCountFunction;

	protected OozingStatusEffect(StatusEffectCategory category, int color, ToIntFunction<Random> slimeCountFunction) {
		super(category, color, ParticleTypes.ITEM_SLIME);
		this.slimeCountFunction = slimeCountFunction;
	}

	@VisibleForTesting
	protected static int getSlimesToSpawn(int maxEntityCramming, int nearbySlimes, int potentialSlimes) {
		return MathHelper.clamp(0, maxEntityCramming - nearbySlimes, potentialSlimes);
	}

	@Override
	public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
		if (reason == Entity.RemovalReason.KILLED) {
			int i = this.slimeCountFunction.applyAsInt(entity.getRandom());
			World world = entity.getWorld();
			int j = world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
			List<SlimeEntity> list = new ArrayList();
			world.collectEntitiesByType(EntityType.SLIME, entity.getBoundingBox().expand(2.0), entityx -> entityx != entity, list, j);
			int k = getSlimesToSpawn(j, list.size(), i);

			for (int l = 0; l < k; l++) {
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
