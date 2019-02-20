package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.class_4048;
import net.minecraft.class_4050;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class CaveSpiderEntity extends SpiderEntity {
	public CaveSpiderEntity(EntityType<? extends CaveSpiderEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(12.0);
	}

	@Override
	public boolean attack(Entity entity) {
		if (super.attack(entity)) {
			if (entity instanceof LivingEntity) {
				int i = 0;
				if (this.world.getDifficulty() == Difficulty.NORMAL) {
					i = 7;
				} else if (this.world.getDifficulty() == Difficulty.HARD) {
					i = 15;
				}

				if (i > 0) {
					((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, i * 20, 0));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		return entityData;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 0.45F;
	}
}
