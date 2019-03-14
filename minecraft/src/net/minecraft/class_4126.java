package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class class_4126 extends Task<VillagerEntity> {
	private final int field_18388 = 5;
	private Set<Item> field_18389 = ImmutableSet.of();

	protected boolean method_19015(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.method_18918(villagerEntity.getBrain(), MemoryModuleType.field_18447, EntityType.VILLAGER);
	}

	protected boolean method_19016(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.method_18918(villagerEntity.getBrain(), MemoryModuleType.field_18447, EntityType.VILLAGER);
	}

	protected void method_19017(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getMemory(MemoryModuleType.field_18447).get();
		this.method_18916(villagerEntity, villagerEntity2);
		ImmutableSet<Item> immutableSet = villagerEntity2.getVillagerData().getProfession().getGatherableItems();
		ImmutableSet<Item> immutableSet2 = villagerEntity.getVillagerData().getProfession().getGatherableItems();
		this.field_18389 = (Set<Item>)immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
	}

	protected void method_19018(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getMemory(MemoryModuleType.field_18447).get();
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			this.method_18916(villagerEntity, villagerEntity2);
			villagerEntity.talkWithVillager(villagerEntity2, l);
			if (villagerEntity.wantsToStartBreeding() && villagerEntity2.canBreed()) {
				this.method_19013(villagerEntity, VillagerEntity.ITEM_FOOD_VALUES.keySet());
			}

			if (!this.field_18389.isEmpty() && villagerEntity.getInventory().method_18862(this.field_18389)) {
				this.method_19013(villagerEntity, this.field_18389);
			}
		}
	}

	protected void method_19019(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18447, MemoryModuleState.field_18456), Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
	}

	private void method_19013(VillagerEntity villagerEntity, Set<Item> set) {
		BasicInventory basicInventory = villagerEntity.getInventory();
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < basicInventory.getInvSize(); i++) {
			ItemStack itemStack2 = basicInventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (set.contains(item)) {
					int j = itemStack2.getAmount() / 2;
					itemStack2.subtractAmount(j);
					itemStack = new ItemStack(item, j);
					break;
				}
			}
		}

		if (!itemStack.isEmpty()) {
			double d = villagerEntity.y - 0.3F + (double)villagerEntity.getStandingEyeHeight();
			ItemEntity itemEntity = new ItemEntity(villagerEntity.world, villagerEntity.x, d, villagerEntity.z, itemStack);
			float f = 0.3F;
			float g = villagerEntity.headYaw;
			float h = villagerEntity.pitch;
			itemEntity.setVelocity(
				(double)(0.3F * -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0))),
				(double)(0.3F * -MathHelper.sin(h * (float) (Math.PI / 180.0)) + 0.1F),
				(double)(0.3F * MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0)))
			);
			itemEntity.setToDefaultPickupDelay();
			villagerEntity.world.spawnEntity(itemEntity);
		}
	}
}
