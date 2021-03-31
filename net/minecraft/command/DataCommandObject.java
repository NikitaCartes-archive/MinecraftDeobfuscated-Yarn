/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

public interface DataCommandObject {
    public void setNbt(NbtCompound var1) throws CommandSyntaxException;

    public NbtCompound getNbt() throws CommandSyntaxException;

    public Text feedbackModify();

    public Text feedbackQuery(NbtElement var1);

    public Text feedbackGet(NbtPathArgumentType.NbtPath var1, double var2, int var4);
}

