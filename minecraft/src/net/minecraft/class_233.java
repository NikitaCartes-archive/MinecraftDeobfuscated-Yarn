package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_233<C> {
	private static final Logger field_1308 = LogManager.getLogger();
	public static final class_233<MinecraftServer> field_1306 = new class_233<MinecraftServer>()
		.method_971(new class_231.class_232())
		.method_971(new class_229.class_230());
	private final Map<class_2960, class_234.class_235<C, ?>> field_1307 = Maps.<class_2960, class_234.class_235<C, ?>>newHashMap();
	private final Map<Class<?>, class_234.class_235<C, ?>> field_1305 = Maps.<Class<?>, class_234.class_235<C, ?>>newHashMap();

	public class_233<C> method_971(class_234.class_235<C, ?> arg) {
		this.field_1307.put(arg.method_977(), arg);
		this.field_1305.put(arg.method_978(), arg);
		return this;
	}

	private <T extends class_234<C>> class_234.class_235<C, T> method_970(Class<?> class_) {
		return (class_234.class_235<C, T>)this.field_1305.get(class_);
	}

	public <T extends class_234<C>> class_2487 method_973(T arg) {
		class_234.class_235<C, T> lv = this.method_970(arg.getClass());
		class_2487 lv2 = new class_2487();
		lv.method_975(lv2, arg);
		lv2.method_10582("Type", lv.method_977().toString());
		return lv2;
	}

	@Nullable
	public class_234<C> method_972(class_2487 arg) {
		class_2960 lv = class_2960.method_12829(arg.method_10558("Type"));
		class_234.class_235<C, ?> lv2 = (class_234.class_235<C, ?>)this.field_1307.get(lv);
		if (lv2 == null) {
			field_1308.error("Failed to deserialize timer callback: " + arg);
			return null;
		} else {
			try {
				return lv2.method_976(arg);
			} catch (Exception var5) {
				field_1308.error("Failed to deserialize timer callback: " + arg, (Throwable)var5);
				return null;
			}
		}
	}
}
