package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1060 implements class_1061, class_3302 {
	private static final Logger field_5288 = LogManager.getLogger();
	public static final class_2960 field_5285 = new class_2960("");
	private final Map<class_2960, class_1062> field_5286 = Maps.<class_2960, class_1062>newHashMap();
	private final List<class_1061> field_5284 = Lists.<class_1061>newArrayList();
	private final Map<String, Integer> field_5283 = Maps.<String, Integer>newHashMap();
	private final class_3300 field_5287;

	public class_1060(class_3300 arg) {
		this.field_5287 = arg;
	}

	public void method_4618(class_2960 arg) {
		class_1062 lv = (class_1062)this.field_5286.get(arg);
		if (lv == null) {
			lv = new class_1049(arg);
			this.method_4616(arg, lv);
		}

		lv.method_4623();
	}

	public boolean method_4620(class_2960 arg, class_1063 arg2) {
		if (this.method_4616(arg, arg2)) {
			this.field_5284.add(arg2);
			return true;
		} else {
			return false;
		}
	}

	public boolean method_4616(class_2960 arg, class_1062 arg2) {
		boolean bl = true;

		try {
			arg2.method_4625(this.field_5287);
		} catch (IOException var8) {
			if (arg != field_5285) {
				field_5288.warn("Failed to load texture: {}", arg, var8);
			}

			arg2 = class_1047.method_4540();
			this.field_5286.put(arg, arg2);
			bl = false;
		} catch (Throwable var9) {
			class_128 lv = class_128.method_560(var9, "Registering texture");
			class_129 lv2 = lv.method_562("Resource location being registered");
			lv2.method_578("Resource location", arg);
			lv2.method_577("Texture object class", () -> arg2.getClass().getName());
			throw new class_148(lv);
		}

		this.field_5286.put(arg, arg2);
		return bl;
	}

	public class_1062 method_4619(class_2960 arg) {
		return (class_1062)this.field_5286.get(arg);
	}

	public class_2960 method_4617(String string, class_1043 arg) {
		Integer integer = (Integer)this.field_5283.get(string);
		if (integer == null) {
			integer = 1;
		} else {
			integer = integer + 1;
		}

		this.field_5283.put(string, integer);
		class_2960 lv = new class_2960(String.format("dynamic/%s_%d", string, integer));
		this.method_4616(lv, arg);
		return lv;
	}

	public CompletableFuture<Void> method_18168(class_2960 arg, Executor executor) {
		if (!this.field_5286.containsKey(arg)) {
			class_4005 lv = new class_4005(this.field_5287, arg, executor);
			this.field_5286.put(arg, lv);
			return lv.method_18148().thenRunAsync(() -> this.method_4616(arg, lv), class_310.method_1551());
		} else {
			return CompletableFuture.completedFuture(null);
		}
	}

	@Override
	public void method_4622() {
		for (class_1061 lv : this.field_5284) {
			lv.method_4622();
		}
	}

	public void method_4615(class_2960 arg) {
		class_1062 lv = this.method_4619(arg);
		if (lv != null) {
			TextureUtil.releaseTextureId(lv.method_4624());
		}
	}

	@Override
	public CompletableFuture<Void> reload(class_3302.class_4045 arg, class_3300 arg2, class_3695 arg3, class_3695 arg4, Executor executor, Executor executor2) {
		return CompletableFuture.allOf(class_442.method_18105(this, executor), this.method_18168(class_339.WIDGETS_LOCATION, executor))
			.thenCompose(arg::method_18352)
			.thenAcceptAsync(void_ -> {
				class_1047.method_4540();
				Iterator<Entry<class_2960, class_1062>> iterator = this.field_5286.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<class_2960, class_1062> entry = (Entry<class_2960, class_1062>)iterator.next();
					class_2960 lv = (class_2960)entry.getKey();
					class_1062 lv2 = (class_1062)entry.getValue();
					if (lv2 == class_1047.method_4540() && !lv.equals(class_1047.method_4539())) {
						iterator.remove();
					} else {
						lv2.method_18169(this, arg2, lv, executor2);
					}
				}
			}, executor2);
	}
}
