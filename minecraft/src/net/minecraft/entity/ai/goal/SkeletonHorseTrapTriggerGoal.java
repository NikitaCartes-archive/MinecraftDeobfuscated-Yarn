package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;

public class SkeletonHorseTrapTriggerGoal extends Goal {
	private final SkeletonHorseEntity skeletonHorse;

	public SkeletonHorseTrapTriggerGoal(SkeletonHorseEntity skeletonHorseEntity) {
		this.skeletonHorse = skeletonHorseEntity;
	}

	@Override
	public boolean canStart() {
		return this.skeletonHorse.world.isPlayerInRange(this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z, 10.0);
	}

	@Override
	public void tick() {
		LocalDifficulty localDifficulty = this.skeletonHorse.world.getLocalDifficulty(new BlockPos(this.skeletonHorse));
		this.skeletonHorse.setTrapped(false);
		this.skeletonHorse.setTame(true);
		this.skeletonHorse.setBreedingAge(0);
		((ServerWorld)this.skeletonHorse.world)
			.addLightning(new LightningEntity(this.skeletonHorse.world, this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z, true));
		SkeletonEntity skeletonEntity = this.getSkeleton(localDifficulty, this.skeletonHorse);
		skeletonEntity.startRiding(this.skeletonHorse);

		for (int i = 0; i < 3; i++) {
			HorseBaseEntity horseBaseEntity = this.getHorse(localDifficulty);
			SkeletonEntity skeletonEntity2 = this.getSkeleton(localDifficulty, horseBaseEntity);
			skeletonEntity2.startRiding(horseBaseEntity);
			horseBaseEntity.addVelocity(this.skeletonHorse.getRand().nextGaussian() * 0.5, 0.0, this.skeletonHorse.getRand().nextGaussian() * 0.5);
		}
	}

	private HorseBaseEntity getHorse(LocalDifficulty localDifficulty) {
		SkeletonHorseEntity skeletonHorseEntity = EntityType.field_6075.create(this.skeletonHorse.world);
		skeletonHorseEntity.initialize(this.skeletonHorse.world, localDifficulty, SpawnType.field_16461, null, null);
		skeletonHorseEntity.setPosition(this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z);
		skeletonHorseEntity.timeUntilRegen = 60;
		skeletonHorseEntity.setPersistent();
		skeletonHorseEntity.setTame(true);
		skeletonHorseEntity.setBreedingAge(0);
		skeletonHorseEntity.world.spawnEntity(skeletonHorseEntity);
		return skeletonHorseEntity;
	}

	private SkeletonEntity getSkeleton(LocalDifficulty localDifficulty, HorseBaseEntity horseBaseEntity) {
		SkeletonEntity skeletonEntity = EntityType.field_6137.create(horseBaseEntity.world);
		skeletonEntity.initialize(horseBaseEntity.world, localDifficulty, SpawnType.field_16461, null, null);
		skeletonEntity.setPosition(horseBaseEntity.x, horseBaseEntity.y, horseBaseEntity.z);
		skeletonEntity.timeUntilRegen = 60;
		skeletonEntity.setPersistent();
		if (skeletonEntity.getEquippedStack(EquipmentSlot.field_6169).isEmpty()) {
			skeletonEntity.setEquippedStack(EquipmentSlot.field_6169, new ItemStack(Items.field_8743));
		}

		skeletonEntity.setEquippedStack(
			EquipmentSlot.field_6173,
			EnchantmentHelper.enchant(
				skeletonEntity.getRand(),
				skeletonEntity.getMainHandStack(),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRand().nextInt(18)),
				false
			)
		);
		skeletonEntity.setEquippedStack(
			EquipmentSlot.field_6169,
			EnchantmentHelper.enchant(
				skeletonEntity.getRand(),
				skeletonEntity.getEquippedStack(EquipmentSlot.field_6169),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRand().nextInt(18)),
				false
			)
		);
		skeletonEntity.world.spawnEntity(skeletonEntity);
		return skeletonEntity;
	}
}
