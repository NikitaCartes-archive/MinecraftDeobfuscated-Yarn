package net.minecraft.world.timer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackSerializer<C> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final TimerCallbackSerializer<MinecraftServer> INSTANCE = new TimerCallbackSerializer<MinecraftServer>()
		.registerSerializer(new FunctionTimerCallback.Serializer())
		.registerSerializer(new FunctionTagTimerCallback.Serializer());
	private final Map<Identifier, TimerCallback.Serializer<C, ?>> serializersByType = Maps.<Identifier, TimerCallback.Serializer<C, ?>>newHashMap();
	private final Map<Class<?>, TimerCallback.Serializer<C, ?>> serializersByClass = Maps.<Class<?>, TimerCallback.Serializer<C, ?>>newHashMap();

	public TimerCallbackSerializer<C> registerSerializer(TimerCallback.Serializer<C, ?> serializer) {
		this.serializersByType.put(serializer.getId(), serializer);
		this.serializersByClass.put(serializer.getCallbackClass(), serializer);
		return this;
	}

	private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> class_) {
		return (TimerCallback.Serializer<C, T>)this.serializersByClass.get(class_);
	}

	public <T extends TimerCallback<C>> NbtCompound serialize(T callback) {
		TimerCallback.Serializer<C, T> serializer = this.getSerializer(callback.getClass());
		NbtCompound nbtCompound = new NbtCompound();
		serializer.serialize(nbtCompound, callback);
		nbtCompound.putString("Type", serializer.getId().toString());
		return nbtCompound;
	}

	@Nullable
	public TimerCallback<C> deserialize(NbtCompound tag) {
		Identifier identifier = Identifier.tryParse(tag.getString("Type"));
		TimerCallback.Serializer<C, ?> serializer = (TimerCallback.Serializer<C, ?>)this.serializersByType.get(identifier);
		if (serializer == null) {
			LOGGER.error("Failed to deserialize timer callback: {}", tag);
			return null;
		} else {
			try {
				return serializer.deserialize(tag);
			} catch (Exception var5) {
				LOGGER.error("Failed to deserialize timer callback: {}", tag, var5);
				return null;
			}
		}
	}
}
