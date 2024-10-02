package net.minecraft.screen;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class PlayerScreenHandler extends AbstractCraftingScreenHandler {
	public static final int field_30802 = 0;
	public static final int CRAFTING_RESULT_ID = 0;
	private static final int field_52570 = 2;
	private static final int field_52571 = 2;
	public static final int CRAFTING_INPUT_START = 1;
	public static final int CRAFTING_INPUT_COUNT = 4;
	public static final int CRAFTING_INPUT_END = 5;
	public static final int EQUIPMENT_START = 5;
	public static final int EQUIPMENT_COUNT = 4;
	public static final int EQUIPMENT_END = 9;
	public static final int INVENTORY_START = 9;
	public static final int INVENTORY_END = 36;
	public static final int HOTBAR_START = 36;
	public static final int HOTBAR_END = 45;
	public static final int OFFHAND_ID = 45;
	public static final Identifier BLOCK_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/blocks.png");
	public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
	public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
	public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
	public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");
	public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = Identifier.ofVanilla("item/empty_armor_slot_shield");
	private static final Map<EquipmentSlot, Identifier> EMPTY_ARMOR_SLOT_TEXTURES = Map.of(
		EquipmentSlot.FEET,
		EMPTY_BOOTS_SLOT_TEXTURE,
		EquipmentSlot.LEGS,
		EMPTY_LEGGINGS_SLOT_TEXTURE,
		EquipmentSlot.CHEST,
		EMPTY_CHESTPLATE_SLOT_TEXTURE,
		EquipmentSlot.HEAD,
		EMPTY_HELMET_SLOT_TEXTURE
	);
	private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	public final boolean onServer;
	private final PlayerEntity owner;

	public PlayerScreenHandler(PlayerInventory inventory, boolean onServer, PlayerEntity owner) {
		super(null, 0, 2, 2);
		this.onServer = onServer;
		this.owner = owner;
		this.addResultSlot(owner, 154, 28);
		this.addInputSlots(98, 18);

		for (int i = 0; i < 4; i++) {
			EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
			Identifier identifier = (Identifier)EMPTY_ARMOR_SLOT_TEXTURES.get(equipmentSlot);
			this.addSlot(new ArmorSlot(inventory, owner, equipmentSlot, 39 - i, 8, 8 + i * 18, identifier));
		}

		this.addPlayerSlots(inventory, 8, 84);
		this.addSlot(new Slot(inventory, 40, 77, 62) {
			@Override
			public void setStack(ItemStack stack, ItemStack previousStack) {
				owner.onEquipStack(EquipmentSlot.OFFHAND, previousStack, stack);
				super.setStack(stack, previousStack);
			}

			@Override
			public Pair<Identifier, Identifier> getBackgroundSprite() {
				return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
			}
		});
	}

	public static boolean isInHotbar(int slot) {
		return slot >= 36 && slot < 45 || slot == 45;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (this.owner.getWorld() instanceof ServerWorld serverWorld) {
			CraftingScreenHandler.updateResult(this, serverWorld, this.owner, this.craftingInventory, this.craftingResultInventory, null);
		}
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.craftingResultInventory.clear();
		if (!player.getWorld().isClient) {
			this.dropInventory(player, this.craftingInventory);
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
			EquipmentSlot equipmentSlot = player.getPreferredEquipmentSlot(itemStack);
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
			} else if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !this.slots.get(8 - equipmentSlot.getEntitySlotId()).hasStack()) {
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
				slot2.setStack(ItemStack.EMPTY, itemStack);
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
		return slot.inventory != this.craftingResultInventory && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public Slot getOutputSlot() {
		return this.slots.get(0);
	}

	@Override
	public List<Slot> getInputSlots() {
		return this.slots.subList(1, 5);
	}

	public RecipeInputInventory getCraftingInput() {
		return this.craftingInventory;
	}

	@Override
	public RecipeBookType getCategory() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	protected PlayerEntity getPlayer() {
		return this.owner;
	}
}
