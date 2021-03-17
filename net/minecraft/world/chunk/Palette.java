/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public interface Palette<T> {
    public int getIndex(T var1);

    public boolean accepts(Predicate<T> var1);

    @Nullable
    public T getByIndex(int var1);

    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf var1);

    public void toPacket(PacketByteBuf var1);

    public int getPacketSize();

    public void readNbt(NbtList var1);
}

