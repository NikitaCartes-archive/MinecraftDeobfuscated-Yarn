package net.minecraft;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
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

@Environment(EnvType.CLIENT)
public class class_1140 {
	private static final Marker field_5553 = MarkerManager.getMarker("SOUNDS");
	private static final Logger field_5559 = LogManager.getLogger();
	private static final Set<class_2960> field_5561 = Sets.<class_2960>newHashSet();
	private final class_1144 field_5552;
	private final class_315 field_5555;
	private boolean field_5563;
	private final class_4225 field_18945 = new class_4225();
	private final class_4227 field_18946 = this.field_18945.method_19665();
	private final class_4237 field_18947;
	private final class_4238 field_18948 = new class_4238();
	private final class_4235 field_18949 = new class_4235(this.field_18945, this.field_18948);
	private int field_5550;
	private final Map<class_1113, class_4235.class_4236> field_18950 = Maps.<class_1113, class_4235.class_4236>newHashMap();
	private final Multimap<class_3419, class_1113> field_18951 = HashMultimap.create();
	private final List<class_1117> field_5557 = Lists.<class_1117>newArrayList();
	private final Map<class_1113, Integer> field_5566 = Maps.<class_1113, Integer>newHashMap();
	private final Map<class_1113, Integer> field_18952 = Maps.<class_1113, Integer>newHashMap();
	private final List<class_1145> field_5558 = Lists.<class_1145>newArrayList();
	private final List<class_1111> field_5551 = Lists.<class_1111>newArrayList();

