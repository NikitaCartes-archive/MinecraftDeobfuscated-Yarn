/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.util.PacketByteBuf;

public interface ArgumentSerializer<T extends ArgumentType<?>> {
    public void toPacket(T var1, PacketByteBuf var2);

    public T fromPacket(PacketByteBuf var1);

    public void toJson(T var1, JsonObject var2);
}

