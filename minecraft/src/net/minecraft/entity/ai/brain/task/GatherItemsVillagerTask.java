package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;

public class GatherItemsVillagerTask extends Task<VillagerEntity> {
	private Set<Item> items = ImmutableSet.of();

	public GatherItemsVillagerTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18447, MemoryModuleState.field_18456, MemoryModuleType.field_18442, MemoryModuleState.field_18456));
	}

	protected boolean method_19015(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return LookTargetUtil.canSee(villagerEntity.getBrain(), MemoryModuleType.field_18447, EntityType.field_6077);
	}

	protected boolean method_19016(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.method_19015(serverWorld, villagerEntity);
	}

	protected void method_19017(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18447).get();
		LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
		this.items = getGatherableItems(villagerEntity, villagerEntity2);
	}

	protected void method_19018(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18447).get();
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
			villagerEntity.talkWithVillager(villagerEntity2, l);
			if (villagerEntity.wantsToStartBreeding()
				&& (villagerEntity.getVillagerData().getProfession() == VillagerProfession.field_17056 || villagerEntity2.canBreed())) {
				giveHalfOfStack(villagerEntity, VillagerEntity.ITEM_FOOD_VALUES.keySet(), villagerEntity2);
			}

			if (!this.items.isEmpty() && villagerEntity.getInventory().containsAnyInInv(this.items)) {
				giveHalfOfStack(villagerEntity, this.items, villagerEntity2);
			}
		}
	}

	protected void method_19019(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
	}

	private static Set<Item> getGatherableItems(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		ImmutableSet<Item> immutableSet = villagerEntity2.getVillagerData().getProfession().getGatherableItems();
		ImmutableSet<Item> immutableSet2 = villagerEntity.getVillagerData().getProfession().getGatherableItems();
		return (Set<Item>)immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
	}

	private static void giveHalfOfStack(VillagerEntity villagerEntity, Set<Item> set, LivingEntity livingEntity) {
		BasicInventory basicInventory = villagerEntity.getInventory();
		ItemStack itemStack = ItemStack.EMPTY;
		int i = 0;

		while (i < basicInventory.getInvSize()) {
			ItemStack itemStack2;
			Item item;
			int j;
			label28: {
				itemStack2 = basicInventory.getInvStack(i);
				if (!itemStack2.isEmpty()) {
					item = itemStack2.getItem();
					if (set.contains(item)) {
						if (itemStack2.getCount() > itemStack2.getMaxCount() / 2) {
							j = itemStack2.getCount() / 2;
							break label28;
						}

						if (itemStack2.getCount() > 24) {
							j = itemStack2.getCount() - 24;
							break label28;
						}
					}
				}

				i++;
				continue;
			}

			itemStack2.decrement(j);
			itemStack = new ItemStack(item, j);
			break;
		}

		if (!itemStack.isEmpty()) {
			LookTargetUtil.give(villagerEntity, itemStack, livingEntity);
		}
	}
}
