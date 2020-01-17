/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.container.BlockContext;
import net.minecraft.container.ContainerType;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class CraftingTableContainer
extends CraftingContainer<CraftingInventory> {
    private final CraftingInventory craftingInv = new CraftingInventory(this, 3, 3);
    private final CraftingResultInventory resultInv = new CraftingResultInventory();
    private final BlockContext context;
    private final PlayerEntity player;

    public CraftingTableContainer(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }

    public CraftingTableContainer(int syncId, PlayerInventory playerInventory, BlockContext blockContext) {
        super(ContainerType.CRAFTING, syncId);
        int j;
        int i;
        this.context = blockContext;
        this.player = playerInventory.player;
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInv, this.resultInv, 0, 124, 35));
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftingInv, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    protected static void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
        CraftingRecipe craftingRecipe;
        if (world.isClient) {
            return;
        }
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
        ItemStack itemStack = ItemStack.EMPTY;
        Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        if (optional.isPresent() && resultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe = optional.get())) {
            itemStack = craftingRecipe.craft(craftingInventory);
        }
        resultInventory.setInvStack(0, itemStack);
        serverPlayerEntity.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(syncId, 0, itemStack));
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.context.run((world, blockPos) -> CraftingTableContainer.updateResult(this.syncId, world, this.player, this.craftingInv, this.resultInv));
    }

    @Override
    public void populateRecipeFinder(RecipeFinder recipeFinder) {
        this.craftingInv.provideRecipeInputs(recipeFinder);
    }

    @Override
    public void clearCraftingSlots() {
        this.craftingInv.clear();
        this.resultInv.clear();
    }

    @Override
    public boolean matches(Recipe<? super CraftingInventory> recipe) {
        return recipe.matches(this.craftingInv, this.player.world);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, (World)world, this.craftingInv));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return CraftingTableContainer.canUse(this.context, player, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (invSlot == 0) {
                this.context.run((world, blockPos) -> itemStack2.getItem().onCraft(itemStack2, (World)world, player));
                if (!this.insertItem(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (invSlot >= 10 && invSlot < 46 ? !this.insertItem(itemStack2, 1, 10, false) && (invSlot < 37 ? !this.insertItem(itemStack2, 37, 46, false) : !this.insertItem(itemStack2, 10, 37, false)) : !this.insertItem(itemStack2, 10, 46, false)) {
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
        return slot.inventory != this.resultInv && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return this.craftingInv.getWidth();
    }

    @Override
    public int getCraftingHeight() {
        return this.craftingInv.getHeight();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 10;
    }
}

