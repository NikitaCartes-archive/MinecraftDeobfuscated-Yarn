/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.timer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.TimerCallback;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TimerCallbackSerializer<C> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final TimerCallbackSerializer<MinecraftServer> INSTANCE = new TimerCallbackSerializer<MinecraftServer>().registerSerializer(new FunctionTimerCallback.Serializer()).registerSerializer(new FunctionTagTimerCallback.Serializer());
    private final Map<Identifier, TimerCallback.Serializer<C, ?>> serializersByType = Maps.newHashMap();
    private final Map<Class<?>, TimerCallback.Serializer<C, ?>> serializersByClass = Maps.newHashMap();

    @VisibleForTesting
    public TimerCallbackSerializer() {
    }

    public TimerCallbackSerializer<C> registerSerializer(TimerCallback.Serializer<C, ?> serializer) {
        this.serializersByType.put(serializer.getId(), serializer);
        this.serializersByClass.put(serializer.getCallbackClass(), serializer);
        return this;
    }

    private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> clazz) {
        return this.serializersByClass.get(clazz);
    }

    public <T extends TimerCallback<C>> NbtCompound serialize(T callback) {
        TimerCallback.Serializer<T, T> serializer = this.getSerializer(callback.getClass());
        NbtCompound nbtCompound = new NbtCompound();
        serializer.serialize(nbtCompound, callback);
        nbtCompound.putString("Type", serializer.getId().toString());
        return nbtCompound;
    }

    @Nullable
    public TimerCallback<C> deserialize(NbtCompound nbt) {
        Identifier identifier = Identifier.tryParse(nbt.getString("Type"));
        TimerCallback.Serializer<C, ?> serializer = this.serializersByType.get(identifier);
        if (serializer == null) {
            LOGGER.error("Failed to deserialize timer callback: {}", (Object)nbt);
            return null;
        }
        try {
            return serializer.deserialize(nbt);
        } catch (Exception exception) {
            LOGGER.error("Failed to deserialize timer callback: {}", (Object)nbt, (Object)exception);
            return null;
        }
    }
}

