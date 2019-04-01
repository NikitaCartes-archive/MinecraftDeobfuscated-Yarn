package net.minecraft;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;

public class class_1738 extends class_1792 {
	private static final UUID[] field_7876 = new UUID[]{
		UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
		UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
		UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
		UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};
	public static final class_2357 field_7879 = new class_2347() {
		@Override
		protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
			class_1799 lv = class_1738.method_7684(arg, arg2);
			return lv.method_7960() ? super.method_10135(arg, arg2) : lv;
		}
	};
	protected final class_1304 field_7880;
	protected final int field_7878;
	protected final float field_7877;
	protected final class_1741 field_7881;

	public static class_1799 method_7684(class_2342 arg, class_1799 arg2) {
		class_2338 lv = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));
		List<class_1309> list = arg.method_10207().method_8390(class_1309.class, new class_238(lv), class_1301.field_6155.and(new class_1301.class_1302(arg2)));
		if (list.isEmpty()) {
			return class_1799.field_8037;
		} else {
			class_1309 lv2 = (class_1309)list.get(0);
			class_1304 lv3 = class_1308.method_5953(arg2);
			class_1799 lv4 = arg2.method_7971(1);
			lv2.method_5673(lv3, lv4);
			if (lv2 instanceof class_1308) {
				((class_1308)lv2).method_5946(lv3, 2.0F);
				((class_1308)lv2).method_5971();
			}

			return arg2;
		}
	}

	public class_1738(class_1741 arg, class_1304 arg2, class_1792.class_1793 arg3) {
		super(arg3.method_7898(arg.method_7696(arg2)));
		this.field_7881 = arg;
		this.field_7880 = arg2;
		this.field_7878 = arg.method_7697(arg2);
		this.field_7877 = arg.method_7700();
		class_2315.method_10009(this, field_7879);
	}

	public class_1304 method_7685() {
		return this.field_7880;
	}

	@Override
	public int method_7837() {
		return this.field_7881.method_7699();
	}

	public class_1741 method_7686() {
		return this.field_7881;
	}

	@Override
	public boolean method_7878(class_1799 arg, class_1799 arg2) {
		return this.field_7881.method_7695().method_8093(arg2) || super.method_7878(arg, arg2);
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_1304 lv2 = class_1308.method_5953(lv);
		class_1799 lv3 = arg2.method_6118(lv2);
		if (lv3.method_7960()) {
			arg2.method_5673(lv2, lv.method_7972());
			lv.method_7939(0);
			return new class_1271<>(class_1269.field_5812, lv);
		} else {
			return new class_1271<>(class_1269.field_5814, lv);
		}
	}

	@Override
	public Multimap<String, class_1322> method_7844(class_1304 arg) {
		Multimap<String, class_1322> multimap = super.method_7844(arg);
		if (arg == this.field_7880) {
			multimap.put(
				class_1612.field_7358.method_6167(),
				new class_1322(field_7876[arg.method_5927()], "Armor modifier", (double)this.field_7878, class_1322.class_1323.field_6328)
			);
			multimap.put(
				class_1612.field_7364.method_6167(),
				new class_1322(field_7876[arg.method_5927()], "Armor toughness", (double)this.field_7877, class_1322.class_1323.field_6328)
			);
		}

		return multimap;
	}

	public int method_7687() {
		return this.field_7878;
	}
}
