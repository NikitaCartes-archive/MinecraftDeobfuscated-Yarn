package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;

public class SkeletonHorseTrapTriggerGoal extends Goal {
	private final SkeletonHorseEntity skeletonHorse;

	public SkeletonHorseTrapTriggerGoal(SkeletonHorseEntity skeletonHorse) {
		this.skeletonHorse = skeletonHorse;
	}

	@Override
	public boolean canStart() {
		return this.skeletonHorse.world.isPlayerInRange(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ(), 10.0);
	}

	@Override
	public void tick() {
		ServerWorld serverWorld = (ServerWorld)this.skeletonHorse.world;
		LocalDifficulty localDifficulty = serverWorld.getLocalDifficulty(this.skeletonHorse.getBlockPos());
		this.skeletonHorse.setTrapped(false);
		this.skeletonHorse.setTame(true);
		this.skeletonHorse.setBreedingAge(0);
		LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(serverWorld);
		lightningEntity.refreshPositionAfterTeleport(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ());
		lightningEntity.setCosmetic(true);
		serverWorld.spawnEntity(lightningEntity);
		SkeletonEntity skeletonEntity = this.getSkeleton(localDifficulty, this.skeletonHorse);
		skeletonEntity.startRiding(this.skeletonHorse);
		serverWorld.spawnEntityAndPassengers(skeletonEntity);

		for (int i = 0; i < 3; i++) {
			HorseBaseEntity horseBaseEntity = this.getHorse(localDifficulty);
			SkeletonEntity skeletonEntity2 = this.getSkeleton(localDifficulty, horseBaseEntity);
			skeletonEntity2.startRiding(horseBaseEntity);
			horseBaseEntity.addVelocity(this.skeletonHorse.getRandom().nextGaussian() * 0.5, 0.0, this.skeletonHorse.getRandom().nextGaussian() * 0.5);
			serverWorld.spawnEntityAndPassengers(horseBaseEntity);
		}
	}

	private HorseBaseEntity getHorse(LocalDifficulty localDifficulty) {
		SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.create(this.skeletonHorse.world);
		skeletonHorseEntity.initialize((ServerWorld)this.skeletonHorse.world, localDifficulty, SpawnReason.TRIGGERED, null, null);
		skeletonHorseEntity.setPosition(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ());
		skeletonHorseEntity.timeUntilRegen = 60;
		skeletonHorseEntity.setPersistent();
		skeletonHorseEntity.setTame(true);
		skeletonHorseEntity.setBreedingAge(0);
		return skeletonHorseEntity;
	}

	private SkeletonEntity getSkeleton(LocalDifficulty localDifficulty, HorseBaseEntity vehicle) {
		SkeletonEntity skeletonEntity = EntityType.SKELETON.create(vehicle.world);
		skeletonEntity.initialize((ServerWorld)vehicle.world, localDifficulty, SpawnReason.TRIGGERED, null, null);
		skeletonEntity.setPosition(vehicle.getX(), vehicle.getY(), vehicle.getZ());
		skeletonEntity.timeUntilRegen = 60;
		skeletonEntity.setPersistent();
		if (skeletonEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
			skeletonEntity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		}

		skeletonEntity.equipStack(
			EquipmentSlot.MAINHAND,
			EnchantmentHelper.enchant(
				skeletonEntity.getRandom(),
				this.removeEnchantments(skeletonEntity.getMainHandStack()),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)),
				false
			)
		);
		skeletonEntity.equipStack(
			EquipmentSlot.HEAD,
			EnchantmentHelper.enchant(
				skeletonEntity.getRandom(),
				this.removeEnchantments(skeletonEntity.getEquippedStack(EquipmentSlot.HEAD)),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)),
				false
			)
		);
		return skeletonEntity;
	}

	private ItemStack removeEnchantments(ItemStack stack) {
		stack.removeSubTag("Enchantments");
		return stack;
	}
}
