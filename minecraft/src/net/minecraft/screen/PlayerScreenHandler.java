package net.minecraft.screen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class PlayerScreenHandler extends AbstractRecipeScreenHandler<RecipeInputInventory> {
	public static final int field_30802 = 0;
	public static final int CRAFTING_RESULT_ID = 0;
	public static final int CRAFTING_INPUT_START = 1;
	public static final int CRAFTING_INPUT_END = 5;
	public static final int EQUIPMENT_START = 5;
	public static final int EQUIPMENT_END = 9;
	public static final int INVENTORY_START = 9;
	public static final int INVENTORY_END = 36;
	public static final int HOTBAR_START = 36;
	public static final int HOTBAR_END = 45;
	public static final int OFFHAND_ID = 45;
	public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");
	public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
	public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
	public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
	public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
	public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = new Identifier("item/empty_armor_slot_shield");
	static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{
		EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE
	};
	private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	private final RecipeInputInventory craftingInput = new CraftingInventory(this, 2, 2);
	private final CraftingResultInventory craftingResult = new CraftingResultInventory();
	public final boolean onServer;
	private final PlayerEntity owner;

	public PlayerScreenHandler(PlayerInventory inventory, boolean onServer, PlayerEntity owner) {
		super(null, 0);
		this.onServer = onServer;
		this.owner = owner;
		this.addSlot(new CraftingResultSlot(inventory.player, this.craftingInput, this.craftingResult, 0, 154, 28));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new Slot(this.craftingInput, j + i * 2, 98 + j * 18, 18 + i * 18));
			}
		}

		for (int i = 0; i < 4; i++) {
			final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
			this.addSlot(new Slot(inventory, 39 - i, 8, 8 + i * 18) {
				@Override
				public void setStack(ItemStack stack) {
					PlayerScreenHandler.onEquipStack(owner, equipmentSlot, stack, this.getStack());
					super.setStack(stack);
				}

				@Override
				public int getMaxItemCount() {
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

				@Override
				public Pair<Identifier, Identifier> getBackgroundSprite() {
					return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
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
			@Override
			public void setStack(ItemStack stack) {
				PlayerScreenHandler.onEquipStack(owner, EquipmentSlot.OFFHAND, stack, this.getStack());
				super.setStack(stack);
			}

			@Override
			public Pair<Identifier, Identifier> getBackgroundSprite() {
				return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
			}
		});
	}

	static void onEquipStack(PlayerEntity player, EquipmentSlot slot, ItemStack newStack, ItemStack currentStack) {
		Equipment equipment = Equipment.fromStack(newStack);
		if (equipment != null) {
			player.onEquipStack(slot, currentStack, newStack);
		}
	}

	public static boolean isInHotbar(int slot) {
		return slot >= 36 && slot < 45 || slot == 45;
	}

	@Override
	public void populateRecipeFinder(RecipeMatcher finder) {
		this.craftingInput.provideRecipeInputs(finder);
	}

	@Override
	public void clearCraftingSlots() {
		this.craftingResult.clear();
		this.craftingInput.clear();
	}

	@Override
	public boolean matches(Recipe<? super RecipeInputInventory> recipe) {
		return recipe.matches(this.craftingInput, this.owner.getWorld());
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		CraftingScreenHandler.updateResult(this, this.owner.getWorld(), this.owner, this.craftingInput, this.craftingResult);
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.craftingResult.clear();
		if (!player.getWorld().isClient) {
			this.dropInventory(player, this.craftingInput);
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
			if (slot == 0) {
				if (!this.insertItem(itemStack2, 9, 45, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot >= 1 && slot < 5) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slot >= 5 && slot < 9) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(8 - equipmentSlot.getEntitySlotId()).hasStack()) {
				int i = 8 - equipmentSlot.getEntitySlotId();
				if (!this.insertItem(itemStack2, i, i + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasStack()) {
				if (!this.insertItem(itemStack2, 45, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slot >= 9 && slot < 36) {
				if (!this.insertItem(itemStack2, 36, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slot >= 36 && slot < 45) {
				if (!this.insertItem(itemStack2, 9, 36, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 9, 45, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
			if (slot == 0) {
				player.dropItem(itemStack2, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.craftingResult && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return this.craftingInput.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.craftingInput.getHeight();
	}

	@Override
	public int getCraftingSlotCount() {
		return 5;
	}

	public RecipeInputInventory getCraftingInput() {
		return this.craftingInput;
	}

	@Override
	public RecipeBookCategory getCategory() {
		return RecipeBookCategory.CRAFTING;
	}

	@Override
	public boolean canInsertIntoSlot(int index) {
		return index != this.getCraftingResultSlotIndex();
	}
}
