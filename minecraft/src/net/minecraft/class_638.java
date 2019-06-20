package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_638 extends class_1937 {
	private final List<class_1297> field_17777 = Lists.<class_1297>newArrayList();
	private final Int2ObjectMap<class_1297> field_17778 = new Int2ObjectOpenHashMap<>();
	private final class_634 field_3727;
	private final class_761 field_17780;
	private final class_310 field_3729 = class_310.method_1551();
	private final List<class_742> field_18226 = Lists.<class_742>newArrayList();
	private int field_3730 = this.field_9229.nextInt(12000);
	private class_269 field_3733 = new class_269();
	private final Map<String, class_22> field_17675 = Maps.<String, class_22>newHashMap();

	public class_638(class_634 arg, class_1940 arg2, class_2874 arg3, int i, class_3695 arg4, class_761 arg5) {
		super(new class_31(arg2, "MpServer"), arg3, (argx, arg2x) -> new class_631((class_638)argx, i), arg4, true);
		this.field_3727 = arg;
		this.field_17780 = arg5;
		this.method_8554(new class_2338(8, 64, 8));
		this.method_8533();
		this.method_8543();
	}

	public void method_8441(BooleanSupplier booleanSupplier) {
		this.method_8621().method_11982();
		this.method_8560();
		this.method_16107().method_15396("blocks");
		this.field_9248.method_12127(booleanSupplier);
		this.method_2939();
		this.method_16107().method_15407();
	}

	public Iterable<class_1297> method_18112() {
		return Iterables.concat(this.field_17778.values(), this.field_17777);
	}

	public void method_18116() {
		class_3695 lv = this.method_16107();
		lv.method_15396("entities");
		lv.method_15396("global");

		for (int i = 0; i < this.field_17777.size(); i++) {
			class_1297 lv2 = (class_1297)this.field_17777.get(i);
			this.method_18472(arg -> {
				arg.field_6012++;
				arg.method_5773();
			}, lv2);
			if (lv2.field_5988) {
				this.field_17777.remove(i--);
			}
		}

		lv.method_15405("regular");
		ObjectIterator<Entry<class_1297>> objectIterator = this.field_17778.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<class_1297> entry = (Entry<class_1297>)objectIterator.next();
			class_1297 lv3 = (class_1297)entry.getValue();
			if (!lv3.method_5765()) {
				lv.method_15396("tick");
				if (!lv3.field_5988) {
					this.method_18472(this::method_18646, lv3);
				}

				lv.method_15407();
				lv.method_15396("remove");
				if (lv3.field_5988) {
					objectIterator.remove();
					this.method_18117(lv3);
				}

				lv.method_15407();
			}
		}

		lv.method_15407();
		this.method_18471();
		lv.method_15407();
	}

	public void method_18646(class_1297 arg) {
		if (arg instanceof class_1657 || this.method_2935().method_12125(arg)) {
			arg.field_6038 = arg.field_5987;
			arg.field_5971 = arg.field_6010;
			arg.field_5989 = arg.field_6035;
			arg.field_5982 = arg.field_6031;
			arg.field_6004 = arg.field_5965;
			if (arg.field_6016 || arg.method_7325()) {
				arg.field_6012++;
				this.method_16107().method_15400(() -> class_2378.field_11145.method_10221(arg.method_5864()).toString());
				arg.method_5773();
				this.method_16107().method_15407();
			}

			this.method_18648(arg);
			if (arg.field_6016) {
				for (class_1297 lv : arg.method_5685()) {
					this.method_18647(arg, lv);
				}
			}
		}
	}

	public void method_18647(class_1297 arg, class_1297 arg2) {
		if (arg2.field_5988 || arg2.method_5854() != arg) {
			arg2.method_5848();
		} else if (arg2 instanceof class_1657 || this.method_2935().method_12125(arg2)) {
			arg2.field_6038 = arg2.field_5987;
			arg2.field_5971 = arg2.field_6010;
			arg2.field_5989 = arg2.field_6035;
			arg2.field_5982 = arg2.field_6031;
			arg2.field_6004 = arg2.field_5965;
			if (arg2.field_6016) {
				arg2.field_6012++;
				arg2.method_5842();
			}

			this.method_18648(arg2);
			if (arg2.field_6016) {
				for (class_1297 lv : arg2.method_5685()) {
					this.method_18647(arg2, lv);
				}
			}
		}
	}

	public void method_18648(class_1297 arg) {
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

	public void method_18110(class_2818 arg) {
		this.field_18139.addAll(arg.method_12214().values());
		this.field_9248.method_12130().method_15557(arg.method_12004(), false);
	}

	@Override
	public boolean method_8393(int i, int j) {
		return true;
	}

	private void method_2939() {
		if (this.field_3729.field_1724 != null) {
			if (this.field_3730 > 0) {
				this.field_3730--;
			} else {
				class_2338 lv = new class_2338(this.field_3729.field_1724);
				class_2338 lv2 = lv.method_10069(4 * (this.field_9229.nextInt(3) - 1), 4 * (this.field_9229.nextInt(3) - 1), 4 * (this.field_9229.nextInt(3) - 1));
				double d = lv.method_10262(lv2);
				if (d >= 4.0 && d <= 256.0) {
					class_2680 lv3 = this.method_8320(lv2);
					if (lv3.method_11588() && this.method_8624(lv2, 0) <= this.field_9229.nextInt(8) && this.method_8314(class_1944.field_9284, lv2) <= 0) {
						this.method_8486(
							(double)lv2.method_10263() + 0.5,
							(double)lv2.method_10264() + 0.5,
							(double)lv2.method_10260() + 0.5,
							class_3417.field_14564,
							class_3419.field_15256,
							0.7F,
							0.8F + this.field_9229.nextFloat() * 0.2F,
							false
						);
						this.field_3730 = this.field_9229.nextInt(12000) + 6000;
					}
				}
			}
		}
	}

	public int method_18120() {
		return this.field_17778.size();
	}

	public void method_18108(class_1538 arg) {
		this.field_17777.add(arg);
	}

	public void method_18107(int i, class_742 arg) {
		this.method_18114(i, arg);
		this.field_18226.add(arg);
	}

	public void method_2942(int i, class_1297 arg) {
		this.method_18114(i, arg);
	}

	private void method_18114(int i, class_1297 arg) {
		this.method_2945(i);
		this.field_17778.put(i, arg);
		this.method_2935()
			.method_2857(class_3532.method_15357(arg.field_5987 / 16.0), class_3532.method_15357(arg.field_6035 / 16.0), class_2806.field_12803, true)
			.method_12002(arg);
	}

	public void method_2945(int i) {
		class_1297 lv = this.field_17778.remove(i);
		if (lv != null) {
			lv.method_5650();
			this.method_18117(lv);
		}
	}

	private void method_18117(class_1297 arg) {
		arg.method_18375();
		if (arg.field_6016) {
			this.method_8497(arg.field_6024, arg.field_5980).method_12203(arg);
		}

		this.field_18226.remove(arg);
	}

	public void method_18115(class_2818 arg) {
		for (Entry<class_1297> entry : this.field_17778.int2ObjectEntrySet()) {
			class_1297 lv = (class_1297)entry.getValue();
			int i = class_3532.method_15357(lv.field_5987 / 16.0);
			int j = class_3532.method_15357(lv.field_6035 / 16.0);
			if (i == arg.method_12004().field_9181 && j == arg.method_12004().field_9180) {
				arg.method_12002(lv);
			}
		}
	}

	@Nullable
	@Override
	public class_1297 method_8469(int i) {
		return this.field_17778.get(i);
	}

	public void method_2937(class_2338 arg, class_2680 arg2) {
		this.method_8652(arg, arg2, 19);
	}

	@Override
	public void method_8525() {
		this.field_3727.method_2872().method_10747(new class_2588("multiplayer.status.quitting"));
	}

	public void method_2941(int i, int j, int k) {
		int l = 32;
		Random random = new Random();
		class_1799 lv = this.field_3729.field_1724.method_6047();
		boolean bl = this.field_3729.field_1761.method_2920() == class_1934.field_9220
			&& !lv.method_7960()
			&& lv.method_7909() == class_2246.field_10499.method_8389();
		class_2338.class_2339 lv2 = new class_2338.class_2339();

		for (int m = 0; m < 667; m++) {
			this.method_2943(i, j, k, 16, random, bl, lv2);
			this.method_2943(i, j, k, 32, random, bl, lv2);
		}
	}

	public void method_2943(int i, int j, int k, int l, Random random, boolean bl, class_2338.class_2339 arg) {
		int m = i + this.field_9229.nextInt(l) - this.field_9229.nextInt(l);
		int n = j + this.field_9229.nextInt(l) - this.field_9229.nextInt(l);
		int o = k + this.field_9229.nextInt(l) - this.field_9229.nextInt(l);
		arg.method_10103(m, n, o);
		class_2680 lv = this.method_8320(arg);
		lv.method_11614().method_9496(lv, this, arg, random);
		class_3610 lv2 = this.method_8316(arg);
		if (!lv2.method_15769()) {
			lv2.method_15768(this, arg, random);
			class_2394 lv3 = lv2.method_15766();
			if (lv3 != null && this.field_9229.nextInt(10) == 0) {
				boolean bl2 = class_2248.method_20045(lv, this, arg, class_2350.field_11033);
				class_2338 lv4 = arg.method_10074();
				this.method_2938(lv4, this.method_8320(lv4), lv3, bl2);
			}
		}

		if (bl && lv.method_11614() == class_2246.field_10499) {
			this.method_8406(class_2398.field_11235, (double)((float)m + 0.5F), (double)((float)n + 0.5F), (double)((float)o + 0.5F), 0.0, 0.0, 0.0);
		}
	}

	private void method_2938(class_2338 arg, class_2680 arg2, class_2394 arg3, boolean bl) {
		if (arg2.method_11618().method_15769()) {
			class_265 lv = arg2.method_11628(this, arg);
			double d = lv.method_1105(class_2350.class_2351.field_11052);
			if (d < 1.0) {
				if (bl) {
					this.method_2932(
						(double)arg.method_10263(),
						(double)(arg.method_10263() + 1),
						(double)arg.method_10260(),
						(double)(arg.method_10260() + 1),
						(double)(arg.method_10264() + 1) - 0.05,
						arg3
					);
				}
			} else if (!arg2.method_11602(class_3481.field_15490)) {
				double e = lv.method_1091(class_2350.class_2351.field_11052);
				if (e > 0.0) {
					this.method_2948(arg, arg3, lv, (double)arg.method_10264() + e - 0.05);
				} else {
					class_2338 lv2 = arg.method_10074();
					class_2680 lv3 = this.method_8320(lv2);
					class_265 lv4 = lv3.method_11628(this, lv2);
					double f = lv4.method_1105(class_2350.class_2351.field_11052);
					if (f < 1.0 && lv3.method_11618().method_15769()) {
						this.method_2948(arg, arg3, lv, (double)arg.method_10264() - 0.05);
					}
				}
			}
		}
	}

	private void method_2948(class_2338 arg, class_2394 arg2, class_265 arg3, double d) {
		this.method_2932(
			(double)arg.method_10263() + arg3.method_1091(class_2350.class_2351.field_11048),
			(double)arg.method_10263() + arg3.method_1105(class_2350.class_2351.field_11048),
			(double)arg.method_10260() + arg3.method_1091(class_2350.class_2351.field_11051),
			(double)arg.method_10260() + arg3.method_1105(class_2350.class_2351.field_11051),
			d,
			arg2
		);
	}

	private void method_2932(double d, double e, double f, double g, double h, class_2394 arg) {
		this.method_8406(
			arg, class_3532.method_16436(this.field_9229.nextDouble(), d, e), h, class_3532.method_16436(this.field_9229.nextDouble(), f, g), 0.0, 0.0, 0.0
		);
	}

	public void method_2936() {
		ObjectIterator<Entry<class_1297>> objectIterator = this.field_17778.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<class_1297> entry = (Entry<class_1297>)objectIterator.next();
			class_1297 lv = (class_1297)entry.getValue();
			if (lv.field_5988) {
				objectIterator.remove();
				this.method_18117(lv);
			}
		}
	}

	@Override
	public class_129 method_8538(class_128 arg) {
		class_129 lv = super.method_8538(arg);
		lv.method_577("Server brand", () -> this.field_3729.field_1724.method_3135());
		lv.method_577("Server type", () -> this.field_3729.method_1576() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
		return lv;
	}

	@Override
	public void method_8465(@Nullable class_1657 arg, double d, double e, double f, class_3414 arg2, class_3419 arg3, float g, float h) {
		if (arg == this.field_3729.field_1724) {
			this.method_8486(d, e, f, arg2, arg3, g, h, false);
		}
	}

	@Override
	public void method_8449(@Nullable class_1657 arg, class_1297 arg2, class_3414 arg3, class_3419 arg4, float f, float g) {
		if (arg == this.field_3729.field_1724) {
			this.field_3729.method_1483().method_4873(new class_1106(arg3, arg4, arg2));
		}
	}

	public void method_2947(class_2338 arg, class_3414 arg2, class_3419 arg3, float f, float g, boolean bl) {
		this.method_8486((double)arg.method_10263() + 0.5, (double)arg.method_10264() + 0.5, (double)arg.method_10260() + 0.5, arg2, arg3, f, g, bl);
	}

	@Override
	public void method_8486(double d, double e, double f, class_3414 arg, class_3419 arg2, float g, float h, boolean bl) {
		double i = this.field_3729.field_1773.method_19418().method_19326().method_1028(d, e, f);
		class_1109 lv = new class_1109(arg, arg2, g, h, (float)d, (float)e, (float)f);
		if (bl && i > 100.0) {
			double j = Math.sqrt(i) / 40.0;
			this.field_3729.method_1483().method_4872(lv, (int)(j * 20.0));
		} else {
			this.field_3729.method_1483().method_4873(lv);
		}
	}

	@Override
	public void method_8547(double d, double e, double f, double g, double h, double i, @Nullable class_2487 arg) {
		this.field_3729.field_1713.method_3058(new class_677.class_681(this, d, e, f, g, h, i, this.field_3729.field_1713, arg));
	}

	@Override
	public void method_8522(class_2596<?> arg) {
		this.field_3727.method_2883(arg);
	}

	@Override
	public class_1863 method_8433() {
		return this.field_3727.method_2877();
	}

	public void method_2944(class_269 arg) {
		this.field_3733 = arg;
	}

	@Override
	public void method_8435(long l) {
		if (l < 0L) {
			l = -l;
			this.method_8450().method_20746(class_1928.field_19396).method_20758(false, null);
		} else {
			this.method_8450().method_20746(class_1928.field_19396).method_20758(true, null);
		}

		super.method_8435(l);
	}

	@Override
	public class_1951<class_2248> method_8397() {
		return class_1925.method_8339();
	}

	@Override
	public class_1951<class_3611> method_8405() {
		return class_1925.method_8339();
	}

	public class_631 method_2935() {
		return (class_631)super.method_8398();
	}

	@Nullable
	@Override
	public class_22 method_17891(String string) {
		return (class_22)this.field_17675.get(string);
	}

	@Override
	public void method_17890(class_22 arg) {
		this.field_17675.put(arg.method_76(), arg);
	}

	@Override
	public int method_17889() {
		return 0;
	}

	@Override
	public class_269 method_8428() {
		return this.field_3733;
	}

	@Override
	public class_3505 method_8514() {
		return this.field_3727.method_2867();
	}

	@Override
	public void method_8413(class_2338 arg, class_2680 arg2, class_2680 arg3, int i) {
		this.field_17780.method_8570(this, arg, arg2, arg3, i);
	}

	@Override
	public void method_16109(class_2338 arg) {
		this.field_17780.method_18146(arg.method_10263(), arg.method_10264(), arg.method_10260(), arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	public void method_18113(int i, int j, int k) {
		this.field_17780.method_18145(i, j, k);
	}

	@Override
	public void method_8517(int i, class_2338 arg, int j) {
		this.field_17780.method_8569(i, arg, j);
	}

	@Override
	public void method_8474(int i, class_2338 arg, int j) {
		this.field_17780.method_8564(i, arg, j);
	}

	@Override
	public void method_8444(@Nullable class_1657 arg, int i, class_2338 arg2, int j) {
		try {
			this.field_17780.method_8567(arg, i, arg2, j);
		} catch (Throwable var8) {
			class_128 lv = class_128.method_560(var8, "Playing level event");
			class_129 lv2 = lv.method_562("Level event being played");
			lv2.method_578("Block coordinates", class_129.method_582(arg2));
			lv2.method_578("Event source", arg);
			lv2.method_578("Event type", i);
			lv2.method_578("Event data", j);
			throw new class_148(lv);
		}
	}

	@Override
	public void method_8406(class_2394 arg, double d, double e, double f, double g, double h, double i) {
		this.field_17780.method_8568(arg, arg.method_10295().method_10299(), d, e, f, g, h, i);
	}

	@Override
	public void method_8466(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.field_17780.method_8568(arg, arg.method_10295().method_10299() || bl, d, e, f, g, h, i);
	}

	@Override
	public void method_8494(class_2394 arg, double d, double e, double f, double g, double h, double i) {
		this.field_17780.method_8563(arg, false, true, d, e, f, g, h, i);
	}

	@Override
	public void method_17452(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.field_17780.method_8563(arg, arg.method_10295().method_10299() || bl, true, d, e, f, g, h, i);
	}

	@Override
	public List<class_742> method_18456() {
		return this.field_18226;
	}
}
