package net.minecraft;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;

@Environment(EnvType.CLIENT)
public class class_1140 {
	private static final Marker field_5553 = MarkerManager.getMarker("SOUNDS");
	private static final Logger field_5559 = LogManager.getLogger();
	private static final Set<class_2960> field_5561 = Sets.<class_2960>newHashSet();
	private final class_1144 field_5552;
	private final class_315 field_5555;
	private class_1140.class_1141 field_5560;
	private boolean field_5563;
	private int field_5550;
	private final Map<String, class_1113> field_5565 = HashBiMap.create();
	private final Map<class_1113, String> field_5556 = ((BiMap)this.field_5565).inverse();
	private final Multimap<class_3419, String> field_5562 = HashMultimap.create();
	private final List<class_1117> field_5557 = Lists.<class_1117>newArrayList();
	private final Map<class_1113, Integer> field_5566 = Maps.<class_1113, Integer>newHashMap();
	private final Map<String, Integer> field_5554 = Maps.<String, Integer>newHashMap();
	private final List<class_1145> field_5558 = Lists.<class_1145>newArrayList();
	private final List<String> field_5564 = Lists.<String>newArrayList();
	private final List<class_1111> field_5551 = Lists.<class_1111>newArrayList();

	public class_1140(class_1144 arg, class_315 arg2) {
		this.field_5552 = arg;
		this.field_5555 = arg2;

		try {
			SoundSystemConfig.addLibrary(class_1138.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		} catch (SoundSystemException var4) {
			field_5559.error(field_5553, "Error linking with the LibraryJavaSound plug-in", (Throwable)var4);
		}
	}

	public void method_4837() {
		field_5561.clear();

		for (class_3414 lv : class_2378.field_11156) {
			class_2960 lv2 = lv.method_14833();
			if (this.field_5552.method_4869(lv2) == null) {
				field_5559.warn("Missing sound for event: {}", class_2378.field_11156.method_10221(lv));
				field_5561.add(lv2);
			}
		}

		this.method_4856();
		this.method_4846();
	}

	private synchronized void method_4846() {
		if (!this.field_5563) {
			try {
				Thread thread = new Thread(() -> {
					SoundSystemConfig.setLogger(new SoundSystemLogger() {
						@Override
						public void message(String string, int i) {
							if (!string.isEmpty()) {
								class_1140.field_5559.info(string);
							}
						}

						@Override
						public void importantMessage(String string, int i) {
							if (string.startsWith("Author:")) {
								class_1140.field_5559.info("SoundSystem {}", string);
							} else if (!string.isEmpty()) {
								class_1140.field_5559.warn(string);
							}
						}

						@Override
						public void errorMessage(String string, String string2, int i) {
							if (!string2.isEmpty()) {
								class_1140.field_5559.error("Error in class '{}'", string);
								class_1140.field_5559.error(string2);
							}
						}
					});
					this.field_5560 = new class_1140.class_1141();
					this.field_5563 = true;
					this.field_5560.setMasterVolume(this.field_5555.method_1630(class_3419.field_15250));
					Iterator<class_1111> iterator = this.field_5551.iterator();

					while (iterator.hasNext()) {
						class_1111 lv = (class_1111)iterator.next();
						this.method_4836(lv);
						iterator.remove();
					}

					field_5559.info(field_5553, "Sound engine started");
				}, "Sound Library Loader");
				thread.setUncaughtExceptionHandler(new class_140(field_5559));
				thread.start();
			} catch (RuntimeException var2) {
				field_5559.error(field_5553, "Error starting SoundSystem. Turning off sounds & music", (Throwable)var2);
				this.field_5555.method_1624(class_3419.field_15250, 0.0F);
				this.field_5555.method_1640();
			}
		}
	}

	private float method_4850(class_3419 arg) {
		return arg != null && arg != class_3419.field_15250 ? this.field_5555.method_1630(arg) : 1.0F;
	}

	public void method_4844(class_3419 arg, float f) {
		if (this.field_5563) {
			if (arg == class_3419.field_15250) {
				this.field_5560.setMasterVolume(f);
			} else {
				for (String string : this.field_5562.get(arg)) {
					class_1113 lv = (class_1113)this.field_5565.get(string);
					float g = this.method_4853(lv);
					if (g <= 0.0F) {
						this.method_4848(lv);
					} else {
						this.field_5560.setVolume(string, g);
					}
				}
			}
		}
	}

	public void method_4856() {
		if (this.field_5563) {
			this.method_4843();
			this.field_5560.cleanup();
			this.field_5563 = false;
		}
	}

	public void method_4843() {
		if (this.field_5563) {
			for (String string : this.field_5565.keySet()) {
				this.field_5560.stop(string);
			}

			this.field_5565.clear();
			this.field_5566.clear();
			this.field_5557.clear();
			this.field_5564.clear();
			this.field_5562.clear();
			this.field_5554.clear();
		}
	}

	public void method_4855(class_1145 arg) {
		this.field_5558.add(arg);
	}

	public void method_4847(class_1145 arg) {
		this.field_5558.remove(arg);
	}

	public void method_4857() {
		this.field_5550++;

		for (class_1117 lv : this.field_5557) {
			lv.method_16896();
			if (lv.method_4793()) {
				this.method_4848(lv);
			} else {
				String string = (String)this.field_5556.get(lv);
				this.field_5560.setVolume(string, this.method_4853(lv));
				this.field_5560.setPitch(string, this.method_4849(lv));
				this.field_5560.setPosition(string, lv.method_4784(), lv.method_4779(), lv.method_4778());
			}
		}

		Iterator<Entry<String, class_1113>> iterator = this.field_5565.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, class_1113> entry = (Entry<String, class_1113>)iterator.next();
			String string = (String)entry.getKey();
			class_1113 lv2 = (class_1113)entry.getValue();
			float f = this.field_5555.method_1630(lv2.method_4774());
			if (f <= 0.0F) {
				this.method_4848(lv2);
			}

			if (!this.field_5560.playing(string)) {
				int i = (Integer)this.field_5554.get(string);
				if (i <= this.field_5550) {
					int j = lv2.method_4780();
					if (lv2.method_4786() && j > 0) {
						this.field_5566.put(lv2, this.field_5550 + j);
					}

					iterator.remove();
					field_5559.debug(field_5553, "Removed channel {} because it's not playing anymore", string);
					this.field_5560.removeSource(string);
					this.field_5554.remove(string);

					try {
						this.field_5562.remove(lv2.method_4774(), string);
					} catch (RuntimeException var9) {
					}

					if (lv2 instanceof class_1117) {
						this.field_5557.remove(lv2);
					}
				}
			}
		}

		Iterator<Entry<class_1113, Integer>> iterator2 = this.field_5566.entrySet().iterator();

		while (iterator2.hasNext()) {
			Entry<class_1113, Integer> entry2 = (Entry<class_1113, Integer>)iterator2.next();
			if (this.field_5550 >= (Integer)entry2.getValue()) {
				class_1113 lv2x = (class_1113)entry2.getKey();
				if (lv2x instanceof class_1117) {
					((class_1117)lv2x).method_16896();
				}

				this.method_4854(lv2x);
				iterator2.remove();
			}
		}
	}

