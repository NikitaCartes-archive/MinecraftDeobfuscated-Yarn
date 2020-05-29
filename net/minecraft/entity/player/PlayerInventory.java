/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

public class PlayerInventory
implements Inventory,
Nameable {
    public final DefaultedList<ItemStack> main = DefaultedList.ofSize(36, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
    public int selectedSlot;
    public final PlayerEntity player;
    private ItemStack cursorStack = ItemStack.EMPTY;
    private int changeCount;

    public PlayerInventory(PlayerEntity player) {
        this.player = player;
    }

    public ItemStack getMainHandStack() {
        if (PlayerInventory.isValidHotbarIndex(this.selectedSlot)) {
            return this.main.get(this.selectedSlot);
        }
        return ItemStack.EMPTY;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && this.areItemsEqual(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }

    private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
    }

    public int getEmptySlot() {
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.main.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    public void addPickBlock(ItemStack stack) {
        int i = this.getSlotWithStack(stack);
        if (PlayerInventory.isValidHotbarIndex(i)) {
            this.selectedSlot = i;
            return;
        }
        if (i == -1) {
            int j;
            this.selectedSlot = this.getSwappableHotbarSlot();
            if (!this.main.get(this.selectedSlot).isEmpty() && (j = this.getEmptySlot()) != -1) {
                this.main.set(j, this.main.get(this.selectedSlot));
            }
            this.main.set(this.selectedSlot, stack);
        } else {
            this.swapSlotWithHotbar(i);
        }
    }

    public void swapSlotWithHotbar(int hotbarSlot) {
        this.selectedSlot = this.getSwappableHotbarSlot();
        ItemStack itemStack = this.main.get(this.selectedSlot);
        this.main.set(this.selectedSlot, this.main.get(hotbarSlot));
        this.main.set(hotbarSlot, itemStack);
    }

    public static boolean isValidHotbarIndex(int slot) {
        return slot >= 0 && slot < 9;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlotWithStack(ItemStack stack) {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty() || !this.areItemsEqual(stack, this.main.get(i))) continue;
            return i;
        }
        return -1;
    }

    public int method_7371(ItemStack itemStack) {
        for (int i = 0; i < this.main.size(); ++i) {
            ItemStack itemStack2 = this.main.get(i);
            if (this.main.get(i).isEmpty() || !this.areItemsEqual(itemStack, this.main.get(i)) || this.main.get(i).isDamaged() || itemStack2.hasEnchantments() || itemStack2.hasCustomName()) continue;
            return i;
        }
        return -1;
    }

    public int getSwappableHotbarSlot() {
        int j;
        int i;
        for (i = 0; i < 9; ++i) {
            j = (this.selectedSlot + i) % 9;
            if (!this.main.get(j).isEmpty()) continue;
            return j;
        }
        for (i = 0; i < 9; ++i) {
            j = (this.selectedSlot + i) % 9;
            if (this.main.get(j).hasEnchantments()) continue;
            return j;
        }
        return this.selectedSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public void scrollInHotbar(double scrollAmount) {
        if (scrollAmount > 0.0) {
            scrollAmount = 1.0;
        }
        if (scrollAmount < 0.0) {
            scrollAmount = -1.0;
        }
        this.selectedSlot = (int)((double)this.selectedSlot - scrollAmount);
        while (this.selectedSlot < 0) {
            this.selectedSlot += 9;
        }
        while (this.selectedSlot >= 9) {
            this.selectedSlot -= 9;
        }
    }

    public int method_29280(Predicate<ItemStack> predicate, int i, Inventory inventory) {
        int j = 0;
        boolean bl = i == 0;
        j += Inventories.method_29234(this, predicate, i - j, bl);
        j += Inventories.method_29234(inventory, predicate, i - j, bl);
        j += Inventories.method_29235(this.cursorStack, predicate, i - j, bl);
        if (this.cursorStack.isEmpty()) {
            this.cursorStack = ItemStack.EMPTY;
        }
        return j;
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }
        if (i == -1) {
            return stack.getCount();
        }
        return this.addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        int j;
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = this.getStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasTag()) {
                itemStack.setTag(stack.getTag().copy());
            }
            this.setStack(slot, itemStack);
        }
        if ((j = i) > itemStack.getMaxCount() - itemStack.getCount()) {
            j = itemStack.getMaxCount() - itemStack.getCount();
        }
        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }
        if (j == 0) {
            return i;
        }
        itemStack.increment(j);
        itemStack.setCooldown(5);
        return i -= j;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getStack(this.selectedSlot), stack)) {
            return this.selectedSlot;
        }
        if (this.canStackAddMore(this.getStack(40), stack)) {
            return 40;
        }
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.canStackAddMore(this.main.get(i), stack)) continue;
            return i;
        }
        return -1;
    }

    public void updateItems() {
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (defaultedList.get(i).isEmpty()) continue;
                defaultedList.get(i).inventoryTick(this.player.world, this.player, i, this.selectedSlot == i);
            }
        }
    }

    public boolean insertStack(ItemStack stack) {
        return this.insertStack(-1, stack);
    }

    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        try {
            if (!stack.isDamaged()) {
                int i;
                do {
                    i = stack.getCount();
                    if (slot == -1) {
                        stack.setCount(this.addStack(stack));
                        continue;
                    }
                    stack.setCount(this.addStack(slot, stack));
                } while (!stack.isEmpty() && stack.getCount() < i);
                if (stack.getCount() == i && this.player.abilities.creativeMode) {
                    stack.setCount(0);
                    return true;
                }
                return stack.getCount() < i;
            }
            if (slot == -1) {
                slot = this.getEmptySlot();
            }
            if (slot >= 0) {
                this.main.set(slot, stack.copy());
                this.main.get(slot).setCooldown(5);
                stack.setCount(0);
                return true;
            }
            if (this.player.abilities.creativeMode) {
                stack.setCount(0);
                return true;
            }
            return false;
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Adding item to inventory");
            CrashReportSection crashReportSection = crashReport.addElement("Item being added");
            crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
            crashReportSection.add("Item data", stack.getDamage());
            crashReportSection.add("Item name", () -> stack.getName().getString());
            throw new CrashException(crashReport);
        }
    }

    public void offerOrDrop(World world, ItemStack stack) {
        if (world.isClient) {
            return;
        }
        while (!stack.isEmpty()) {
            int i = this.getOccupiedSlotWithRoomForStack(stack);
            if (i == -1) {
                i = this.getEmptySlot();
            }
            if (i == -1) {
                this.player.dropItem(stack, false);
                break;
            }
            int j = stack.getMaxCount() - this.getStack(i).getCount();
            if (!this.insertStack(i, stack.split(j))) continue;
            ((ServerPlayerEntity)this.player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, i, this.getStack(i)));
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
            slot -= defaultedList.size();
        }
        if (list != null && !((ItemStack)list.get(slot)).isEmpty()) {
            return Inventories.splitStack(list, slot, amount);
        }
        return ItemStack.EMPTY;
    }

    public void removeOne(ItemStack stack) {
        block0: for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (defaultedList.get(i) != stack) continue;
                defaultedList.set(i, ItemStack.EMPTY);
                continue block0;
            }
        }
    }

    @Override
    public ItemStack removeStack(int slot) {
        DefaultedList<ItemStack> defaultedList = null;
        for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
            slot -= defaultedList2.size();
        }
        if (defaultedList != null && !((ItemStack)defaultedList.get(slot)).isEmpty()) {
            ItemStack itemStack = defaultedList.get(slot);
            defaultedList.set(slot, ItemStack.EMPTY);
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = null;
        for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
            slot -= defaultedList2.size();
        }
        if (defaultedList != null) {
            defaultedList.set(slot, stack);
        }
    }

    public float getBlockBreakingSpeed(BlockState block) {
        return this.main.get(this.selectedSlot).getMiningSpeedMultiplier(block);
    }

    public ListTag serialize(ListTag tag) {
        CompoundTag compoundTag;
        int i;
        for (i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)i);
            this.main.get(i).toTag(compoundTag);
            tag.add(compoundTag);
        }
        for (i = 0; i < this.armor.size(); ++i) {
            if (this.armor.get(i).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)(i + 100));
            this.armor.get(i).toTag(compoundTag);
            tag.add(compoundTag);
        }
        for (i = 0; i < this.offHand.size(); ++i) {
            if (this.offHand.get(i).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)(i + 150));
            this.offHand.get(i).toTag(compoundTag);
            tag.add(compoundTag);
        }
        return tag;
    }

    public void deserialize(ListTag tag) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 0xFF;
            ItemStack itemStack = ItemStack.fromTag(compoundTag);
            if (itemStack.isEmpty()) continue;
            if (j >= 0 && j < this.main.size()) {
                this.main.set(j, itemStack);
                continue;
            }
            if (j >= 100 && j < this.armor.size() + 100) {
                this.armor.set(j - 100, itemStack);
                continue;
            }
            if (j < 150 || j >= this.offHand.size() + 150) continue;
            this.offHand.set(j - 150, itemStack);
        }
    }

    @Override
    public int size() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.main) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        for (ItemStack itemStack : this.armor) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        for (ItemStack itemStack : this.offHand) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
            slot -= defaultedList.size();
        }
        return list == null ? ItemStack.EMPTY : (ItemStack)list.get(slot);
    }

    @Override
    public Text getName() {
        return new TranslatableText("container.inventory");
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getArmorStack(int slot) {
        return this.armor.get(slot);
    }

    public void damageArmor(DamageSource damageSource, float f) {
        if (f <= 0.0f) {
            return;
        }
        if ((f /= 4.0f) < 1.0f) {
            f = 1.0f;
        }
        for (int i = 0; i < this.armor.size(); ++i) {
            ItemStack itemStack = this.armor.get(i);
            if (damageSource.isFire() && itemStack.getItem().isFireproof() || !(itemStack.getItem() instanceof ArmorItem)) continue;
            int j = i;
            itemStack.damage((int)f, this.player, playerEntity -> playerEntity.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, j)));
        }
    }

    public void dropAll() {
        for (List list : this.combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = (ItemStack)list.get(i);
                if (itemStack.isEmpty()) continue;
                this.player.dropItem(itemStack, true, false);
                list.set(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void markDirty() {
        ++this.changeCount;
    }

    @Environment(value=EnvType.CLIENT)
    public int getChangeCount() {
        return this.changeCount;
    }

    public void setCursorStack(ItemStack stack) {
        this.cursorStack = stack;
    }

    public ItemStack getCursorStack() {
        return this.cursorStack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.player.removed) {
            return false;
        }
        return !(player.squaredDistanceTo(this.player) > 64.0);
    }

    public boolean contains(ItemStack stack) {
        for (List list : this.combinedInventory) {
            for (ItemStack itemStack : list) {
                if (itemStack.isEmpty() || !itemStack.isItemEqualIgnoreDamage(stack)) continue;
                return true;
            }
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean contains(Tag<Item> tag) {
        for (List list : this.combinedInventory) {
            for (ItemStack itemStack : list) {
                if (itemStack.isEmpty() || !tag.contains(itemStack.getItem())) continue;
                return true;
            }
        }
        return false;
    }

    public void clone(PlayerInventory other) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, other.getStack(i));
        }
        this.selectedSlot = other.selectedSlot;
    }

    @Override
    public void clear() {
        for (List list : this.combinedInventory) {
            list.clear();
        }
    }

    public void populateRecipeFinder(RecipeFinder finder) {
        for (ItemStack itemStack : this.main) {
            finder.addNormalItem(itemStack);
        }
    }
}

