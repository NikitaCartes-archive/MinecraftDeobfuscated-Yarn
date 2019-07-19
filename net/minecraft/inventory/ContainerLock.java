/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class ContainerLock {
    public static final ContainerLock EMPTY = new ContainerLock("");
    private final String key;

    public ContainerLock(String string) {
        this.key = string;
    }

    public boolean canOpen(ItemStack itemStack) {
        return this.key.isEmpty() || !itemStack.isEmpty() && itemStack.hasCustomName() && this.key.equals(itemStack.getName().getString());
    }

    public void toTag(CompoundTag compoundTag) {
        if (!this.key.isEmpty()) {
            compoundTag.putString("Lock", this.key);
        }
    }

    public static ContainerLock fromTag(CompoundTag compoundTag) {
        if (compoundTag.contains("Lock", 8)) {
            return new ContainerLock(compoundTag.getString("Lock"));
        }
        return EMPTY;
    }
}

