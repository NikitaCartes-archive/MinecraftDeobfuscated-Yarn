package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3233 implements class_1936 {
	private static final Logger field_14092 = LogManager.getLogger();
	private final List<class_2791> field_14098;
	private final int field_14090;
	private final int field_14089;
	private final int field_14088;
	private final class_3218 field_14093;
	private final long field_14087;
	private final int field_14086;
	private final class_31 field_14097;
	private final Random field_14091;
	private final class_2869 field_14096;
	private final class_2888 field_14095;
	private final class_1951<class_2248> field_14099 = new class_3235<>(argx -> this.method_16955(argx).method_12013());
	private final class_1951<class_3611> field_14094 = new class_3235<>(argx -> this.method_16955(argx).method_12014());

	public class_3233(class_3218 arg, List<class_2791> list) {
		int i = class_3532.method_15357(Math.sqrt((double)list.size()));
		if (i * i != list.size()) {
			throw new IllegalStateException("Cache size is not a square.");
		} else {
			class_1923 lv = ((class_2791)list.get(list.size() / 2)).method_12004();
			this.field_14098 = list;
			this.field_14090 = lv.field_9181;
			this.field_14089 = lv.field_9180;
			this.field_14088 = i;
			this.field_14093 = arg;
			this.field_14087 = arg.method_8412();
			this.field_14095 = arg.method_14178().method_12129().method_12109();
			this.field_14086 = arg.method_8615();
			this.field_14097 = arg.method_8401();
			this.field_14091 = arg.method_8409();
			this.field_14096 = arg.method_8597();
		}
	}

	public int method_14336() {
		return this.field_14090;
	}

	public int method_14339() {
		return this.field_14089;
	}

	@Override
	public class_2791 method_8392(int i, int j) {
		return this.method_16956(i, j, class_2806.field_12798);
	}

	@Nullable
	@Override
	public class_2791 method_8402(int i, int j, class_2806 arg, boolean bl) {
		class_2791 lv2;
		if (this.method_8393(i, j)) {
			class_1923 lv = ((class_2791)this.field_14098.get(0)).method_12004();
			int k = i - lv.field_9181;
			int l = j - lv.field_9180;
			lv2 = (class_2791)this.field_14098.get(k + l * this.field_14088);
			if (lv2.method_12009().method_12165(arg)) {
				return lv2;
			}
		} else {
			lv2 = null;
		}

		if (!bl) {
			return null;
		} else {
			class_2791 lv3 = (class_2791)this.field_14098.get(0);
			class_2791 lv4 = (class_2791)this.field_14098.get(this.field_14098.size() - 1);
			field_14092.error("Requested chunk : {} {}", i, j);
			field_14092.error(
				"Region bounds : {} {} | {} {}", lv3.method_12004().field_9181, lv3.method_12004().field_9180, lv4.method_12004().field_9181, lv4.method_12004().field_9180
			);
			if (lv2 != null) {
				throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", arg, lv2.method_12009(), i, j));
			} else {
				throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j));
			}
		}
	}

	@Override
	public boolean method_8393(int i, int j) {
		class_2791 lv = (class_2791)this.field_14098.get(0);
		class_2791 lv2 = (class_2791)this.field_14098.get(this.field_14098.size() - 1);
		return i >= lv.method_12004().field_9181 && i <= lv2.method_12004().field_9181 && j >= lv.method_12004().field_9180 && j <= lv2.method_12004().field_9180;
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		return this.method_8392(arg.method_10263() >> 4, arg.method_10260() >> 4).method_8320(arg);
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		return this.method_16955(arg).method_8316(arg);
	}

	@Nullable
	@Override
	public class_1657 method_8604(double d, double e, double f, double g, Predicate<class_1297> predicate) {
		return null;
	}

	@Override
	public int method_8594() {
		return 0;
	}

	@Override
	public class_1959 method_8310(class_2338 arg) {
		class_1959 lv = this.method_16955(arg).method_12036()[arg.method_10263() & 15 | (arg.method_10260() & 15) << 4];
		if (lv == null) {
			throw new RuntimeException(String.format("Biome is null @ %s", arg));
		} else {
			return lv;
		}
	}

	@Override
	public int method_8314(class_1944 arg, class_2338 arg2) {
		return this.method_8398().method_12130().method_15562(arg).method_15543(arg2);
	}

	@Override
	public int method_8624(class_2338 arg, int i) {
		return this.method_16955(arg).method_12035(arg, i, this.method_8597().method_12451());
	}

	@Override
	public boolean method_8651(class_2338 arg, boolean bl) {
		class_2680 lv = this.method_8320(arg);
		if (lv.method_11588()) {
			return false;
		} else {
			if (bl) {
				class_2586 lv2 = lv.method_11614().method_9570() ? this.method_8321(arg) : null;
				class_2248.method_9610(lv, this.field_14093, arg, lv2);
			}

			return this.method_8652(arg, class_2246.field_10124.method_9564(), 3);
		}
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		class_2791 lv = this.method_16955(arg);
		class_2586 lv2 = lv.method_8321(arg);
		if (lv2 != null) {
			return lv2;
		} else {
			class_2487 lv3 = lv.method_12024(arg);
			if (lv3 != null) {
				if ("DUMMY".equals(lv3.method_10558("id"))) {
					class_2248 lv4 = this.method_8320(arg).method_11614();
					if (!(lv4 instanceof class_2343)) {
						return null;
					}

					lv2 = ((class_2343)lv4).method_10123(this.field_14093);
				} else {
					lv2 = class_2586.method_11005(lv3);
				}

				if (lv2 != null) {
					lv.method_12007(arg, lv2);
					return lv2;
				}
			}

			if (lv.method_8320(arg).method_11614() instanceof class_2343) {
				field_14092.warn("Tried to access a block entity before it was created. {}", arg);
			}

			return null;
		}
	}

	@Override
	public boolean method_8652(class_2338 arg, class_2680 arg2, int i) {
		class_2791 lv = this.method_16955(arg);
		class_2680 lv2 = lv.method_12010(arg, arg2, false);
		if (lv2 != null) {
			this.field_14093.method_19282(arg, lv2, arg2);
		}

		class_2248 lv3 = arg2.method_11614();
		if (lv3.method_9570()) {
			if (lv.method_12009().method_12164() == class_2806.class_2808.field_12807) {
				lv.method_12007(arg, ((class_2343)lv3).method_10123(this));
			} else {
				class_2487 lv4 = new class_2487();
				lv4.method_10569("x", arg.method_10263());
				lv4.method_10569("y", arg.method_10264());
				lv4.method_10569("z", arg.method_10260());
				lv4.method_10582("id", "DUMMY");
				lv.method_12042(lv4);
			}
		} else if (lv2 != null && lv2.method_11614().method_9570()) {
			lv.method_12041(arg);
		}

		if (arg2.method_11601(this, arg)) {
			this.method_14338(arg);
		}

		return true;
	}

	private void method_14338(class_2338 arg) {
		this.method_16955(arg).method_12039(arg);
	}

	@Override
	public boolean method_8649(class_1297 arg) {
		int i = class_3532.method_15357(arg.field_5987 / 16.0);
		int j = class_3532.method_15357(arg.field_6035 / 16.0);
		this.method_8392(i, j).method_12002(arg);
		return true;
	}

	@Override
	public boolean method_8650(class_2338 arg, boolean bl) {
		return this.method_8652(arg, class_2246.field_10124.method_9564(), 3);
	}

	@Override
	public class_2784 method_8621() {
		return this.field_14093.method_8621();
	}

	@Override
	public boolean method_8611(@Nullable class_1297 arg, class_265 arg2) {
		return true;
	}

	@Override
	public boolean method_8608() {
		return false;
	}

	@Deprecated
	public class_3218 method_19506() {
		return this.field_14093;
	}

	@Override
	public class_31 method_8401() {
		return this.field_14097;
	}

	@Override
	public class_1266 method_8404(class_2338 arg) {
		if (!this.method_8393(arg.method_10263() >> 4, arg.method_10260() >> 4)) {
			throw new RuntimeException("We are asking a region for a chunk out of bound");
		} else {
			return new class_1266(this.field_14093.method_8407(), this.field_14093.method_8532(), 0L, this.field_14093.method_8391());
		}
	}

	@Override
	public class_2802 method_8398() {
		return this.field_14093.method_14178();
	}

	@Override
	public long method_8412() {
		return this.field_14087;
	}

	@Override
	public class_1951<class_2248> method_8397() {
		return this.field_14099;
	}

	@Override
	public class_1951<class_3611> method_8405() {
		return this.field_14094;
	}

	@Override
	public int method_8615() {
		return this.field_14086;
	}

	@Override
	public Random method_8409() {
		return this.field_14091;
	}

	@Override
	public void method_8408(class_2338 arg, class_2248 arg2) {
	}

	@Override
	public int method_8589(class_2902.class_2903 arg, int i, int j) {
		return this.method_8392(i >> 4, j >> 4).method_12005(arg, i & 15, j & 15) + 1;
	}

	@Override
	public void method_8396(@Nullable class_1657 arg, class_2338 arg2, class_3414 arg3, class_3419 arg4, float f, float g) {
	}

	@Override
	public void method_8406(class_2394 arg, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void method_8444(@Nullable class_1657 arg, int i, class_2338 arg2, int j) {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_2338 method_8395() {
		return this.field_14093.method_8395();
	}

	@Override
	public class_2869 method_8597() {
		return this.field_14096;
	}

	@Override
	public boolean method_16358(class_2338 arg, Predicate<class_2680> predicate) {
		return predicate.test(this.method_8320(arg));
	}

	@Override
	public <T extends class_1297> List<T> method_8390(Class<? extends T> class_, class_238 arg, @Nullable Predicate<? super T> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<class_1297> method_8333(@Nullable class_1297 arg, class_238 arg2, @Nullable Predicate<? super class_1297> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<class_1657> method_18456() {
		return Collections.emptyList();
	}

	@Override
	public class_2338 method_8598(class_2902.class_2903 arg, class_2338 arg2) {
		return new class_2338(arg2.method_10263(), this.method_8589(arg, arg2.method_10263(), arg2.method_10260()), arg2.method_10260());
	}
}
