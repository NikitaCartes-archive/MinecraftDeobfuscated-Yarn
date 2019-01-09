package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3218 extends class_1937 {
	private static final Logger field_13952 = LogManager.getLogger();
	private final MinecraftServer field_13959;
	private final class_3208 field_13954;
	private final Map<UUID, class_1297> field_13960 = Maps.<UUID, class_1297>newHashMap();
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
	protected final class_1419 field_13958 = new class_1419(this);
	private final ObjectLinkedOpenHashSet<class_1919> field_13950 = new ObjectLinkedOpenHashSet<>();
	private boolean field_13953;

	public class_3218(
		MinecraftServer minecraftServer, Executor executor, class_30 arg, class_37 arg2, class_31 arg3, class_2874 arg4, class_3695 arg5, class_3949 arg6
	) {
		super(
			arg,
			arg2,
			arg3,
			arg4,
			(arg3x, arg4x) -> new class_3215(arg3x, arg.method_135(arg4x), executor, arg4x.method_12443(), minecraftServer.method_3760().method_14568(), arg6),
			arg5,
			false
		);
		this.field_13959 = minecraftServer;
		this.field_13954 = new class_3208(this);
		this.field_13956 = new class_1946(this);
		this.method_8451();
		this.method_8543();
		this.method_8621().method_11973(minecraftServer.method_3749());
	}

	public class_3218 method_14185() {
		String string = class_3767.method_16533(this.field_9247);
		class_3767 lv = this.method_8648(this.field_9247.method_12460(), class_3767::new, string);
		if (lv == null) {
			this.field_16642 = new class_3767(this);
			this.method_8647(this.field_9247.method_12460(), string, this.field_16642);
		} else {
			this.field_16642 = lv;
			this.field_16642.method_16530(this);
		}

		String string2 = class_1418.method_6434(this.field_9247);
		class_1418 lv2 = this.method_8648(class_2874.field_13072, class_1418::new, string2);
		if (lv2 == null) {
			this.field_9254 = new class_1418(this);
			this.method_8647(class_2874.field_13072, string2, this.field_9254);
		} else {
			this.field_9254 = lv2;
			this.field_9254.method_6433(this);
		}

		this.field_9254.method_16471();
		class_273 lv3 = this.method_8648(class_2874.field_13072, class_273::new, "scoreboard");
		if (lv3 == null) {
			lv3 = new class_273();
			this.method_8647(class_2874.field_13072, "scoreboard", lv3);
		}

		lv3.method_1218(this.field_13959.method_3845());
		this.field_13959.method_3845().method_12935(new class_16(lv3));
		this.method_8621().method_11978(this.field_9232.method_204(), this.field_9232.method_139());
		this.method_8621().method_11955(this.field_9232.method_202());
		this.method_8621().method_11981(this.field_9232.method_178());
		this.method_8621().method_11967(this.field_9232.method_227());
		this.method_8621().method_11975(this.field_9232.method_161());
		if (this.field_9232.method_183() > 0L) {
			this.method_8621().method_11957(this.field_9232.method_206(), this.field_9232.method_159(), this.field_9232.method_183());
		} else {
			this.method_8621().method_11969(this.field_9232.method_206());
		}

		return this;
	}

	@Override
	public void method_8441(BooleanSupplier booleanSupplier) {
		this.field_13953 = true;
		super.method_8441(booleanSupplier);
		if (this.method_8401().method_152() && this.method_8407() != class_1267.field_5807) {
			this.method_8401().method_208(class_1267.field_5807);
		}

		if (this.method_14172()) {
			if (this.method_8450().method_8355("doDaylightCycle")) {
				long l = this.field_9232.method_217() + 24000L;
				this.method_8435(l - l % 24000L);
			}

			this.method_14200();
		}

		int i = this.method_8533(1.0F);
		if (i != this.method_8594()) {
			this.method_8417(i);
		}

		this.method_8560();
		this.method_16107().method_15396("chunkSource");
		this.method_14178().method_12127(booleanSupplier);
		this.method_16107().method_15405("tickPending");
		this.method_14181();
		this.method_16107().method_15405("village");
		this.field_9254.method_6440();
		this.field_13958.method_6445();
		this.method_16107().method_15405("portalForcer");
		this.field_13956.method_8656(this.method_8510());
		this.method_16107().method_15405("raid");
		this.field_16642.method_16539();
		this.method_16107().method_15407();
		this.method_14192();
		this.field_13953 = false;
	}

	public boolean method_14177() {
		return this.field_13953;
	}

	@Override
	public void method_8448() {
		this.field_13955 = false;
		if (!this.field_9228.isEmpty()) {
			int i = 0;
			int j = 0;

			for (class_1657 lv : this.field_9228) {
				if (lv.method_7325()) {
					i++;
				} else if (lv.method_6113()) {
					j++;
				}
			}

			this.field_13955 = j > 0 && j >= this.field_9228.size() - i;
		}
	}

	public class_2995 method_14170() {
		return this.field_13959.method_3845();
	}

	protected void method_14200() {
		this.field_13955 = false;

		for (class_1657 lv : (List)this.field_9228.stream().filter(class_1657::method_6113).collect(Collectors.toList())) {
			lv.method_7358(false, false, true);
		}

		if (this.method_8450().method_8355("doWeatherCycle")) {
			this.method_14195();
		}
	}

	private void method_14195() {
		this.field_9232.method_164(0);
		this.field_9232.method_157(false);
		this.field_9232.method_173(0);
		this.field_9232.method_147(false);
	}

	public boolean method_14172() {
		if (this.field_13955 && !this.field_9236) {
			for (class_1657 lv : this.field_9228) {
				if (!lv.method_7325() && !lv.method_7276()) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
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

	@Override
	public void method_8429() {
		if (this.field_9228.isEmpty() && !this.method_8551()) {
			if (this.field_13948++ >= 300) {
				return;
			}
		} else {
			this.method_14197();
		}

		this.field_9247.method_12461();
		super.method_8429();
	}

	@Override
	protected void method_8541() {
		super.method_8541();
		this.method_16107().method_15405("players");

		for (int i = 0; i < this.field_9228.size(); i++) {
			class_1297 lv = (class_1297)this.field_9228.get(i);
			class_1297 lv2 = lv.method_5854();
			if (lv2 != null) {
				if (!lv2.field_5988 && lv2.method_5626(lv)) {
					continue;
				}

				lv.method_5848();
			}

			this.method_16107().method_15396("tick");
			if (!lv.field_5988) {
				try {
					this.method_8552(lv);
				} catch (Throwable var7) {
					class_128 lv3 = class_128.method_560(var7, "Ticking player");
					class_129 lv4 = lv3.method_562("Player being ticked");
					lv.method_5819(lv4);
					throw new class_148(lv3);
				}
			}

			this.method_16107().method_15407();
			this.method_16107().method_15396("remove");
			if (lv.field_5988) {
				int j = lv.field_6024;
				int k = lv.field_5980;
				if (lv.field_6016 && this.method_8393(j, k)) {
					this.method_8497(j, k).method_12203(lv);
				}

				this.field_9240.remove(lv);
				this.method_8539(lv);
			}

			this.method_16107().method_15407();
		}
	}

	public void method_14197() {
		this.field_13948 = 0;
	}

	public void method_14181() {
		if (this.field_9232.method_153() != class_1942.field_9266) {
			this.field_13949.method_8670();
			this.field_13951.method_8670();
		}
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

	@Override
	public void method_8553(class_1297 arg, boolean bl) {
		if (!this.method_14182() && (arg instanceof class_1429 || arg instanceof class_1480)) {
			arg.method_5650();
		}

		if (!this.method_14186() && arg instanceof class_1655) {
			arg.method_5650();
		}

		super.method_8553(arg, bl);
	}

	private boolean method_14186() {
		return this.field_13959.method_3736();
	}

	private boolean method_14182() {
		return this.field_13959.method_3796();
	}

	@Override
	public boolean method_8505(class_1657 arg, class_2338 arg2) {
		return !this.field_13959.method_3785(this, arg2, arg) && this.method_8621().method_11952(arg2);
	}

	@Override
	public void method_8414(class_1940 arg) {
		if (!this.field_9232.method_222()) {
			try {
				this.method_14184(arg);
				if (this.field_9232.method_153() == class_1942.field_9266) {
					this.method_14167();
				}

				super.method_8414(arg);
			} catch (Throwable var6) {
				class_128 lv = class_128.method_560(var6, "Exception initializing level");

				try {
					this.method_8538(lv);
				} catch (Throwable var5) {
				}

				throw new class_148(lv);
			}

			this.field_9232.method_223(true);
		}
	}

	private void method_14167() {
		this.field_9232.method_196(false);
		this.field_9232.method_211(true);
		this.field_9232.method_157(false);
		this.field_9232.method_147(false);
		this.field_9232.method_167(1000000000);
		this.field_9232.method_165(6000L);
		this.field_9232.method_193(class_1934.field_9219);
		this.field_9232.method_198(false);
		this.field_9232.method_208(class_1267.field_5801);
		this.field_9232.method_186(true);
		this.method_8450().method_8359("doDaylightCycle", "false", this.field_13959);
	}

	private void method_14184(class_1940 arg) {
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

	public void method_14176(@Nullable class_3536 arg, boolean bl) throws class_1939 {
		class_3215 lv = this.method_14178();
		if (!this.field_13957) {
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

		for (class_3218 lv : this.field_13959.method_3738()) {
			if (lv instanceof class_3202) {
				((class_3202)lv).method_14032();
			}
		}

		this.field_9232.method_162(this.method_8621().method_11965());
		this.field_9232.method_200(this.method_8621().method_11964());
		this.field_9232.method_189(this.method_8621().method_11980());
		this.field_9232.method_216(this.method_8621().method_11971());
		this.field_9232.method_229(this.method_8621().method_11953());
		this.field_9232.method_201(this.method_8621().method_11972());
		this.field_9232.method_192(this.method_8621().method_11956());
		this.field_9232.method_174(this.method_8621().method_11954());
		this.field_9232.method_195(this.method_8621().method_11962());
		this.field_9232.method_221(this.field_13959.method_3837().method_12974());
		this.field_9243.method_131(this.field_9232, this.field_13959.method_3760().method_14567());
		this.method_8646().method_265();
	}

	@Override
	public boolean method_8649(class_1297 arg) {
		return this.method_14175(arg) ? super.method_8649(arg) : false;
	}

	@Override
	public void method_8555(Stream<class_1297> stream) {
		stream.forEach(arg -> {
			if (this.method_14175(arg)) {
				this.field_9240.add(arg);
				this.method_8485(arg);
			}
		});
	}

	private boolean method_14175(class_1297 arg) {
		if (arg.field_5988) {
			field_13952.warn("Tried to add entity {} but it was marked as removed already", class_1299.method_5890(arg.method_5864()));
			return false;
		} else {
			UUID uUID = arg.method_5667();
			if (this.field_13960.containsKey(uUID)) {
				class_1297 lv = (class_1297)this.field_13960.get(uUID);
				if (this.field_9227.contains(lv)) {
					this.field_9227.remove(lv);
				} else {
					if (!(arg instanceof class_1657)) {
						field_13952.warn("Keeping entity {} that already exists with UUID {}", class_1299.method_5890(lv.method_5864()), uUID.toString());
						return false;
					}

					field_13952.warn("Force-added player with duplicate UUID {}", uUID.toString());
				}

				this.method_8507(lv);
			}

			return true;
		}
	}

	@Override
	protected void method_8485(class_1297 arg) {
		super.method_8485(arg);
		this.field_9225.method_15313(arg.method_5628(), arg);
		this.field_13960.put(arg.method_5667(), arg);
		class_1297[] lvs = arg.method_5690();
		if (lvs != null) {
			for (class_1297 lv : lvs) {
				this.field_9225.method_15313(lv.method_5628(), lv);
			}
		}
	}

	@Override
	protected void method_8539(class_1297 arg) {
		super.method_8539(arg);
		this.field_9225.method_15312(arg.method_5628());
		this.field_13960.remove(arg.method_5667());
		class_1297[] lvs = arg.method_5690();
		if (lvs != null) {
			for (class_1297 lv : lvs) {
				this.field_9225.method_15312(lv.method_5628());
			}
		}
	}

	@Override
	public boolean method_8416(class_1297 arg) {
		if (super.method_8416(arg)) {
			this.field_13959
				.method_3760()
				.method_14605(null, arg.field_5987, arg.field_6010, arg.field_6035, 512.0, this.field_9247.method_12460(), new class_2607(arg));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_8421(class_1297 arg, byte b) {
		this.method_14180().method_14073(arg, new class_2663(arg, b));
	}

	public class_3215 method_14178() {
		return (class_3215)super.method_8398();
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<class_2818> method_16177(int i, int j, boolean bl) {
		return this.method_14178().method_17299(i, j, class_2806.field_12803, bl).thenApply(either -> either.map(arg -> (class_2818)arg, arg -> null));
	}

	@Override
	public class_1927 method_8454(@Nullable class_1297 arg, class_1282 arg2, double d, double e, double f, float g, boolean bl, boolean bl2) {
		class_1927 lv = new class_1927(this, arg, d, e, f, g, bl, bl2);
		if (arg2 != null) {
			lv.method_8345(arg2);
		}

		lv.method_8348();
		lv.method_8350(false);
		if (!bl2) {
			lv.method_8352();
		}

		for (class_1657 lv2 : this.field_9228) {
			if (lv2.method_5649(d, e, f) < 4096.0) {
				((class_3222)lv2).field_13987.method_14364(new class_2664(d, e, f, g, lv.method_8346(), (class_243)lv.method_8351().get(lv2)));
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
	public void close() {
		this.field_9248.close();
		super.close();
	}

	@Override
	protected void method_8511() {
		boolean bl = this.method_8419();
		super.method_8511();
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

	public class_3208 method_14180() {
		return this.field_13954;
	}

	public class_1946 method_14173() {
		return this.field_13956;
	}

	public class_3485 method_14183() {
		return this.field_9243.method_134();
	}

	public <T extends class_2394> int method_14199(T arg, double d, double e, double f, int i, double g, double h, double j, double k) {
		class_2675 lv = new class_2675(arg, false, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		int l = 0;

		for (int m = 0; m < this.field_9228.size(); m++) {
			class_3222 lv2 = (class_3222)this.field_9228.get(m);
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
			double g = lv.method_10261(d, e, f);
			if (!(g <= 1024.0) && (!bl || !(g <= 262144.0))) {
				return false;
			} else {
				arg.field_13987.method_14364(arg2);
				return true;
			}
		}
	}

	@Nullable
	@Override
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
}
