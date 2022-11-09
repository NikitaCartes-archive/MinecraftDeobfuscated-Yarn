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
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;

public class GatherItemsVillagerTask extends MultiTickTask<VillagerEntity> {
	private static final int MAX_RANGE = 5;
	private static final float WALK_TOGETHER_SPEED = 0.5F;
	private Set<Item> items = ImmutableSet.of();

	public GatherItemsVillagerTask() {
		super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return LookTargetUtil.canSee(villagerEntity.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.shouldRun(serverWorld, villagerEntity);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
		LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2, 0.5F);
		this.items = getGatherableItems(villagerEntity, villagerEntity2);
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2, 0.5F);
			villagerEntity.talkWithVillager(serverWorld, villagerEntity2, l);
			if (villagerEntity.wantsToStartBreeding() && (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER || villagerEntity2.canBreed())) {
				giveHalfOfStack(villagerEntity, VillagerEntity.ITEM_FOOD_VALUES.keySet(), villagerEntity2);
			}

			if (villagerEntity2.getVillagerData().getProfession() == VillagerProfession.FARMER
				&& villagerEntity.getInventory().count(Items.WHEAT) > Items.WHEAT.getMaxCount() / 2) {
				giveHalfOfStack(villagerEntity, ImmutableSet.of(Items.WHEAT), villagerEntity2);
			}

			if (!this.items.isEmpty() && villagerEntity.getInventory().containsAny(this.items)) {
				giveHalfOfStack(villagerEntity, this.items, villagerEntity2);
			}
		}
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
	}

	private static Set<Item> getGatherableItems(VillagerEntity entity, VillagerEntity target) {
		ImmutableSet<Item> immutableSet = target.getVillagerData().getProfession().gatherableItems();
		ImmutableSet<Item> immutableSet2 = entity.getVillagerData().getProfession().gatherableItems();
		return (Set<Item>)immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
	}

	private static void giveHalfOfStack(VillagerEntity villager, Set<Item> validItems, LivingEntity target) {
		SimpleInventory simpleInventory = villager.getInventory();
		ItemStack itemStack = ItemStack.EMPTY;
		int i = 0;

		while (i < simpleInventory.size()) {
			ItemStack itemStack2;
			Item item;
			int j;
			label28: {
				itemStack2 = simpleInventory.getStack(i);
				if (!itemStack2.isEmpty()) {
					item = itemStack2.getItem();
					if (validItems.contains(item)) {
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
			LookTargetUtil.give(villager, itemStack, target.getPos());
		}
	}
}
