/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;

public interface ParticleEffect {
    public ParticleType<?> getType();

    public void write(PacketByteBuf var1);

    public String asString();

    public static interface Factory<T extends ParticleEffect> {
        public T read(ParticleType<T> var1, StringReader var2) throws CommandSyntaxException;

        public T read(ParticleType<T> var1, PacketByteBuf var2);
    }
}

