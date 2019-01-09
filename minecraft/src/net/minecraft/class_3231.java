package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3231 {
	private static final Logger field_14041 = LogManager.getLogger();
	private final class_1297 field_14049;
	private final int field_14038;
	private int field_14052;
	private final int field_14037;
	private long field_14050;
	private long field_14035;
	private long field_14048;
	private int field_14060;
	private int field_14047;
	private int field_14059;
	private double field_14046;
	private double field_14057;
	private double field_14044;
	public int field_14040;
	private double field_14056;
	private double field_14042;
	private double field_14055;
	private boolean field_14054;
	private final boolean field_14039;
	private int field_14043;
	private List<class_1297> field_14045 = Collections.emptyList();
	private boolean field_14051;
	private boolean field_14036;
	public boolean field_14058;
	private final Set<class_3222> field_14053 = Sets.<class_3222>newHashSet();

	public class_3231(class_1297 arg, int i, int j, int k, boolean bl) {
		this.field_14049 = arg;
		this.field_14038 = i;
		this.field_14052 = j;
		this.field_14037 = k;
		this.field_14039 = bl;
		this.field_14050 = class_3208.method_14076(arg.field_5987);
		this.field_14035 = class_3208.method_14076(arg.field_6010);
		this.field_14048 = class_3208.method_14076(arg.field_6035);
		this.field_14060 = class_3532.method_15375(arg.field_6031 * 256.0F / 360.0F);
		this.field_14047 = class_3532.method_15375(arg.field_5965 * 256.0F / 360.0F);
		this.field_14059 = class_3532.method_15375(arg.method_5791() * 256.0F / 360.0F);
		this.field_14036 = arg.field_5952;
	}

	public boolean equals(Object object) {
		return object instanceof class_3231 ? ((class_3231)object).field_14049.method_5628() == this.field_14049.method_5628() : false;
	}

	public int hashCode() {
		return this.field_14049.method_5628();
	}

	public void method_14297(List<class_1657> list) {
		this.field_14058 = false;
		if (!this.field_14054 || this.field_14049.method_5649(this.field_14056, this.field_14042, this.field_14055) > 16.0) {
			this.field_14056 = this.field_14049.field_5987;
			this.field_14042 = this.field_14049.field_6010;
			this.field_14055 = this.field_14049.field_6035;
			this.field_14054 = true;
			this.field_14058 = true;
			this.method_14300(list);
		}

		List<class_1297> list2 = this.field_14049.method_5685();
		if (!list2.equals(this.field_14045)) {
			this.field_14045 = list2;
			this.method_14293(new class_2752(this.field_14049));
		}

		if (this.field_14049 instanceof class_1533 && this.field_14040 % 10 == 0) {
			class_1533 lv = (class_1533)this.field_14049;
			class_1799 lv2 = lv.method_6940();
			if (lv2.method_7909() instanceof class_1806) {
				class_22 lv3 = class_1806.method_8001(lv2, this.field_14049.field_6002);

				for (class_1657 lv4 : list) {
					class_3222 lv5 = (class_3222)lv4;
					lv3.method_102(lv5, lv2);
					class_2596<?> lv6 = ((class_1806)lv2.method_7909()).method_7757(lv2, this.field_14049.field_6002, lv5);
					if (lv6 != null) {
						lv5.field_13987.method_14364(lv6);
					}
				}
			}

			this.method_14306();
		}

		if (this.field_14040 % this.field_14037 == 0 || this.field_14049.field_6007 || this.field_14049.method_5841().method_12786()) {
			if (this.field_14049.method_5765()) {
				int i = class_3532.method_15375(this.field_14049.field_6031 * 256.0F / 360.0F);
				int j = class_3532.method_15375(this.field_14049.field_5965 * 256.0F / 360.0F);
				boolean bl = Math.abs(i - this.field_14060) >= 1 || Math.abs(j - this.field_14047) >= 1;
				if (bl) {
					this.method_14293(new class_2684.class_2687(this.field_14049.method_5628(), (byte)i, (byte)j, this.field_14049.field_5952));
					this.field_14060 = i;
					this.field_14047 = j;
				}

				this.field_14050 = class_3208.method_14076(this.field_14049.field_5987);
				this.field_14035 = class_3208.method_14076(this.field_14049.field_6010);
				this.field_14048 = class_3208.method_14076(this.field_14049.field_6035);
				this.method_14306();
				this.field_14051 = true;
			} else {
				this.field_14043++;
				long l = class_3208.method_14076(this.field_14049.field_5987);
				long m = class_3208.method_14076(this.field_14049.field_6010);
				long n = class_3208.method_14076(this.field_14049.field_6035);
				int k = class_3532.method_15375(this.field_14049.field_6031 * 256.0F / 360.0F);
				int o = class_3532.method_15375(this.field_14049.field_5965 * 256.0F / 360.0F);
				long p = l - this.field_14050;
				long q = m - this.field_14035;
				long r = n - this.field_14048;
				class_2596<?> lv7 = null;
				boolean bl2 = p * p + q * q + r * r >= 128L || this.field_14040 % 60 == 0;
				boolean bl3 = Math.abs(k - this.field_14060) >= 1 || Math.abs(o - this.field_14047) >= 1;
				if (this.field_14040 > 0 || this.field_14049 instanceof class_1665) {
					if (p >= -32768L
						&& p < 32768L
						&& q >= -32768L
						&& q < 32768L
						&& r >= -32768L
						&& r < 32768L
						&& this.field_14043 <= 400
						&& !this.field_14051
						&& this.field_14036 == this.field_14049.field_5952) {
						if ((!bl2 || !bl3) && !(this.field_14049 instanceof class_1665)) {
							if (bl2) {
								lv7 = new class_2684.class_2685(this.field_14049.method_5628(), p, q, r, this.field_14049.field_5952);
							} else if (bl3) {
								lv7 = new class_2684.class_2687(this.field_14049.method_5628(), (byte)k, (byte)o, this.field_14049.field_5952);
							}
						} else {
							lv7 = new class_2684.class_2686(this.field_14049.method_5628(), p, q, r, (byte)k, (byte)o, this.field_14049.field_5952);
						}
					} else {
						this.field_14036 = this.field_14049.field_5952;
						this.field_14043 = 0;
						this.method_14307();
						lv7 = new class_2777(this.field_14049);
					}
				}

				boolean bl4 = this.field_14039 || this.field_14049.field_6007;
				if (this.field_14049 instanceof class_1309 && ((class_1309)this.field_14049).method_6128()) {
					bl4 = true;
				}

				if (bl4 && this.field_14040 > 0) {
					double d = this.field_14049.field_5967 - this.field_14046;
					double e = this.field_14049.field_5984 - this.field_14057;
					double f = this.field_14049.field_6006 - this.field_14044;
					double g = 0.02;
					double h = d * d + e * e + f * f;
					if (h > 4.0E-4 || h > 0.0 && this.field_14049.field_5967 == 0.0 && this.field_14049.field_5984 == 0.0 && this.field_14049.field_6006 == 0.0) {
						this.field_14046 = this.field_14049.field_5967;
						this.field_14057 = this.field_14049.field_5984;
						this.field_14044 = this.field_14049.field_6006;
						this.method_14293(new class_2743(this.field_14049.method_5628(), this.field_14046, this.field_14057, this.field_14044));
					}
				}

				if (lv7 != null) {
					this.method_14293(lv7);
				}

				this.method_14306();
				if (bl2) {
					this.field_14050 = l;
					this.field_14035 = m;
					this.field_14048 = n;
				}

				if (bl3) {
					this.field_14060 = k;
					this.field_14047 = o;
				}

				this.field_14051 = false;
			}

			int i = class_3532.method_15375(this.field_14049.method_5791() * 256.0F / 360.0F);
			if (Math.abs(i - this.field_14059) >= 1) {
				this.method_14293(new class_2726(this.field_14049, (byte)i));
				this.field_14059 = i;
			}

			this.field_14049.field_6007 = false;
		}

		this.field_14040++;
		if (this.field_14049.field_6037) {
			this.method_14295(new class_2743(this.field_14049));
			this.field_14049.field_6037 = false;
		}
	}

	private void method_14306() {
		class_2945 lv = this.field_14049.method_5841();
		if (lv.method_12786()) {
			this.method_14295(new class_2739(this.field_14049.method_5628(), lv, false));
		}

		if (this.field_14049 instanceof class_1309) {
			class_1327 lv2 = (class_1327)((class_1309)this.field_14049).method_6127();
			Set<class_1324> set = lv2.method_6215();
			if (!set.isEmpty()) {
				this.method_14295(new class_2781(this.field_14049.method_5628(), set));
			}

			set.clear();
		}
	}

	public void method_14293(class_2596<?> arg) {
		for (class_3222 lv : this.field_14053) {
			lv.field_13987.method_14364(arg);
		}
	}

	public void method_14295(class_2596<?> arg) {
		this.method_14293(arg);
		if (this.field_14049 instanceof class_3222) {
			((class_3222)this.field_14049).field_13987.method_14364(arg);
		}
	}

	public void method_14304() {
		for (class_3222 lv : this.field_14053) {
			this.field_14049.method_5742(lv);
			lv.method_14249(this.field_14049);
		}
	}

	public void method_14302(class_3222 arg) {
		if (this.field_14053.contains(arg)) {
			this.field_14049.method_5742(arg);
			arg.method_14249(this.field_14049);
			this.field_14053.remove(arg);
		}
	}

	public void method_14303(class_3222 arg) {
		if (arg != this.field_14049) {
			if (this.method_14294(arg)) {
				if (!this.field_14053.contains(arg) && (this.method_14298(arg) || this.field_14049.field_5983)) {
					this.field_14053.add(arg);
					class_2596<?> lv = this.method_14299();
					arg.field_13987.method_14364(lv);
					if (!this.field_14049.method_5841().method_12790()) {
						arg.field_13987.method_14364(new class_2739(this.field_14049.method_5628(), this.field_14049.method_5841(), true));
					}

					boolean bl = this.field_14039;
					if (this.field_14049 instanceof class_1309) {
						class_1327 lv2 = (class_1327)((class_1309)this.field_14049).method_6127();
						Collection<class_1324> collection = lv2.method_6213();
						if (!collection.isEmpty()) {
							arg.field_13987.method_14364(new class_2781(this.field_14049.method_5628(), collection));
						}

						if (((class_1309)this.field_14049).method_6128()) {
							bl = true;
						}
					}

					this.field_14046 = this.field_14049.field_5967;
					this.field_14057 = this.field_14049.field_5984;
					this.field_14044 = this.field_14049.field_6006;
					if (bl && !(lv instanceof class_2610)) {
						arg.field_13987
							.method_14364(new class_2743(this.field_14049.method_5628(), this.field_14049.field_5967, this.field_14049.field_5984, this.field_14049.field_6006));
					}

					if (this.field_14049 instanceof class_1309) {
						for (class_1304 lv3 : class_1304.values()) {
							class_1799 lv4 = ((class_1309)this.field_14049).method_6118(lv3);
							if (!lv4.method_7960()) {
								arg.field_13987.method_14364(new class_2744(this.field_14049.method_5628(), lv3, lv4));
							}
						}
					}

					if (this.field_14049 instanceof class_1657) {
						class_1657 lv5 = (class_1657)this.field_14049;
						if (lv5.method_6113()) {
							arg.field_13987.method_14364(new class_2711(lv5, new class_2338(this.field_14049)));
						}
					}

					if (this.field_14049 instanceof class_1309) {
						class_1309 lv6 = (class_1309)this.field_14049;

						for (class_1293 lv7 : lv6.method_6026()) {
							arg.field_13987.method_14364(new class_2783(this.field_14049.method_5628(), lv7));
						}
					}

					if (!this.field_14049.method_5685().isEmpty()) {
						arg.field_13987.method_14364(new class_2752(this.field_14049));
					}

					if (this.field_14049.method_5765()) {
						arg.field_13987.method_14364(new class_2752(this.field_14049.method_5854()));
					}

					this.field_14049.method_5837(arg);
					arg.method_14211(this.field_14049);
				}
			} else if (this.field_14053.contains(arg)) {
				this.field_14053.remove(arg);
				this.field_14049.method_5742(arg);
				arg.method_14249(this.field_14049);
			}
		}
	}

	public boolean method_14294(class_3222 arg) {
		double d = arg.field_5987 - (double)this.field_14050 / 4096.0;
		double e = arg.field_6035 - (double)this.field_14048 / 4096.0;
		int i = Math.min(this.field_14038, this.field_14052);
		return d >= (double)(-i) && d <= (double)i && e >= (double)(-i) && e <= (double)i && this.field_14049.method_5680(arg);
	}

	private boolean method_14298(class_3222 arg) {
		return arg.method_14220().method_14178().method_14154(arg, this.field_14049.field_6024, this.field_14049.field_5980);
	}

	public void method_14300(List<class_1657> list) {
		for (int i = 0; i < list.size(); i++) {
			this.method_14303((class_3222)list.get(i));
		}
	}

	private class_2596<?> method_14299() {
		if (this.field_14049.field_5988) {
			field_14041.warn("Fetching addPacket for removed entity");
		}

		if (this.field_14049 instanceof class_3222) {
			return new class_2613((class_1657)this.field_14049);
		} else if (this.field_14049 instanceof class_1298) {
			this.field_14059 = class_3532.method_15375(this.field_14049.method_5791() * 256.0F / 360.0F);
			return new class_2610((class_1309)this.field_14049);
		} else if (this.field_14049 instanceof class_1534) {
			return new class_2612((class_1534)this.field_14049);
		} else if (this.field_14049 instanceof class_1542) {
			return new class_2604(this.field_14049, 2, 1);
		} else if (this.field_14049 instanceof class_1688) {
			class_1688 lv = (class_1688)this.field_14049;
			return new class_2604(this.field_14049, 10, lv.method_7518().method_7530());
		} else if (this.field_14049 instanceof class_1690) {
			return new class_2604(this.field_14049, 1);
		} else if (this.field_14049 instanceof class_1303) {
			return new class_2606((class_1303)this.field_14049);
		} else if (this.field_14049 instanceof class_1536) {
			class_1297 lv2 = ((class_1536)this.field_14049).method_6947();
			return new class_2604(this.field_14049, 90, lv2 == null ? this.field_14049.method_5628() : lv2.method_5628());
		} else if (this.field_14049 instanceof class_1679) {
			class_1297 lv2 = ((class_1679)this.field_14049).method_7452();
			return new class_2604(this.field_14049, 91, 1 + (lv2 == null ? this.field_14049.method_5628() : lv2.method_5628()));
		} else if (this.field_14049 instanceof class_1667) {
			class_1297 lv2 = ((class_1665)this.field_14049).method_7452();
			return new class_2604(this.field_14049, 60, 1 + (lv2 == null ? this.field_14049.method_5628() : lv2.method_5628()));
		} else if (this.field_14049 instanceof class_1680) {
			return new class_2604(this.field_14049, 61);
		} else if (this.field_14049 instanceof class_1685) {
			class_1297 lv2 = ((class_1665)this.field_14049).method_7452();
			return new class_2604(this.field_14049, 94, 1 + (lv2 == null ? this.field_14049.method_5628() : lv2.method_5628()));
		} else if (this.field_14049 instanceof class_1673) {
			return new class_2604(this.field_14049, 68);
		} else if (this.field_14049 instanceof class_1686) {
			return new class_2604(this.field_14049, 73);
		} else if (this.field_14049 instanceof class_1683) {
			return new class_2604(this.field_14049, 75);
		} else if (this.field_14049 instanceof class_1684) {
			return new class_2604(this.field_14049, 65);
		} else if (this.field_14049 instanceof class_1672) {
			return new class_2604(this.field_14049, 72);
		} else if (this.field_14049 instanceof class_1671) {
			return new class_2604(this.field_14049, 76);
		} else if (this.field_14049 instanceof class_1668) {
			class_1668 lv3 = (class_1668)this.field_14049;
			int i = 63;
			if (this.field_14049 instanceof class_1677) {
				i = 64;
			} else if (this.field_14049 instanceof class_1670) {
				i = 93;
			} else if (this.field_14049 instanceof class_1687) {
				i = 66;
			}

			class_2604 lv4;
			if (lv3.field_7604 == null) {
				lv4 = new class_2604(this.field_14049, i, 0);
			} else {
				lv4 = new class_2604(this.field_14049, i, ((class_1668)this.field_14049).field_7604.method_5628());
			}

			lv4.method_11162((int)(lv3.field_7601 * 8000.0));
			lv4.method_11165((int)(lv3.field_7600 * 8000.0));
			lv4.method_11177((int)(lv3.field_7599 * 8000.0));
			return lv4;
		} else if (this.field_14049 instanceof class_1678) {
			class_2604 lv5 = new class_2604(this.field_14049, 67, 0);
			lv5.method_11162((int)(this.field_14049.field_5967 * 8000.0));
			lv5.method_11165((int)(this.field_14049.field_5984 * 8000.0));
			lv5.method_11177((int)(this.field_14049.field_6006 * 8000.0));
			return lv5;
		} else if (this.field_14049 instanceof class_1681) {
			return new class_2604(this.field_14049, 62);
		} else if (this.field_14049 instanceof class_1669) {
			return new class_2604(this.field_14049, 79);
		} else if (this.field_14049 instanceof class_1541) {
			return new class_2604(this.field_14049, 50);
		} else if (this.field_14049 instanceof class_1511) {
			return new class_2604(this.field_14049, 51);
		} else if (this.field_14049 instanceof class_1540) {
			class_1540 lv6 = (class_1540)this.field_14049;
			return new class_2604(this.field_14049, 70, class_2248.method_9507(lv6.method_6962()));
		} else if (this.field_14049 instanceof class_1531) {
			return new class_2604(this.field_14049, 78);
		} else if (this.field_14049 instanceof class_1533) {
			class_1533 lv7 = (class_1533)this.field_14049;
			return new class_2604(this.field_14049, 71, lv7.field_7099.method_10146(), lv7.method_6896());
		} else if (this.field_14049 instanceof class_1532) {
			class_1532 lv8 = (class_1532)this.field_14049;
			return new class_2604(this.field_14049, 77, 0, lv8.method_6896());
		} else if (this.field_14049 instanceof class_1295) {
			return new class_2604(this.field_14049, 3);
		} else {
			throw new IllegalArgumentException("Don't know how to add " + this.field_14049.getClass() + "!");
		}
	}

	public void method_14301(class_3222 arg) {
		if (this.field_14053.contains(arg)) {
			this.field_14053.remove(arg);
			this.field_14049.method_5742(arg);
			arg.method_14249(this.field_14049);
		}
	}

	public class_1297 method_14305() {
		return this.field_14049;
	}

	public void method_14296(int i) {
		this.field_14052 = i;
	}

	public void method_14307() {
		this.field_14054 = false;
	}
}
