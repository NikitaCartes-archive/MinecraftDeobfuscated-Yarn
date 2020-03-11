/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class EnchantmentScreenHandler
extends ScreenHandler {
    private final Inventory inventory = new BasicInventory(2){

        @Override
        public void markDirty() {
            super.markDirty();
            EnchantmentScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final Random random = new Random();
    private final Property seed = Property.create();
    public final int[] enchantmentPower = new int[3];
    public final int[] enchantmentId = new int[]{-1, -1, -1};
    public final int[] enchantmentLevel = new int[]{-1, -1, -1};

    public EnchantmentScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public EnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ScreenHandlerType.ENCHANTMENT, syncId);
        int i;
        this.context = context;
        this.addSlot(new Slot(this.inventory, 0, 15, 47){

            @Override
            public boolean canInsert(ItemStack stack) {
                return true;
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 35, 47){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.LAPIS_LAZULI;
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
                            if (j == 0 && k == 0 || !world.isAir(blockPos.add(k, 0, j)) || !world.isAir(blockPos.add(k, 1, j))) continue;
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
                        this.enchantmentPower[j] = EnchantmentHelper.calculateRequiredExperienceLevel(this.random, j, i, itemStack);
                        this.enchantmentId[j] = -1;
                        this.enchantmentLevel[j] = -1;
                        if (this.enchantmentPower[j] >= j + 1) continue;
                        this.enchantmentPower[j] = 0;
                    }
                    for (j = 0; j < 3; ++j) {
                        List<EnchantmentLevelEntry> list;
                        if (this.enchantmentPower[j] <= 0 || (list = this.generateEnchantments(itemStack, j, this.enchantmentPower[j])) == null || list.isEmpty()) continue;
                        EnchantmentLevelEntry enchantmentLevelEntry = list.get(this.random.nextInt(list.size()));
                        this.enchantmentId[j] = Registry.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment);
                        this.enchantmentLevel[j] = enchantmentLevelEntry.level;
                    }
                    this.sendContentUpdates();
                });
            }
        }
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        ItemStack itemStack = this.inventory.getInvStack(0);
        ItemStack itemStack2 = this.inventory.getInvStack(1);
        int i = id + 1;
        if ((itemStack2.isEmpty() || itemStack2.getCount() < i) && !player.abilities.creativeMode) {
            return false;
        }
        if (this.enchantmentPower[id] > 0 && !itemStack.isEmpty() && (player.experienceLevel >= i && player.experienceLevel >= this.enchantmentPower[id] || player.abilities.creativeMode)) {
            this.context.run((world, blockPos) -> {
                ItemStack itemStack3 = itemStack;
                List<EnchantmentLevelEntry> list = this.generateEnchantments(itemStack3, id, this.enchantmentPower[id]);
                if (!list.isEmpty()) {
                    boolean bl;
                    player.applyEnchantmentCosts(itemStack3, i);
                    boolean bl2 = bl = itemStack3.getItem() == Items.BOOK;
                    if (bl) {
                        itemStack3 = new ItemStack(Items.ENCHANTED_BOOK);
                        CompoundTag compoundTag = itemStack.getTag();
                        if (compoundTag != null) {
                            itemStack3.setTag(compoundTag.copy());
                        }
                        this.inventory.setInvStack(0, itemStack3);
                    }
                    for (int k = 0; k < list.size(); ++k) {
                        EnchantmentLevelEntry enchantmentLevelEntry = list.get(k);
                        if (bl) {
                            EnchantedBookItem.addEnchantment(itemStack3, enchantmentLevelEntry);
                            continue;
                        }
                        itemStack3.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
                    }
                    if (!playerEntity.abilities.creativeMode) {
                        itemStack2.decrement(i);
                        if (itemStack2.isEmpty()) {
                            this.inventory.setInvStack(1, ItemStack.EMPTY);
                        }
                    }
                    player.incrementStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayerEntity) {
                        Criterions.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, itemStack3, i);
                    }
                    this.inventory.markDirty();
                    this.seed.set(player.getEnchantmentTableSeed());
                    this.onContentChanged(this.inventory);
                    world.playSound(null, (BlockPos)blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.1f + 0.9f);
                }
            });
            return true;
        }
        return false;
    }

    private List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level) {
        this.random.setSeed(this.seed.get() + slot);
        List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, stack, level, false);
        if (stack.getItem() == Items.BOOK && list.size() > 1) {
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
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, playerEntity.world, this.inventory));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return EnchantmentScreenHandler.canUse(this.context, player, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 0) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 1) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.getItem() == Items.LAPIS_LAZULI) {
                if (!this.insertItem(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!((Slot)this.slots.get(0)).hasStack() && ((Slot)this.slots.get(0)).canInsert(itemStack2)) {
                ItemStack itemStack3 = itemStack2.copy();
                itemStack3.setCount(1);
                itemStack2.decrement(1);
                ((Slot)this.slots.get(0)).setStack(itemStack3);
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
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }
}

