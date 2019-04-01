package net.minecraft;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3231 {
	private static final Logger field_14041 = LogManager.getLogger();
	private final class_3218 field_18258;
	private final class_1297 field_14049;
	private final int field_14037;
	private final boolean field_14039;
	private final Consumer<class_2596<?>> field_18259;
	private long field_14050;
	private long field_14035;
	private long field_14048;
	private int field_14060;
	private int field_14047;
	private int field_14059;
	private class_243 field_18278 = class_243.field_1353;
	private int field_14040;
	private int field_14043;
	private List<class_1297> field_14045 = Collections.emptyList();
	private boolean field_14051;
	private boolean field_14036;

	public class_3231(class_3218 arg, class_1297 arg2, int i, boolean bl, Consumer<class_2596<?>> consumer) {
		this.field_18258 = arg;
		this.field_18259 = consumer;
		this.field_14049 = arg2;
		this.field_14037 = i;
		this.field_14039 = bl;
		this.method_18761();
		this.field_14060 = class_3532.method_15375(arg2.field_6031 * 256.0F / 360.0F);
		this.field_14047 = class_3532.method_15375(arg2.field_5965 * 256.0F / 360.0F);
		this.field_14059 = class_3532.method_15375(arg2.method_5791() * 256.0F / 360.0F);
		this.field_14036 = arg2.field_5952;
	}

	public void method_18756() {
		List<class_1297> list = this.field_14049.method_5685();
		if (!list.equals(this.field_14045)) {
			this.field_14045 = list;
			this.field_18259.accept(new class_2752(this.field_14049));
		}

		if (this.field_14049 instanceof class_1533 && this.field_14040 % 10 == 0) {
			class_1533 lv = (class_1533)this.field_14049;
			class_1799 lv2 = lv.method_6940();
			if (lv2.method_7909() instanceof class_1806) {
				class_22 lv3 = class_1806.method_8001(lv2, this.field_18258);

				for (class_3222 lv4 : this.field_18258.method_18456()) {
					lv3.method_102(lv4, lv2);
					class_2596<?> lv5 = ((class_1806)lv2.method_7909()).method_7757(lv2, this.field_18258, lv4);
					if (lv5 != null) {
						lv4.field_13987.method_14364(lv5);
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
					this.field_18259.accept(new class_2684.class_2687(this.field_14049.method_5628(), (byte)i, (byte)j, this.field_14049.field_5952));
					this.field_14060 = i;
					this.field_14047 = j;
				}

				this.method_18761();
				this.method_14306();
				this.field_14051 = true;
			} else {
				this.field_14043++;
				int i = class_3532.method_15375(this.field_14049.field_6031 * 256.0F / 360.0F);
				int j = class_3532.method_15375(this.field_14049.field_5965 * 256.0F / 360.0F);
				class_243 lv6 = new class_243(this.field_14049.field_5987, this.field_14049.field_6010, this.field_14049.field_6035)
					.method_1020(class_2684.method_18695(this.field_14050, this.field_14035, this.field_14048));
				boolean bl2 = lv6.method_1027() >= 7.6293945E-6F;
				class_2596<?> lv7 = null;
				boolean bl3 = bl2 || this.field_14040 % 60 == 0;
				boolean bl4 = Math.abs(i - this.field_14060) >= 1 || Math.abs(j - this.field_14047) >= 1;
				if (this.field_14040 > 0 || this.field_14049 instanceof class_1665) {
					long l = class_2684.method_18047(lv6.field_1352);
					long m = class_2684.method_18047(lv6.field_1351);
					long n = class_2684.method_18047(lv6.field_1350);
					boolean bl5 = l < -32768L || l > 32767L || m < -32768L || m > 32767L || n < -32768L || n > 32767L;
					if (!bl5 && this.field_14043 <= 400 && !this.field_14051 && this.field_14036 == this.field_14049.field_5952) {
						if ((!bl3 || !bl4) && !(this.field_14049 instanceof class_1665)) {
							if (bl3) {
								lv7 = new class_2684.class_2685(this.field_14049.method_5628(), (short)((int)l), (short)((int)m), (short)((int)n), this.field_14049.field_5952);
							} else if (bl4) {
								lv7 = new class_2684.class_2687(this.field_14049.method_5628(), (byte)i, (byte)j, this.field_14049.field_5952);
							}
						} else {
							lv7 = new class_2684.class_2686(
								this.field_14049.method_5628(), (short)((int)l), (short)((int)m), (short)((int)n), (byte)i, (byte)j, this.field_14049.field_5952
							);
						}
					} else {
						this.field_14036 = this.field_14049.field_5952;
						this.field_14043 = 0;
						lv7 = new class_2777(this.field_14049);
					}
				}

				if ((this.field_14039 || this.field_14049.field_6007 || this.field_14049 instanceof class_1309 && ((class_1309)this.field_14049).method_6128())
					&& this.field_14040 > 0) {
					class_243 lv8 = this.field_14049.method_18798();
					double d = lv8.method_1025(this.field_18278);
					if (d > 1.0E-7 || d > 0.0 && lv8.method_1027() == 0.0) {
						this.field_18278 = lv8;
						this.field_18259.accept(new class_2743(this.field_14049.method_5628(), this.field_18278));
					}
				}

				if (lv7 != null) {
					this.field_18259.accept(lv7);
				}

				this.method_14306();
				if (bl3) {
					this.method_18761();
				}

				if (bl4) {
					this.field_14060 = i;
					this.field_14047 = j;
				}

				this.field_14051 = false;
			}

			int ix = class_3532.method_15375(this.field_14049.method_5791() * 256.0F / 360.0F);
			if (Math.abs(ix - this.field_14059) >= 1) {
				this.field_18259.accept(new class_2726(this.field_14049, (byte)ix));
				this.field_14059 = ix;
			}

			this.field_14049.field_6007 = false;
		}

		this.field_14040++;
		if (this.field_14049.field_6037) {
			this.method_18758(new class_2743(this.field_14049));
			this.field_14049.field_6037 = false;
		}
	}

	public void method_14302(class_3222 arg) {
		this.field_14049.method_5742(arg);
		arg.method_14249(this.field_14049);
	}

	public void method_18760(class_3222 arg) {
		this.method_18757(arg.field_13987::method_14364);
		this.field_14049.method_5837(arg);
		arg.method_14211(this.field_14049);
	}

	public void method_18757(Consumer<class_2596<?>> consumer) {
		if (this.field_14049.field_5988) {
			field_14041.warn("Fetching addPacket for removed entity");
		}

		class_2596<?> lv = this.field_14049.method_18002();
		this.field_14059 = class_3532.method_15375(this.field_14049.method_5791() * 256.0F / 360.0F);
		consumer.accept(lv);
		if (!this.field_14049.method_5841().method_12790()) {
			consumer.accept(new class_2739(this.field_14049.method_5628(), this.field_14049.method_5841(), true));
		}

		boolean bl = this.field_14039;
		if (this.field_14049 instanceof class_1309) {
			class_1327 lv2 = (class_1327)((class_1309)this.field_14049).method_6127();
			Collection<class_1324> collection = lv2.method_6213();
			if (!collection.isEmpty()) {
				consumer.accept(new class_2781(this.field_14049.method_5628(), collection));
			}

			if (((class_1309)this.field_14049).method_6128()) {
				bl = true;
			}
		}

		this.field_18278 = this.field_14049.method_18798();
		if (bl && !(lv instanceof class_2610)) {
			consumer.accept(new class_2743(this.field_14049.method_5628(), this.field_18278));
		}

		if (this.field_14049 instanceof class_1309) {
			for (class_1304 lv3 : class_1304.values()) {
				class_1799 lv4 = ((class_1309)this.field_14049).method_6118(lv3);
				if (!lv4.method_7960()) {
					consumer.accept(new class_2744(this.field_14049.method_5628(), lv3, lv4));
				}
			}
		}

		if (this.field_14049 instanceof class_1309) {
			class_1309 lv5 = (class_1309)this.field_14049;

			for (class_1293 lv6 : lv5.method_6026()) {
				consumer.accept(new class_2783(this.field_14049.method_5628(), lv6));
			}
		}

		if (!this.field_14049.method_5685().isEmpty()) {
			consumer.accept(new class_2752(this.field_14049));
		}

		if (this.field_14049.method_5765()) {
			consumer.accept(new class_2752(this.field_14049.method_5854()));
		}
	}

	private void method_14306() {
		class_2945 lv = this.field_14049.method_5841();
		if (lv.method_12786()) {
			this.method_18758(new class_2739(this.field_14049.method_5628(), lv, false));
		}

		if (this.field_14049 instanceof class_1309) {
			class_1327 lv2 = (class_1327)((class_1309)this.field_14049).method_6127();
			Set<class_1324> set = lv2.method_6215();
			if (!set.isEmpty()) {
				this.method_18758(new class_2781(this.field_14049.method_5628(), set));
			}

			set.clear();
		}
	}

	private void method_18761() {
		this.field_14050 = class_2684.method_18047(this.field_14049.field_5987);
		this.field_14035 = class_2684.method_18047(this.field_14049.field_6010);
		this.field_14048 = class_2684.method_18047(this.field_14049.field_6035);
	}

	public class_243 method_18759() {
		return class_2684.method_18695(this.field_14050, this.field_14035, this.field_14048);
	}

	private void method_18758(class_2596<?> arg) {
		this.field_18259.accept(arg);
		if (this.field_14049 instanceof class_3222) {
			((class_3222)this.field_14049).field_13987.method_14364(arg);
		}
	}
}
