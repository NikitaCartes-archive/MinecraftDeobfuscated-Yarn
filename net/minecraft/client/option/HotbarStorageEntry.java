/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import com.google.common.collect.ForwardingList;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class HotbarStorageEntry
extends ForwardingList<ItemStack> {
    private final DefaultedList<ItemStack> delegate = DefaultedList.ofSize(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);

    @Override
    protected List<ItemStack> delegate() {
        return this.delegate;
    }

    public ListTag toListTag() {
        ListTag listTag = new ListTag();
        for (ItemStack itemStack : this.delegate()) {
            listTag.add(itemStack.toTag(new CompoundTag()));
        }
        return listTag;
    }

    public void fromListTag(ListTag tag) {
        Collection list = this.delegate();
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, ItemStack.fromTag(tag.getCompound(i)));
        }
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.delegate()) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }
}