	public class_1140(class_1144 arg, class_315 arg2, class_3300 arg3) {
		this.field_5552 = arg;
		this.field_5555 = arg2;
		this.field_18947 = new class_4237(arg3);
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
				this.field_18945.method_19661();
				this.field_18946.method_19673();
				this.field_18946.method_19670(this.field_5555.method_1630(class_3419.field_15250));
				this.field_18947.method_19741(this.field_5551).thenRun(this.field_5551::clear);
				this.field_5563 = true;
				field_5559.info(field_5553, "Sound engine started");
			} catch (RuntimeException var2) {
				field_5559.error(field_5553, "Error starting SoundSystem. Turning off sounds & music", (Throwable)var2);
			}
		}
	}

	private float method_4850(class_3419 arg) {
		return arg != null && arg != class_3419.field_15250 ? this.field_5555.method_1630(arg) : 1.0F;
	}

	public void method_4844(class_3419 arg, float f) {
		if (this.field_5563) {
			if (arg == class_3419.field_15250) {
				this.field_18946.method_19670(f);
			} else {
				this.field_18950.forEach((argx, arg2) -> {
					float fx = this.method_4853(argx);
					arg2.method_19735(argxx -> {
						if (fx <= 0.0F) {
							argxx.method_19655();
						} else {
							argxx.method_19647(fx);
						}
					});
				});
			}
		}
	}

	public void method_4856() {
		if (this.field_5563) {
			this.method_4843();
			this.field_18947.method_19738();
			this.field_18945.method_19664();
			this.field_5563 = false;
		}
	}

	public void method_19753(class_1113 arg) {
		if (this.field_5563) {
			class_4235.class_4236 lv = (class_4235.class_4236)this.field_18950.get(arg);
			if (lv != null) {
				lv.method_19735(class_4224::method_19655);
			}
		}
	}

	public void method_4843() {
		if (this.field_5563) {
			this.field_18948.method_19763();
			this.field_18950.values().forEach(arg -> arg.method_19735(class_4224::method_19655));
			this.field_18950.clear();
			this.field_18949.method_19728();
			this.field_5566.clear();
			this.field_5557.clear();
			this.field_18951.clear();
			this.field_18952.clear();
		}
	}

	public void method_4855(class_1145 arg) {
		this.field_5558.add(arg);
	}

	public void method_4847(class_1145 arg) {
		this.field_5558.remove(arg);
	}

	public void method_20185(boolean bl) {
		if (!bl) {
			this.method_4857();
		}

		this.field_18949.method_19722();
	}

	private void method_4857() {
		this.field_5550++;

		for (class_1117 lv : this.field_5557) {
			lv.method_16896();
			if (lv.method_4793()) {
				this.method_19753(lv);
			} else {
				float f = this.method_4853(lv);
				float g = this.method_4849(lv);
				class_243 lv2 = new class_243((double)lv.method_4784(), (double)lv.method_4779(), (double)lv.method_4778());
				class_4235.class_4236 lv3 = (class_4235.class_4236)this.field_18950.get(lv);
				if (lv3 != null) {
					lv3.method_19735(arg2 -> {
						arg2.method_19647(f);
						arg2.method_19639(g);
						arg2.method_19641(lv2);
					});
				}
			}
		}

		Iterator<Entry<class_1113, class_4235.class_4236>> iterator = this.field_18950.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<class_1113, class_4235.class_4236> entry = (Entry<class_1113, class_4235.class_4236>)iterator.next();
			class_4235.class_4236 lv4 = (class_4235.class_4236)entry.getValue();
			class_1113 lv5 = (class_1113)entry.getKey();
			float h = this.field_5555.method_1630(lv5.method_4774());
			if (h <= 0.0F) {
				lv4.method_19735(class_4224::method_19655);
				iterator.remove();
			} else if (lv4.method_19732()) {
				int i = (Integer)this.field_18952.get(lv5);
				if (i <= this.field_5550) {
					int j = lv5.method_4780();
					if (lv5.method_4786() && j > 0) {
						this.field_5566.put(lv5, this.field_5550 + j);
					}

					iterator.remove();
					field_5559.debug(field_5553, "Removed channel {} because it's not playing anymore", lv4);
					this.field_18952.remove(lv5);

					try {
						this.field_18951.remove(lv5.method_4774(), lv5);
					} catch (RuntimeException var9) {
					}

					if (lv5 instanceof class_1117) {
						this.field_5557.remove(lv5);
					}
				}
			}
		}

		Iterator<Entry<class_1113, Integer>> iterator2 = this.field_5566.entrySet().iterator();

		while (iterator2.hasNext()) {
			Entry<class_1113, Integer> entry2 = (Entry<class_1113, Integer>)iterator2.next();
			if (this.field_5550 >= (Integer)entry2.getValue()) {
				class_1113 lv5 = (class_1113)entry2.getKey();
				if (lv5 instanceof class_1117) {
					((class_1117)lv5).method_16896();
				}

				this.method_4854(lv5);
				iterator2.remove();
			}
		}
	}

	public boolean method_4835(class_1113 arg) {
		if (!this.field_5563) {
			return false;
		} else {
			return this.field_18952.containsKey(arg) && this.field_18952.get(arg) <= this.field_5550 ? true : this.field_18950.containsKey(arg);
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

				if (this.field_18946.method_19669() <= 0.0F) {
					field_5559.debug(field_5553, "Skipped playing soundEvent: {}, master volume was zero", lv2);
				} else {
					class_1111 lv4 = arg.method_4776();
					if (lv4 == class_1144.field_5592) {
						if (field_5561.add(lv2)) {
							field_5559.warn(field_5553, "Unable to play empty soundEvent: {}", lv2);
						}
					} else {
						float f = arg.method_4781();
						float g = Math.max(f, 1.0F) * (float)lv4.method_4770();
						class_3419 lv5 = arg.method_4774();
						float h = this.method_4853(arg);
						float i = this.method_4849(arg);
						class_1113.class_1114 lv6 = arg.method_4777();
						boolean bl = arg.method_4787();
						if (h == 0.0F && !arg.method_4785()) {
							field_5559.debug(field_5553, "Skipped playing sound {}, volume was zero.", lv4.method_4767());
						} else {
							boolean bl2 = arg.method_4786() && arg.method_4780() == 0;
							class_243 lv7 = new class_243((double)arg.method_4784(), (double)arg.method_4779(), (double)arg.method_4778());
							class_4235.class_4236 lv8 = this.field_18949.method_19723(lv4.method_4769() ? class_4225.class_4105.field_18353 : class_4225.class_4105.field_18352);
							field_5559.debug(field_5553, "Playing sound {} for event {}", lv4.method_4767(), lv2);
							this.field_18952.put(arg, this.field_5550 + 20);
							this.field_18950.put(arg, lv8);
							this.field_18951.put(lv5, arg);
							lv8.method_19735(arg3 -> {
								arg3.method_19639(i);
								arg3.method_19647(h);
								if (lv6 == class_1113.class_1114.field_5476) {
									arg3.method_19651(g);
								} else {
									arg3.method_19657();
								}

								arg3.method_19645(bl2);
								arg3.method_19641(lv7);
								arg3.method_19649(bl);
							});
							if (!lv4.method_4769()) {
								this.field_18947.method_19743(lv4.method_4766()).thenAccept(arg2 -> lv8.method_19735(arg2x -> {
										arg2x.method_19642(arg2);
										arg2x.method_19650();
									}));
							} else {
								this.field_18947.method_19744(lv4.method_4766()).thenAccept(arg2 -> lv8.method_19735(arg2x -> {
										arg2x.method_19643(arg2);
										arg2x.method_19650();
									}));
							}

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

	private float method_4849(class_1113 arg) {
		return class_3532.method_15363(arg.method_4782(), 0.5F, 2.0F);
	}

	private float method_4853(class_1113 arg) {
		return class_3532.method_15363(arg.method_4781() * this.method_4850(arg.method_4774()), 0.0F, 1.0F);
	}

	public void method_19761() {
		if (this.field_5563) {
			this.field_18949.method_19727(stream -> stream.forEach(class_4224::method_19653));
		}
	}

	public void method_19762() {
		if (this.field_5563) {
			this.field_18949.method_19727(stream -> stream.forEach(class_4224::method_19654));
		}
	}

	public void method_4852(class_1113 arg, int i) {
		this.field_5566.put(arg, this.field_5550 + i);
	}

	public void method_4840(class_4184 arg) {
		if (this.field_5563 && arg.method_19332()) {
			class_243 lv = arg.method_19326();
			class_243 lv2 = arg.method_19335();
			class_243 lv3 = arg.method_19336();
			this.field_18948.execute(() -> {
				this.field_18946.method_19671(lv);
				this.field_18946.method_19672(lv2, lv3);
			});
		}
	}

	public void method_4838(@Nullable class_2960 arg, @Nullable class_3419 arg2) {
		if (arg2 != null) {
			for (class_1113 lv : this.field_18951.get(arg2)) {
				if (arg == null || lv.method_4775().equals(arg)) {
					this.method_19753(lv);
				}
			}
		} else if (arg == null) {
			this.method_4843();
		} else {
			for (class_1113 lvx : this.field_18950.keySet()) {
				if (lvx.method_4775().equals(arg)) {
					this.method_19753(lvx);
				}
			}
		}
	}

	public String method_20304() {
		return this.field_18945.method_20296();
	}
}
