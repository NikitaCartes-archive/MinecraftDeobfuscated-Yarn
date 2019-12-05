/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class LoomContainer
extends Container {
    private final BlockContext context;
    private final Property selectedPattern = Property.create();
    private Runnable inventoryChangeListener = () -> {};
    private final Slot bannerSlot;
    private final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot outputSlot;
    private long lastTakeResultTime;
    private final Inventory inputInventory = new BasicInventory(3){

        @Override
        public void markDirty() {
            super.markDirty();
            LoomContainer.this.onContentChanged(this);
            LoomContainer.this.inventoryChangeListener.run();
        }
    };
    private final Inventory outputInventory = new BasicInventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            LoomContainer.this.inventoryChangeListener.run();
        }
    };

    public LoomContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, BlockContext.EMPTY);
    }

    public LoomContainer(int i, PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.LOOM, i);
        int j;
        this.context = blockContext;
        this.bannerSlot = this.addSlot(new Slot(this.inputInventory, 0, 13, 26){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.inputInventory, 1, 33, 26){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.inputInventory, 2, 23, 45){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.getItem() instanceof BannerPatternItem;
            }
        });
        this.outputSlot = this.addSlot(new Slot(this.outputInventory, 0, 143, 58){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                LoomContainer.this.bannerSlot.takeStack(1);
                LoomContainer.this.dyeSlot.takeStack(1);
                if (!LoomContainer.this.bannerSlot.hasStack() || !LoomContainer.this.dyeSlot.hasStack()) {
                    LoomContainer.this.selectedPattern.set(0);
                }
                blockContext.run((world, blockPos) -> {
                    long l = world.getTime();
                    if (LoomContainer.this.lastTakeResultTime != l) {
                        world.playSound(null, (BlockPos)blockPos, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        LoomContainer.this.lastTakeResultTime = l;
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
        this.addProperty(this.selectedPattern);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSelectedPattern() {
        return this.selectedPattern.get();
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return LoomContainer.canUse(this.context, playerEntity, Blocks.LOOM);
    }

    @Override
    public boolean onButtonClick(PlayerEntity playerEntity, int i) {
        if (i > 0 && i <= BannerPattern.LOOM_APPLICABLE_COUNT) {
            this.selectedPattern.set(i);
            this.updateOutputSlot();
            return true;
        }
        return false;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.bannerSlot.getStack();
        ItemStack itemStack2 = this.dyeSlot.getStack();
        ItemStack itemStack3 = this.patternSlot.getStack();
        ItemStack itemStack4 = this.outputSlot.getStack();
        if (!itemStack4.isEmpty() && (itemStack.isEmpty() || itemStack2.isEmpty() || this.selectedPattern.get() <= 0 || this.selectedPattern.get() >= BannerPattern.COUNT - 5 && itemStack3.isEmpty())) {
            this.outputSlot.setStack(ItemStack.EMPTY);
            this.selectedPattern.set(0);
        } else if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof BannerPatternItem) {
            boolean bl;
            CompoundTag compoundTag = itemStack.getOrCreateSubTag("BlockEntityTag");
            boolean bl2 = bl = compoundTag.contains("Patterns", 9) && !itemStack.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
            if (bl) {
                this.selectedPattern.set(0);
            } else {
                this.selectedPattern.set(((BannerPatternItem)itemStack3.getItem()).getPattern().ordinal());
            }
        }
        this.updateOutputSlot();
        this.sendContentUpdates();
    }

    @Environment(value=EnvType.CLIENT)
    public void setInventoryChangeListener(Runnable runnable) {
        this.inventoryChangeListener = runnable;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slotList.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (i == this.outputSlot.id) {
                if (!this.insertItem(itemStack2, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (i == this.dyeSlot.id || i == this.bannerSlot.id || i == this.patternSlot.id ? !this.insertItem(itemStack2, 4, 40, false) : (itemStack2.getItem() instanceof BannerItem ? !this.insertItem(itemStack2, this.bannerSlot.id, this.bannerSlot.id + 1, false) : (itemStack2.getItem() instanceof DyeItem ? !this.insertItem(itemStack2, this.dyeSlot.id, this.dyeSlot.id + 1, false) : (itemStack2.getItem() instanceof BannerPatternItem ? !this.insertItem(itemStack2, this.patternSlot.id, this.patternSlot.id + 1, false) : (i >= 4 && i < 31 ? !this.insertItem(itemStack2, 31, 40, false) : i >= 31 && i < 40 && !this.insertItem(itemStack2, 4, 31, false)))))) {
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
            slot.onTakeItem(playerEntity, itemStack2);
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        this.context.run((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inputInventory));
    }

    private void updateOutputSlot() {
        if (this.selectedPattern.get() > 0) {
            ItemStack itemStack = this.bannerSlot.getStack();
            ItemStack itemStack2 = this.dyeSlot.getStack();
            ItemStack itemStack3 = ItemStack.EMPTY;
            if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
                ListTag listTag;
                itemStack3 = itemStack.copy();
                itemStack3.setCount(1);
                BannerPattern bannerPattern = BannerPattern.values()[this.selectedPattern.get()];
                DyeColor dyeColor = ((DyeItem)itemStack2.getItem()).getColor();
                CompoundTag compoundTag = itemStack3.getOrCreateSubTag("BlockEntityTag");
                if (compoundTag.contains("Patterns", 9)) {
                    listTag = compoundTag.getList("Patterns", 10);
                } else {
                    listTag = new ListTag();
                    compoundTag.put("Patterns", listTag);
                }
                CompoundTag compoundTag2 = new CompoundTag();
                compoundTag2.putString("Pattern", bannerPattern.getId());
                compoundTag2.putInt("Color", dyeColor.getId());
                listTag.add(compoundTag2);
            }
            if (!ItemStack.areEqualIgnoreDamage(itemStack3, this.outputSlot.getStack())) {
                this.outputSlot.setStack(itemStack3);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getBannerSlot() {
        return this.bannerSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getDyeSlot() {
        return this.dyeSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getPatternSlot() {
        return this.patternSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getOutputSlot() {
        return this.outputSlot;
    }
}

