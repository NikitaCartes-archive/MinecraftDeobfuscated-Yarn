package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1257 {
	private static final Logger field_5756 = LogManager.getLogger();
	private static final ThreadFactory field_5757 = new ThreadFactoryBuilder().setDaemon(true).build();
	private final String field_5758;
	private final boolean field_19225;
	private final class_29 field_5761;
	private final Thread field_5767;
	private final File field_17621;
	private volatile boolean field_5760 = true;
	private volatile boolean field_5759;
	private volatile float field_5763;
	private volatile int field_5768;
	private volatile int field_5766;
	private volatile int field_5764;
	private final Object2FloatMap<class_2874> field_5762 = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(class_156.method_655()));
	private volatile class_2561 field_5765 = new class_2588("optimizeWorld.stage.counting");
	private static final Pattern field_17622 = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
	private final class_26 field_5755;

	public class_1257(String string, class_32 arg, class_31 arg2, boolean bl) {
		this.field_5758 = arg2.method_150();
		this.field_19225 = bl;
		this.field_5761 = arg.method_242(string, null);
		this.field_5761.method_136(arg2);
		this.field_5755 = new class_26(new File(class_2874.field_13072.method_12488(this.field_5761.method_132()), "data"), this.field_5761.method_130());
		this.field_17621 = this.field_5761.method_132();
		this.field_5767 = field_5757.newThread(this::method_5404);
		this.field_5767.setUncaughtExceptionHandler((thread, throwable) -> {
			field_5756.error("Error upgrading world", throwable);
			this.field_5765 = new class_2588("optimizeWorld.stage.failed");
		});
		this.field_5767.start();
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
		this.field_5768 = 0;
		Builder<class_2874, ListIterator<class_1923>> builder = ImmutableMap.builder();

		for (class_2874 lv : class_2874.method_12482()) {
			List<class_1923> list = this.method_17830(lv);
			builder.put(lv, list.listIterator());
			this.field_5768 = this.field_5768 + list.size();
		}

		if (this.field_5768 == 0) {
			this.field_5759 = true;
		} else {
			float f = (float)this.field_5768;
			ImmutableMap<class_2874, ListIterator<class_1923>> immutableMap = builder.build();
			Builder<class_2874, class_3977> builder2 = ImmutableMap.builder();

			for (class_2874 lv2 : class_2874.method_12482()) {
				File file2 = lv2.method_12488(file);
				builder2.put(lv2, new class_3977(new File(file2, "region"), this.field_5761.method_130()));
			}

			ImmutableMap<class_2874, class_3977> immutableMap2 = builder2.build();
			long l = class_156.method_658();
			this.field_5765 = new class_2588("optimizeWorld.stage.upgrading");

			while (this.field_5760) {
				boolean bl = false;
				float g = 0.0F;

				for (class_2874 lv3 : class_2874.method_12482()) {
					ListIterator<class_1923> listIterator = immutableMap.get(lv3);
					class_3977 lv4 = immutableMap2.get(lv3);
					if (listIterator.hasNext()) {
						class_1923 lv5 = (class_1923)listIterator.next();
						boolean bl2 = false;

						try {
							class_2487 lv6 = lv4.method_17911(lv5);
							if (lv6 != null) {
								int i = class_3977.method_17908(lv6);
								class_2487 lv7 = lv4.method_17907(lv3, () -> this.field_5755, lv6);
								boolean bl3 = i < class_155.method_16673().getWorldVersion();
								if (this.field_19225) {
									class_2487 lv8 = lv7.method_10562("Level");
									bl3 = bl3 || lv8.method_10545("Heightmaps");
									lv8.method_10551("Heightmaps");
									bl3 = bl3 || lv8.method_10545("isLightOn");
									lv8.method_10551("isLightOn");
								}

								if (bl3) {
									lv4.method_17910(lv5, lv7);
									bl2 = true;
								}
							}
						} catch (class_148 var23) {
							Throwable throwable = var23.getCause();
							if (!(throwable instanceof IOException)) {
								throw var23;
							}

							field_5756.error("Error upgrading chunk {}", lv5, throwable);
						} catch (IOException var24) {
							field_5756.error("Error upgrading chunk {}", lv5, var24);
						}

						if (bl2) {
							this.field_5766++;
						} else {
							this.field_5764++;
						}

						bl = true;
					}

					float h = (float)listIterator.nextIndex() / f;
					this.field_5762.put(lv3, h);
					g += h;
				}

				this.field_5763 = g;
				if (!bl) {
					this.field_5760 = false;
				}
			}

			this.field_5765 = new class_2588("optimizeWorld.stage.finished");

			for (class_3977 lv10 : immutableMap2.values()) {
				try {
					lv10.close();
				} catch (IOException var22) {
					field_5756.error("Error upgrading chunk", (Throwable)var22);
				}
			}

			this.field_5755.method_125();
			l = class_156.method_658() - l;
			field_5756.info("World optimizaton finished after {} ms", l);
			this.field_5759 = true;
		}
	}

	private List<class_1923> method_17830(class_2874 arg) {
		File file = arg.method_12488(this.field_17621);
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mca"));
		if (files == null) {
			return ImmutableList.of();
		} else {
			List<class_1923> list = Lists.<class_1923>newArrayList();

			for (File file3 : files) {
				Matcher matcher = field_17622.matcher(file3.getName());
				if (matcher.matches()) {
					int i = Integer.parseInt(matcher.group(1)) << 5;
					int j = Integer.parseInt(matcher.group(2)) << 5;

					try (class_2861 lv = new class_2861(file3)) {
						for (int k = 0; k < 32; k++) {
							for (int l = 0; l < 32; l++) {
								class_1923 lv2 = new class_1923(k + i, l + j);
								if (lv.method_12420(lv2)) {
									list.add(lv2);
								}
							}
						}
					} catch (Throwable var28) {
					}
				}
			}

			return list;
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
}