	public boolean method_4835(class_1113 arg) {
		if (!this.field_5563) {
			return false;
		} else {
			String string = (String)this.field_5556.get(arg);
			return string == null
				? false
				: this.field_5560.playing(string) || this.field_5554.containsKey(string) && (Integer)this.field_5554.get(string) <= this.field_5550;
		}
	}

	public void method_4848(class_1113 arg) {
		if (this.field_5563) {
			String string = (String)this.field_5556.get(arg);
			if (string != null) {
				this.field_5560.stop(string);
			}
		}
	}

	public void method_4854(class_1113 arg) {
		if (this.field_5563) {
			class_1146 lv = arg.method_4783(this.field_5552);
			class_2960 lv2 = arg.method_4775();
			if (lv == null) {
				if (field_5561.add(lv2)) {
					field_5559.warn(field_5553, "Unable to play unknown soundEvent: {}", lv2);
				}
			} else {
				if (!this.field_5558.isEmpty()) {
					for (class_1145 lv3 : this.field_5558) {
						lv3.method_4884(arg, lv);
					}
				}

				if (this.field_5560.getMasterVolume() <= 0.0F) {
					field_5559.debug(field_5553, "Skipped playing soundEvent: {}, master volume was zero", lv2);
				} else {
					class_1111 lv4 = arg.method_4776();
					if (lv4 == class_1144.field_5592) {
						if (field_5561.add(lv2)) {
							field_5559.warn(field_5553, "Unable to play empty soundEvent: {}", lv2);
						}
					} else {
						float f = arg.method_4781();
						float g = (float)lv4.method_4770();
						if (f > 1.0F) {
							g *= f;
						}

						class_3419 lv5 = arg.method_4774();
						float h = this.method_4853(arg);
						float i = this.method_4849(arg);
						if (h == 0.0F && !arg.method_4785()) {
							field_5559.debug(field_5553, "Skipped playing sound {}, volume was zero.", lv4.method_4767());
						} else {
							boolean bl = arg.method_4786() && arg.method_4780() == 0;
							String string = class_3532.method_15378(ThreadLocalRandom.current()).toString();
							class_2960 lv6 = lv4.method_4766();
							if (lv4.method_4769()) {
								this.field_5560
									.newStreamingSource(
										arg.method_4787(),
										string,
										method_4834(lv6),
										lv6.toString(),
										bl,
										arg.method_4784(),
										arg.method_4779(),
										arg.method_4778(),
										arg.method_4777().method_4788(),
										g
									);
							} else {
								this.field_5560
									.newSource(
										arg.method_4787(),
										string,
										method_4834(lv6),
										lv6.toString(),
										bl,
										arg.method_4784(),
										arg.method_4779(),
										arg.method_4778(),
										arg.method_4777().method_4788(),
										g
									);
							}

							field_5559.debug(field_5553, "Playing sound {} for event {} as channel {}", lv4.method_4767(), lv2, string);
							this.field_5560.setPitch(string, i);
							this.field_5560.setVolume(string, h);
							this.field_5560.play(string);
							this.field_5554.put(string, this.field_5550 + 20);
							this.field_5565.put(string, arg);
							this.field_5562.put(lv5, string);
							if (arg instanceof class_1117) {
								this.field_5557.add((class_1117)arg);
							}
						}
					}
				}
			}
		}
	}

