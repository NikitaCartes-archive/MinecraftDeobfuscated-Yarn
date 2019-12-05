package net.minecraft.container;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.util.Identifier;

public class PlayerContainer extends CraftingContainer<CraftingInventory> {
	public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");
	public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
	public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
	public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
	public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
	public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = new Identifier("item/empty_armor_slot_shield");
	private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{
		EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE
	};
	private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	private final CraftingInventory invCrafting = new CraftingInventory(this, 2, 2);
	private final CraftingResultInventory invCraftingResult = new CraftingResultInventory();
	public final boolean local;
	private final PlayerEntity owner;

	public PlayerContainer(PlayerInventory inventory, boolean local, PlayerEntity playerEntity) {
		super(null, 0);
		this.local = local;
		this.owner = playerEntity;
		this.addSlot(new CraftingResultSlot(inventory.player, this.invCrafting, this.invCraftingResult, 0, 154, 28));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new Slot(this.invCrafting, j + i * 2, 98 + j * 18, 18 + i * 18));
			}
		}

		for (int i = 0; i < 4; i++) {
			final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
			this.addSlot(new Slot(inventory, 39 - i, 8, 8 + i * 18) {
				@Override
				public int getMaxStackAmount() {
					return 1;
				}

				@Override
				public boolean canInsert(ItemStack stack) {
					return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
				}

				@Override
				public boolean canTakeItems(PlayerEntity playerEntity) {
					ItemStack itemStack = this.getStack();
					return !itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack) ? false : super.canTakeItems(playerEntity);
				}

				@Environment(EnvType.CLIENT)
				@Override
				public Pair<Identifier, Identifier> getBackgroundSprite() {
					return Pair.of(PlayerContainer.BLOCK_ATLAS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
				}
			});
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}

		this.addSlot(new Slot(inventory, 40, 77, 62) {
			@Environment(EnvType.CLIENT)
			@Override
			public Pair<Identifier, Identifier> getBackgroundSprite() {
				return Pair.of(PlayerContainer.BLOCK_ATLAS_TEXTURE, PlayerContainer.EMPTY_OFFHAND_ARMOR_SLOT);
			}
		});
	}

	@Override
	public void populateRecipeFinder(RecipeFinder recipeFinder) {
		this.invCrafting.provideRecipeInputs(recipeFinder);
	}

	@Override
	public void clearCraftingSlots() {
		this.invCraftingResult.clear();
		this.invCrafting.clear();
	}

	@Override
	public boolean matches(Recipe<? super CraftingInventory> recipe) {
		return recipe.matches(this.invCrafting, this.owner.world);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		CraftingTableContainer.updateResult(this.syncId, this.owner.world, this.owner, this.invCrafting, this.invCraftingResult);
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.invCraftingResult.clear();
		if (!player.world.isClient) {
			this.dropInventory(player, player.world, this.invCrafting);
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
			if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 9, 45, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot >= 1 && invSlot < 5) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 5 && invSlot < 9) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slotList.get(8 - equipmentSlot.getEntitySlotId())).hasStack()) {
				int i = 8 - equipmentSlot.getEntitySlotId();
				if (!this.insertItem(itemStack2, i, i + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot)this.slotList.get(45)).hasStack()) {
				if (!this.insertItem(itemStack2, 45, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 9 && invSlot < 36) {
				if (!this.insertItem(itemStack2, 36, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 36 && invSlot < 45) {
				if (!this.insertItem(itemStack2, 9, 36, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 9, 45, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemStack3 = slot.onTakeItem(player, itemStack2);
			if (invSlot == 0) {
				player.dropItem(itemStack3, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.invCraftingResult && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return this.invCrafting.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.invCrafting.getHeight();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getCraftingSlotCount() {
		return 5;
	}
}
