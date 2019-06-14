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
		return this.skeletonHorse.field_6002.isPlayerInRange(this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z, 10.0);
	}

	@Override
	public void tick() {
		LocalDifficulty localDifficulty = this.skeletonHorse.field_6002.getLocalDifficulty(new BlockPos(this.skeletonHorse));
		this.skeletonHorse.setTrapped(false);
		this.skeletonHorse.setTame(true);
		this.skeletonHorse.setBreedingAge(0);
		((ServerWorld)this.skeletonHorse.field_6002)
			.addLightning(new LightningEntity(this.skeletonHorse.field_6002, this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z, true));
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
		SkeletonHorseEntity skeletonHorseEntity = EntityType.field_6075.method_5883(this.skeletonHorse.field_6002);
		skeletonHorseEntity.method_5943(this.skeletonHorse.field_6002, localDifficulty, SpawnType.field_16461, null, null);
		skeletonHorseEntity.setPosition(this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z);
		skeletonHorseEntity.field_6008 = 60;
		skeletonHorseEntity.setPersistent();
		skeletonHorseEntity.setTame(true);
		skeletonHorseEntity.setBreedingAge(0);
		skeletonHorseEntity.field_6002.spawnEntity(skeletonHorseEntity);
		return skeletonHorseEntity;
	}

	private SkeletonEntity getSkeleton(LocalDifficulty localDifficulty, HorseBaseEntity horseBaseEntity) {
		SkeletonEntity skeletonEntity = EntityType.field_6137.method_5883(horseBaseEntity.field_6002);
		skeletonEntity.method_5943(horseBaseEntity.field_6002, localDifficulty, SpawnType.field_16461, null, null);
		skeletonEntity.setPosition(horseBaseEntity.x, horseBaseEntity.y, horseBaseEntity.z);
		skeletonEntity.field_6008 = 60;
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
		skeletonEntity.field_6002.spawnEntity(skeletonEntity);
		return skeletonEntity;
	}
}
