/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
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
import org.jetbrains.annotations.Nullable;

public class PlayerContainer
extends CraftingContainer<CraftingInventory> {
    private static final String[] EMPTY_ARMOR_SLOT_IDS = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final CraftingInventory invCrafting = new CraftingInventory(this, 2, 2);
    private final CraftingResultInventory invCraftingResult = new CraftingResultInventory();
    public final boolean local;
    private final PlayerEntity owner;

    public PlayerContainer(PlayerInventory playerInventory, boolean bl, PlayerEntity playerEntity) {
        super(null, 0);
        int i;
        this.local = bl;
        this.owner = playerEntity;
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.invCrafting, this.invCraftingResult, 0, 154, 28));
        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.invCrafting, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }
        for (i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            this.addSlot(new Slot(playerInventory, 39 - i, 8, 8 + i * 18){

                @Override
                public int getMaxStackAmount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack itemStack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(itemStack);
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }

                @Override
                @Nullable
                @Environment(value=EnvType.CLIENT)
                public String getBackgroundSprite() {
                    return EMPTY_ARMOR_SLOT_IDS[equipmentSlot.getEntitySlotId()];
                }
            });
        }
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addSlot(new Slot(playerInventory, 40, 77, 62){

            @Override
            @Nullable
            @Environment(value=EnvType.CLIENT)
            public String getBackgroundSprite() {
                return "item/empty_armor_slot_shield";
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
    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        this.invCraftingResult.clear();
        if (playerEntity.world.isClient) {
            return;
        }
        this.dropInventory(playerEntity, playerEntity.world, this.invCrafting);
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slotList.get(i);
        if (slot != null && slot.hasStack()) {
            int j;
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
            if (i == 0) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (i >= 1 && i < 5 ? !this.insertItem(itemStack2, 9, 45, false) : (i >= 5 && i < 9 ? !this.insertItem(itemStack2, 9, 45, false) : (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slotList.get(8 - equipmentSlot.getEntitySlotId())).hasStack() ? !this.insertItem(itemStack2, j = 8 - equipmentSlot.getEntitySlotId(), j + 1, false) : (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot)this.slotList.get(45)).hasStack() ? !this.insertItem(itemStack2, 45, 46, false) : (i >= 9 && i < 36 ? !this.insertItem(itemStack2, 36, 45, false) : (i >= 36 && i < 45 ? !this.insertItem(itemStack2, 9, 36, false) : !this.insertItem(itemStack2, 9, 45, false))))))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getAmount() == itemStack.getAmount()) {
                return ItemStack.EMPTY;
            }
            ItemStack itemStack3 = slot.onTakeItem(playerEntity, itemStack2);
            if (i == 0) {
                playerEntity.dropItem(itemStack3, false);
            }
        }
        return itemStack;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
        return slot.inventory != this.invCraftingResult && super.canInsertIntoSlot(itemStack, slot);
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

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 5;
    }
}

