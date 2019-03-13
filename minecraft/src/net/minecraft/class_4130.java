package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TraderRecipe;

public class class_4130 extends class_4097<LivingEntity> {
	private ItemStack field_18392;
	private final ArrayList<ItemStack> field_18393 = new ArrayList();
	private int field_18394;
	private int field_18395;
	private int field_18396;

	public class_4130(int i, int j) {
		super(i, j);
	}

	@Override
	public boolean method_18919(ServerWorld serverWorld, LivingEntity livingEntity) {
		class_4095<?> lv = livingEntity.method_18868();
		if (!lv.method_18904(class_4140.field_18447).isPresent()) {
			return false;
		} else {
			LivingEntity livingEntity2 = (LivingEntity)lv.method_18904(class_4140.field_18447).get();
			return livingEntity.method_5864() == EntityType.VILLAGER
				&& livingEntity2.method_5864() == EntityType.PLAYER
				&& livingEntity.isValid()
				&& livingEntity2.isValid()
				&& !livingEntity.isChild()
				&& livingEntity.squaredDistanceTo(livingEntity2) <= 17.0;
		}
	}

	@Override
	public boolean method_18927(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		return this.method_18919(serverWorld, livingEntity) && this.field_18396 > 0 && livingEntity.method_18868().method_18904(class_4140.field_18447).isPresent();
	}

	@Override
	public void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		super.method_18920(serverWorld, livingEntity, l);
		class_4095<?> lv = livingEntity.method_18868();
		PlayerEntity playerEntity = (PlayerEntity)lv.method_18904(class_4140.field_18447).get();
		if (lv.method_18896(class_4140.field_18446)) {
			lv.method_18878(class_4140.field_18446, new class_4102(playerEntity));
		}

		this.field_18394 = 0;
		this.field_18395 = 0;
		this.field_18396 = 40;
	}

	@Override
	public void method_18924(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		class_4095<?> lv = livingEntity.method_18868();
		PlayerEntity playerEntity = (PlayerEntity)lv.method_18904(class_4140.field_18447).get();
		VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
		if (lv.method_18896(class_4140.field_18446)) {
			lv.method_18878(class_4140.field_18446, new class_4102(playerEntity));
		}

		this.method_19027(playerEntity, villagerEntity);
		if (!this.field_18393.isEmpty()) {
			this.method_19026(villagerEntity);
		} else {
			villagerEntity.method_5673(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
			this.field_18396 = Math.min(this.field_18396, 40);
		}

		this.field_18396--;
	}

	@Override
	public void method_18926(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		super.method_18926(serverWorld, livingEntity, l);
		livingEntity.method_18868().method_18875(class_4140.field_18447);
		livingEntity.method_5673(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
		this.field_18392 = null;
	}

	@Override
	public Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18447, class_4141.field_18456));
	}

	private void method_19027(PlayerEntity playerEntity, VillagerEntity villagerEntity) {
		boolean bl = false;
		ItemStack itemStack = playerEntity.method_6047();
		if (this.field_18392 == null || this.method_19028(itemStack)) {
			this.field_18392 = itemStack;
			bl = true;
			this.field_18393.clear();
		}

		if (bl && !this.field_18392.isEmpty()) {
			for (TraderRecipe traderRecipe : villagerEntity.method_8264()) {
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
				villagerEntity.method_5673(EquipmentSlot.HAND_MAIN, (ItemStack)this.field_18393.get(0));
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

			villagerEntity.method_5673(EquipmentSlot.HAND_MAIN, (ItemStack)this.field_18393.get(this.field_18395));
		}
	}
}
