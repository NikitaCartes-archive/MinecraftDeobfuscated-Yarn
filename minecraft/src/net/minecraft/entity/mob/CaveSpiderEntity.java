package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
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
	public boolean tryAttack(Entity entity) {
		if (super.tryAttack(entity)) {
			if (entity instanceof LivingEntity) {
				int i = 0;
				if (this.world.getDifficulty() == Difficulty.field_5802) {
					i = 7;
				} else if (this.world.getDifficulty() == Difficulty.field_5807) {
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
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		return entityData;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 0.45F;
	}
}
