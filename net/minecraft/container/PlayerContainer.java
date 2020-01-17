/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.util.Identifier;

public class PlayerContainer
extends CraftingContainer<CraftingInventory> {
    public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");
    public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
    public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = new Identifier("item/empty_armor_slot_shield");
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final CraftingInventory craftingInventory = new CraftingInventory(this, 2, 2);
    private final CraftingResultInventory craftingResultInventory = new CraftingResultInventory();
    public final boolean onServer;
    private final PlayerEntity owner;

    public PlayerContainer(PlayerInventory inventory, boolean onServer, PlayerEntity owner) {
        super(null, 0);
        int i;
        this.onServer = onServer;
        this.owner = owner;
        this.addSlot(new CraftingResultSlot(inventory.player, this.craftingInventory, this.craftingResultInventory, 0, 154, 28));
        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftingInventory, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }
        for (i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            this.addSlot(new Slot(inventory, 39 - i, 8, 8 + i * 18){

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
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                }
            });
        }
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
        this.addSlot(new Slot(inventory, 40, 77, 62){

            @Override
            @Environment(value=EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }

    @Override
    public void populateRecipeFinder(RecipeFinder recipeFinder) {
        this.craftingInventory.provideRecipeInputs(recipeFinder);
    }

    @Override
    public void clearCraftingSlots() {
        this.craftingResultInventory.clear();
        this.craftingInventory.clear();
    }

    @Override
    public boolean matches(Recipe<? super CraftingInventory> recipe) {
        return recipe.matches(this.craftingInventory, this.owner.world);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        CraftingTableContainer.updateResult(this.syncId, this.owner.world, this.owner, this.craftingInventory, this.craftingResultInventory);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.craftingResultInventory.clear();
        if (player.world.isClient) {
            return;
        }
        this.dropInventory(player, player.world, this.craftingInventory);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            int i;
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
            if (invSlot == 0) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (invSlot >= 1 && invSlot < 5 ? !this.insertItem(itemStack2, 9, 45, false) : (invSlot >= 5 && invSlot < 9 ? !this.insertItem(itemStack2, 9, 45, false) : (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slots.get(8 - equipmentSlot.getEntitySlotId())).hasStack() ? !this.insertItem(itemStack2, i = 8 - equipmentSlot.getEntitySlotId(), i + 1, false) : (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(45)).hasStack() ? !this.insertItem(itemStack2, 45, 46, false) : (invSlot >= 9 && invSlot < 36 ? !this.insertItem(itemStack2, 36, 45, false) : (invSlot >= 36 && invSlot < 45 ? !this.insertItem(itemStack2, 9, 36, false) : !this.insertItem(itemStack2, 9, 45, false))))))) {
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
        return slot.inventory != this.craftingResultInventory && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return this.craftingInventory.getWidth();
    }

    @Override
    public int getCraftingHeight() {
        return this.craftingInventory.getHeight();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 5;
    }
}

