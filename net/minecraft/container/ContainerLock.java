/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class ContainerLock {
    public static final ContainerLock NONE = new ContainerLock("");
    private final String key;

    public ContainerLock(String string) {
        this.key = string;
    }

    public boolean isEmpty(ItemStack itemStack) {
        return this.key.isEmpty() || !itemStack.isEmpty() && itemStack.hasDisplayName() && this.key.equals(itemStack.getDisplayName().getString());
    }

    public void serialize(CompoundTag compoundTag) {
        if (!this.key.isEmpty()) {
            compoundTag.putString("Lock", this.key);
        }
    }

    public static ContainerLock deserialize(CompoundTag compoundTag) {
        if (compoundTag.containsKey("Lock", 8)) {
            return new ContainerLock(compoundTag.getString("Lock"));
        }
        return NONE;
    }
}

