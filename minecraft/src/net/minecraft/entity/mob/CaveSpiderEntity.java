package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class CaveSpiderEntity extends SpiderEntity {
	public CaveSpiderEntity(EntityType<? extends CaveSpiderEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createCaveSpiderAttributes() {
		return SpiderEntity.createSpiderAttributes().add(EntityAttributes.MAX_HEALTH, 12.0);
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (super.tryAttack(target)) {
			if (target instanceof LivingEntity) {
				int i = 0;
				if (this.getWorld().getDifficulty() == Difficulty.NORMAL) {
					i = 7;
				} else if (this.getWorld().getDifficulty() == Difficulty.HARD) {
					i = 15;
				}

				if (i > 0) {
					((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, i * 20, 0), this);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		return entityData;
	}

	@Override
	public Vec3d getVehicleAttachmentPos(Entity vehicle) {
		return vehicle.getWidth() <= this.getWidth() ? new Vec3d(0.0, 0.21875 * (double)this.getScale(), 0.0) : super.getVehicleAttachmentPos(vehicle);
	}
}
