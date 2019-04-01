package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1703 {
	private final class_2371<class_1799> field_7764 = class_2371.method_10211();
	public final List<class_1735> field_7761 = Lists.<class_1735>newArrayList();
	private final List<class_3915> field_17285 = Lists.<class_3915>newArrayList();
	@Nullable
	private final class_3917<?> field_17493;
	public final int field_7763;
	@Environment(EnvType.CLIENT)
	private short field_7758;
	private int field_7762 = -1;
	private int field_7759;
	private final Set<class_1735> field_7757 = Sets.<class_1735>newHashSet();
	private final List<class_1712> field_7765 = Lists.<class_1712>newArrayList();
	private final Set<class_1657> field_7760 = Sets.<class_1657>newHashSet();

	protected class_1703(@Nullable class_3917<?> arg, int i) {
		this.field_17493 = arg;
		this.field_7763 = i;
	}

	protected static boolean method_17695(class_3914 arg, class_1657 arg2, class_2248 arg3) {
		return arg.method_17396(
			(arg3x, arg4) -> arg3x.method_8320(arg4).method_11614() != arg3
					? false
					: arg2.method_5649((double)arg4.method_10263() + 0.5, (double)arg4.method_10264() + 0.5, (double)arg4.method_10260() + 0.5) <= 64.0,
			true
		);
	}

	public class_3917<?> method_17358() {
		if (this.field_17493 == null) {
			throw new UnsupportedOperationException("Unable to construct this menu by type");
		} else {
			return this.field_17493;
		}
	}

	protected static void method_17359(class_1263 arg, int i) {
		int j = arg.method_5439();
		if (j < i) {
			throw new IllegalArgumentException("Container size " + j + " is smaller than expected " + i);
		}
	}

	protected static void method_17361(class_3913 arg, int i) {
		int j = arg.method_17389();
		if (j < i) {
			throw new IllegalArgumentException("Container data count " + j + " is smaller than expected " + i);
		}
	}

	protected class_1735 method_7621(class_1735 arg) {
		arg.field_7874 = this.field_7761.size();
		this.field_7761.add(arg);
		this.field_7764.add(class_1799.field_8037);
		return arg;
	}

	protected class_3915 method_17362(class_3915 arg) {
		this.field_17285.add(arg);
		return arg;
	}

	protected void method_17360(class_3913 arg) {
		for (int i = 0; i < arg.method_17389(); i++) {
			this.method_17362(class_3915.method_17405(arg, i));
		}
	}

	public void method_7596(class_1712 arg) {
		if (!this.field_7765.contains(arg)) {
			this.field_7765.add(arg);
			arg.method_7634(this, this.method_7602());
			this.method_7623();
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_7603(class_1712 arg) {
		this.field_7765.remove(arg);
	}

	public class_2371<class_1799> method_7602() {
		class_2371<class_1799> lv = class_2371.method_10211();

		for (int i = 0; i < this.field_7761.size(); i++) {
			lv.add(((class_1735)this.field_7761.get(i)).method_7677());
		}

		return lv;
	}

	public void method_7623() {
		for (int i = 0; i < this.field_7761.size(); i++) {
			class_1799 lv = ((class_1735)this.field_7761.get(i)).method_7677();
			class_1799 lv2 = this.field_7764.get(i);
			if (!class_1799.method_7973(lv2, lv)) {
				lv2 = lv.method_7960() ? class_1799.field_8037 : lv.method_7972();
				this.field_7764.set(i, lv2);

				for (class_1712 lv3 : this.field_7765) {
					lv3.method_7635(this, i, lv2);
				}
			}
		}

		for (int ix = 0; ix < this.field_17285.size(); ix++) {
			class_3915 lv4 = (class_3915)this.field_17285.get(ix);
			if (lv4.method_17408()) {
				for (class_1712 lv5 : this.field_7765) {
					lv5.method_7633(this, ix, lv4.method_17407());
				}
			}
		}
	}

	public boolean method_7604(class_1657 arg, int i) {
		return false;
	}

	public class_1735 method_7611(int i) {
		return (class_1735)this.field_7761.get(i);
	}

	public class_1799 method_7601(class_1657 arg, int i) {
		class_1735 lv = (class_1735)this.field_7761.get(i);
		return lv != null ? lv.method_7677() : class_1799.field_8037;
	}

	public class_1799 method_7593(int i, int j, class_1713 arg, class_1657 arg2) {
		class_1799 lv = class_1799.field_8037;
		class_1661 lv2 = arg2.field_7514;
		if (arg == class_1713.field_7789) {
			int k = this.field_7759;
			this.field_7759 = method_7594(j);
			if ((k != 1 || this.field_7759 != 2) && k != this.field_7759) {
				this.method_7605();
			} else if (lv2.method_7399().method_7960()) {
				this.method_7605();
			} else if (this.field_7759 == 0) {
				this.field_7762 = method_7620(j);
				if (method_7600(this.field_7762, arg2)) {
					this.field_7759 = 1;
					this.field_7757.clear();
				} else {
					this.method_7605();
				}
			} else if (this.field_7759 == 1) {
				class_1735 lv3 = (class_1735)this.field_7761.get(i);
				class_1799 lv4 = lv2.method_7399();
				if (lv3 != null
					&& method_7592(lv3, lv4, true)
					&& lv3.method_7680(lv4)
					&& (this.field_7762 == 2 || lv4.method_7947() > this.field_7757.size())
					&& this.method_7615(lv3)) {
					this.field_7757.add(lv3);
				}
			} else if (this.field_7759 == 2) {
				if (!this.field_7757.isEmpty()) {
					class_1799 lv5 = lv2.method_7399().method_7972();
					int l = lv2.method_7399().method_7947();

					for (class_1735 lv6 : this.field_7757) {
						class_1799 lv7 = lv2.method_7399();
						if (lv6 != null
							&& method_7592(lv6, lv7, true)
							&& lv6.method_7680(lv7)
							&& (this.field_7762 == 2 || lv7.method_7947() >= this.field_7757.size())
							&& this.method_7615(lv6)) {
							class_1799 lv8 = lv5.method_7972();
							int m = lv6.method_7681() ? lv6.method_7677().method_7947() : 0;
							method_7617(this.field_7757, this.field_7762, lv8, m);
							int n = Math.min(lv8.method_7914(), lv6.method_7676(lv8));
							if (lv8.method_7947() > n) {
								lv8.method_7939(n);
							}

							l -= lv8.method_7947() - m;
							lv6.method_7673(lv8);
						}
					}

					lv5.method_7939(l);
					lv2.method_7396(lv5);
				}

				this.method_7605();
			} else {
				this.method_7605();
			}
		} else if (this.field_7759 != 0) {
			this.method_7605();
		} else if ((arg == class_1713.field_7790 || arg == class_1713.field_7794) && (j == 0 || j == 1)) {
			if (i == -999) {
				if (!lv2.method_7399().method_7960()) {
					if (j == 0) {
						arg2.method_7328(lv2.method_7399(), true);
						lv2.method_7396(class_1799.field_8037);
					}

					if (j == 1) {
						arg2.method_7328(lv2.method_7399().method_7971(1), true);
					}
				}
			} else if (arg == class_1713.field_7794) {
				if (i < 0) {
					return class_1799.field_8037;
				}

				class_1735 lv9 = (class_1735)this.field_7761.get(i);
				if (lv9 == null || !lv9.method_7674(arg2)) {
					return class_1799.field_8037;
				}

				for (class_1799 lv5 = this.method_7601(arg2, i); !lv5.method_7960() && class_1799.method_7984(lv9.method_7677(), lv5); lv5 = this.method_7601(arg2, i)) {
					lv = lv5.method_7972();
				}
			} else {
				if (i < 0) {
					return class_1799.field_8037;
				}

				class_1735 lv9 = (class_1735)this.field_7761.get(i);
				if (lv9 != null) {
					class_1799 lv5 = lv9.method_7677();
					class_1799 lv4 = lv2.method_7399();
					if (!lv5.method_7960()) {
						lv = lv5.method_7972();
					}

					if (lv5.method_7960()) {
						if (!lv4.method_7960() && lv9.method_7680(lv4)) {
							int o = j == 0 ? lv4.method_7947() : 1;
							if (o > lv9.method_7676(lv4)) {
								o = lv9.method_7676(lv4);
							}

							lv9.method_7673(lv4.method_7971(o));
						}
					} else if (lv9.method_7674(arg2)) {
						if (lv4.method_7960()) {
							if (lv5.method_7960()) {
								lv9.method_7673(class_1799.field_8037);
								lv2.method_7396(class_1799.field_8037);
							} else {
								int o = j == 0 ? lv5.method_7947() : (lv5.method_7947() + 1) / 2;
								lv2.method_7396(lv9.method_7671(o));
								if (lv5.method_7960()) {
									lv9.method_7673(class_1799.field_8037);
								}

								lv9.method_7667(arg2, lv2.method_7399());
							}
						} else if (lv9.method_7680(lv4)) {
							if (method_7612(lv5, lv4)) {
								int o = j == 0 ? lv4.method_7947() : 1;
								if (o > lv9.method_7676(lv4) - lv5.method_7947()) {
									o = lv9.method_7676(lv4) - lv5.method_7947();
								}

								if (o > lv4.method_7914() - lv5.method_7947()) {
									o = lv4.method_7914() - lv5.method_7947();
								}

								lv4.method_7934(o);
								lv5.method_7933(o);
							} else if (lv4.method_7947() <= lv9.method_7676(lv4)) {
								lv9.method_7673(lv4);
								lv2.method_7396(lv5);
							}
						} else if (lv4.method_7914() > 1 && method_7612(lv5, lv4) && !lv5.method_7960()) {
							int ox = lv5.method_7947();
							if (ox + lv4.method_7947() <= lv4.method_7914()) {
								lv4.method_7933(ox);
								lv5 = lv9.method_7671(ox);
								if (lv5.method_7960()) {
									lv9.method_7673(class_1799.field_8037);
								}

								lv9.method_7667(arg2, lv2.method_7399());
							}
						}
					}

					lv9.method_7668();
				}
			}
		} else if (arg == class_1713.field_7791 && j >= 0 && j < 9) {
			class_1735 lv9 = (class_1735)this.field_7761.get(i);
			class_1799 lv5x = lv2.method_5438(j);
			class_1799 lv4x = lv9.method_7677();
			if (!lv5x.method_7960() || !lv4x.method_7960()) {
				if (lv5x.method_7960()) {
					if (lv9.method_7674(arg2)) {
						lv2.method_5447(j, lv4x);
						lv9.method_7672(lv4x.method_7947());
						lv9.method_7673(class_1799.field_8037);
						lv9.method_7667(arg2, lv4x);
					}
				} else if (lv4x.method_7960()) {
					if (lv9.method_7680(lv5x)) {
						int ox = lv9.method_7676(lv5x);
						if (lv5x.method_7947() > ox) {
							lv9.method_7673(lv5x.method_7971(ox));
						} else {
							lv9.method_7673(lv5x);
							lv2.method_5447(j, class_1799.field_8037);
						}
					}
				} else if (lv9.method_7674(arg2) && lv9.method_7680(lv5x)) {
					int ox = lv9.method_7676(lv5x);
					if (lv5x.method_7947() > ox) {
						lv9.method_7673(lv5x.method_7971(ox));
						lv9.method_7667(arg2, lv4x);
						if (!lv2.method_7394(lv4x)) {
							arg2.method_7328(lv4x, true);
						}
					} else {
						lv9.method_7673(lv5x);
						lv2.method_5447(j, lv4x);
						lv9.method_7667(arg2, lv4x);
					}
				}
			}
		} else if (arg == class_1713.field_7796 && arg2.field_7503.field_7477 && lv2.method_7399().method_7960() && i >= 0) {
			class_1735 lv9 = (class_1735)this.field_7761.get(i);
			if (lv9 != null && lv9.method_7681()) {
				class_1799 lv5x = lv9.method_7677().method_7972();
				lv5x.method_7939(lv5x.method_7914());
				lv2.method_7396(lv5x);
			}
		} else if (arg == class_1713.field_7795 && lv2.method_7399().method_7960() && i >= 0) {
			class_1735 lv9 = (class_1735)this.field_7761.get(i);
			if (lv9 != null && lv9.method_7681() && lv9.method_7674(arg2)) {
				class_1799 lv5x = lv9.method_7671(j == 0 ? 1 : lv9.method_7677().method_7947());
				lv9.method_7667(arg2, lv5x);
				arg2.method_7328(lv5x, true);
			}
		} else if (arg == class_1713.field_7793 && i >= 0) {
			class_1735 lv9 = (class_1735)this.field_7761.get(i);
			class_1799 lv5x = lv2.method_7399();
			if (!lv5x.method_7960() && (lv9 == null || !lv9.method_7681() || !lv9.method_7674(arg2))) {
				int l = j == 0 ? 0 : this.field_7761.size() - 1;
				int ox = j == 0 ? 1 : -1;

				for (int p = 0; p < 2; p++) {
					for (int q = l; q >= 0 && q < this.field_7761.size() && lv5x.method_7947() < lv5x.method_7914(); q += ox) {
						class_1735 lv10 = (class_1735)this.field_7761.get(q);
						if (lv10.method_7681() && method_7592(lv10, lv5x, true) && lv10.method_7674(arg2) && this.method_7613(lv5x, lv10)) {
							class_1799 lv11 = lv10.method_7677();
							if (p != 0 || lv11.method_7947() != lv11.method_7914()) {
								int n = Math.min(lv5x.method_7914() - lv5x.method_7947(), lv11.method_7947());
								class_1799 lv12 = lv10.method_7671(n);
								lv5x.method_7933(n);
								if (lv12.method_7960()) {
									lv10.method_7673(class_1799.field_8037);
								}

								lv10.method_7667(arg2, lv12);
							}
						}
					}
				}
			}

			this.method_7623();
		}

		return lv;
	}

	public static boolean method_7612(class_1799 arg, class_1799 arg2) {
		return arg.method_7909() == arg2.method_7909() && class_1799.method_7975(arg, arg2);
	}

	public boolean method_7613(class_1799 arg, class_1735 arg2) {
		return true;
	}

	public void method_7595(class_1657 arg) {
		class_1661 lv = arg.field_7514;
		if (!lv.method_7399().method_7960()) {
			arg.method_7328(lv.method_7399(), false);
			lv.method_7396(class_1799.field_8037);
		}
	}

	protected void method_7607(class_1657 arg, class_1937 arg2, class_1263 arg3) {
		if (!arg.method_5805() || arg instanceof class_3222 && ((class_3222)arg).method_14239()) {
			for (int i = 0; i < arg3.method_5439(); i++) {
				arg.method_7328(arg3.method_5441(i), false);
			}
		} else {
			for (int i = 0; i < arg3.method_5439(); i++) {
				arg.field_7514.method_7398(arg2, arg3.method_5441(i));
			}
		}
	}

	public void method_7609(class_1263 arg) {
		this.method_7623();
	}

	public void method_7619(int i, class_1799 arg) {
		this.method_7611(i).method_7673(arg);
	}

	@Environment(EnvType.CLIENT)
	public void method_7610(List<class_1799> list) {
		for (int i = 0; i < list.size(); i++) {
			this.method_7611(i).method_7673((class_1799)list.get(i));
		}
	}

	public void method_7606(int i, int j) {
		((class_3915)this.field_17285.get(i)).method_17404(j);
	}

	@Environment(EnvType.CLIENT)
	public short method_7614(class_1661 arg) {
		this.field_7758++;
		return this.field_7758;
	}

	public boolean method_7622(class_1657 arg) {
		return !this.field_7760.contains(arg);
	}

	public void method_7590(class_1657 arg, boolean bl) {
		if (bl) {
			this.field_7760.remove(arg);
		} else {
			this.field_7760.add(arg);
		}
	}

	public abstract boolean method_7597(class_1657 arg);

	protected boolean method_7616(class_1799 arg, int i, int j, boolean bl) {
		boolean bl2 = false;
		int k = i;
		if (bl) {
			k = j - 1;
		}

		if (arg.method_7946()) {
			while (!arg.method_7960() && (bl ? k >= i : k < j)) {
				class_1735 lv = (class_1735)this.field_7761.get(k);
				class_1799 lv2 = lv.method_7677();
				if (!lv2.method_7960() && method_7612(arg, lv2)) {
					int l = lv2.method_7947() + arg.method_7947();
					if (l <= arg.method_7914()) {
						arg.method_7939(0);
						lv2.method_7939(l);
						lv.method_7668();
						bl2 = true;
					} else if (lv2.method_7947() < arg.method_7914()) {
						arg.method_7934(arg.method_7914() - lv2.method_7947());
						lv2.method_7939(arg.method_7914());
						lv.method_7668();
						bl2 = true;
					}
				}

				if (bl) {
					k--;
				} else {
					k++;
				}
			}
		}

		if (!arg.method_7960()) {
			if (bl) {
				k = j - 1;
			} else {
				k = i;
			}

			while (bl ? k >= i : k < j) {
				class_1735 lvx = (class_1735)this.field_7761.get(k);
				class_1799 lv2x = lvx.method_7677();
				if (lv2x.method_7960() && lvx.method_7680(arg)) {
					if (arg.method_7947() > lvx.method_7675()) {
						lvx.method_7673(arg.method_7971(lvx.method_7675()));
					} else {
						lvx.method_7673(arg.method_7971(arg.method_7947()));
					}

					lvx.method_7668();
					bl2 = true;
					break;
				}

				if (bl) {
					k--;
				} else {
					k++;
				}
			}
		}

		return bl2;
	}

	public static int method_7620(int i) {
		return i >> 2 & 3;
	}

	public static int method_7594(int i) {
		return i & 3;
	}

	@Environment(EnvType.CLIENT)
	public static int method_7591(int i, int j) {
		return i & 3 | (j & 3) << 2;
	}

	public static boolean method_7600(int i, class_1657 arg) {
		if (i == 0) {
			return true;
		} else {
			return i == 1 ? true : i == 2 && arg.field_7503.field_7477;
		}
	}

	protected void method_7605() {
		this.field_7759 = 0;
		this.field_7757.clear();
	}

	public static boolean method_7592(@Nullable class_1735 arg, class_1799 arg2, boolean bl) {
		boolean bl2 = arg == null || !arg.method_7681();
		return !bl2 && arg2.method_7962(arg.method_7677()) && class_1799.method_7975(arg.method_7677(), arg2)
			? arg.method_7677().method_7947() + (bl ? 0 : arg2.method_7947()) <= arg2.method_7914()
			: bl2;
	}

	public static void method_7617(Set<class_1735> set, int i, class_1799 arg, int j) {
		switch (i) {
			case 0:
				arg.method_7939(class_3532.method_15375((float)arg.method_7947() / (float)set.size()));
				break;
			case 1:
				arg.method_7939(1);
				break;
			case 2:
				arg.method_7939(arg.method_7909().method_7882());
		}

		arg.method_7933(j);
	}

	public boolean method_7615(class_1735 arg) {
		return true;
	}

	public static int method_7608(@Nullable class_2586 arg) {
		return arg instanceof class_1263 ? method_7618((class_1263)arg) : 0;
	}

	public static int method_7618(@Nullable class_1263 arg) {
		if (arg == null) {
			return 0;
		} else {
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < arg.method_5439(); j++) {
				class_1799 lv = arg.method_5438(j);
				if (!lv.method_7960()) {
					f += (float)lv.method_7947() / (float)Math.min(arg.method_5444(), lv.method_7914());
					i++;
				}
			}

			f /= (float)arg.method_5439();
			return class_3532.method_15375(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}
}
