/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

public interface DataCommandObject {
    public void setTag(CompoundTag var1) throws CommandSyntaxException;

    public CompoundTag getTag() throws CommandSyntaxException;

    public Component getModifiedFeedback();

    public Component getQueryFeedback(Tag var1);

    public Component getGetFeedback(NbtPathArgumentType.NbtPath var1, double var2, int var4);
}

