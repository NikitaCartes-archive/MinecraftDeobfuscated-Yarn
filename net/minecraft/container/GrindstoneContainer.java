/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrindstoneContainer
extends Container {
    private final Inventory resultInventory = new CraftingResultInventory();
    private final Inventory craftingInventory = new BasicInventory(2){

        @Override
        public void markDirty() {
            super.markDirty();
            GrindstoneContainer.this.onContentChanged(this);
        }
    };
    private final BlockContext context;

    public GrindstoneContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, BlockContext.EMPTY);
    }

    public GrindstoneContainer(int i, PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.GRINDSTONE, i);
        int j;
        this.context = blockContext;
        this.addSlot(new Slot(this.craftingInventory, 0, 49, 19){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.isDamageable() || itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.hasEnchantments();
            }
        });
        this.addSlot(new Slot(this.craftingInventory, 1, 49, 40){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.isDamageable() || itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.hasEnchantments();
            }
        });
        this.addSlot(new Slot(this.resultInventory, 2, 129, 34){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                blockContext.run((world, blockPos) -> {
                    int j;
                    for (int i = this.getExperience((World)world); i > 0; i -= j) {
                        j = ExperienceOrbEntity.roundToOrbSize(i);
                        world.spawnEntity(new ExperienceOrbEntity((World)world, blockPos.getX(), (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, j));
                    }
                    world.playLevelEvent(1042, (BlockPos)blockPos, 0);
                });
                GrindstoneContainer.this.craftingInventory.setInvStack(0, ItemStack.EMPTY);
                GrindstoneContainer.this.craftingInventory.setInvStack(1, ItemStack.EMPTY);
                return itemStack;
            }

            private int getExperience(World world) {
                int i = 0;
                i += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(0));
                if ((i += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(1))) > 0) {
                    int j = (int)Math.ceil((double)i / 2.0);
                    return j + world.random.nextInt(j);
                }
                return 0;
            }

            private int getExperience(ItemStack itemStack) {
                int i = 0;
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);
                for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    Integer integer = entry.getValue();
                    if (enchantment.isCursed()) continue;
                    i += enchantment.getMinimumPower(integer);
                }
                return i;
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
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.craftingInventory) {
            this.updateResult();
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void updateResult() {
        boolean bl2;
        ItemStack itemStack = this.craftingInventory.getInvStack(0);
        ItemStack itemStack2 = this.craftingInventory.getInvStack(1);
        boolean bl = !itemStack.isEmpty() || !itemStack2.isEmpty();
        boolean bl3 = bl2 = !itemStack.isEmpty() && !itemStack2.isEmpty();
        if (bl) {
            ItemStack itemStack3;
            int m;
            boolean bl32;
            boolean bl4 = bl32 = !itemStack.isEmpty() && itemStack.getItem() != Items.ENCHANTED_BOOK && !itemStack.hasEnchantments() || !itemStack2.isEmpty() && itemStack2.getItem() != Items.ENCHANTED_BOOK && !itemStack2.hasEnchantments();
            if (itemStack.getCount() > 1 || itemStack2.getCount() > 1 || !bl2 && bl32) {
                this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                this.sendContentUpdates();
                return;
            }
            int i = 1;
            if (bl2) {
                if (itemStack.getItem() != itemStack2.getItem()) {
                    this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                Item item = itemStack.getItem();
                int j = item.getMaxDamage() - itemStack.getDamage();
                int k = item.getMaxDamage() - itemStack2.getDamage();
                int l = j + k + item.getMaxDamage() * 5 / 100;
                m = Math.max(item.getMaxDamage() - l, 0);
                itemStack3 = this.transferEnchantments(itemStack, itemStack2);
                if (!itemStack3.isDamageable()) {
                    if (!ItemStack.areEqualIgnoreDamage(itemStack, itemStack2)) {
                        this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                        this.sendContentUpdates();
                        return;
                    }
                    i = 2;
                }
            } else {
                boolean bl42 = !itemStack.isEmpty();
                m = bl42 ? itemStack.getDamage() : itemStack2.getDamage();
                itemStack3 = bl42 ? itemStack : itemStack2;
            }
            this.resultInventory.setInvStack(0, this.grind(itemStack3, m, i));
        } else {
            this.resultInventory.setInvStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    private ItemStack transferEnchantments(ItemStack itemStack, ItemStack itemStack2) {
        ItemStack itemStack3 = itemStack.copy();
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack2);
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment.isCursed() && EnchantmentHelper.getLevel(enchantment, itemStack3) != 0) continue;
            itemStack3.addEnchantment(enchantment, entry.getValue());
        }
        return itemStack3;
    }

    private ItemStack grind(ItemStack itemStack, int i, int j) {
        ItemStack itemStack2 = itemStack.copy();
        itemStack2.removeSubTag("Enchantments");
        itemStack2.removeSubTag("StoredEnchantments");
        if (i > 0) {
            itemStack2.setDamage(i);
        } else {
            itemStack2.removeSubTag("Damage");
        }
        itemStack2.setCount(j);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack).entrySet().stream().filter(entry -> ((Enchantment)entry.getKey()).isCursed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.set(map, itemStack2);
        itemStack2.setRepairCost(0);
        if (itemStack2.getItem() == Items.ENCHANTED_BOOK && map.size() == 0) {
            itemStack2 = new ItemStack(Items.BOOK);
            if (itemStack.hasCustomName()) {
                itemStack2.setCustomName(itemStack.getName());
            }
        }
        for (int k = 0; k < map.size(); ++k) {
            itemStack2.setRepairCost(AnvilContainer.getNextCost(itemStack2.getRepairCost()));
        }
        return itemStack2;
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        this.context.run((world, blockPos) -> this.dropInventory(playerEntity, (World)world, this.craftingInventory));
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return GrindstoneContainer.canUse(this.context, playerEntity, Blocks.GRINDSTONE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slotList.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            ItemStack itemStack3 = this.craftingInventory.getInvStack(0);
            ItemStack itemStack4 = this.craftingInventory.getInvStack(1);
            if (i == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (i == 0 || i == 1 ? !this.insertItem(itemStack2, 3, 39, false) : (itemStack3.isEmpty() || itemStack4.isEmpty() ? !this.insertItem(itemStack2, 0, 2, false) : (i >= 3 && i < 30 ? !this.insertItem(itemStack2, 30, 39, false) : i >= 30 && i < 39 && !this.insertItem(itemStack2, 3, 30, false)))) {
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
}

