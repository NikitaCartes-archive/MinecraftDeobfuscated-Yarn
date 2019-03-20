package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
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
import net.minecraft.village.TraderRecipe;

public class InteractTask extends Task<VillagerEntity> {
	@Nullable
	private ItemStack field_18392;
	private final List<ItemStack> field_18393 = Lists.<ItemStack>newArrayList();
	private int field_18394;
	private int field_18395;
	private int field_18396;

	public InteractTask(int i, int j) {
		super(i, j);
	}

	public boolean method_19599(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		if (!brain.getMemory(MemoryModuleType.field_18447).isPresent()) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)brain.getMemory(MemoryModuleType.field_18447).get();
			return livingEntity.getType() == EntityType.PLAYER
				&& villagerEntity.isValid()
				&& livingEntity.isValid()
				&& !villagerEntity.isChild()
				&& villagerEntity.squaredDistanceTo(livingEntity) <= 17.0;
		}
	}

	public boolean method_19600(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.method_19599(serverWorld, villagerEntity)
			&& this.field_18396 > 0
			&& villagerEntity.getBrain().getMemory(MemoryModuleType.field_18447).isPresent();
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
		if (!this.field_18393.isEmpty()) {
			this.method_19026(villagerEntity);
		} else {
			villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
			this.field_18396 = Math.min(this.field_18396, 40);
		}

		this.field_18396--;
	}

	public void method_19605(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.stopInternal(serverWorld, villagerEntity, l);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
		villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
		this.field_18392 = null;
	}

	@Override
	public Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18447, MemoryModuleState.field_18456));
	}

	private void method_19027(LivingEntity livingEntity, VillagerEntity villagerEntity) {
		boolean bl = false;
		ItemStack itemStack = livingEntity.getMainHandStack();
		if (this.field_18392 == null || !ItemStack.areEqualIgnoreTags(this.field_18392, itemStack)) {
			this.field_18392 = itemStack;
			bl = true;
			this.field_18393.clear();
		}

		if (bl && !this.field_18392.isEmpty()) {
			this.method_19601(villagerEntity);
			if (!this.field_18393.isEmpty()) {
				this.field_18396 = 900;
				this.method_19598(villagerEntity);
			}
		}
	}

	private void method_19598(VillagerEntity villagerEntity) {
		villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, (ItemStack)this.field_18393.get(0));
	}

	private void method_19601(VillagerEntity villagerEntity) {
		for (TraderRecipe traderRecipe : villagerEntity.getRecipes()) {
			if (!traderRecipe.isDisabled() && this.method_19028(traderRecipe)) {
				this.field_18393.add(traderRecipe.getModifiableSellItem());
			}
		}
	}

	private boolean method_19028(TraderRecipe traderRecipe) {
		return ItemStack.areEqualIgnoreTags(this.field_18392, traderRecipe.getDiscountedFirstBuyItem())
			|| ItemStack.areEqualIgnoreTags(this.field_18392, traderRecipe.getSecondBuyItem());
	}

	private LivingEntity method_19603(VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		LivingEntity livingEntity = (LivingEntity)brain.getMemory(MemoryModuleType.field_18447).get();
		brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntity));
		return livingEntity;
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
