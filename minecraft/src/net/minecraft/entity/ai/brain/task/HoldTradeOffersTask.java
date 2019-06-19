package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;

public class HoldTradeOffersTask extends Task<VillagerEntity> {
	@Nullable
	private ItemStack field_18392;
	private final List<ItemStack> offers = Lists.<ItemStack>newArrayList();
	private int field_18394;
	private int field_18395;
	private int field_18396;

	public HoldTradeOffersTask(int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.field_18447, MemoryModuleState.field_18456), i, j);
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
			&& this.field_18396 > 0
			&& villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18447).isPresent();
	}

	public void method_19602(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.run(serverWorld, villagerEntity, l);
		this.method_19603(villagerEntity);
		this.field_18394 = 0;
		this.field_18395 = 0;
		this.field_18396 = 40;
	}

	public void method_19604(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		LivingEntity livingEntity = this.method_19603(villagerEntity);
		this.method_19027(livingEntity, villagerEntity);
		if (!this.offers.isEmpty()) {
			this.method_19026(villagerEntity);
		} else {
			villagerEntity.setEquippedStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
			this.field_18396 = Math.min(this.field_18396, 40);
		}

		this.field_18396--;
	}

	public void method_19605(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.finishRunning(serverWorld, villagerEntity, l);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
		villagerEntity.setEquippedStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
		this.field_18392 = null;
	}

	private void method_19027(LivingEntity livingEntity, VillagerEntity villagerEntity) {
		boolean bl = false;
		ItemStack itemStack = livingEntity.getMainHandStack();
		if (this.field_18392 == null || !ItemStack.areItemsEqualIgnoreDamage(this.field_18392, itemStack)) {
			this.field_18392 = itemStack;
			bl = true;
			this.offers.clear();
		}

		if (bl && !this.field_18392.isEmpty()) {
			this.method_19601(villagerEntity);
			if (!this.offers.isEmpty()) {
				this.field_18396 = 900;
				this.method_19598(villagerEntity);
			}
		}
	}

	private void method_19598(VillagerEntity villagerEntity) {
		villagerEntity.setEquippedStack(EquipmentSlot.field_6173, (ItemStack)this.offers.get(0));
	}

	private void method_19601(VillagerEntity villagerEntity) {
		for (TradeOffer tradeOffer : villagerEntity.getOffers()) {
			if (!tradeOffer.isDisabled() && this.method_19028(tradeOffer)) {
				this.offers.add(tradeOffer.getMutableSellItem());
			}
		}
	}

	private boolean method_19028(TradeOffer tradeOffer) {
		return ItemStack.areItemsEqualIgnoreDamage(this.field_18392, tradeOffer.getAdjustedFirstBuyItem())
			|| ItemStack.areItemsEqualIgnoreDamage(this.field_18392, tradeOffer.getSecondBuyItem());
	}

	private LivingEntity method_19603(VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_18447).get();
		brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntity));
		return livingEntity;
	}

	private void method_19026(VillagerEntity villagerEntity) {
		if (this.offers.size() >= 2 && ++this.field_18394 >= 40) {
			this.field_18395++;
			this.field_18394 = 0;
			if (this.field_18395 > this.offers.size() - 1) {
				this.field_18395 = 0;
			}

			villagerEntity.setEquippedStack(EquipmentSlot.field_6173, (ItemStack)this.offers.get(this.field_18395));
		}
	}
}
