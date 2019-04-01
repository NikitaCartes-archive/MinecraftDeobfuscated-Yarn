package net.minecraft;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1498 extends class_1496 {
	private static final UUID field_6985 = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
	private static final class_2940<Integer> field_6990 = class_2945.method_12791(class_1498.class, class_2943.field_13327);
	private static final String[] field_6992 = new String[]{
		"textures/entity/horse/horse_white.png",
		"textures/entity/horse/horse_creamy.png",
		"textures/entity/horse/horse_chestnut.png",
		"textures/entity/horse/horse_brown.png",
		"textures/entity/horse/horse_black.png",
		"textures/entity/horse/horse_gray.png",
		"textures/entity/horse/horse_darkbrown.png"
	};
	private static final String[] field_6991 = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
	private static final String[] field_6986 = new String[]{
		null,
		"textures/entity/horse/horse_markings_white.png",
		"textures/entity/horse/horse_markings_whitefield.png",
		"textures/entity/horse/horse_markings_whitedots.png",
		"textures/entity/horse/horse_markings_blackdots.png"
	};
	private static final String[] field_6989 = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
	private String field_6987;
	private final String[] field_6988 = new String[2];

	public class_1498(class_1299<? extends class_1498> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6990, 0);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Variant", this.method_6788());
		if (!this.field_6962.method_5438(1).method_7960()) {
			arg.method_10566("ArmorItem", this.field_6962.method_5438(1).method_7953(new class_2487()));
		}
	}

	public class_1799 method_6786() {
		return this.method_6118(class_1304.field_6174);
	}

	private void method_18445(class_1799 arg) {
		this.method_5673(class_1304.field_6174, arg);
		this.method_5946(class_1304.field_6174, 0.0F);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6783(arg.method_10550("Variant"));
		if (arg.method_10573("ArmorItem", 10)) {
			class_1799 lv = class_1799.method_7915(arg.method_10562("ArmorItem"));
			if (!lv.method_7960() && this.method_6773(lv)) {
				this.field_6962.method_5447(1, lv);
			}
		}

		this.method_6731();
	}

	public void method_6783(int i) {
		this.field_6011.method_12778(field_6990, i);
		this.method_6785();
	}

	public int method_6788() {
		return this.field_6011.method_12789(field_6990);
	}

	private void method_6785() {
		this.field_6987 = null;
	}

	@Environment(EnvType.CLIENT)
	private void method_6789() {
		int i = this.method_6788();
		int j = (i & 0xFF) % 7;
		int k = ((i & 0xFF00) >> 8) % 5;
		this.field_6988[0] = field_6992[j];
		this.field_6988[1] = field_6986[k];
		this.field_6987 = "horse/" + field_6991[j] + field_6989[k];
	}

	@Environment(EnvType.CLIENT)
	public String method_6784() {
		if (this.field_6987 == null) {
			this.method_6789();
		}

		return this.field_6987;
	}

	@Environment(EnvType.CLIENT)
	public String[] method_6787() {
		if (this.field_6987 == null) {
			this.method_6789();
		}

		return this.field_6988;
	}

	@Override
	protected void method_6731() {
		super.method_6731();
		this.method_6790(this.field_6962.method_5438(1));
	}

	private void method_6790(class_1799 arg) {
		this.method_18445(arg);
		if (!this.field_6002.field_9236) {
			this.method_5996(class_1612.field_7358).method_6200(field_6985);
			if (this.method_6773(arg)) {
				int i = ((class_4059)arg.method_7909()).method_18455();
				if (i != 0) {
					this.method_5996(class_1612.field_7358)
						.method_6197(new class_1322(field_6985, "Horse armor bonus", (double)i, class_1322.class_1323.field_6328).method_6187(false));
				}
			}
		}
	}

	@Override
	public void method_5453(class_1263 arg) {
		class_1799 lv = this.method_6786();
		super.method_5453(arg);
		class_1799 lv2 = this.method_6786();
		if (this.field_6012 > 20 && this.method_6773(lv2) && lv != lv2) {
			this.method_5783(class_3417.field_15141, 0.5F, 1.0F);
		}
	}

	@Override
	protected void method_6761(class_2498 arg) {
		super.method_6761(arg);
		if (this.field_5974.nextInt(10) == 0) {
			this.method_5783(class_3417.field_14556, arg.method_10597() * 0.6F, arg.method_10599());
		}
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192((double)this.method_6754());
		this.method_5996(class_1612.field_7357).method_6192(this.method_6728());
		this.method_5996(field_6974).method_6192(this.method_6774());
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6002.field_9236 && this.field_6011.method_12786()) {
			this.field_6011.method_12792();
			this.method_6785();
		}
	}

	@Override
	protected class_3414 method_5994() {
		super.method_5994();
		return class_3417.field_14947;
	}

	@Override
	protected class_3414 method_6002() {
		super.method_6002();
		return class_3417.field_15166;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		super.method_6011(arg);
		return class_3417.field_14923;
	}

	@Override
	protected class_3414 method_6747() {
		super.method_6747();
		return class_3417.field_15043;
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		boolean bl = !lv.method_7960();
		if (bl && lv.method_7909() instanceof class_1826) {
			return super.method_5992(arg, arg2);
		} else {
			if (!this.method_6109()) {
				if (this.method_6727() && arg.method_5715()) {
					this.method_6722(arg);
					return true;
				}

				if (this.method_5782()) {
					return super.method_5992(arg, arg2);
				}
			}

			if (bl) {
				if (this.method_6742(arg, lv)) {
					if (!arg.field_7503.field_7477) {
						lv.method_7934(1);
					}

					return true;
				}

				if (lv.method_7920(arg, this, arg2)) {
					return true;
				}

				if (!this.method_6727()) {
					this.method_6757();
					return true;
				}

				boolean bl2 = !this.method_6109() && !this.method_6725() && lv.method_7909() == class_1802.field_8175;
				if (this.method_6773(lv) || bl2) {
					this.method_6722(arg);
					return true;
				}
			}

			if (this.method_6109()) {
				return super.method_5992(arg, arg2);
			} else {
				this.method_6726(arg);
				return true;
			}
		}
	}

	@Override
	public boolean method_6474(class_1429 arg) {
		if (arg == this) {
			return false;
		} else {
			return !(arg instanceof class_1495) && !(arg instanceof class_1498) ? false : this.method_6734() && ((class_1496)arg).method_6734();
		}
	}

	@Override
	public class_1296 method_5613(class_1296 arg) {
		class_1496 lv;
		if (arg instanceof class_1495) {
			lv = class_1299.field_6057.method_5883(this.field_6002);
		} else {
			class_1498 lv2 = (class_1498)arg;
			lv = class_1299.field_6139.method_5883(this.field_6002);
			int i = this.field_5974.nextInt(9);
			int j;
			if (i < 4) {
				j = this.method_6788() & 0xFF;
			} else if (i < 8) {
				j = lv2.method_6788() & 0xFF;
			} else {
				j = this.field_5974.nextInt(7);
			}

			int k = this.field_5974.nextInt(5);
			if (k < 2) {
				j |= this.method_6788() & 0xFF00;
			} else if (k < 4) {
				j |= lv2.method_6788() & 0xFF00;
			} else {
				j |= this.field_5974.nextInt(5) << 8 & 0xFF00;
			}

			((class_1498)lv).method_6783(j);
		}

		this.method_6743(arg, lv);
		return lv;
	}

	@Override
	public boolean method_6735() {
		return true;
	}

	@Override
	public boolean method_6773(class_1799 arg) {
		return arg.method_7909() instanceof class_4059;
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		int i;
		if (arg4 instanceof class_1498.class_1499) {
			i = ((class_1498.class_1499)arg4).field_6994;
		} else {
			i = this.field_5974.nextInt(7);
			arg4 = new class_1498.class_1499(i);
		}

		this.method_6783(i | this.field_5974.nextInt(5) << 8);
		return arg4;
	}

	public static class class_1499 implements class_1315 {
		public final int field_6994;

		public class_1499(int i) {
			this.field_6994 = i;
		}
	}
}
