/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;

public interface PositionSourceType<T extends PositionSource> {
    public static final PositionSourceType<BlockPositionSource> BLOCK = PositionSourceType.register("block", new BlockPositionSource.Type());
    public static final PositionSourceType<EntityPositionSource> ENTITY = PositionSourceType.register("entity", new EntityPositionSource.Type());

    public T readFromBuf(PacketByteBuf var1);

    public void writeToBuf(PacketByteBuf var1, T var2);

    public Codec<T> getCodec();

    public static <S extends PositionSourceType<T>, T extends PositionSource> S register(String id, S positionSourceType) {
        return (S)Registry.register(Registry.POSITION_SOURCE_TYPE, id, positionSourceType);
    }

    public static PositionSource read(PacketByteBuf buf) {
        Identifier identifier = buf.readIdentifier();
        return Registry.POSITION_SOURCE_TYPE.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + identifier)).readFromBuf(buf);
    }

    public static <T extends PositionSource> void write(T positionSource, PacketByteBuf buf) {
        buf.writeIdentifier(Registry.POSITION_SOURCE_TYPE.getId(positionSource.getType()));
        positionSource.getType().writeToBuf(buf, positionSource);
    }
}

