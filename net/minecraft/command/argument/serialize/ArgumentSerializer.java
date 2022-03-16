/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;

/**
 * Serializes an argument type to be sent to the client.
 */
public interface ArgumentSerializer<A extends ArgumentType<?>, T extends ArgumentTypeProperties<A>> {
    public void writePacket(T var1, PacketByteBuf var2);

    public T fromPacket(PacketByteBuf var1);

    public void writeJson(T var1, JsonObject var2);

    public T getArgumentTypeProperties(A var1);

    public static interface ArgumentTypeProperties<A extends ArgumentType<?>> {
        public A createType(CommandRegistryAccess var1);

        public ArgumentSerializer<A, ?> getSerializer();
    }
}

