package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1257 {
	private static final Logger field_5756 = LogManager.getLogger();
	private static final ThreadFactory field_5757 = new ThreadFactoryBuilder().setDaemon(true).build();
	private final String field_5758;
	private final class_30 field_5761;
	private final class_37 field_5755;
	private final Thread field_5767;
	private volatile boolean field_5760 = true;
	private volatile boolean field_5759;
	private volatile float field_5763;
	private volatile int field_5768;
	private volatile int field_5766;
	private volatile int field_5764;
	private final Object2FloatMap<class_2874> field_5762 = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(class_156.method_655()));
	private volatile class_2561 field_5765 = new class_2588("optimizeWorld.stage.counting");

	public class_1257(String string, class_32 arg, class_31 arg2) {
		this.field_5758 = arg2.method_150();
		this.field_5761 = arg.method_242(string, null);
		this.field_5761.method_136(arg2);
		this.field_5755 = new class_37(this.field_5761);
		this.field_5767 = field_5757.newThread(this::method_5404);
		this.field_5767.setUncaughtExceptionHandler(this::method_5398);
		this.field_5767.start();
	}

	private void method_5398(Thread thread, Throwable throwable) {
		field_5756.error("Error upgrading world", throwable);
		this.field_5760 = false;
		this.field_5765 = new class_2588("optimizeWorld.stage.failed");
	}

	public void method_5402() {
		this.field_5760 = false;

		try {
			this.field_5767.join();
		} catch (InterruptedException var2) {
		}
	}

	private void method_5404() {
		File file = this.field_5761.method_132();
		class_1256 lv = new class_1256(file);
		Builder<class_2874, class_2852> builder = ImmutableMap.builder();

		for (class_2874 lv2 : class_2874.method_12482()) {
			builder.put(lv2, new class_2852(lv2.method_12488(file), this.field_5761.method_130()));
		}

		Map<class_2874, class_2852> map = builder.build();
		long l = class_156.method_658();
		this.field_5768 = 0;
		Builder<class_2874, ListIterator<class_1923>> builder2 = ImmutableMap.builder();

		for (class_2874 lv3 : class_2874.method_12482()) {
			List<class_1923> list = lv.method_5391(lv3);
			builder2.put(lv3, list.listIterator());
			this.field_5768 = this.field_5768 + list.size();
		}

		ImmutableMap<class_2874, ListIterator<class_1923>> immutableMap = builder2.build();
		float f = (float)this.field_5768;
		this.field_5765 = new class_2588("optimizeWorld.stage.structures");

		for (Entry<class_2874, class_2852> entry : map.entrySet()) {
			((class_2852)entry.getValue()).method_12380((class_2874)entry.getKey(), this.field_5755);
		}

		this.field_5755.method_265();
		this.field_5765 = new class_2588("optimizeWorld.stage.upgrading");
		if (f <= 0.0F) {
			for (class_2874 lv4 : class_2874.method_12482()) {
				this.field_5762.put(lv4, 1.0F / (float)map.size());
			}
		}

		while (this.field_5760) {
			boolean bl = false;
			float g = 0.0F;

			for (class_2874 lv5 : class_2874.method_12482()) {
				ListIterator<class_1923> listIterator = immutableMap.get(lv5);
				bl |= this.method_5396((class_2852)map.get(lv5), listIterator, lv5);
				if (f > 0.0F) {
					float h = (float)listIterator.nextIndex() / f;
					this.field_5762.put(lv5, h);
					g += h;
				}
			}

			this.field_5763 = g;
			if (!bl) {
				this.field_5760 = false;
			}
		}

		this.field_5765 = new class_2588("optimizeWorld.stage.finished");
		l = class_156.method_658() - l;
		field_5756.info("World optimizaton finished after {} ms", l);
		map.values().forEach(class_2852::method_12413);
		this.field_5755.method_265();
		this.field_5759 = true;
	}

	private boolean method_5396(class_2852 arg, ListIterator<class_1923> listIterator, class_2874 arg2) {
		if (listIterator.hasNext()) {
			boolean bl;
			synchronized (arg) {
				bl = arg.method_12375((class_1923)listIterator.next(), arg2, this.field_5755);
			}

			if (bl) {
				this.field_5766++;
			} else {
				this.field_5764++;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean method_5403() {
		return this.field_5759;
	}

	@Environment(EnvType.CLIENT)
	public float method_5393(class_2874 arg) {
		return this.field_5762.getFloat(arg);
	}

	@Environment(EnvType.CLIENT)
	public float method_5401() {
		return this.field_5763;
	}

	public int method_5397() {
		return this.field_5768;
	}

	public int method_5400() {
		return this.field_5766;
	}

	public int method_5399() {
		return this.field_5764;
	}

	public class_2561 method_5394() {
		return this.field_5765;
	}

	@Environment(EnvType.CLIENT)
	public String method_5395() {
		return this.field_5758;
	}
}
