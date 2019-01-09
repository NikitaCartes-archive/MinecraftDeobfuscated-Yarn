package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class class_1415 {
	private class_1937 field_6701;
	private final List<class_1417> field_6704 = Lists.<class_1417>newArrayList();
	private class_2338 field_6694 = class_2338.field_10980;
	private class_2338 field_6696 = class_2338.field_10980;
	private int field_6699;
	private int field_6698;
	private int field_6697;
	private int field_6695;
	private int field_6693;
	private int field_16598 = 0;
	private final Map<String, Integer> field_6702 = Maps.<String, Integer>newHashMap();
	private final List<class_1415.class_1416> field_6700 = Lists.<class_1415.class_1416>newArrayList();
	private int field_6703;

	public class_1415() {
	}

	public class_1415(class_1937 arg) {
		this.field_6701 = arg;
	}

	public void method_6399(class_1937 arg) {
		this.field_6701 = arg;
	}

	public void method_6400(int i) {
		this.field_6697 = i;
		this.method_6395();
		this.method_6409();
		if (i % 20 == 0) {
			this.method_6396();
		}

		if (i % 30 == 0) {
			this.method_6411();
		}

		int j = this.field_6695 / 10;
		if (this.field_6703 < j && this.field_6704.size() > 20 && this.field_6701.field_9229.nextInt(7000) == 0) {
			class_1297 lv = this.method_6394(this.field_6696);
			if (lv != null) {
				this.field_6703++;
			}
		}
	}

	@Nullable
	private class_1297 method_6394(class_2338 arg) {
		for (int i = 0; i < 10; i++) {
			class_2338 lv = arg.method_10069(
				this.field_6701.field_9229.nextInt(16) - 8, this.field_6701.field_9229.nextInt(6) - 3, this.field_6701.field_9229.nextInt(16) - 8
			);
			if (this.method_6383(lv)) {
				class_1439 lv2 = class_1299.field_6147.method_5888(this.field_6701, null, null, null, lv, class_3730.field_16474, false, false);
				if (lv2 != null) {
					if (lv2.method_5979(this.field_6701, class_3730.field_16474) && lv2.method_5957(this.field_6701)) {
						this.field_6701.method_8649(lv2);
						return lv2;
					}

					lv2.method_5650();
				}
			}
		}

		return null;
	}

	private void method_6411() {
		List<class_1439> list = this.field_6701
			.method_8403(
				class_1439.class,
				new class_238(
					(double)(this.field_6696.method_10263() - this.field_6699),
					(double)(this.field_6696.method_10264() - 4),
					(double)(this.field_6696.method_10260() - this.field_6699),
					(double)(this.field_6696.method_10263() + this.field_6699),
					(double)(this.field_6696.method_10264() + 4),
					(double)(this.field_6696.method_10260() + this.field_6699)
				)
			);
		this.field_6703 = list.size();
	}

	private void method_6396() {
		List<class_1646> list = this.field_6701
			.method_8403(
				class_1646.class,
				new class_238(
					(double)(this.field_6696.method_10263() - this.field_6699),
					(double)(this.field_6696.method_10264() - 4),
					(double)(this.field_6696.method_10260() - this.field_6699),
					(double)(this.field_6696.method_10263() + this.field_6699),
					(double)(this.field_6696.method_10264() + 4),
					(double)(this.field_6696.method_10260() + this.field_6699)
				)
			);
		this.field_6695 = list.size();
		if (this.field_6695 == 0) {
			this.field_6702.clear();
		}
	}

	public class_2338 method_6382() {
		return this.field_6696;
	}

	public int method_6403() {
		return this.field_6699;
	}

	public int method_6384() {
		return this.field_6704.size();
	}

	public int method_6402() {
		return this.field_6697 - this.field_6698;
	}

	public int method_6387() {
		return this.field_6695;
	}

	public boolean method_6383(class_2338 arg) {
		return this.field_6696.method_10262(arg) < (double)(this.field_6699 * this.field_6699);
	}

	public boolean method_16470(class_2338 arg, int i) {
		return this.field_6696.method_10262(arg) < (double)(this.field_6699 * this.field_6699 + i);
	}

	public List<class_1417> method_6405() {
		return this.field_6704;
	}

	public class_1417 method_6386(class_2338 arg) {
		class_1417 lv = null;
		int i = Integer.MAX_VALUE;

		for (class_1417 lv2 : this.field_6704) {
			int j = lv2.method_6423(arg);
			if (j < i) {
				lv = lv2;
				i = j;
			}
		}

		return lv;
	}

	public class_1417 method_6412(class_2338 arg) {
		class_1417 lv = null;
		int i = Integer.MAX_VALUE;

		for (class_1417 lv2 : this.field_6704) {
			int j = lv2.method_6423(arg);
			if (j > 256) {
				j *= 1000;
			} else {
				j = lv2.method_6416();
			}

			if (j < i) {
				class_2338 lv3 = lv2.method_6429();
				class_2350 lv4 = lv2.method_6424();
				if (this.field_6701.method_8320(lv3.method_10079(lv4, 1)).method_11609(this.field_6701, lv3.method_10079(lv4, 1), class_10.field_50)
					&& this.field_6701.method_8320(lv3.method_10079(lv4, -1)).method_11609(this.field_6701, lv3.method_10079(lv4, -1), class_10.field_50)
					&& this.field_6701
						.method_8320(lv3.method_10084().method_10079(lv4, 1))
						.method_11609(this.field_6701, lv3.method_10084().method_10079(lv4, 1), class_10.field_50)
					&& this.field_6701
						.method_8320(lv3.method_10084().method_10079(lv4, -1))
						.method_11609(this.field_6701, lv3.method_10084().method_10079(lv4, -1), class_10.field_50)) {
					lv = lv2;
					i = j;
				}
			}
		}

		return lv;
	}

	@Nullable
	public class_1417 method_6390(class_2338 arg) {
		if (this.field_6696.method_10262(arg) > (double)(this.field_6699 * this.field_6699)) {
			return null;
		} else {
			for (class_1417 lv : this.field_6704) {
				if (lv.method_6429().method_10263() == arg.method_10263()
					&& lv.method_6429().method_10260() == arg.method_10260()
					&& Math.abs(lv.method_6429().method_10264() - arg.method_10264()) <= 1) {
					return lv;
				}
			}

			return null;
		}
	}

	public void method_6392(class_1417 arg) {
		this.field_6704.add(arg);
		this.field_6694 = this.field_6694.method_10081(arg.method_6429());
		this.method_6407();
		this.field_6698 = arg.method_6421();
	}

	public boolean method_6397() {
		return this.field_6704.isEmpty();
	}

	public void method_6404(class_1309 arg) {
		for (class_1415.class_1416 lv : this.field_6700) {
			if (lv.field_6706 == arg) {
				lv.field_6705 = this.field_6697;
				return;
			}
		}

		this.field_6700.add(new class_1415.class_1416(arg, this.field_6697));
	}

	@Nullable
	public class_1309 method_6385(class_1309 arg) {
		double d = Double.MAX_VALUE;
		class_1415.class_1416 lv = null;

		for (int i = 0; i < this.field_6700.size(); i++) {
			class_1415.class_1416 lv2 = (class_1415.class_1416)this.field_6700.get(i);
			double e = lv2.field_6706.method_5858(arg);
			if (!(e > d)) {
				lv = lv2;
				d = e;
			}
		}

		return lv == null ? null : lv.field_6706;
	}

	public class_1657 method_6391(class_1309 arg) {
		double d = Double.MAX_VALUE;
		class_1657 lv = null;

		for (String string : this.field_6702.keySet()) {
			if (this.method_6389(string)) {
				class_1657 lv2 = this.field_6701.method_8434(string);
				if (lv2 != null) {
					double e = lv2.method_5858(arg);
					if (!(e > d)) {
						lv = lv2;
						d = e;
					}
				}
			}
		}

		return lv;
	}

	private void method_6409() {
		Iterator<class_1415.class_1416> iterator = this.field_6700.iterator();

		while (iterator.hasNext()) {
			class_1415.class_1416 lv = (class_1415.class_1416)iterator.next();
			if (!lv.field_6706.method_5805() || Math.abs(this.field_6697 - lv.field_6705) > 300) {
				iterator.remove();
			}
		}
	}

	private void method_6395() {
		boolean bl = false;
		boolean bl2 = this.field_6701.field_9229.nextInt(50) == 0;
		Iterator<class_1417> iterator = this.field_6704.iterator();

		while (iterator.hasNext()) {
			class_1417 lv = (class_1417)iterator.next();
			if (bl2) {
				lv.method_6426();
			}

			if (!this.method_6406(lv.method_6429()) || Math.abs(this.field_6697 - lv.method_6421()) > 1200) {
				this.field_6694 = this.field_6694.method_10059(lv.method_6429());
				bl = true;
				lv.method_6418(true);
				iterator.remove();
			}
		}

		if (bl) {
			this.method_6407();
		}
	}

	private boolean method_6406(class_2338 arg) {
		if (!this.field_6701.method_8393(arg.method_10263() >> 4, arg.method_10260() >> 4)) {
			return true;
		} else {
			class_2680 lv = this.field_6701.method_8320(arg);
			class_2248 lv2 = lv.method_11614();
			return lv2 instanceof class_2323 ? lv.method_11620() == class_3614.field_15932 : false;
		}
	}

	private void method_6407() {
		int i = this.field_6704.size();
		if (i == 0) {
			this.field_6696 = class_2338.field_10980;
			this.field_6699 = 0;
		} else {
			this.field_6696 = new class_2338(this.field_6694.method_10263() / i, this.field_6694.method_10264() / i, this.field_6694.method_10260() / i);
			int j = 0;

			for (class_1417 lv : this.field_6704) {
				j = Math.max(lv.method_6423(this.field_6696), j);
			}

			this.field_6699 = Math.max(32, (int)Math.sqrt((double)j) + 1);
		}
	}

	public int method_6388(String string) {
		Integer integer = (Integer)this.field_6702.get(string);
		return integer == null ? 0 : integer;
	}

	public int method_6393(String string, int i) {
		int j = this.method_6388(string);
		int k = class_3532.method_15340(j + i, -30, 10);
		this.field_6702.put(string, k);
		return k;
	}

	public boolean method_6389(String string) {
		return this.method_6388(string) <= -15;
	}

	public void method_6410(class_2487 arg) {
		this.field_6695 = arg.method_10550("PopSize");
		this.field_6699 = arg.method_10550("Radius");
		this.field_6703 = arg.method_10550("Golems");
		this.field_6698 = arg.method_10550("Stable");
		this.field_6697 = arg.method_10550("Tick");
		this.field_6693 = arg.method_10550("MTick");
		this.field_6696 = new class_2338(arg.method_10550("CX"), arg.method_10550("CY"), arg.method_10550("CZ"));
		this.field_6694 = new class_2338(arg.method_10550("ACX"), arg.method_10550("ACY"), arg.method_10550("ACZ"));
		class_2499 lv = arg.method_10554("Doors", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv2 = lv.method_10602(i);
			class_1417 lv3 = new class_1417(
				new class_2338(lv2.method_10550("X"), lv2.method_10550("Y"), lv2.method_10550("Z")),
				lv2.method_10550("IDX"),
				lv2.method_10550("IDZ"),
				lv2.method_10550("TS")
			);
			this.field_6704.add(lv3);
		}

		class_2499 lv4 = arg.method_10554("Players", 10);

		for (int j = 0; j < lv4.size(); j++) {
			class_2487 lv5 = lv4.method_10602(j);
			if (lv5.method_10545("UUID") && this.field_6701 != null && this.field_6701.method_8503() != null) {
				class_3312 lv6 = this.field_6701.method_8503().method_3793();
				GameProfile gameProfile = lv6.method_14512(UUID.fromString(lv5.method_10558("UUID")));
				if (gameProfile != null) {
					this.field_6702.put(gameProfile.getName(), lv5.method_10550("S"));
				}
			} else {
				this.field_6702.put(lv5.method_10558("Name"), lv5.method_10550("S"));
			}
		}

		this.field_16598 = arg.method_10550("RaidId");
	}

	public void method_6408(class_2487 arg) {
		arg.method_10569("PopSize", this.field_6695);
		arg.method_10569("Radius", this.field_6699);
		arg.method_10569("Golems", this.field_6703);
		arg.method_10569("Stable", this.field_6698);
		arg.method_10569("Tick", this.field_6697);
		arg.method_10569("MTick", this.field_6693);
		arg.method_10569("CX", this.field_6696.method_10263());
		arg.method_10569("CY", this.field_6696.method_10264());
		arg.method_10569("CZ", this.field_6696.method_10260());
		arg.method_10569("ACX", this.field_6694.method_10263());
		arg.method_10569("ACY", this.field_6694.method_10264());
		arg.method_10569("ACZ", this.field_6694.method_10260());
		class_2499 lv = new class_2499();

		for (class_1417 lv2 : this.field_6704) {
			class_2487 lv3 = new class_2487();
			lv3.method_10569("X", lv2.method_6429().method_10263());
			lv3.method_10569("Y", lv2.method_6429().method_10264());
			lv3.method_10569("Z", lv2.method_6429().method_10260());
			lv3.method_10569("IDX", lv2.method_6419());
			lv3.method_10569("IDZ", lv2.method_6420());
			lv3.method_10569("TS", lv2.method_6421());
			lv.method_10606(lv3);
		}

		arg.method_10566("Doors", lv);
		class_2499 lv4 = new class_2499();

		for (String string : this.field_6702.keySet()) {
			class_2487 lv5 = new class_2487();
			class_3312 lv6 = this.field_6701.method_8503().method_3793();

			try {
				GameProfile gameProfile = lv6.method_14515(string);
				if (gameProfile != null) {
					lv5.method_10582("UUID", gameProfile.getId().toString());
					lv5.method_10569("S", (Integer)this.field_6702.get(string));
					lv4.method_10606(lv5);
				}
			} catch (RuntimeException var9) {
			}
		}

		arg.method_10566("Players", lv4);
		arg.method_10569("RaidId", this.field_16598);
	}

	public void method_6398() {
		this.field_6693 = this.field_6697;
	}

	public boolean method_6381() {
		return this.field_6693 == 0 || this.field_6697 - this.field_6693 >= 3600;
	}

	public void method_6401(int i) {
		for (String string : this.field_6702.keySet()) {
			this.method_6393(string, i);
		}
	}

	public int method_16467() {
		return this.field_16598;
	}

	public void method_16468(int i) {
		this.field_16598 = i;
		this.field_6701.method_8557().method_80();
	}

	@Nullable
	public class_3765 method_16469() {
		return this.field_6701 != null && this.field_6701.method_16542() != null ? this.field_6701.method_16542().method_16541(this.field_16598) : null;
	}

	class class_1416 {
		public final class_1309 field_6706;
		public int field_6705;

		class_1416(class_1309 arg2, int i) {
			this.field_6706 = arg2;
			this.field_6705 = i;
		}
	}
}
