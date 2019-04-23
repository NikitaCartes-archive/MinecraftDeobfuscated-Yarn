/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class GatherItemsVillagerTask
extends Task<VillagerEntity> {
    private Set<Item> items = ImmutableSet.of();

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT), Pair.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean method_19015(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return LookTargetUtil.canSee(villagerEntity.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
    }

    protected boolean method_19016(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.method_19015(serverWorld, villagerEntity);
    }

    protected void method_19017(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
        this.items = GatherItemsVillagerTask.getGatherableItems(villagerEntity, villagerEntity2);
    }

    protected void method_19018(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
        villagerEntity.talkWithVillager(villagerEntity2, l);
        if (villagerEntity.wantsToStartBreeding() && villagerEntity2.canBreed()) {
            GatherItemsVillagerTask.giveHalfOfStack(villagerEntity, VillagerEntity.ITEM_FOOD_VALUES.keySet(), villagerEntity2);
        }
        if (!this.items.isEmpty() && villagerEntity.getInventory().containsAnyInInv(this.items)) {
            GatherItemsVillagerTask.giveHalfOfStack(villagerEntity, this.items, villagerEntity2);
        }
    }

    protected void method_19019(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> getGatherableItems(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
        ImmutableSet<Item> immutableSet = villagerEntity2.getVillagerData().getProfession().getGatherableItems();
        ImmutableSet<Item> immutableSet2 = villagerEntity.getVillagerData().getProfession().getGatherableItems();
        return immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
    }

    private static void giveHalfOfStack(VillagerEntity villagerEntity, Set<Item> set, LivingEntity livingEntity) {
        BasicInventory basicInventory = villagerEntity.getInventory();
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < basicInventory.getInvSize(); ++i) {
            Item item;
            ItemStack itemStack2 = basicInventory.getInvStack(i);
            if (itemStack2.isEmpty() || !set.contains(item = itemStack2.getItem())) continue;
            int j = itemStack2.getAmount() / 2;
            itemStack2.subtractAmount(j);
            itemStack = new ItemStack(item, j);
            break;
        }
        if (!itemStack.isEmpty()) {
            LookTargetUtil.give(villagerEntity, itemStack, livingEntity);
        }
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        return this.method_19016(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_19019(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_19018(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_19017(serverWorld, (VillagerEntity)livingEntity, l);
    }
}

