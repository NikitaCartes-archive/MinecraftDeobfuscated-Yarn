package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TraderRecipe;

public class InteractTask extends Task<LivingEntity> {
	private ItemStack field_18392;
	private final ArrayList<ItemStack> field_18393 = new ArrayList();
	private int field_18394;
	private int field_18395;
	private int field_18396;

	public InteractTask(int i, int j) {
		super(i, j);
	}

	@Override
	public boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		if (!brain.getMemory(MemoryModuleType.field_18447).isPresent()) {
			return false;
		} else {
			LivingEntity livingEntity2 = (LivingEntity)brain.getMemory(MemoryModuleType.field_18447).get();
			return livingEntity.getType() == EntityType.VILLAGER
				&& livingEntity2.getType() == EntityType.PLAYER
				&& livingEntity.isValid()
				&& livingEntity2.isValid()
				&& !livingEntity.isChild()
				&& livingEntity.squaredDistanceTo(livingEntity2) <= 17.0;
		}
	}

	@Override
	public boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		return this.shouldRun(serverWorld, livingEntity) && this.field_18396 > 0 && livingEntity.getBrain().getMemory(MemoryModuleType.field_18447).isPresent();
	}

	@Override
	public void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		super.run(serverWorld, livingEntity, l);
		Brain<?> brain = livingEntity.getBrain();
		PlayerEntity playerEntity = (PlayerEntity)brain.getMemory(MemoryModuleType.field_18447).get();
		if (brain.hasMemoryModule(MemoryModuleType.field_18446)) {
			brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(playerEntity));
		}

		this.field_18394 = 0;
		this.field_18395 = 0;
		this.field_18396 = 40;
	}

	@Override
	public void keepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		PlayerEntity playerEntity = (PlayerEntity)brain.getMemory(MemoryModuleType.field_18447).get();
		VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
		if (brain.hasMemoryModule(MemoryModuleType.field_18446)) {
			brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(playerEntity));
		}

		this.method_19027(playerEntity, villagerEntity);
		if (!this.field_18393.isEmpty()) {
			this.method_19026(villagerEntity);
		} else {
			villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
			this.field_18396 = Math.min(this.field_18396, 40);
		}

		this.field_18396--;
	}

	@Override
	public void method_18926(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		super.method_18926(serverWorld, livingEntity, l);
		livingEntity.getBrain().forget(MemoryModuleType.field_18447);
		livingEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
		this.field_18392 = null;
	}

	@Override
	public Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18447, MemoryModuleState.field_18456));
	}

	private void method_19027(PlayerEntity playerEntity, VillagerEntity villagerEntity) {
		boolean bl = false;
		ItemStack itemStack = playerEntity.getMainHandStack();
		if (this.field_18392 == null || this.method_19028(itemStack)) {
			this.field_18392 = itemStack;
			bl = true;
			this.field_18393.clear();
		}

		if (bl && !this.field_18392.isEmpty()) {
			for (TraderRecipe traderRecipe : villagerEntity.getRecipes()) {
				if (!traderRecipe.isDisabled()
					&& (
						ItemStack.areEqualIgnoreTags(this.field_18392, traderRecipe.getSecondBuyItem())
							|| ItemStack.areEqualIgnoreTags(this.field_18392, traderRecipe.method_19272())
					)) {
					this.field_18393.add(traderRecipe.getModifiableSellItem());
				}
			}

			if (!this.field_18393.isEmpty()) {
				this.field_18396 = 900;
				villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, (ItemStack)this.field_18393.get(0));
			}
		}
	}

	private boolean method_19028(ItemStack itemStack) {
		return !ItemStack.areEqualIgnoreTags(this.field_18392, itemStack);
	}

	private void method_19026(VillagerEntity villagerEntity) {
		if (this.field_18393.size() >= 2 && ++this.field_18394 >= 40) {
			this.field_18395++;
			this.field_18394 = 0;
			if (this.field_18395 > this.field_18393.size() - 1) {
				this.field_18395 = 0;
			}

			villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, (ItemStack)this.field_18393.get(this.field_18395));
		}
	}
}
