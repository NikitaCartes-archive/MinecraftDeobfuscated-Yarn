/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.util.AbstractList;
import net.minecraft.nbt.Tag;

public abstract class AbstractListTag<T extends Tag>
extends AbstractList<T>
implements Tag {
    public abstract T method_10606(int var1, T var2);

    public abstract void method_10531(int var1, T var2);

    public abstract T method_10536(int var1);

    public abstract boolean setTag(int var1, Tag var2);

    public abstract boolean addTag(int var1, Tag var2);

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.method_10531(i, (Tag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.method_10606(i, (Tag)object);
    }
}

