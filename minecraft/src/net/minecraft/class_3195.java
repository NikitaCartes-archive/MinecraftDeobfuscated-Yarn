package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3195<C extends class_3037> extends class_3031<C> {
	private static final Logger field_13879 = LogManager.getLogger();

	public class_3195(Function<Dynamic<?>, ? extends C> function) {
		super(function, false);
	}

	@Override
	public boolean method_13151(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, C arg4) {
		if (!arg.method_8401().method_220()) {
			return false;
		} else {
			int i = arg3.method_10263() >> 4;
			int j = arg3.method_10260() >> 4;
			int k = i << 4;
			int l = j << 4;
			boolean bl = false;
			LongIterator var11 = arg.method_8392(i, j).method_12180(this.method_14019()).iterator();

			while (var11.hasNext()) {
				Long long_ = (Long)var11.next();
				class_1923 lv = new class_1923(long_);
				class_3449 lv2 = arg.method_8392(lv.field_9181, lv.field_9180).method_12181(this.method_14019());
				if (lv2 != null && lv2 != class_3449.field_16713) {
					lv2.method_14974(arg, random, new class_3341(k, l, k + 15, l + 15), new class_1923(i, j));
					bl = true;
				}
			}

			return bl;
		}
	}

	protected class_3449 method_14025(class_1936 arg, class_2338 arg2, boolean bl) {
		for (class_3449 lv : this.method_14017(arg, arg2.method_10263() >> 4, arg2.method_10260() >> 4)) {
			if (lv.method_16657() && lv.method_14968().method_14662(arg2)) {
				if (!bl) {
					return lv;
				}

				for (class_3443 lv2 : lv.method_14963()) {
					if (lv2.method_14935().method_14662(arg2)) {
						return lv;
					}
				}
			}
		}

		return class_3449.field_16713;
	}

	public boolean method_14023(class_1936 arg, class_2338 arg2) {
		return this.method_14025(arg, arg2, false).method_16657();
	}

	public boolean method_14024(class_1936 arg, class_2338 arg2) {
		return this.method_14025(arg, arg2, true).method_16657();
	}

	@Nullable
	public class_2338 method_14015(class_1937 arg, class_2794<? extends class_2888> arg2, class_2338 arg3, int i, boolean bl) {
		if (!arg2.method_12098().method_8754(this)) {
			return null;
		} else {
			int j = arg3.method_10263() >> 4;
			int k = arg3.method_10260() >> 4;
			int l = 0;

			for (class_2919 lv = new class_2919(); l <= i; l++) {
				for (int m = -l; m <= l; m++) {
					boolean bl2 = m == -l || m == l;

					for (int n = -l; n <= l; n++) {
						boolean bl3 = n == -l || n == l;
						if (bl2 || bl3) {
							class_1923 lv2 = this.method_14018(arg2, lv, j, k, m, n);
							class_3449 lv3 = arg.method_16956(lv2.field_9181, lv2.field_9180, class_2806.field_16423).method_12181(this.method_14019());
							if (lv3 != null && lv3.method_16657()) {
								if (bl && lv3.method_14979()) {
									lv3.method_14964();
									return lv3.method_14962();
								}

								if (!bl) {
									return lv3.method_14962();
								}
							}

							if (l == 0) {
								break;
							}
						}
					}

					if (l == 0) {
						break;
					}
				}
			}

			return null;
		}
	}

	private List<class_3449> method_14017(class_1936 arg, int i, int j) {
		List<class_3449> list = Lists.<class_3449>newArrayList();
		class_2791 lv = arg.method_16956(i, j, class_2806.field_12798);
		LongIterator longIterator = lv.method_12180(this.method_14019()).iterator();

		while (longIterator.hasNext()) {
			long l = longIterator.nextLong();
			class_2810 lv2 = arg.method_16956(class_1923.method_8325(l), class_1923.method_8332(l), class_2806.field_12798);
			class_3449 lv3 = lv2.method_12181(this.method_14019());
			if (lv3 != null) {
				list.add(lv3);
			}
		}

		return list;
	}

	protected class_1923 method_14018(class_2794<?> arg, Random random, int i, int j, int k, int l) {
		return new class_1923(i + k, j + l);
	}

	public abstract boolean method_14026(class_2794<?> arg, Random random, int i, int j);

	public abstract class_3195.class_3774 method_14016();

	public abstract String method_14019();

	public abstract int method_14021();

	public interface class_3774 {
		class_3449 create(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l);
	}
}