	public void method_4851(class_1111 arg) {
		this.field_5551.add(arg);
	}

	private void method_4836(class_1111 arg) {
		class_2960 lv = arg.method_4766();
		field_5559.info(field_5553, "Preloading sound {}", lv);
		this.field_5560.loadSound(method_4834(lv), lv.toString());
	}

	private float method_4849(class_1113 arg) {
		return class_3532.method_15363(arg.method_4782(), 0.5F, 2.0F);
	}

	private float method_4853(class_1113 arg) {
		return class_3532.method_15363(arg.method_4781() * this.method_4850(arg.method_4774()), 0.0F, 1.0F);
	}

	public void method_4842() {
		for (Entry<String, class_1113> entry : this.field_5565.entrySet()) {
			String string = (String)entry.getKey();
			boolean bl = this.method_4835((class_1113)entry.getValue());
			if (bl) {
				field_5559.debug(field_5553, "Pausing channel {}", string);
				this.field_5560.pause(string);
				this.field_5564.add(string);
			}
		}
	}

	public void method_4845() {
		for (String string : this.field_5564) {
			field_5559.debug(field_5553, "Resuming channel {}", string);
			this.field_5560.play(string);
		}

		this.field_5564.clear();
	}

	public void method_4852(class_1113 arg, int i) {
		this.field_5566.put(arg, this.field_5550 + i);
	}

	private static URL method_4834(class_2960 arg) {
		String string = String.format("%s:%s:%s", "mcsounddomain", arg.method_12836(), arg.method_12832());
		URLStreamHandler uRLStreamHandler = new URLStreamHandler() {
			protected URLConnection openConnection(URL uRL) {
				return new URLConnection(uRL) {
					public void connect() {
					}

					public InputStream getInputStream() throws IOException {
						return class_310.method_1551().method_1478().method_14486(arg).method_14482();
					}
				};
			}
		};

		try {
			return new URL(null, string, uRLStreamHandler);
		} catch (MalformedURLException var4) {
			throw new Error("TODO: Sanely handle url exception! :D");
		}
	}

	public void method_4840(class_1657 arg, float f) {
		if (this.field_5563 && arg != null) {
			float g = class_3532.method_16439(f, arg.field_6004, arg.field_5965);
			float h = class_3532.method_16439(f, arg.field_5982, arg.field_6031);
			double d = class_3532.method_16436((double)f, arg.field_6014, arg.field_5987);
			double e = class_3532.method_16436((double)f, arg.field_6036, arg.field_6010) + (double)arg.method_5751();
			double i = class_3532.method_16436((double)f, arg.field_5969, arg.field_6035);
			float j = class_3532.method_15362((h + 90.0F) * (float) (Math.PI / 180.0));
			float k = class_3532.method_15374((h + 90.0F) * (float) (Math.PI / 180.0));
			float l = class_3532.method_15362(-g * (float) (Math.PI / 180.0));
			float m = class_3532.method_15374(-g * (float) (Math.PI / 180.0));
			float n = class_3532.method_15362((-g + 90.0F) * (float) (Math.PI / 180.0));
			float o = class_3532.method_15374((-g + 90.0F) * (float) (Math.PI / 180.0));
			float p = j * l;
			float r = k * l;
			float s = j * n;
			float u = k * n;
			this.field_5560.setListenerPosition((float)d, (float)e, (float)i);
			this.field_5560.setListenerOrientation(p, m, r, s, o, u);
		}
	}

	public void method_4838(@Nullable class_2960 arg, @Nullable class_3419 arg2) {
		if (arg2 != null) {
			for (String string : this.field_5562.get(arg2)) {
				class_1113 lv = (class_1113)this.field_5565.get(string);
				if (arg == null) {
					this.method_4848(lv);
				} else if (lv.method_4775().equals(arg)) {
					this.method_4848(lv);
				}
			}
		} else if (arg == null) {
			this.method_4843();
		} else {
			for (class_1113 lv2 : this.field_5565.values()) {
				if (lv2.method_4775().equals(arg)) {
					this.method_4848(lv2);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_1141 extends SoundSystem {
		private class_1141() {
		}

		@Override
		public boolean playing(String string) {
			synchronized (SoundSystemConfig.THREAD_SYNC) {
				if (this.soundLibrary == null) {
					return false;
				} else {
					Map<String, Source> map = this.soundLibrary.getSources();
					if (map == null) {
						return false;
					} else {
						Source source = (Source)map.get(string);
						return source == null ? false : source.playing() || source.paused() || source.preLoad;
					}
				}
			}
		}
	}
}
