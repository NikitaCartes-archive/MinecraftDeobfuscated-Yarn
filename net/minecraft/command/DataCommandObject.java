/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;

public interface DataCommandObject {
    public void setTag(CompoundTag var1) throws CommandSyntaxException;

    public CompoundTag getTag() throws CommandSyntaxException;

    public Text feedbackModify();

    public Text feedbackQuery(Tag var1);

    public Text feedbackGet(NbtPathArgumentType.NbtPath var1, double var2, int var4);
}

