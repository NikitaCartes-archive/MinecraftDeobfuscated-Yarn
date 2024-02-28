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

public class HoldTradeOffersTask extends MultiTickTask<VillagerEntity> {
	private static final int RUN_INTERVAL = 900;
	private static final int OFFER_SHOWING_INTERVAL = 40;
	@Nullable
	private ItemStack customerHeldStack;
	private final List<ItemStack> offers = Lists.<ItemStack>newArrayList();
	private int offerShownTicks;
	private int offerIndex;
	private int ticksLeft;

	public HoldTradeOffersTask(int minRunTime, int maxRunTime) {
		super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT), minRunTime, maxRunTime);
	}

	public boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		if (brain.getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).isEmpty()) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
			return livingEntity.getType() == EntityType.PLAYER
				&& villagerEntity.isAlive()
				&& livingEntity.isAlive()
				&& !villagerEntity.isBaby()
				&& villagerEntity.squaredDistanceTo(livingEntity) <= 17.0;
		}
	}

	public boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.shouldRun(serverWorld, villagerEntity)
			&& this.ticksLeft > 0
			&& villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
	}

	public void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.run(serverWorld, villagerEntity, l);
		this.findPotentialCustomer(villagerEntity);
		this.offerShownTicks = 0;
		this.offerIndex = 0;
		this.ticksLeft = 40;
	}

	public void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		LivingEntity livingEntity = this.findPotentialCustomer(villagerEntity);
		this.setupOffers(livingEntity, villagerEntity);
		if (!this.offers.isEmpty()) {
			this.refreshShownOffer(villagerEntity);
		} else {
			holdNothing(villagerEntity);
			this.ticksLeft = Math.min(this.ticksLeft, 40);
		}

		this.ticksLeft--;
	}

	public void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.finishRunning(serverWorld, villagerEntity, l);
		villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
		holdNothing(villagerEntity);
		this.customerHeldStack = null;
	}

	private void setupOffers(LivingEntity customer, VillagerEntity villager) {
		boolean bl = false;
		ItemStack itemStack = customer.getMainHandStack();
		if (this.customerHeldStack == null || !ItemStack.areItemsEqual(this.customerHeldStack, itemStack)) {
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
		holdOffer(villager, (ItemStack)this.offers.get(0));
	}

	private void loadPossibleOffers(VillagerEntity villager) {
		for (TradeOffer tradeOffer : villager.getOffers()) {
			if (!tradeOffer.isDisabled() && this.isPossible(tradeOffer)) {
				this.offers.add(tradeOffer.copySellItem());
			}
		}
	}

	private boolean isPossible(TradeOffer offer) {
		return ItemStack.areItemsEqual(this.customerHeldStack, offer.getDisplayedFirstBuyItem())
			|| ItemStack.areItemsEqual(this.customerHeldStack, offer.getDisplayedSecondBuyItem());
	}

	private static void holdNothing(VillagerEntity villager) {
		villager.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		villager.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085F);
	}

	private static void holdOffer(VillagerEntity villager, ItemStack stack) {
		villager.equipStack(EquipmentSlot.MAINHAND, stack);
		villager.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
	}

	private LivingEntity findPotentialCustomer(VillagerEntity villager) {
		Brain<?> brain = villager.getBrain();
		LivingEntity livingEntity = (LivingEntity)brain.getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
		brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(livingEntity, true));
		return livingEntity;
	}

	private void refreshShownOffer(VillagerEntity villager) {
		if (this.offers.size() >= 2 && ++this.offerShownTicks >= 40) {
			this.offerIndex++;
			this.offerShownTicks = 0;
			if (this.offerIndex > this.offers.size() - 1) {
				this.offerIndex = 0;
			}

			holdOffer(villager, (ItemStack)this.offers.get(this.offerIndex));
		}
	}
}
