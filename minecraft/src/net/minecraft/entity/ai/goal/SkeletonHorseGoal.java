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

public class SkeletonHorseGoal extends Goal {
	private final SkeletonHorseEntity owner;

	public SkeletonHorseGoal(SkeletonHorseEntity skeletonHorseEntity) {
		this.owner = skeletonHorseEntity;
	}

	@Override
	public boolean canStart() {
		return this.owner.field_6002.method_18458(this.owner.x, this.owner.y, this.owner.z, 10.0);
	}

	@Override
	public void tick() {
		LocalDifficulty localDifficulty = this.owner.field_6002.method_8404(new BlockPos(this.owner));
		this.owner.method_6813(false);
		this.owner.setTame(true);
		this.owner.setBreedingAge(0);
		((ServerWorld)this.owner.field_6002).addLightning(new LightningEntity(this.owner.field_6002, this.owner.x, this.owner.y, this.owner.z, true));
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
		SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.method_5883(this.owner.field_6002);
		skeletonHorseEntity.method_5943(this.owner.field_6002, localDifficulty, SpawnType.field_16461, null, null);
		skeletonHorseEntity.setPosition(this.owner.x, this.owner.y, this.owner.z);
		skeletonHorseEntity.field_6008 = 60;
		skeletonHorseEntity.setPersistent();
		skeletonHorseEntity.setTame(true);
		skeletonHorseEntity.setBreedingAge(0);
		skeletonHorseEntity.field_6002.spawnEntity(skeletonHorseEntity);
		return skeletonHorseEntity;
	}

	private SkeletonEntity method_6811(LocalDifficulty localDifficulty, HorseBaseEntity horseBaseEntity) {
		SkeletonEntity skeletonEntity = EntityType.SKELETON.method_5883(horseBaseEntity.field_6002);
		skeletonEntity.method_5943(horseBaseEntity.field_6002, localDifficulty, SpawnType.field_16461, null, null);
		skeletonEntity.setPosition(horseBaseEntity.x, horseBaseEntity.y, horseBaseEntity.z);
		skeletonEntity.field_6008 = 60;
		skeletonEntity.setPersistent();
		if (skeletonEntity.method_6118(EquipmentSlot.HEAD).isEmpty()) {
			skeletonEntity.method_5673(EquipmentSlot.HEAD, new ItemStack(Items.field_8743));
		}

		skeletonEntity.method_5673(
			EquipmentSlot.HAND_MAIN,
			EnchantmentHelper.enchant(
				skeletonEntity.getRand(),
				skeletonEntity.method_6047(),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRand().nextInt(18)),
				false
			)
		);
		skeletonEntity.method_5673(
			EquipmentSlot.HEAD,
			EnchantmentHelper.enchant(
				skeletonEntity.getRand(),
				skeletonEntity.method_6118(EquipmentSlot.HEAD),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRand().nextInt(18)),
				false
			)
		);
		skeletonEntity.field_6002.spawnEntity(skeletonEntity);
		return skeletonEntity;
	}
}
