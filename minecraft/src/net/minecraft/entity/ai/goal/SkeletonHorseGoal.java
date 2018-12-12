package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;

public class SkeletonHorseGoal extends Goal {
	private final SkeletonHorseEntity owner;

	public SkeletonHorseGoal(SkeletonHorseEntity skeletonHorseEntity) {
		this.owner = skeletonHorseEntity;
	}

	@Override
	public boolean canStart() {
		return this.owner.world.containsVisiblePlayer(this.owner.x, this.owner.y, this.owner.z, 10.0);
	}

	@Override
	public void tick() {
		LocalDifficulty localDifficulty = this.owner.world.getLocalDifficulty(new BlockPos(this.owner));
		this.owner.method_6813(false);
		this.owner.setTame(true);
		this.owner.setBreedingAge(0);
		this.owner.world.addGlobalEntity(new LightningEntity(this.owner.world, this.owner.x, this.owner.y, this.owner.z, true));
		SkeletonEntity skeletonEntity = this.method_6811(localDifficulty, this.owner);
		skeletonEntity.startRiding(this.owner);

		for (int i = 0; i < 3; i++) {
			HorseBaseEntity horseBaseEntity = this.method_6810(localDifficulty);
			SkeletonEntity skeletonEntity2 = this.method_6811(localDifficulty, horseBaseEntity);
			skeletonEntity2.startRiding(horseBaseEntity);
			horseBaseEntity.addVelocity(this.owner.getRand().nextGaussian() * 0.5, 0.0, this.owner.getRand().nextGaussian() * 0.5);
		}
	}

	private HorseBaseEntity method_6810(LocalDifficulty localDifficulty) {
		SkeletonHorseEntity skeletonHorseEntity = new SkeletonHorseEntity(this.owner.world);
		skeletonHorseEntity.prepareEntityData(this.owner.world, localDifficulty, SpawnType.field_16461, null, null);
		skeletonHorseEntity.setPosition(this.owner.x, this.owner.y, this.owner.z);
		skeletonHorseEntity.field_6008 = 60;
		skeletonHorseEntity.setPersistent();
		skeletonHorseEntity.setTame(true);
		skeletonHorseEntity.setBreedingAge(0);
		skeletonHorseEntity.world.spawnEntity(skeletonHorseEntity);
		return skeletonHorseEntity;
	}

	private SkeletonEntity method_6811(LocalDifficulty localDifficulty, HorseBaseEntity horseBaseEntity) {
		SkeletonEntity skeletonEntity = new SkeletonEntity(horseBaseEntity.world);
		skeletonEntity.prepareEntityData(horseBaseEntity.world, localDifficulty, SpawnType.field_16461, null, null);
		skeletonEntity.setPosition(horseBaseEntity.x, horseBaseEntity.y, horseBaseEntity.z);
		skeletonEntity.field_6008 = 60;
		skeletonEntity.setPersistent();
		if (skeletonEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
			skeletonEntity.setEquippedStack(EquipmentSlot.HEAD, new ItemStack(Items.field_8743));
		}

		skeletonEntity.setEquippedStack(
			EquipmentSlot.HAND_MAIN,
			EnchantmentHelper.enchant(
				skeletonEntity.getRand(),
				skeletonEntity.getMainHandStack(),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRand().nextInt(18)),
				false
			)
		);
		skeletonEntity.setEquippedStack(
			EquipmentSlot.HEAD,
			EnchantmentHelper.enchant(
				skeletonEntity.getRand(),
				skeletonEntity.getEquippedStack(EquipmentSlot.HEAD),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRand().nextInt(18)),
				false
			)
		);
		skeletonEntity.world.spawnEntity(skeletonEntity);
		return skeletonEntity;
	}
}
