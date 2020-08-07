package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;

public class HoldTradeOffersTask extends Task<VillagerEntity> {
	@Nullable
	private ItemStack customerHeldStack;
	private final List<ItemStack> offers = Lists.<ItemStack>newArrayList();
	private int offerShownTicks;
	private int offerIndex;
	private int ticksLeft;

	public HoldTradeOffersTask(int minRunTime, int maxRunTime) {
		super(ImmutableMap.of(MemoryModuleType.field_18447, MemoryModuleState.field_18456), minRunTime, maxRunTime);
	}

	public boolean method_19599(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		if (!brain.getOptionalMemory(MemoryModuleType.field_18447).isPresent()) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_18447).get();
			return livingEntity.getType() == EntityType.field_6097
				&& villagerEntity.isAlive()
				&& livingEntity.isAlive()
				&& !villagerEntity.isBaby()
				&& villagerEntity.squaredDistanceTo(livingEntity) <= 17.0;
		}
	}

	public boolean method_19600(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.method_19599(serverWorld, villagerEntity)
			&& this.ticksLeft > 0
			&& villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18447).isPresent();
	}

	public void method_19602(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.run(serverWorld, villagerEntity, l);
		this.findPotentialCustomer(villagerEntity);
		this.offerShownTicks = 0;
		this.offerIndex = 0;
		this.ticksLeft = 40;
	}

	public void method_19604(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		LivingEntity livingEntity = this.findPotentialCustomer(villagerEntity);
		this.setupOffers(livingEntity, villagerEntity);
		if (!this.offers.isEmpty()) {
			this.refreshShownOffer(villagerEntity);
		} else {
			villagerEntity.equipStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
			this.ticksLeft = Math.min(this.ticksLeft, 40);
		}

		this.ticksLeft--;
	}

	public void method_19605(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.finishRunning(serverWorld, villagerEntity, l);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
		villagerEntity.equipStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
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
		villager.equipStack(EquipmentSlot.field_6173, (ItemStack)this.offers.get(0));
	}

	private void loadPossibleOffers(VillagerEntity villager) {
		for (TradeOffer tradeOffer : villager.getOffers()) {
			if (!tradeOffer.isDisabled() && this.isPossible(tradeOffer)) {
				this.offers.add(tradeOffer.getMutableSellItem());
			}
		}
	}

	private boolean isPossible(TradeOffer offer) {
		return ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, offer.getAdjustedFirstBuyItem())
			|| ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, offer.getSecondBuyItem());
	}

	private LivingEntity findPotentialCustomer(VillagerEntity villager) {
		Brain<?> brain = villager.getBrain();
		LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_18447).get();
		brain.remember(MemoryModuleType.field_18446, new EntityLookTarget(livingEntity, true));
		return livingEntity;
	}

	private void refreshShownOffer(VillagerEntity villager) {
		if (this.offers.size() >= 2 && ++this.offerShownTicks >= 40) {
			this.offerIndex++;
			this.offerShownTicks = 0;
			if (this.offerIndex > this.offers.size() - 1) {
				this.offerIndex = 0;
			}

			villager.equipStack(EquipmentSlot.field_6173, (ItemStack)this.offers.get(this.offerIndex));
		}
	}
}
