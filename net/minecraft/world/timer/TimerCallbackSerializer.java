/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.timer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.TimerCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TimerCallbackSerializer<C> {
    private static final Logger LOGGER = LogManager.getLogger();
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

    private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> class_) {
        return this.serializersByClass.get(class_);
    }

    public <T extends TimerCallback<C>> CompoundTag serialize(T callback) {
        TimerCallback.Serializer<T, T> serializer = this.getSerializer(callback.getClass());
        CompoundTag compoundTag = new CompoundTag();
        serializer.serialize(compoundTag, callback);
        compoundTag.putString("Type", serializer.getId().toString());
        return compoundTag;
    }

    @Nullable
    public TimerCallback<C> deserialize(CompoundTag tag) {
        Identifier identifier = Identifier.tryParse(tag.getString("Type"));
        TimerCallback.Serializer<C, ?> serializer = this.serializersByType.get(identifier);
        if (serializer == null) {
            LOGGER.error("Failed to deserialize timer callback: " + tag);
            return null;
        }
        try {
            return serializer.deserialize(tag);
        } catch (Exception exception) {
            LOGGER.error("Failed to deserialize timer callback: " + tag, (Throwable)exception);
            return null;
        }
    }
}

