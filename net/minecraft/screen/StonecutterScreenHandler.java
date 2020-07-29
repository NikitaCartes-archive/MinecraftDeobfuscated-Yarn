/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterScreenHandler
extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final Property selectedRecipe = Property.create();
    private final World world;
    private List<StonecuttingRecipe> availableRecipes = Lists.newArrayList();
    private ItemStack inputStack = ItemStack.EMPTY;
    private long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    private Runnable contentsChangedListener = () -> {};
    public final Inventory input = new SimpleInventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            StonecutterScreenHandler.this.onContentChanged(this);
            StonecutterScreenHandler.this.contentsChangedListener.run();
        }
    };
    private final CraftingResultInventory output = new CraftingResultInventory();

    public StonecutterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public StonecutterScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(ScreenHandlerType.STONECUTTER, syncId);
        int i;
        this.context = context;
        this.world = playerInventory.player.world;
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33){

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
                ItemStack itemStack = StonecutterScreenHandler.this.inputSlot.takeStack(1);
                if (!itemStack.isEmpty()) {
                    StonecutterScreenHandler.this.populateResult();
                }
                stack.onCraft(player.world, player, stack.getCount());
                StonecutterScreenHandler.this.output.unlockLastRecipe(player);
                context.run((world, blockPos) -> {
                    long l = world.getTime();
                    if (StonecutterScreenHandler.this.lastTakeTime != l) {
                        world.playSound(null, (BlockPos)blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        StonecutterScreenHandler.this.lastTakeTime = l;
                    }
                });
                return super.onTakeItem(player, stack);
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addProperty(this.selectedRecipe);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    @Environment(value=EnvType.CLIENT)
    public List<StonecuttingRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    @Environment(value=EnvType.CLIENT)
    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean canCraft() {
        return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return StonecutterScreenHandler.canUse(this.context, player, Blocks.STONECUTTER);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (this.method_30160(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }

    private boolean method_30160(int i) {
        return i >= 0 && i < this.availableRecipes.size();
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inputSlot.getStack();
        if (itemStack.getItem() != this.inputStack.getItem()) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }
    }

    private void updateInput(Inventory input, ItemStack stack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.setStack(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.availableRecipes = this.world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING, input, this.world);
        }
    }

    private void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.method_30160(this.selectedRecipe.get())) {
            StonecuttingRecipe stonecuttingRecipe = this.availableRecipes.get(this.selectedRecipe.get());
            this.output.setLastRecipe(stonecuttingRecipe);
            this.outputSlot.setStack(stonecuttingRecipe.craft(this.input));
        } else {
            this.outputSlot.setStack(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return ScreenHandlerType.STONECUTTER;
    }

    @Environment(value=EnvType.CLIENT)
    public void setContentsChangedListener(Runnable runnable) {
        this.contentsChangedListener = runnable;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (index == 1) {
                item.onCraft(itemStack2, player.world, player);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (index == 0 ? !this.insertItem(itemStack2, 2, 38, false) : (this.world.getRecipeManager().getFirstMatch(RecipeType.STONECUTTING, new SimpleInventory(itemStack2), this.world).isPresent() ? !this.insertItem(itemStack2, 0, 1, false) : (index >= 2 && index < 29 ? !this.insertItem(itemStack2, 29, 38, false) : index >= 29 && index < 38 && !this.insertItem(itemStack2, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            slot.markDirty();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.output.removeStack(1);
        this.context.run((world, blockPos) -> this.dropInventory(player, playerEntity.world, this.input));
    }
}

