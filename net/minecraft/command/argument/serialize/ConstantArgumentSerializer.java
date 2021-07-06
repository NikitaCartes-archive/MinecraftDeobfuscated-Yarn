/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class ConstantArgumentSerializer<T extends ArgumentType<?>>
implements ArgumentSerializer<T> {
    private final Supplier<T> supplier;

    public ConstantArgumentSerializer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void toPacket(T type, PacketByteBuf buf) {
    }

    @Override
    public T fromPacket(PacketByteBuf buf) {
        return (T)((ArgumentType)this.supplier.get());
    }

    @Override
    public void toJson(T type, JsonObject json) {
    }
}

