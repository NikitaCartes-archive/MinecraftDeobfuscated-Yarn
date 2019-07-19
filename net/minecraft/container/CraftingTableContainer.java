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

    public CraftingTableContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, BlockContext.EMPTY);
    }

    public CraftingTableContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
        super(ContainerType.CRAFTING, i);
        int k;
        int j;
        this.context = blockContext;
        this.player = playerInventory.player;
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInv, this.resultInv, 0, 124, 35));
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 3; ++k) {
                this.addSlot(new Slot(this.craftingInv, k + j * 3, 30 + k * 18, 17 + j * 18));
            }
        }
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
        }
    }

    protected static void updateResult(int i, World world, PlayerEntity playerEntity, CraftingInventory craftingInventory, CraftingResultInventory craftingResultInventory) {
        CraftingRecipe craftingRecipe;
        if (world.isClient) {
            return;
        }
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
        ItemStack itemStack = ItemStack.EMPTY;
        Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        if (optional.isPresent() && craftingResultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe = optional.get())) {
            itemStack = craftingRecipe.craft(craftingInventory);
        }
        craftingResultInventory.setInvStack(0, itemStack);
        serverPlayerEntity.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(i, 0, itemStack));
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
    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        this.context.run((world, blockPos) -> this.dropInventory(playerEntity, (World)world, this.craftingInv));
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return CraftingTableContainer.canUse(this.context, playerEntity, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (i == 0) {
                this.context.run((world, blockPos) -> itemStack2.getItem().onCraft(itemStack2, (World)world, playerEntity));
                if (!this.insertItem(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (i >= 10 && i < 37 ? !this.insertItem(itemStack2, 37, 46, false) : (i >= 37 && i < 46 ? !this.insertItem(itemStack2, 10, 37, false) : !this.insertItem(itemStack2, 10, 46, false))) {
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
            ItemStack itemStack3 = slot.onTakeItem(playerEntity, itemStack2);
            if (i == 0) {
                playerEntity.dropItem(itemStack3, false);
            }
        }
        return itemStack;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
        return slot.inventory != this.resultInv && super.canInsertIntoSlot(itemStack, slot);
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

