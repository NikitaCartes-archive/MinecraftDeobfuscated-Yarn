/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;

public class HoldTradeOffersTask
extends Task<VillagerEntity> {
    @Nullable
    private ItemStack customerHeldStack;
    private final List<ItemStack> offers = Lists.newArrayList();
    private int offerShownTicks;
    private int offerIndex;
    private int ticksLeft;

    public HoldTradeOffersTask(int rminRunTime, int maxRunTime) {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT), rminRunTime, maxRunTime);
    }

    @Override
    public boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        if (!brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        }
        LivingEntity livingEntity = brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return livingEntity.getType() == EntityType.PLAYER && villagerEntity.isAlive() && livingEntity.isAlive() && !villagerEntity.isBaby() && villagerEntity.squaredDistanceTo(livingEntity) <= 17.0;
    }

    @Override
    public boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.shouldRun(serverWorld, villagerEntity) && this.ticksLeft > 0 && villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        super.run(serverWorld, villagerEntity, l);
        this.findPotentialCuatomer(villagerEntity);
        this.offerShownTicks = 0;
        this.offerIndex = 0;
        this.ticksLeft = 40;
    }

    @Override
    public void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        LivingEntity livingEntity = this.findPotentialCuatomer(villagerEntity);
        this.setupOffers(livingEntity, villagerEntity);
        if (!this.offers.isEmpty()) {
            this.refreshShownOffer(villagerEntity);
        } else {
            villagerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.ticksLeft = Math.min(this.ticksLeft, 40);
        }
        --this.ticksLeft;
    }

    @Override
    public void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        super.finishRunning(serverWorld, villagerEntity, l);
        villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
        villagerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.customerHeldStack = null;
    }

    private void setupOffers(LivingEntity customer, VillagerEntity villager) {
        boolean bl = false;
        ItemStack itemStack = customer.getMainHandStack();
        if (this.customerHeldStack == null || !ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, itemStack)) {
            this.customerHeldStack = itemStack;
            bl = true;
            this.offers.clear();
        }
        if (bl && !this.customerHeldStack.isEmpty()) {
            this.loadPossibleOffers(villager);
            if (!this.offers.isEmpty()) {
                this.ticksLeft = 900;
                this.holdOffer(villager);
            }
        }
    }

    private void holdOffer(VillagerEntity villager) {
        villager.equipStack(EquipmentSlot.MAINHAND, this.offers.get(0));
    }

    private void loadPossibleOffers(VillagerEntity villager) {
        for (TradeOffer tradeOffer : villager.getOffers()) {
            if (tradeOffer.isDisabled() || !this.isPossible(tradeOffer)) continue;
            this.offers.add(tradeOffer.getMutableSellItem());
        }
    }

    private boolean isPossible(TradeOffer offer) {
        return ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, offer.getAdjustedFirstBuyItem()) || ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, offer.getSecondBuyItem());
    }

    private LivingEntity findPotentialCuatomer(VillagerEntity villager) {
        Brain<VillagerEntity> brain = villager.getBrain();
        LivingEntity livingEntity = brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(livingEntity));
        return livingEntity;
    }

    private void refreshShownOffer(VillagerEntity villager) {
        if (this.offers.size() >= 2 && ++this.offerShownTicks >= 40) {
            ++this.offerIndex;
            this.offerShownTicks = 0;
            if (this.offerIndex > this.offers.size() - 1) {
                this.offerIndex = 0;
            }
            villager.equipStack(EquipmentSlot.MAINHAND, this.offers.get(this.offerIndex));
        }
    }

    @Override
    public /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    public /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    public /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    public /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}

