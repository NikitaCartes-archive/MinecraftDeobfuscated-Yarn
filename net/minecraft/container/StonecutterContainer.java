/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterContainer
extends Container {
    static final ImmutableList<Item> INGREDIENTS = ImmutableList.of(Items.STONE, Items.SANDSTONE, Items.RED_SANDSTONE, Items.QUARTZ_BLOCK, Items.COBBLESTONE, Items.STONE_BRICKS, Items.BRICKS, Items.NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.PURPUR_BLOCK, Items.PRISMARINE, Items.PRISMARINE_BRICKS, new Item[]{Items.DARK_PRISMARINE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.GRANITE, Items.POLISHED_GRANITE, Items.DIORITE, Items.POLISHED_DIORITE, Items.MOSSY_STONE_BRICKS, Items.MOSSY_COBBLESTONE, Items.SMOOTH_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.SMOOTH_QUARTZ, Items.END_STONE, Items.END_STONE_BRICKS, Items.SMOOTH_STONE, Items.CUT_SANDSTONE, Items.CUT_RED_SANDSTONE});
    private final BlockContext context;
    private final Property selectedRecipe = Property.create();
    private final World world;
    private List<StonecuttingRecipe> availableRecipes = Lists.newArrayList();
    private ItemStack inputStack = ItemStack.EMPTY;
    private long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    private Runnable contentsChangedListener = () -> {};
    public final Inventory inventory = new BasicInventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            StonecutterContainer.this.onContentChanged(this);
            StonecutterContainer.this.contentsChangedListener.run();
        }
    };
    private final CraftingResultInventory field_19173 = new CraftingResultInventory();

    public StonecutterContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, BlockContext.EMPTY);
    }

    public StonecutterContainer(int i, PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.STONECUTTER, i);
        int j;
        this.context = blockContext;
        this.world = playerInventory.player.world;
        this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.field_19173, 1, 143, 33){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                ItemStack itemStack2 = StonecutterContainer.this.inputSlot.takeStack(1);
                if (!itemStack2.isEmpty()) {
                    StonecutterContainer.this.populateResult();
                }
                itemStack.getItem().onCrafted(itemStack, playerEntity.world, playerEntity);
                blockContext.run((world, blockPos) -> {
                    long l = world.getTime();
                    if (StonecutterContainer.this.lastTakeTime != l) {
                        world.playSound(null, (BlockPos)blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        StonecutterContainer.this.lastTakeTime = l;
                    }
                });
                return super.onTakeItem(playerEntity, itemStack);
            }
        });
        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
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
    public boolean canUse(PlayerEntity playerEntity) {
        return StonecutterContainer.canUse(this.context, playerEntity, Blocks.STONECUTTER);
    }

    @Override
    public boolean onButtonClick(PlayerEntity playerEntity, int i) {
        if (i >= 0 && i < this.availableRecipes.size()) {
            this.selectedRecipe.set(i);
            this.populateResult();
        }
        return true;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inputSlot.getStack();
        if (itemStack.getItem() != this.inputStack.getItem()) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }
    }

    private void updateInput(Inventory inventory, ItemStack itemStack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.setStack(ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.availableRecipes = this.world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING, inventory, this.world);
        }
    }

    private void populateResult() {
        if (!this.availableRecipes.isEmpty()) {
            StonecuttingRecipe stonecuttingRecipe = this.availableRecipes.get(this.selectedRecipe.get());
            this.outputSlot.setStack(stonecuttingRecipe.craft(this.inventory));
        } else {
            this.outputSlot.setStack(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    @Override
    public ContainerType<?> getType() {
        return ContainerType.STONECUTTER;
    }

    @Environment(value=EnvType.CLIENT)
    public void setContentsChangedListener(Runnable runnable) {
        this.contentsChangedListener = runnable;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slotList.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (i == 1) {
                item.onCrafted(itemStack2, playerEntity.world, playerEntity);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (i == 0 ? !this.insertItem(itemStack2, 2, 38, false) : (INGREDIENTS.contains(item) ? !this.insertItem(itemStack2, 0, 1, false) : (i >= 2 && i < 29 ? !this.insertItem(itemStack2, 29, 38, false) : i >= 29 && i < 38 && !this.insertItem(itemStack2, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            slot.markDirty();
            if (itemStack2.getAmount() == itemStack.getAmount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(playerEntity, itemStack2);
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        this.field_19173.removeInvStack(1);
        this.context.run((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inventory));
    }
}

