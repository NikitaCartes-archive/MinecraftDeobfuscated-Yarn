/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class EnchantingTableContainer
extends Container {
    private final Inventory inventory = new BasicInventory(2){

        @Override
        public void markDirty() {
            super.markDirty();
            EnchantingTableContainer.this.onContentChanged(this);
        }
    };
    private final BlockContext context;
    private final Random random = new Random();
    private final Property seed = Property.create();
    public final int[] enchantmentPower = new int[3];
    public final int[] enchantmentId = new int[]{-1, -1, -1};
    public final int[] enchantmentLevel = new int[]{-1, -1, -1};

    public EnchantingTableContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, BlockContext.EMPTY);
    }

    public EnchantingTableContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
        super(ContainerType.ENCHANTMENT, i);
        int j;
        this.context = blockContext;
        this.addSlot(new Slot(this.inventory, 0, 15, 47){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return true;
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 35, 47){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.getItem() == Items.LAPIS_LAZULI;
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
        this.addProperty(Property.create(this.enchantmentPower, 0));
        this.addProperty(Property.create(this.enchantmentPower, 1));
        this.addProperty(Property.create(this.enchantmentPower, 2));
        this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());
        this.addProperty(Property.create(this.enchantmentId, 0));
        this.addProperty(Property.create(this.enchantmentId, 1));
        this.addProperty(Property.create(this.enchantmentId, 2));
        this.addProperty(Property.create(this.enchantmentLevel, 0));
        this.addProperty(Property.create(this.enchantmentLevel, 1));
        this.addProperty(Property.create(this.enchantmentLevel, 2));
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            ItemStack itemStack = inventory.getInvStack(0);
            if (itemStack.isEmpty() || !itemStack.isEnchantable()) {
                for (int i = 0; i < 3; ++i) {
                    this.enchantmentPower[i] = 0;
                    this.enchantmentId[i] = -1;
                    this.enchantmentLevel[i] = -1;
                }
            } else {
                this.context.run((world, blockPos) -> {
                    int j;
                    int i = 0;
                    for (j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if (j == 0 && k == 0 || !world.method_22347(blockPos.add(k, 0, j)) || !world.method_22347(blockPos.add(k, 1, j))) continue;
                            if (world.getBlockState(blockPos.add(k * 2, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                ++i;
                            }
                            if (world.getBlockState(blockPos.add(k * 2, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                ++i;
                            }
                            if (k == 0 || j == 0) continue;
                            if (world.getBlockState(blockPos.add(k * 2, 0, j)).getBlock() == Blocks.BOOKSHELF) {
                                ++i;
                            }
                            if (world.getBlockState(blockPos.add(k * 2, 1, j)).getBlock() == Blocks.BOOKSHELF) {
                                ++i;
                            }
                            if (world.getBlockState(blockPos.add(k, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                ++i;
                            }
                            if (world.getBlockState(blockPos.add(k, 1, j * 2)).getBlock() != Blocks.BOOKSHELF) continue;
                            ++i;
                        }
                    }
                    this.random.setSeed(this.seed.get());
                    for (j = 0; j < 3; ++j) {
                        this.enchantmentPower[j] = EnchantmentHelper.calculateEnchantmentPower(this.random, j, i, itemStack);
                        this.enchantmentId[j] = -1;
                        this.enchantmentLevel[j] = -1;
                        if (this.enchantmentPower[j] >= j + 1) continue;
                        this.enchantmentPower[j] = 0;
                    }
                    for (j = 0; j < 3; ++j) {
                        List<InfoEnchantment> list;
                        if (this.enchantmentPower[j] <= 0 || (list = this.getRandomEnchantments(itemStack, j, this.enchantmentPower[j])) == null || list.isEmpty()) continue;
                        InfoEnchantment infoEnchantment = list.get(this.random.nextInt(list.size()));
                        this.enchantmentId[j] = Registry.ENCHANTMENT.getRawId(infoEnchantment.enchantment);
                        this.enchantmentLevel[j] = infoEnchantment.level;
                    }
                    this.sendContentUpdates();
                });
            }
        }
    }

    @Override
    public boolean onButtonClick(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = this.inventory.getInvStack(0);
        ItemStack itemStack2 = this.inventory.getInvStack(1);
        int j = i + 1;
        if ((itemStack2.isEmpty() || itemStack2.getCount() < j) && !playerEntity.abilities.creativeMode) {
            return false;
        }
        if (this.enchantmentPower[i] > 0 && !itemStack.isEmpty() && (playerEntity.experienceLevel >= j && playerEntity.experienceLevel >= this.enchantmentPower[i] || playerEntity.abilities.creativeMode)) {
            this.context.run((world, blockPos) -> {
                ItemStack itemStack3 = itemStack;
                List<InfoEnchantment> list = this.getRandomEnchantments(itemStack3, i, this.enchantmentPower[i]);
                if (!list.isEmpty()) {
                    boolean bl;
                    playerEntity.applyEnchantmentCosts(itemStack3, j);
                    boolean bl2 = bl = itemStack3.getItem() == Items.BOOK;
                    if (bl) {
                        itemStack3 = new ItemStack(Items.ENCHANTED_BOOK);
                        this.inventory.setInvStack(0, itemStack3);
                    }
                    for (int k = 0; k < list.size(); ++k) {
                        InfoEnchantment infoEnchantment = list.get(k);
                        if (bl) {
                            EnchantedBookItem.addEnchantment(itemStack3, infoEnchantment);
                            continue;
                        }
                        itemStack3.addEnchantment(infoEnchantment.enchantment, infoEnchantment.level);
                    }
                    if (!playerEntity.abilities.creativeMode) {
                        itemStack2.decrement(j);
                        if (itemStack2.isEmpty()) {
                            this.inventory.setInvStack(1, ItemStack.EMPTY);
                        }
                    }
                    playerEntity.incrementStat(Stats.ENCHANT_ITEM);
                    if (playerEntity instanceof ServerPlayerEntity) {
                        Criterions.ENCHANTED_ITEM.handle((ServerPlayerEntity)playerEntity, itemStack3, j);
                    }
                    this.inventory.markDirty();
                    this.seed.set(playerEntity.getEnchantmentTableSeed());
                    this.onContentChanged(this.inventory);
                    world.playSound(null, (BlockPos)blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.1f + 0.9f);
                }
            });
            return true;
        }
        return false;
    }

    private List<InfoEnchantment> getRandomEnchantments(ItemStack itemStack, int i, int j) {
        this.random.setSeed(this.seed.get() + i);
        List<InfoEnchantment> list = EnchantmentHelper.getEnchantments(this.random, itemStack, j, false);
        if (itemStack.getItem() == Items.BOOK && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public int getLapisCount() {
        ItemStack itemStack = this.inventory.getInvStack(1);
        if (itemStack.isEmpty()) {
            return 0;
        }
        return itemStack.getCount();
    }

    @Environment(value=EnvType.CLIENT)
    public int getSeed() {
        return this.seed.get();
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        this.context.run((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inventory));
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return EnchantingTableContainer.canUse(this.context, playerEntity, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slotList.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (i == 0) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (i == 1) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.getItem() == Items.LAPIS_LAZULI) {
                if (!this.insertItem(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!((Slot)this.slotList.get(0)).hasStack() && ((Slot)this.slotList.get(0)).canInsert(itemStack2)) {
                if (itemStack2.hasTag() && itemStack2.getCount() == 1) {
                    ((Slot)this.slotList.get(0)).setStack(itemStack2.copy());
                    itemStack2.setCount(0);
                } else if (!itemStack2.isEmpty()) {
                    ((Slot)this.slotList.get(0)).setStack(new ItemStack(itemStack2.getItem()));
                    itemStack2.decrement(1);
                }
            } else {
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

