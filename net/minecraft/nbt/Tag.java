/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.class_5627;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.visitor.StringNbtWriter;

public interface Tag {
    public void write(DataOutput var1) throws IOException;

    public String toString();

    public byte getType();

    public TagReader<?> getReader();

    public Tag copy();

    default public String asString() {
        return new StringNbtWriter().apply(this);
    }

    public void method_32289(class_5627 var1);
}

