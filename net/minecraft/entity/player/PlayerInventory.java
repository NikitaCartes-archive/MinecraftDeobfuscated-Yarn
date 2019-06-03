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
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

public class PlayerInventory
implements Inventory,
Nameable {
    public final DefaultedList<ItemStack> main = DefaultedList.create(36, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> armor = DefaultedList.create(4, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> offHand = DefaultedList.create(1, ItemStack.EMPTY);
    private final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
    public int selectedSlot;
    public final PlayerEntity player;
    private ItemStack cursorStack = ItemStack.EMPTY;
    private int changeCount;

    public PlayerInventory(PlayerEntity playerEntity) {
        this.player = playerEntity;
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

    private boolean canStackAddMore(ItemStack itemStack, ItemStack itemStack2) {
        return !itemStack.isEmpty() && this.areItemsEqual(itemStack, itemStack2) && itemStack.isStackable() && itemStack.getCount() < itemStack.getMaxCount() && itemStack.getCount() < this.getInvMaxStackAmount();
    }

    private boolean areItemsEqual(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
    }

    public int getEmptySlot() {
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.main.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    public void addPickBlock(ItemStack itemStack) {
        int i = this.getSlotWithStack(itemStack);
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
            this.main.set(this.selectedSlot, itemStack);
        } else {
            this.swapSlotWithHotbar(i);
        }
    }

    public void swapSlotWithHotbar(int i) {
        this.selectedSlot = this.getSwappableHotbarSlot();
        ItemStack itemStack = this.main.get(this.selectedSlot);
        this.main.set(this.selectedSlot, this.main.get(i));
        this.main.set(i, itemStack);
    }

    public static boolean isValidHotbarIndex(int i) {
        return i >= 0 && i < 9;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlotWithStack(ItemStack itemStack) {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty() || !this.areItemsEqual(itemStack, this.main.get(i))) continue;
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
    public void scrollInHotbar(double d) {
        if (d > 0.0) {
            d = 1.0;
        }
        if (d < 0.0) {
            d = -1.0;
        }
        this.selectedSlot = (int)((double)this.selectedSlot - d);
        while (this.selectedSlot < 0) {
            this.selectedSlot += 9;
        }
        while (this.selectedSlot >= 9) {
            this.selectedSlot -= 9;
        }
    }

    public int method_7369(Predicate<ItemStack> predicate, int i) {
        int k;
        int j = 0;
        for (k = 0; k < this.getInvSize(); ++k) {
            ItemStack itemStack = this.getInvStack(k);
            if (itemStack.isEmpty() || !predicate.test(itemStack)) continue;
            int l = i <= 0 ? itemStack.getCount() : Math.min(i - j, itemStack.getCount());
            j += l;
            if (i == 0) continue;
            itemStack.decrement(l);
            if (itemStack.isEmpty()) {
                this.setInvStack(k, ItemStack.EMPTY);
            }
            if (i <= 0 || j < i) continue;
            return j;
        }
        if (!this.cursorStack.isEmpty() && predicate.test(this.cursorStack)) {
            k = i <= 0 ? this.cursorStack.getCount() : Math.min(i - j, this.cursorStack.getCount());
            j += k;
            if (i != 0) {
                this.cursorStack.decrement(k);
                if (this.cursorStack.isEmpty()) {
                    this.cursorStack = ItemStack.EMPTY;
                }
                if (i > 0 && j >= i) {
                    return j;
                }
            }
        }
        return j;
    }

    private int addStack(ItemStack itemStack) {
        int i = this.getOccupiedSlotWithRoomForStack(itemStack);
        if (i == -1) {
            i = this.getEmptySlot();
        }
        if (i == -1) {
            return itemStack.getCount();
        }
        return this.addStack(i, itemStack);
    }

    private int addStack(int i, ItemStack itemStack) {
        int k;
        Item item = itemStack.getItem();
        int j = itemStack.getCount();
        ItemStack itemStack2 = this.getInvStack(i);
        if (itemStack2.isEmpty()) {
            itemStack2 = new ItemStack(item, 0);
            if (itemStack.hasTag()) {
                itemStack2.setTag(itemStack.getTag().method_10553());
            }
            this.setInvStack(i, itemStack2);
        }
        if ((k = j) > itemStack2.getMaxCount() - itemStack2.getCount()) {
            k = itemStack2.getMaxCount() - itemStack2.getCount();
        }
        if (k > this.getInvMaxStackAmount() - itemStack2.getCount()) {
            k = this.getInvMaxStackAmount() - itemStack2.getCount();
        }
        if (k == 0) {
            return j;
        }
        itemStack2.increment(k);
        itemStack2.setCooldown(5);
        return j -= k;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack itemStack) {
        if (this.canStackAddMore(this.getInvStack(this.selectedSlot), itemStack)) {
            return this.selectedSlot;
        }
        if (this.canStackAddMore(this.getInvStack(40), itemStack)) {
            return 40;
        }
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.canStackAddMore(this.main.get(i), itemStack)) continue;
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

    public boolean insertStack(ItemStack itemStack) {
        return this.insertStack(-1, itemStack);
    }

    public boolean insertStack(int i, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        try {
            if (!itemStack.isDamaged()) {
                int j;
                do {
                    j = itemStack.getCount();
                    if (i == -1) {
                        itemStack.setCount(this.addStack(itemStack));
                        continue;
                    }
                    itemStack.setCount(this.addStack(i, itemStack));
                } while (!itemStack.isEmpty() && itemStack.getCount() < j);
                if (itemStack.getCount() == j && this.player.abilities.creativeMode) {
                    itemStack.setCount(0);
                    return true;
                }
                return itemStack.getCount() < j;
            }
            if (i == -1) {
                i = this.getEmptySlot();
            }
            if (i >= 0) {
                this.main.set(i, itemStack.copy());
                this.main.get(i).setCooldown(5);
                itemStack.setCount(0);
                return true;
            }
            if (this.player.abilities.creativeMode) {
                itemStack.setCount(0);
                return true;
            }
            return false;
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Adding item to inventory");
            CrashReportSection crashReportSection = crashReport.addElement("Item being added");
            crashReportSection.add("Item ID", Item.getRawId(itemStack.getItem()));
            crashReportSection.add("Item data", itemStack.getDamage());
            crashReportSection.add("Item name", () -> itemStack.getName().getString());
            throw new CrashException(crashReport);
        }
    }

    public void offerOrDrop(World world, ItemStack itemStack) {
        if (world.isClient) {
            return;
        }
        while (!itemStack.isEmpty()) {
            int i = this.getOccupiedSlotWithRoomForStack(itemStack);
            if (i == -1) {
                i = this.getEmptySlot();
            }
            if (i == -1) {
                this.player.dropItem(itemStack, false);
                break;
            }
            int j = itemStack.getMaxCount() - this.getInvStack(i).getCount();
            if (!this.insertStack(i, itemStack.split(j))) continue;
            ((ServerPlayerEntity)this.player).networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-2, i, this.getInvStack(i)));
        }
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            if (i < defaultedList.size()) {
                list = defaultedList;
                break;
            }
            i -= defaultedList.size();
        }
        if (list != null && !((ItemStack)list.get(i)).isEmpty()) {
            return Inventories.splitStack(list, i, j);
        }
        return ItemStack.EMPTY;
    }

    public void removeOne(ItemStack itemStack) {
        block0: for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (defaultedList.get(i) != itemStack) continue;
                defaultedList.set(i, ItemStack.EMPTY);
                continue block0;
            }
        }
    }

    @Override
    public ItemStack removeInvStack(int i) {
        DefaultedList<ItemStack> defaultedList = null;
        for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
            if (i < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
            i -= defaultedList2.size();
        }
        if (defaultedList != null && !((ItemStack)defaultedList.get(i)).isEmpty()) {
            ItemStack itemStack = defaultedList.get(i);
            defaultedList.set(i, ItemStack.EMPTY);
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        DefaultedList<ItemStack> defaultedList = null;
        for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
            if (i < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
            i -= defaultedList2.size();
        }
        if (defaultedList != null) {
            defaultedList.set(i, itemStack);
        }
    }

    public float getBlockBreakingSpeed(BlockState blockState) {
        return this.main.get(this.selectedSlot).getMiningSpeed(blockState);
    }

    public ListTag serialize(ListTag listTag) {
        CompoundTag compoundTag;
        int i;
        for (i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)i);
            this.main.get(i).toTag(compoundTag);
            listTag.add(compoundTag);
        }
        for (i = 0; i < this.armor.size(); ++i) {
            if (this.armor.get(i).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)(i + 100));
            this.armor.get(i).toTag(compoundTag);
            listTag.add(compoundTag);
        }
        for (i = 0; i < this.offHand.size(); ++i) {
            if (this.offHand.get(i).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)(i + 150));
            this.offHand.get(i).toTag(compoundTag);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    public void deserialize(ListTag listTag) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompoundTag(i);
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
    public int getInvSize() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }

    @Override
    public boolean isInvEmpty() {
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
    public ItemStack getInvStack(int i) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            if (i < defaultedList.size()) {
                list = defaultedList;
                break;
            }
            i -= defaultedList.size();
        }
        return list == null ? ItemStack.EMPTY : (ItemStack)list.get(i);
    }

    @Override
    public Text getName() {
        return new TranslatableText("container.inventory", new Object[0]);
    }

    public boolean isUsingEffectiveTool(BlockState blockState) {
        return this.getInvStack(this.selectedSlot).isEffectiveOn(blockState);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getArmorStack(int i) {
        return this.armor.get(i);
    }

    public void damageArmor(float f) {
        if (f <= 0.0f) {
            return;
        }
        if ((f /= 4.0f) < 1.0f) {
            f = 1.0f;
        }
        for (int i = 0; i < this.armor.size(); ++i) {
            ItemStack itemStack = this.armor.get(i);
            if (!(itemStack.getItem() instanceof ArmorItem)) continue;
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

    public void setCursorStack(ItemStack itemStack) {
        this.cursorStack = itemStack;
    }

    public ItemStack getCursorStack() {
        return this.cursorStack;
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        if (this.player.removed) {
            return false;
        }
        return !(playerEntity.squaredDistanceTo(this.player) > 64.0);
    }

    public boolean contains(ItemStack itemStack) {
        for (List list : this.combinedInventory) {
            for (ItemStack itemStack2 : list) {
                if (itemStack2.isEmpty() || !itemStack2.isItemEqualIgnoreDamage(itemStack)) continue;
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

    public void clone(PlayerInventory playerInventory) {
        for (int i = 0; i < this.getInvSize(); ++i) {
            this.setInvStack(i, playerInventory.getInvStack(i));
        }
        this.selectedSlot = playerInventory.selectedSlot;
    }

    @Override
    public void clear() {
        for (List list : this.combinedInventory) {
            list.clear();
        }
    }

    public void populateRecipeFinder(RecipeFinder recipeFinder) {
        for (ItemStack itemStack : this.main) {
            recipeFinder.addNormalItem(itemStack);
        }
    }
}

