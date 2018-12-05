package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_233<C> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final class_233<MinecraftServer> field_1306 = new class_233<MinecraftServer>()
		.method_971(new class_231.class_232())
		.method_971(new class_229.class_230());
	private final Map<Identifier, class_234.class_235<C, ?>> field_1307 = Maps.<Identifier, class_234.class_235<C, ?>>newHashMap();
	private final Map<Class<?>, class_234.class_235<C, ?>> field_1305 = Maps.<Class<?>, class_234.class_235<C, ?>>newHashMap();

	public class_233<C> method_971(class_234.class_235<C, ?> arg) {
		this.field_1307.put(arg.method_977(), arg);
		this.field_1305.put(arg.method_978(), arg);
		return this;
	}

	private <T extends class_234<C>> class_234.class_235<C, T> method_970(Class<?> class_) {
		return (class_234.class_235<C, T>)this.field_1305.get(class_);
	}

	public <T extends class_234<C>> CompoundTag method_973(T arg) {
		class_234.class_235<C, T> lv = this.method_970(arg.getClass());
		CompoundTag compoundTag = new CompoundTag();
		lv.method_975(compoundTag, arg);
		compoundTag.putString("Type", lv.method_977().toString());
		return compoundTag;
	}

	@Nullable
	public class_234<C> method_972(CompoundTag compoundTag) {
		Identifier identifier = Identifier.create(compoundTag.getString("Type"));
		class_234.class_235<C, ?> lv = (class_234.class_235<C, ?>)this.field_1307.get(identifier);
		if (lv == null) {
			LOGGER.error("Failed to deserialize timer callback: " + compoundTag);
			return null;
		} else {
			try {
				return lv.method_976(compoundTag);
			} catch (Exception var5) {
				LOGGER.error("Failed to deserialize timer callback: " + compoundTag, (Throwable)var5);
				return null;
			}
		}
	}
}
