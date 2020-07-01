/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;

public class GatherItemsVillagerTask
extends Task<VillagerEntity> {
    private Set<Item> items = ImmutableSet.of();

    public GatherItemsVillagerTask() {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return LookTargetUtil.canSee(villagerEntity.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.shouldRun(serverWorld, villagerEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2, 0.5f);
        this.items = GatherItemsVillagerTask.getGatherableItems(villagerEntity, villagerEntity2);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2, 0.5f);
        villagerEntity.talkWithVillager(serverWorld, villagerEntity2, l);
        if (villagerEntity.wantsToStartBreeding() && (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER || villagerEntity2.canBreed())) {
            GatherItemsVillagerTask.giveHalfOfStack(villagerEntity, VillagerEntity.ITEM_FOOD_VALUES.keySet(), villagerEntity2);
        }
        if (villagerEntity2.getVillagerData().getProfession() == VillagerProfession.FARMER && villagerEntity.getInventory().count(Items.WHEAT) > Items.WHEAT.getMaxCount() / 2) {
            GatherItemsVillagerTask.giveHalfOfStack(villagerEntity, ImmutableSet.of(Items.WHEAT), villagerEntity2);
        }
        if (!this.items.isEmpty() && villagerEntity.getInventory().containsAny(this.items)) {
            GatherItemsVillagerTask.giveHalfOfStack(villagerEntity, this.items, villagerEntity2);
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> getGatherableItems(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
        ImmutableSet<Item> immutableSet = villagerEntity2.getVillagerData().getProfession().getGatherableItems();
        ImmutableSet<Item> immutableSet2 = villagerEntity.getVillagerData().getProfession().getGatherableItems();
        return immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
    }

    private static void giveHalfOfStack(VillagerEntity villager, Set<Item> validItems, LivingEntity target) {
        SimpleInventory simpleInventory = villager.getInventory();
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < simpleInventory.size(); ++i) {
            int j;
            Item item;
            ItemStack itemStack2 = simpleInventory.getStack(i);
            if (itemStack2.isEmpty() || !validItems.contains(item = itemStack2.getItem())) continue;
            if (itemStack2.getCount() > itemStack2.getMaxCount() / 2) {
                j = itemStack2.getCount() / 2;
            } else {
                if (itemStack2.getCount() <= 24) continue;
                j = itemStack2.getCount() - 24;
            }
            itemStack2.decrement(j);
            itemStack = new ItemStack(item, j);
            break;
        }
        if (!itemStack.isEmpty()) {
            LookTargetUtil.give(villager, itemStack, target.getPos());
        }
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}

