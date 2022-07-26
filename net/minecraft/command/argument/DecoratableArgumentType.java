/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface DecoratableArgumentType<T>
extends ArgumentType<T> {
    @Nullable
    default public CompletableFuture<Text> decorate(ServerCommandSource source, ParsedArgument<ServerCommandSource, ?> parsedValue) throws CommandSyntaxException {
        if (this.getFormatClass().isInstance(parsedValue.getResult())) {
            return this.decorate(source, this.getFormatClass().cast(parsedValue.getResult()));
        }
        return null;
    }

    public CompletableFuture<Text> decorate(ServerCommandSource var1, T var2) throws CommandSyntaxException;

    public Class<T> getFormatClass();
}

