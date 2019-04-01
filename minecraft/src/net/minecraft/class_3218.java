package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3218 extends class_1937 {
	private static final Logger field_13952 = LogManager.getLogger();
	private final List<class_1297> field_17913 = Lists.<class_1297>newArrayList();
	private final Int2ObjectMap<class_1297> field_17915 = new Int2ObjectOpenHashMap<>();
	private final Map<UUID, class_1297> field_13960 = Maps.<UUID, class_1297>newHashMap();
	private final Queue<class_1297> field_18260 = Queues.<class_1297>newArrayDeque();
	private final List<class_3222> field_18261 = Lists.<class_3222>newArrayList();
	boolean field_18264;
	private final MinecraftServer field_13959;
	private final class_29 field_17709;
	public boolean field_13957;
	private boolean field_13955;
	private int field_13948;
	private final class_1946 field_13956;
	private final class_1949<class_2248> field_13949 = new class_1949<>(
		this,
		argx -> argx == null || argx.method_9564().method_11588(),
		class_2378.field_11146::method_10221,
		class_2378.field_11146::method_10223,
		this::method_14189
	);
	private final class_1949<class_3611> field_13951 = new class_1949<>(
		this, argx -> argx == null || argx == class_3612.field_15906, class_2378.field_11154::method_10221, class_2378.field_11154::method_10223, this::method_14171
	);
	private final Set<class_1408> field_18262 = Sets.<class_1408>newHashSet();
	protected final class_3767 field_18811;
	protected final class_1419 field_13958 = new class_1419(this);
	private final ObjectLinkedOpenHashSet<class_1919> field_13950 = new ObjectLinkedOpenHashSet<>();
	private boolean field_13953;
	@Nullable
	private final class_3990 field_18263;

	public class_3218(MinecraftServer minecraftServer, Executor executor, class_29 arg, class_31 arg2, class_2874 arg3, class_3695 arg4, class_3949 arg5) {
		super(
			arg2,
			arg3,
			(arg3x, arg4x) -> new class_3215(
					(class_3218)arg3x,
					arg.method_132(),
					arg.method_130(),
					arg.method_134(),
					executor,
					arg4x.method_12443(),
					minecraftServer.method_3760().method_14568(),
					minecraftServer.method_3760().method_14568() - 2,
					arg5,
					() -> minecraftServer.method_3847(class_2874.field_13072).method_17983()
				),
			arg4,
			false
		);
		this.field_17709 = arg;
		this.field_13959 = minecraftServer;
		this.field_13956 = new class_1946(this);
		this.method_8533();
		this.method_8543();
		this.method_8621().method_11973(minecraftServer.method_3749());
		this.field_18811 = this.method_17983().method_17924(() -> new class_3767(this), class_3767.method_16533(this.field_9247));
		if (!minecraftServer.method_3724()) {
			this.method_8401().method_193(minecraftServer.method_3790());
		}

		this.field_18263 = this.field_9247.method_12460() == class_2874.field_13072 ? new class_3990(this) : null;
	}

	public void method_18765(BooleanSupplier booleanSupplier) {
		class_3695 lv = this.method_16107();
		this.field_13953 = true;
		lv.method_15396("world border");
		this.method_8621().method_11982();
		lv.method_15405("weather");
		boolean bl = this.method_8419();
		if (this.field_9247.method_12451()) {
			if (this.method_8450().method_8355("doWeatherCycle")) {
				int i = this.field_9232.method_155();
				int j = this.field_9232.method_145();
				int k = this.field_9232.method_190();
				boolean bl2 = this.field_9232.method_203();
				boolean bl3 = this.field_9232.method_156();
				if (i > 0) {
					i--;
					j = bl2 ? 0 : 1;
					k = bl3 ? 0 : 1;
					bl2 = false;
					bl3 = false;
				} else {
					if (j > 0) {
						if (--j == 0) {
							bl2 = !bl2;
						}
					} else if (bl2) {
						j = this.field_9229.nextInt(12000) + 3600;
					} else {
						j = this.field_9229.nextInt(168000) + 12000;
					}

					if (k > 0) {
						if (--k == 0) {
							bl3 = !bl3;
						}
					} else if (bl3) {
						k = this.field_9229.nextInt(12000) + 12000;
					} else {
						k = this.field_9229.nextInt(168000) + 12000;
					}
				}

				this.field_9232.method_173(j);
				this.field_9232.method_164(k);
				this.field_9232.method_167(i);
				this.field_9232.method_147(bl2);
				this.field_9232.method_157(bl3);
			}

			this.field_9251 = this.field_9234;
			if (this.field_9232.method_203()) {
				this.field_9234 = (float)((double)this.field_9234 + 0.01);
			} else {
				this.field_9234 = (float)((double)this.field_9234 - 0.01);
			}

			this.field_9234 = class_3532.method_15363(this.field_9234, 0.0F, 1.0F);
			this.field_9253 = this.field_9235;
			if (this.field_9232.method_156()) {
				this.field_9235 = (float)((double)this.field_9235 + 0.01);
			} else {
				this.field_9235 = (float)((double)this.field_9235 - 0.01);
			}

			this.field_9235 = class_3532.method_15363(this.field_9235, 0.0F, 1.0F);
		}

		if (this.field_9253 != this.field_9235) {
			this.field_13959.method_3760().method_14589(new class_2668(7, this.field_9235), this.field_9247.method_12460());
		}

		if (this.field_9251 != this.field_9234) {
			this.field_13959.method_3760().method_14589(new class_2668(8, this.field_9234), this.field_9247.method_12460());
		}

		if (bl != this.method_8419()) {
			if (bl) {
				this.field_13959.method_3760().method_14581(new class_2668(2, 0.0F));
			} else {
				this.field_13959.method_3760().method_14581(new class_2668(1, 0.0F));
			}

			this.field_13959.method_3760().method_14581(new class_2668(7, this.field_9235));
			this.field_13959.method_3760().method_14581(new class_2668(8, this.field_9234));
		}

		if (this.method_8401().method_152() && this.method_8407() != class_1267.field_5807) {
			this.method_8401().method_208(class_1267.field_5807);
		}

		if (this.field_13955 && this.field_18261.stream().noneMatch(arg -> !arg.method_7325() && !arg.method_7276())) {
			this.field_13955 = false;
			if (this.method_8450().method_8355("doDaylightCycle")) {
				long l = this.field_9232.method_217() + 24000L;
				this.method_8435(l - l % 24000L);
			}

			this.field_18261.stream().filter(class_1309::method_6113).forEach(arg -> arg.method_7358(false, false, true));
			if (this.method_8450().method_8355("doWeatherCycle")) {
				this.method_14195();
			}
		}

		this.method_8533();
		this.method_8560();
		lv.method_15405("chunkSource");
		this.method_14178().method_12127(booleanSupplier);
		lv.method_15405("tickPending");
		if (this.field_9232.method_153() != class_1942.field_9266) {
			this.field_13949.method_8670();
			this.field_13951.method_8670();
		}

		lv.method_15405("village");
		this.field_13958.method_6445();
		lv.method_15405("portalForcer");
		this.field_13956.method_8656(this.method_8510());
		lv.method_15405("raid");
		this.field_18811.method_16539();
		if (this.field_18263 != null) {
			this.field_18263.method_18015();
		}

		lv.method_15405("blockEvents");
		this.method_14192();
		this.field_13953 = false;
		lv.method_15405("entities");
		boolean bl4 = !this.field_18261.isEmpty() || !this.method_17984().isEmpty();
		if (bl4) {
			this.method_14197();
		}

		if (bl4 || this.field_13948++ < 300) {
			this.field_9247.method_12461();
			lv.method_15396("global");

			for (int j = 0; j < this.field_17913.size(); j++) {
				class_1297 lv2 = (class_1297)this.field_17913.get(j);
				this.method_18472(arg -> {
					arg.field_6012++;
					arg.method_5773();
				}, lv2);
				if (lv2.field_5988) {
					this.field_17913.remove(j--);
				}
			}

			lv.method_15405("regular");
			this.field_18264 = true;
			ObjectIterator<Entry<class_1297>> objectIterator = this.field_17915.int2ObjectEntrySet().iterator();

			while (objectIterator.hasNext()) {
				Entry<class_1297> entry = (Entry<class_1297>)objectIterator.next();
				class_1297 lv3 = (class_1297)entry.getValue();
				class_1297 lv4 = lv3.method_5854();
				if (!this.field_13959.method_3796() && (lv3 instanceof class_1429 || lv3 instanceof class_1480)) {
					lv3.method_5650();
				}

				if (!this.field_13959.method_3736() && lv3 instanceof class_1655) {
					lv3.method_5650();
				}

				if (lv4 != null) {
					if (!lv4.field_5988 && lv4.method_5626(lv3)) {
						continue;
					}

					lv3.method_5848();
				}

				lv.method_15396("tick");
				if (!lv3.field_5988 && !(lv3 instanceof class_1508)) {
					this.method_18472(this::method_18762, lv3);
				}

				lv.method_15407();
				lv.method_15396("remove");
				if (lv3.field_5988) {
					this.method_18780(lv3);
					objectIterator.remove();
					this.method_18772(lv3);
				}

				lv.method_15407();
			}

			this.field_18264 = false;

			class_1297 lv2;
			while ((lv2 = (class_1297)this.field_18260.poll()) != null) {
				this.method_18778(lv2);
			}

			lv.method_15407();
			this.method_18471();
		}

		lv.method_15407();
	}

	public void method_18203(class_2818 arg, int i) {
		class_1923 lv = arg.method_12004();
		boolean bl = this.method_8419();
		int j = lv.method_8326();
		int k = lv.method_8328();
		class_3695 lv2 = this.method_16107();
		lv2.method_15396("thunder");
		if (bl && this.method_8546() && this.field_9229.nextInt(100000) == 0) {
			class_2338 lv3 = this.method_18210(this.method_8536(j, 0, k, 15));
			if (this.method_8520(lv3)) {
				class_1266 lv4 = this.method_8404(lv3);
				boolean bl2 = this.method_8450().method_8355("doMobSpawning") && this.field_9229.nextDouble() < (double)lv4.method_5457() * 0.01;
				if (bl2) {
					class_1506 lv5 = class_1299.field_6075.method_5883(this);
					lv5.method_6813(true);
					lv5.method_5614(0);
					lv5.method_5814((double)lv3.method_10263(), (double)lv3.method_10264(), (double)lv3.method_10260());
					this.method_8649(lv5);
				}

				this.method_8416(new class_1538(this, (double)lv3.method_10263() + 0.5, (double)lv3.method_10264(), (double)lv3.method_10260() + 0.5, bl2));
			}
		}

		lv2.method_15405("iceandsnow");
		if (this.field_9229.nextInt(16) == 0) {
			class_2338 lv3 = this.method_8598(class_2902.class_2903.field_13197, this.method_8536(j, 0, k, 15));
			class_2338 lv6 = lv3.method_10074();
			class_1959 lv7 = this.method_8310(lv3);
			if (lv7.method_8705(this, lv6)) {
				this.method_8501(lv6, class_2246.field_10295.method_9564());
			}

			if (bl && lv7.method_8696(this, lv3)) {
				this.method_8501(lv3, class_2246.field_10477.method_9564());
			}

			if (bl && this.method_8310(lv6).method_8694() == class_1959.class_1963.field_9382) {
				this.method_8320(lv6).method_11614().method_9504(this, lv6);
			}
		}

		lv2.method_15405("tickBlocks");
		if (i > 0) {
			for (class_2826 lv8 : arg.method_12006()) {
				if (lv8 != class_2818.field_12852 && lv8.method_12262()) {
					int l = lv8.method_12259();

					for (int m = 0; m < i; m++) {
						class_2338 lv9 = this.method_8536(j, l, k, 15);
						lv2.method_15396("randomTick");
						class_2680 lv10 = lv8.method_12254(lv9.method_10263() - j, lv9.method_10264() - l, lv9.method_10260() - k);
						if (lv10.method_11616()) {
							lv10.method_11624(this, lv9, this.field_9229);
						}

						class_3610 lv11 = lv8.method_12255(lv9.method_10263() - j, lv9.method_10264() - l, lv9.method_10260() - k);
						if (lv11.method_15773()) {
							lv11.method_15757(this, lv9, this.field_9229);
						}

						lv2.method_15407();
					}
				}
			}
		}

		lv2.method_15407();
	}

	protected class_2338 method_18210(class_2338 arg) {
		class_2338 lv = this.method_8598(class_2902.class_2903.field_13197, arg);
		class_238 lv2 = new class_238(lv, new class_2338(lv.method_10263(), this.method_8322(), lv.method_10260())).method_1014(3.0);
		List<class_1309> list = this.method_8390(class_1309.class, lv2, argx -> argx != null && argx.method_5805() && this.method_8311(argx.method_5704()));
		if (!list.isEmpty()) {
			return ((class_1309)list.get(this.field_9229.nextInt(list.size()))).method_5704();
		} else {
			if (lv.method_10264() == -1) {
				lv = lv.method_10086(2);
			}

			return lv;
		}
	}

	public boolean method_14177() {
		return this.field_13953;
	}

	public void method_8448() {
		this.field_13955 = false;
		if (!this.field_18261.isEmpty()) {
			int i = 0;
			int j = 0;

			for (class_3222 lv : this.field_18261) {
				if (lv.method_7325()) {
					i++;
				} else if (lv.method_6113()) {
					j++;
				}
			}

			this.field_13955 = j > 0 && j >= this.field_18261.size() - i;
		}
	}

	public class_2995 method_14170() {
		return this.field_13959.method_3845();
	}

	private void method_14195() {
		this.field_9232.method_164(0);
		this.field_9232.method_157(false);
		this.field_9232.method_173(0);
		this.field_9232.method_147(false);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_8513() {
		if (this.field_9232.method_144() <= 0) {
			this.field_9232.method_213(this.method_8615() + 1);
		}

		int i = this.field_9232.method_215();
		int j = this.field_9232.method_166();
		int k = 0;

		while (this.method_8495(new class_2338(i, 0, j)).method_11588()) {
			i += this.field_9229.nextInt(8) - this.field_9229.nextInt(8);
			j += this.field_9229.nextInt(8) - this.field_9229.nextInt(8);
			if (++k == 10000) {
				break;
			}
		}

		this.field_9232.method_212(i);
		this.field_9232.method_154(j);
	}

	public void method_14197() {
		this.field_13948 = 0;
	}

	private void method_14171(class_1954<class_3611> arg) {
		class_3610 lv = this.method_8316(arg.field_9322);
		if (lv.method_15772() == arg.method_8683()) {
			lv.method_15770(this, arg.field_9322);
		}
	}

	private void method_14189(class_1954<class_2248> arg) {
		class_2680 lv = this.method_8320(arg.field_9322);
		if (lv.method_11614() == arg.method_8683()) {
			lv.method_11585(this, arg.field_9322, this.field_9229);
		}
	}

	public void method_18762(class_1297 arg) {
		if (arg instanceof class_1657 || this.method_14178().method_12125(arg)) {
			arg.field_6038 = arg.field_5987;
			arg.field_5971 = arg.field_6010;
			arg.field_5989 = arg.field_6035;
			arg.field_5982 = arg.field_6031;
			arg.field_6004 = arg.field_5965;
			if (arg.field_6016) {
				arg.field_6012++;
				this.method_16107().method_15400(() -> class_2378.field_11145.method_10221(arg.method_5864()).toString());
				arg.method_5773();
				this.method_16107().method_15407();
			}

			this.method_18767(arg);
			if (arg.field_6016) {
				for (class_1297 lv : arg.method_5685()) {
					this.method_18763(arg, lv);
				}
			}
		}
	}

	public void method_18763(class_1297 arg, class_1297 arg2) {
		if (arg2.field_5988 || arg2.method_5854() != arg) {
			arg2.method_5848();
		} else if (arg2 instanceof class_1657 || this.method_14178().method_12125(arg2)) {
			arg2.field_6038 = arg2.field_5987;
			arg2.field_5971 = arg2.field_6010;
			arg2.field_5989 = arg2.field_6035;
			arg2.field_5982 = arg2.field_6031;
			arg2.field_6004 = arg2.field_5965;
			if (arg2.field_6016) {
				arg2.field_6012++;
				arg2.method_5842();
			}

			this.method_18767(arg2);
			if (arg2.field_6016) {
				for (class_1297 lv : arg2.method_5685()) {
					this.method_18763(arg2, lv);
				}
			}
		}
	}

	public void method_18767(class_1297 arg) {
		this.method_16107().method_15396("chunkCheck");
		int i = class_3532.method_15357(arg.field_5987 / 16.0);
		int j = class_3532.method_15357(arg.field_6010 / 16.0);
		int k = class_3532.method_15357(arg.field_6035 / 16.0);
		if (!arg.field_6016 || arg.field_6024 != i || arg.field_5959 != j || arg.field_5980 != k) {
			if (arg.field_6016 && this.method_8393(arg.field_6024, arg.field_5980)) {
				this.method_8497(arg.field_6024, arg.field_5980).method_12219(arg, arg.field_5959);
			}

			if (!arg.method_5754() && !this.method_8393(i, k)) {
				arg.field_6016 = false;
			} else {
				this.method_8497(i, k).method_12002(arg);
			}
		}

		this.method_16107().method_15407();
	}

	@Override
	public boolean method_8505(class_1657 arg, class_2338 arg2) {
		return !this.field_13959.method_3785(this, arg2, arg) && this.method_8621().method_11952(arg2);
	}

	public void method_8414(class_1940 arg) {
		if (!this.field_9247.method_12448()) {
			this.field_9232.method_187(class_2338.field_10980.method_10086(this.field_9248.method_12129().method_12100()));
		} else if (this.field_9232.method_153() == class_1942.field_9266) {
			this.field_9232.method_187(class_2338.field_10980.method_10084());
		} else {
			class_1966 lv = this.field_9248.method_12129().method_12098();
			List<class_1959> list = lv.method_8759();
			Random random = new Random(this.method_8412());
			class_2338 lv2 = lv.method_8762(0, 0, 256, list, random);
			class_1923 lv3 = lv2 == null ? new class_1923(0, 0) : new class_1923(lv2);
			if (lv2 == null) {
				field_13952.warn("Unable to find spawn biome");
			}

			boolean bl = false;

			for (class_2248 lv4 : class_3481.field_15478.method_15138()) {
				if (lv.method_8761().contains(lv4.method_9564())) {
					bl = true;
					break;
				}
			}

			this.field_9232.method_187(lv3.method_8323().method_10069(8, this.field_9248.method_12129().method_12100(), 8));
			int i = 0;
			int j = 0;
			int k = 0;
			int l = -1;
			int m = 32;

			for (int n = 0; n < 1024; n++) {
				if (i > -16 && i <= 16 && j > -16 && j <= 16) {
					class_2338 lv5 = this.field_9247.method_12452(new class_1923(lv3.field_9181 + i, lv3.field_9180 + j), bl);
					if (lv5 != null) {
						this.field_9232.method_187(lv5);
						break;
					}
				}

				if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
					int o = k;
					k = -l;
					l = o;
				}

				i += k;
				j += l;
			}

			if (arg.method_8581()) {
				this.method_14187();
			}
		}
	}

	protected void method_14187() {
		class_2953 lv = class_3031.field_13526;

		for (int i = 0; i < 10; i++) {
			int j = this.field_9232.method_215() + this.field_9229.nextInt(6) - this.field_9229.nextInt(6);
			int k = this.field_9232.method_166() + this.field_9229.nextInt(6) - this.field_9229.nextInt(6);
			class_2338 lv2 = this.method_8598(class_2902.class_2903.field_13203, new class_2338(j, 0, k)).method_10084();
			if (lv.method_12817(this, (class_2794<? extends class_2888>)this.field_9248.method_12129(), this.field_9229, lv2, class_3037.field_13603)) {
				break;
			}
		}
	}

	@Nullable
	public class_2338 method_14169() {
		return this.field_9247.method_12466();
	}

	public void method_14176(@Nullable class_3536 arg, boolean bl, boolean bl2) throws class_1939 {
		class_3215 lv = this.method_14178();
		if (!bl2) {
			if (arg != null) {
				arg.method_15412(new class_2588("menu.savingLevel"));
			}

			this.method_14188();
			if (arg != null) {
				arg.method_15414(new class_2588("menu.savingChunks"));
			}

			lv.method_17298(bl);
		}
	}

	protected void method_14188() throws class_1939 {
		this.method_8468();
		this.field_9247.method_12450();
		this.method_14178().method_17981().method_125();
	}

	public List<class_1297> method_18198(@Nullable class_1299<?> arg, Predicate<? super class_1297> predicate) {
		List<class_1297> list = Lists.<class_1297>newArrayList();
		class_3215 lv = this.method_14178();

		for (class_1297 lv2 : this.field_17915.values()) {
			if ((arg == null || lv2.method_5864() == arg)
				&& lv.method_12123(class_3532.method_15357(lv2.field_5987) >> 4, class_3532.method_15357(lv2.field_6035) >> 4)
				&& predicate.test(lv2)) {
				list.add(lv2);
			}
		}

		return list;
	}

	public List<class_1510> method_18776() {
		List<class_1510> list = Lists.<class_1510>newArrayList();

		for (class_1297 lv : this.field_17915.values()) {
			if (lv instanceof class_1510 && lv.method_5805()) {
				list.add((class_1510)lv);
			}
		}

		return list;
	}

	public List<class_3222> method_18766(Predicate<? super class_3222> predicate) {
		List<class_3222> list = Lists.<class_3222>newArrayList();

		for (class_3222 lv : this.field_18261) {
			if (predicate.test(lv)) {
				list.add(lv);
			}
		}

		return list;
	}

	@Nullable
	public class_3222 method_18779() {
		List<class_3222> list = this.method_18766(class_1309::method_5805);
		return list.isEmpty() ? null : (class_3222)list.get(this.field_9229.nextInt(list.size()));
	}

	public Object2IntMap<class_1311> method_18219() {
		Object2IntMap<class_1311> object2IntMap = new Object2IntOpenHashMap<>();

		for (class_1297 lv : this.field_17915.values()) {
			if (!(lv instanceof class_1308) || !((class_1308)lv).method_5947()) {
				class_1311 lv2 = lv.method_5864().method_5891();
				if (lv2 != class_1311.field_17715) {
					object2IntMap.computeInt(lv2, (arg, integer) -> 1 + (integer == null ? 0 : integer));
				}
			}
		}

		return object2IntMap;
	}

	@Override
	public boolean method_8649(class_1297 arg) {
		return this.method_14175(arg);
	}

	public boolean method_18768(class_1297 arg) {
		return this.method_14175(arg);
	}

	public void method_18769(class_1297 arg) {
		boolean bl = arg.field_5983;
		arg.field_5983 = true;
		this.method_18768(arg);
		arg.field_5983 = bl;
		this.method_18767(arg);
	}

	public void method_18207(class_3222 arg) {
		this.method_18771(arg);
		this.method_18767(arg);
	}

	public void method_18211(class_3222 arg) {
		this.method_18771(arg);
		this.method_18767(arg);
	}

	public void method_18213(class_3222 arg) {
		this.method_18771(arg);
	}

	public void method_18215(class_3222 arg) {
		this.method_18771(arg);
	}

	private void method_18771(class_3222 arg) {
		class_1297 lv = (class_1297)this.field_13960.get(arg.method_5667());
		if (lv != null) {
			field_13952.warn("Force-added player with duplicate UUID {}", arg.method_5667().toString());
			lv.method_18375();
			this.method_18770((class_3222)lv);
		}

		this.field_18261.add(arg);
		this.method_8448();
		class_2791 lv2 = this.method_8402(
			class_3532.method_15357(arg.field_5987 / 16.0), class_3532.method_15357(arg.field_6035 / 16.0), class_2806.field_12803, true
		);
		if (lv2 instanceof class_2818) {
			lv2.method_12002(arg);
		}

		this.method_18778(arg);
	}

	private boolean method_14175(class_1297 arg) {
		if (arg.field_5988) {
			field_13952.warn("Tried to add entity {} but it was marked as removed already", class_1299.method_5890(arg.method_5864()));
			return false;
		} else if (this.method_18777(arg)) {
			return false;
		} else {
			class_2791 lv = this.method_8402(
				class_3532.method_15357(arg.field_5987 / 16.0), class_3532.method_15357(arg.field_6035 / 16.0), class_2806.field_12803, arg.field_5983
			);
			if (!(lv instanceof class_2818)) {
				return false;
			} else {
				lv.method_12002(arg);
				this.method_18778(arg);
				return true;
			}
		}
	}

	public void method_18214(class_1297 arg) {
		if (!this.method_18777(arg)) {
			this.method_18778(arg);
		}
	}

	private boolean method_18777(class_1297 arg) {
		class_1297 lv = (class_1297)this.field_13960.get(arg.method_5667());
		if (lv == null) {
			return false;
		} else {
			field_13952.warn("Keeping entity {} that already exists with UUID {}", class_1299.method_5890(lv.method_5864()), arg.method_5667().toString());
			return true;
		}
	}

	public void method_18764(class_2818 arg) {
		this.field_18139.addAll(arg.method_12214().values());
		class_3509[] var2 = arg.method_12215();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; var4++) {
			for (class_1297 lv2 : var2[var4]) {
				if (!(lv2 instanceof class_3222)) {
					if (this.field_18264) {
						throw new IllegalStateException("Removing entity while ticking!");
					}

					this.field_17915.remove(lv2.method_5628());
					this.method_18772(lv2);
				}
			}
		}
	}

	public void method_18772(class_1297 arg) {
		if (arg instanceof class_1510) {
			for (class_1508 lv : ((class_1510)arg).method_5690()) {
				lv.method_5650();
			}
		}

		this.field_13960.remove(arg.method_5667());
		this.method_14178().method_18753(arg);
		if (arg instanceof class_3222) {
			class_3222 lv2 = (class_3222)arg;
			this.field_18261.remove(lv2);
		}

		this.method_14170().method_1150(arg);
		if (arg instanceof class_1308) {
			this.field_18262.remove(((class_1308)arg).method_5942());
		}
	}

	private void method_18778(class_1297 arg) {
		if (this.field_18264) {
			this.field_18260.add(arg);
		} else {
			this.field_17915.put(arg.method_5628(), arg);
			if (arg instanceof class_1510) {
				for (class_1508 lv : ((class_1510)arg).method_5690()) {
					this.field_17915.put(lv.method_5628(), lv);
				}
			}

			this.field_13960.put(arg.method_5667(), arg);
			this.method_14178().method_18755(arg);
			if (arg instanceof class_1308) {
				this.field_18262.add(((class_1308)arg).method_5942());
			}
		}
	}

	public void method_18774(class_1297 arg) {
		if (this.field_18264) {
			throw new IllegalStateException("Removing entity while ticking!");
		} else {
			this.method_18780(arg);
			this.field_17915.remove(arg.method_5628());
			this.method_18772(arg);
		}
	}

	private void method_18780(class_1297 arg) {
		class_2791 lv = this.method_8402(arg.field_6024, arg.field_5980, class_2806.field_12803, false);
		if (lv instanceof class_2818) {
			((class_2818)lv).method_12203(arg);
		}
	}

	public void method_18770(class_3222 arg) {
		arg.method_5650();
		this.method_8448();
		this.method_18774(arg);
	}

	public void method_8416(class_1538 arg) {
		this.field_17913.add(arg);
		this.field_13959.method_3760().method_14605(null, arg.field_5987, arg.field_6010, arg.field_6035, 512.0, this.field_9247.method_12460(), new class_2607(arg));
	}

	@Override
	public void method_8517(int i, class_2338 arg, int j) {
		for (class_3222 lv : this.field_13959.method_3760().method_14571()) {
			if (lv != null && lv.field_6002 == this && lv.method_5628() != i) {
				double d = (double)arg.method_10263() - lv.field_5987;
				double e = (double)arg.method_10264() - lv.field_6010;
				double f = (double)arg.method_10260() - lv.field_6035;
				if (d * d + e * e + f * f < 1024.0) {
					lv.field_13987.method_14364(new class_2620(i, arg, j));
				}
			}
		}
	}

	@Override
	public void method_8465(@Nullable class_1657 arg, double d, double e, double f, class_3414 arg2, class_3419 arg3, float g, float h) {
		this.field_13959
			.method_3760()
			.method_14605(arg, d, e, f, g > 1.0F ? (double)(16.0F * g) : 16.0, this.field_9247.method_12460(), new class_2767(arg2, arg3, d, e, f, g, h));
	}

	@Override
	public void method_8449(@Nullable class_1657 arg, class_1297 arg2, class_3414 arg3, class_3419 arg4, float f, float g) {
		this.field_13959
			.method_3760()
			.method_14605(
				arg,
				arg2.field_5987,
				arg2.field_6010,
				arg2.field_6035,
				f > 1.0F ? (double)(16.0F * f) : 16.0,
				this.field_9247.method_12460(),
				new class_2765(arg3, arg4, arg2, f, g)
			);
	}

	@Override
	public void method_8474(int i, class_2338 arg, int j) {
		this.field_13959.method_3760().method_14581(new class_2673(i, arg, j, true));
	}

	@Override
	public void method_8444(@Nullable class_1657 arg, int i, class_2338 arg2, int j) {
		this.field_13959
			.method_3760()
			.method_14605(
				arg,
				(double)arg2.method_10263(),
				(double)arg2.method_10264(),
				(double)arg2.method_10260(),
				64.0,
				this.field_9247.method_12460(),
				new class_2673(i, arg2, j, false)
			);
	}

	@Override
	public void method_8413(class_2338 arg, class_2680 arg2, class_2680 arg3, int i) {
		this.method_14178().method_14128(arg);
		class_265 lv = arg2.method_11628(this, arg);
		class_265 lv2 = arg3.method_11628(this, arg);
		if (class_259.method_1074(lv, lv2, class_247.field_16892)) {
			for (class_1408 lv3 : this.field_18262) {
				if (!lv3.method_6343()) {
					lv3.method_18053(arg);
				}
			}
		}
	}

	@Override
	public void method_8421(class_1297 arg, byte b) {
		this.method_14178().method_18751(arg, new class_2663(arg, b));
	}

	public class_3215 method_14178() {
		return (class_3215)super.method_8398();
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<class_2818> method_16177(int i, int j, boolean bl) {
		return this.method_14178().method_17299(i, j, class_2806.field_12803, bl).thenApply(either -> either.map(arg -> (class_2818)arg, arg -> null));
	}

	@Override
	public class_1927 method_8454(@Nullable class_1297 arg, class_1282 arg2, double d, double e, double f, float g, boolean bl, class_1927.class_4179 arg3) {
		class_1927 lv = new class_1927(this, arg, d, e, f, g, bl, arg3);
		if (arg2 != null) {
			lv.method_8345(arg2);
		}

		lv.method_8348();
		lv.method_8350(false);
		if (arg3 == class_1927.class_4179.field_18685) {
			lv.method_8352();
		}

		for (class_3222 lv2 : this.field_18261) {
			if (lv2.method_5649(d, e, f) < 4096.0) {
				lv2.field_13987.method_14364(new class_2664(d, e, f, g, lv.method_8346(), (class_243)lv.method_8351().get(lv2)));
			}
		}

		return lv;
	}

	@Override
	public void method_8427(class_2338 arg, class_2248 arg2, int i, int j) {
		this.field_13950.add(new class_1919(arg, arg2, i, j));
	}

	private void method_14192() {
		while (!this.field_13950.isEmpty()) {
			class_1919 lv = this.field_13950.removeFirst();
			if (this.method_14174(lv)) {
				this.field_13959
					.method_3760()
					.method_14605(
						null,
						(double)lv.method_8306().method_10263(),
						(double)lv.method_8306().method_10264(),
						(double)lv.method_8306().method_10260(),
						64.0,
						this.field_9247.method_12460(),
						new class_2623(lv.method_8306(), lv.method_8309(), lv.method_8307(), lv.method_8308())
					);
			}
		}
	}

	private boolean method_14174(class_1919 arg) {
		class_2680 lv = this.method_8320(arg.method_8306());
		return lv.method_11614() == arg.method_8309() ? lv.method_11583(this, arg.method_8306(), arg.method_8307(), arg.method_8308()) : false;
	}

	@Override
	public void close() throws IOException {
		this.field_9248.close();
		super.close();
	}

	public class_1949<class_2248> method_14196() {
		return this.field_13949;
	}

	public class_1949<class_3611> method_14179() {
		return this.field_13951;
	}

	@Nonnull
	@Override
	public MinecraftServer method_8503() {
		return this.field_13959;
	}

	public class_1946 method_14173() {
		return this.field_13956;
	}

	public class_3485 method_14183() {
		return this.field_17709.method_134();
	}

	public <T extends class_2394> int method_14199(T arg, double d, double e, double f, int i, double g, double h, double j, double k) {
		class_2675 lv = new class_2675(arg, false, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		int l = 0;

		for (int m = 0; m < this.field_18261.size(); m++) {
			class_3222 lv2 = (class_3222)this.field_18261.get(m);
			if (this.method_14191(lv2, false, d, e, f, lv)) {
				l++;
			}
		}

		return l;
	}

	public <T extends class_2394> boolean method_14166(
		class_3222 arg, T arg2, boolean bl, double d, double e, double f, int i, double g, double h, double j, double k
	) {
		class_2596<?> lv = new class_2675(arg2, bl, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		return this.method_14191(arg, bl, d, e, f, lv);
	}

	private boolean method_14191(class_3222 arg, boolean bl, double d, double e, double f, class_2596<?> arg2) {
		if (arg.method_14220() != this) {
			return false;
		} else {
			class_2338 lv = arg.method_5704();
			if (lv.method_19769(new class_243(d, e, f), bl ? 512.0 : 32.0)) {
				arg.field_13987.method_14364(arg2);
				return true;
			} else {
				return false;
			}
		}
	}

	@Nullable
	@Override
	public class_1297 method_8469(int i) {
		return this.field_17915.get(i);
	}

	@Nullable
	public class_1297 method_14190(UUID uUID) {
		return (class_1297)this.field_13960.get(uUID);
	}

	@Nullable
	@Override
	public class_2338 method_8487(String string, class_2338 arg, int i, boolean bl) {
		return this.method_14178().method_12129().method_12103(this, string, arg, i, bl);
	}

	@Override
	public class_1863 method_8433() {
		return this.field_13959.method_3772();
	}

	@Override
	public class_3505 method_8514() {
		return this.field_13959.method_3801();
	}

	@Override
	public void method_8516(long l) {
		super.method_8516(l);
		this.field_9232.method_143().method_988(this.field_13959, l);
	}

	@Override
	public boolean method_8458() {
		return this.field_13957;
	}

	public void method_8468() throws class_1939 {
		this.field_17709.method_137();
	}

	public class_29 method_17982() {
		return this.field_17709;
	}

	public class_26 method_17983() {
		return this.method_14178().method_17981();
	}

	@Nullable
	@Override
	public class_22 method_17891(String string) {
		return this.method_8503().method_3847(class_2874.field_13072).method_17983().method_120(() -> new class_22(string), string);
	}

	@Override
	public void method_17890(class_22 arg) {
		this.method_8503().method_3847(class_2874.field_13072).method_17983().method_123(arg);
	}

	@Override
	public int method_17889() {
		return this.method_8503().method_3847(class_2874.field_13072).method_17983().<class_3978>method_17924(class_3978::new, "idcounts").method_17920();
	}

	@Override
	public void method_8554(class_2338 arg) {
		class_1923 lv = new class_1923(new class_2338(this.field_9232.method_215(), 0, this.field_9232.method_166()));
		super.method_8554(arg);
		this.method_14178().method_17300(class_3230.field_14030, lv, 11, class_3902.field_17274);
		this.method_14178().method_17297(class_3230.field_14030, new class_1923(arg), 11, class_3902.field_17274);
	}

	public LongSet method_17984() {
		class_1932 lv = this.method_17983().method_120(class_1932::new, "chunks");
		return (LongSet)(lv != null ? LongSets.unmodifiable(lv.method_8375()) : LongSets.EMPTY_SET);
	}

	public boolean method_17988(int i, int j, boolean bl) {
		class_1932 lv = this.method_17983().method_17924(class_1932::new, "chunks");
		class_1923 lv2 = new class_1923(i, j);
		long l = lv2.method_8324();
		boolean bl2;
		if (bl) {
			bl2 = lv.method_8375().add(l);
			if (bl2) {
				this.method_8497(i, j);
			}
		} else {
			bl2 = lv.method_8375().remove(l);
		}

		lv.method_78(bl2);
		if (bl2) {
			this.method_14178().method_12124(lv2, bl);
		}

		return bl2;
	}

	@Override
	public List<class_3222> method_18456() {
		return this.field_18261;
	}

	@Override
	public void method_19282(class_2338 arg, class_2680 arg2, class_2680 arg3) {
		class_2338 lv = arg.method_10062();
		Optional<class_4158> optional = class_4158.method_19516(arg2);
		Optional<class_4158> optional2 = class_4158.method_19516(arg3);
		if (!Objects.equals(optional, optional2)) {
			optional.ifPresent(arg2x -> this.method_8503().execute(() -> {
					this.method_19494().method_19112(lv);
					class_4209.method_19777(this, lv);
				}));
			optional2.ifPresent(arg2x -> this.method_8503().execute(() -> {
					this.method_19494().method_19115(lv, arg2x);
					class_4209.method_19776(this, lv);
				}));
		}
	}

	public class_4153 method_19494() {
		return this.method_14178().method_19493();
	}

	public boolean method_19500(class_2338 arg) {
		return this.method_19497(arg, 1);
	}

	public boolean method_19497(class_2338 arg, int i) {
		return i > 4 ? false : this.method_19498(class_4076.method_18682(arg)) <= i;
	}

	public int method_19498(class_4076 arg) {
		return this.method_19494().method_19118(arg);
	}

	public class_3767 method_19495() {
		return this.field_18811;
	}

	@Nullable
	public class_3765 method_19502(class_2338 arg) {
		return this.field_18811.method_19209(arg);
	}

	public boolean method_19503(class_2338 arg) {
		return this.method_19502(arg) != null;
	}

	public void method_19496(class_4151 arg, class_1297 arg2, class_4094 arg3) {
		arg3.method_18870(arg, arg2);
	}
}
