package net.minecraft.world.timer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
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
		this.serializersByType.put(serializer.method_977(), serializer);
		this.serializersByClass.put(serializer.getCallbackClass(), serializer);
		return this;
	}

	private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> class_) {
		return (TimerCallback.Serializer<C, T>)this.serializersByClass.get(class_);
	}

	public <T extends TimerCallback<C>> CompoundTag method_973(T timerCallback) {
		TimerCallback.Serializer<C, T> serializer = this.getSerializer(timerCallback.getClass());
		CompoundTag compoundTag = new CompoundTag();
		serializer.method_975(compoundTag, timerCallback);
		compoundTag.putString("Type", serializer.method_977().toString());
		return compoundTag;
	}

	@Nullable
	public TimerCallback<C> method_972(CompoundTag compoundTag) {
		Identifier identifier = Identifier.create(compoundTag.getString("Type"));
		TimerCallback.Serializer<C, ?> serializer = (TimerCallback.Serializer<C, ?>)this.serializersByType.get(identifier);
		if (serializer == null) {
			LOGGER.error("Failed to deserialize timer callback: " + compoundTag);
			return null;
		} else {
			try {
				return serializer.method_976(compoundTag);
			} catch (Exception var5) {
				LOGGER.error("Failed to deserialize timer callback: " + compoundTag, (Throwable)var5);
				return null;
			}
		}
	}
}
