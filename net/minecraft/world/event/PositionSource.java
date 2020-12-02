/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSourceType;

public interface PositionSource {
    public static final Codec<PositionSource> TYPE_CODEC = Registry.POSITION_SOURCE_TYPE.dispatch(PositionSource::getType, PositionSourceType::getCodec);

    public Optional<BlockPos> getPos(World var1);

    public PositionSourceType<?> getType();
}

