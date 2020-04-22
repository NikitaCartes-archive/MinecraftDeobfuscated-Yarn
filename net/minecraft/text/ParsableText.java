/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Text} that needs to be parsed when it is loaded into the game.
 */
public interface ParsableText {
    public MutableText parse(@Nullable ServerCommandSource var1, @Nullable Entity var2, int var3) throws CommandSyntaxException;
}

