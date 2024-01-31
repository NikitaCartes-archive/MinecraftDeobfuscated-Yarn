package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
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
		return this.skeletonHorse.getWorld().isPlayerInRange(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ(), 10.0);
	}

	@Override
	public void tick() {
		ServerWorld serverWorld = (ServerWorld)this.skeletonHorse.getWorld();
		LocalDifficulty localDifficulty = serverWorld.getLocalDifficulty(this.skeletonHorse.getBlockPos());
		this.skeletonHorse.setTrapped(false);
		this.skeletonHorse.setTame(true);
		this.skeletonHorse.setBreedingAge(0);
		LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(serverWorld);
		if (lightningEntity != null) {
			lightningEntity.refreshPositionAfterTeleport(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ());
			lightningEntity.setCosmetic(true);
			serverWorld.spawnEntity(lightningEntity);
			SkeletonEntity skeletonEntity = this.getSkeleton(localDifficulty, this.skeletonHorse);
			if (skeletonEntity != null) {
				skeletonEntity.startRiding(this.skeletonHorse);
				serverWorld.spawnEntityAndPassengers(skeletonEntity);

				for (int i = 0; i < 3; i++) {
					AbstractHorseEntity abstractHorseEntity = this.getHorse(localDifficulty);
					if (abstractHorseEntity != null) {
						SkeletonEntity skeletonEntity2 = this.getSkeleton(localDifficulty, abstractHorseEntity);
						if (skeletonEntity2 != null) {
							skeletonEntity2.startRiding(abstractHorseEntity);
							abstractHorseEntity.addVelocity(
								this.skeletonHorse.getRandom().nextTriangular(0.0, 1.1485), 0.0, this.skeletonHorse.getRandom().nextTriangular(0.0, 1.1485)
							);
							serverWorld.spawnEntityAndPassengers(abstractHorseEntity);
						}
					}
				}
			}
		}
	}

	@Nullable
	private AbstractHorseEntity getHorse(LocalDifficulty localDifficulty) {
		SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.create(this.skeletonHorse.getWorld());
		if (skeletonHorseEntity != null) {
			skeletonHorseEntity.initialize((ServerWorld)this.skeletonHorse.getWorld(), localDifficulty, SpawnReason.TRIGGERED, null);
			skeletonHorseEntity.setPosition(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ());
			skeletonHorseEntity.timeUntilRegen = 60;
			skeletonHorseEntity.setPersistent();
			skeletonHorseEntity.setTame(true);
			skeletonHorseEntity.setBreedingAge(0);
		}

		return skeletonHorseEntity;
	}

	@Nullable
	private SkeletonEntity getSkeleton(LocalDifficulty localDifficulty, AbstractHorseEntity vehicle) {
		SkeletonEntity skeletonEntity = EntityType.SKELETON.create(vehicle.getWorld());
		if (skeletonEntity != null) {
			skeletonEntity.initialize((ServerWorld)vehicle.getWorld(), localDifficulty, SpawnReason.TRIGGERED, null);
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
		}

		return skeletonEntity;
	}

	private ItemStack removeEnchantments(ItemStack stack) {
		stack.removeSubNbt("Enchantments");
		return stack;
	}
}
