package net.minecraft;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2881 {
	private static final Logger field_13112 = LogManager.getLogger();
	private static final Predicate<class_1297> field_13113 = class_1301.field_6154.and(class_1301.method_5909(0.0, 128.0, 0.0, 192.0));
	private final class_3213 field_13119 = (class_3213)new class_3213(
			new class_2588("entity.minecraft.ender_dragon"), class_1259.class_1260.field_5788, class_1259.class_1261.field_5795
		)
		.method_5410(true)
		.method_5411(true);
	private final class_3218 field_13108;
	private final List<Integer> field_13121 = Lists.<Integer>newArrayList();
	private final class_2700 field_13110;
	private int field_13107;
	private int field_13106;
	private int field_13105;
	private int field_13122;
	private boolean field_13115;
	private boolean field_13114;
	private UUID field_13116;
	private boolean field_13111 = true;
	private class_2338 field_13117;
	private class_2876 field_13120;
	private int field_13118;
	private List<class_1511> field_13109;

	public class_2881(class_3218 arg, class_2487 arg2) {
		this.field_13108 = arg;
		if (arg2.method_10573("DragonKilled", 99)) {
			if (arg2.method_10576("DragonUUID")) {
				this.field_13116 = arg2.method_10584("DragonUUID");
			}

			this.field_13115 = arg2.method_10577("DragonKilled");
			this.field_13114 = arg2.method_10577("PreviouslyKilled");
			if (arg2.method_10577("IsRespawning")) {
				this.field_13120 = class_2876.field_13097;
			}

			if (arg2.method_10573("ExitPortalLocation", 10)) {
				this.field_13117 = class_2512.method_10691(arg2.method_10562("ExitPortalLocation"));
			}
		} else {
			this.field_13115 = true;
			this.field_13114 = true;
		}

		if (arg2.method_10573("Gateways", 9)) {
			class_2499 lv = arg2.method_10554("Gateways", 3);

			for (int i = 0; i < lv.size(); i++) {
				this.field_13121.add(lv.method_10600(i));
			}
		} else {
			this.field_13121.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
			Collections.shuffle(this.field_13121, new Random(arg.method_8412()));
		}

		this.field_13110 = class_2697.method_11701()
			.method_11702("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.method_11702("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.method_11702("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.method_11702("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ")
			.method_11702("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ")
			.method_11700('#', class_2694.method_11678(class_2717.method_11766(class_2246.field_9987)))
			.method_11704();
	}

	public class_2487 method_12530() {
		class_2487 lv = new class_2487();
		if (this.field_13116 != null) {
			lv.method_10560("DragonUUID", this.field_13116);
		}

		lv.method_10556("DragonKilled", this.field_13115);
		lv.method_10556("PreviouslyKilled", this.field_13114);
		if (this.field_13117 != null) {
			lv.method_10566("ExitPortalLocation", class_2512.method_10692(this.field_13117));
		}

		class_2499 lv2 = new class_2499();

		for (int i : this.field_13121) {
			lv2.method_10606(new class_2497(i));
		}

		lv.method_10566("Gateways", lv2);
		return lv;
	}

	public void method_12538() {
		this.field_13119.method_14091(!this.field_13115);
		if (++this.field_13122 >= 20) {
			this.method_12520();
			this.field_13122 = 0;
		}

		if (!this.field_13119.method_14092().isEmpty()) {
			this.field_13108.method_14178().method_17297(class_3230.field_17264, new class_1923(0, 0), 8, class_3902.field_17274);
			boolean bl = this.method_12533();
			if (this.field_13111 && bl) {
				this.method_12515();
				this.field_13111 = false;
			}

			if (this.field_13120 != null) {
				if (this.field_13109 == null && bl) {
					this.field_13120 = null;
					this.method_12522();
				}

				this.field_13120.method_12507(this.field_13108, this, this.field_13109, this.field_13118++, this.field_13117);
			}

			if (!this.field_13115) {
				if ((this.field_13116 == null || ++this.field_13107 >= 1200) && bl) {
					this.method_12525();
					this.field_13107 = 0;
				}

				if (++this.field_13105 >= 100 && bl) {
					this.method_12535();
					this.field_13105 = 0;
				}
			}
		} else {
			this.field_13108.method_14178().method_17300(class_3230.field_17264, new class_1923(0, 0), 8, class_3902.field_17274);
		}
	}

	private void method_12515() {
		field_13112.info("Scanning for legacy world dragon fight...");
		boolean bl = this.method_12514();
		if (bl) {
			field_13112.info("Found that the dragon has been killed in this world already.");
			this.field_13114 = true;
		} else {
			field_13112.info("Found that the dragon has not yet been killed in this world.");
			this.field_13114 = false;
			this.method_12518(false);
		}

		List<class_1510> list = this.field_13108.method_8490(class_1510.class, class_1301.field_6154);
		if (list.isEmpty()) {
			this.field_13115 = true;
		} else {
			class_1510 lv = (class_1510)list.get(0);
			this.field_13116 = lv.method_5667();
			field_13112.info("Found that there's a dragon still alive ({})", lv);
			this.field_13115 = false;
			if (!bl) {
				field_13112.info("But we didn't have a portal, let's remove it.");
				lv.method_5650();
				this.field_13116 = null;
			}
		}

		if (!this.field_13114 && this.field_13115) {
			this.field_13115 = false;
		}
	}

	private void method_12525() {
		List<class_1510> list = this.field_13108.method_8490(class_1510.class, class_1301.field_6154);
		if (list.isEmpty()) {
			field_13112.debug("Haven't seen the dragon, respawning it");
			this.method_12523();
		} else {
			field_13112.debug("Haven't seen our dragon, but found another one to use.");
			this.field_13116 = ((class_1510)list.get(0)).method_5667();
		}
	}

	protected void method_12521(class_2876 arg) {
		if (this.field_13120 == null) {
			throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
		} else {
			this.field_13118 = 0;
			if (arg == class_2876.field_13099) {
				this.field_13120 = null;
				this.field_13115 = false;
				class_1510 lv = this.method_12523();

				for (class_3222 lv2 : this.field_13119.method_14092()) {
					class_174.field_1182.method_9124(lv2, lv);
				}
			} else {
				this.field_13120 = arg;
			}
		}
	}

	private boolean method_12514() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				class_2818 lv = this.field_13108.method_8497(i, j);

				for (class_2586 lv2 : lv.method_12214().values()) {
					if (lv2 instanceof class_2640) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Nullable
	private class_2700.class_2702 method_12531() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				class_2818 lv = this.field_13108.method_8497(i, j);

				for (class_2586 lv2 : lv.method_12214().values()) {
					if (lv2 instanceof class_2640) {
						class_2700.class_2702 lv3 = this.field_13110.method_11708(this.field_13108, lv2.method_11016());
						if (lv3 != null) {
							class_2338 lv4 = lv3.method_11717(3, 3, 3).method_11683();
							if (this.field_13117 == null && lv4.method_10263() == 0 && lv4.method_10260() == 0) {
								this.field_13117 = lv4;
							}

							return lv3;
						}
					}
				}
			}
		}

		int i = this.field_13108.method_8598(class_2902.class_2903.field_13197, class_3033.field_13600).method_10264();

		for (int j = i; j >= 0; j--) {
			class_2700.class_2702 lv5 = this.field_13110
				.method_11708(this.field_13108, new class_2338(class_3033.field_13600.method_10263(), j, class_3033.field_13600.method_10260()));
			if (lv5 != null) {
				if (this.field_13117 == null) {
					this.field_13117 = lv5.method_11717(3, 3, 3).method_11683();
				}

				return lv5;
			}
		}

		return null;
	}

	private boolean method_12533() {
		for (int i = -8; i <= 8; i++) {
			for (int j = 8; j <= 8; j++) {
				class_2791 lv = this.field_13108.method_8402(i, j, class_2806.field_12803, false);
				if (!(lv instanceof class_2818)) {
					return false;
				}

				class_3193.class_3194 lv2 = ((class_2818)lv).method_12225();
				if (!lv2.method_14014(class_3193.class_3194.field_13875)) {
					return false;
				}
			}
		}

		return true;
	}

	private void method_12520() {
		Set<class_3222> set = Sets.<class_3222>newHashSet();

		for (class_3222 lv : this.field_13108.method_8498(class_3222.class, field_13113)) {
			this.field_13119.method_14088(lv);
			set.add(lv);
		}

		Set<class_3222> set2 = Sets.<class_3222>newHashSet(this.field_13119.method_14092());
		set2.removeAll(set);

		for (class_3222 lv2 : set2) {
			this.field_13119.method_14089(lv2);
		}
	}

	private void method_12535() {
		this.field_13105 = 0;
		this.field_13106 = 0;

		for (class_3310.class_3181 lv : class_3310.method_14506(this.field_13108)) {
			this.field_13106 = this.field_13106 + this.field_13108.method_8403(class_1511.class, lv.method_13968()).size();
		}

		field_13112.debug("Found {} end crystals still alive", this.field_13106);
	}

	public void method_12528(class_1510 arg) {
		if (arg.method_5667().equals(this.field_13116)) {
			this.field_13119.method_5408(0.0F);
			this.field_13119.method_14091(false);
			this.method_12518(true);
			this.method_12519();
			if (!this.field_13114) {
				this.field_13108.method_8501(this.field_13108.method_8598(class_2902.class_2903.field_13197, class_3033.field_13600), class_2246.field_10081.method_9564());
			}

			this.field_13114 = true;
			this.field_13115 = true;
		}
	}

	private void method_12519() {
		if (!this.field_13121.isEmpty()) {
			int i = (Integer)this.field_13121.remove(this.field_13121.size() - 1);
			int j = (int)(96.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			int k = (int)(96.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			this.method_12516(new class_2338(j, 75, k));
		}
	}

	private void method_12516(class_2338 arg) {
		this.field_13108.method_8535(3000, arg, 0);
		class_3031.field_13564
			.method_13151(this.field_13108, (class_2794<? extends class_2888>)this.field_13108.method_14178().method_12129(), new Random(), arg, new class_3018(false));
	}

	private void method_12518(boolean bl) {
		class_3033 lv = new class_3033(bl);
		if (this.field_13117 == null) {
			this.field_13117 = this.field_13108.method_8598(class_2902.class_2903.field_13203, class_3033.field_13600).method_10074();

			while (
				this.field_13108.method_8320(this.field_13117).method_11614() == class_2246.field_9987 && this.field_13117.method_10264() > this.field_13108.method_8615()
			) {
				this.field_13117 = this.field_13117.method_10074();
			}
		}

		lv.method_13163(
			this.field_13108, (class_2794<? extends class_2888>)this.field_13108.method_14178().method_12129(), new Random(), this.field_13117, class_3037.field_13603
		);
	}

	private class_1510 method_12523() {
		this.field_13108.method_8500(new class_2338(0, 128, 0));
		class_1510 lv = new class_1510(this.field_13108);
		lv.method_6831().method_6863(class_1527.field_7069);
		lv.method_5808(0.0, 128.0, 0.0, this.field_13108.field_9229.nextFloat() * 360.0F, 0.0F);
		this.field_13108.method_8649(lv);
		this.field_13116 = lv.method_5667();
		return lv;
	}

	public void method_12532(class_1510 arg) {
		if (arg.method_5667().equals(this.field_13116)) {
			this.field_13119.method_5408(arg.method_6032() / arg.method_6063());
			this.field_13107 = 0;
			if (arg.method_16914()) {
				this.field_13119.method_5413(arg.method_5476());
			}
		}
	}

	public int method_12517() {
		return this.field_13106;
	}

	public void method_12526(class_1511 arg, class_1282 arg2) {
		if (this.field_13120 != null && this.field_13109.contains(arg)) {
			field_13112.debug("Aborting respawn sequence");
			this.field_13120 = null;
			this.field_13118 = 0;
			this.method_12524();
			this.method_12518(true);
		} else {
			this.method_12535();
			class_1297 lv = this.field_13108.method_14190(this.field_13116);
			if (lv instanceof class_1510) {
				((class_1510)lv).method_6828(arg, new class_2338(arg), arg2);
			}
		}
	}

	public boolean method_12536() {
		return this.field_13114;
	}

	public void method_12522() {
		if (this.field_13115 && this.field_13120 == null) {
			class_2338 lv = this.field_13117;
			if (lv == null) {
				field_13112.debug("Tried to respawn, but need to find the portal first.");
				class_2700.class_2702 lv2 = this.method_12531();
				if (lv2 == null) {
					field_13112.debug("Couldn't find a portal, so we made one.");
					this.method_12518(true);
				} else {
					field_13112.debug("Found the exit portal & temporarily using it.");
				}

				lv = this.field_13117;
			}

			List<class_1511> list = Lists.<class_1511>newArrayList();
			class_2338 lv3 = lv.method_10086(1);

			for (class_2350 lv4 : class_2350.class_2353.field_11062) {
				List<class_1511> list2 = this.field_13108.method_8403(class_1511.class, new class_238(lv3.method_10079(lv4, 2)));
				if (list2.isEmpty()) {
					return;
				}

				list.addAll(list2);
			}

			field_13112.debug("Found all crystals, respawning dragon.");
			this.method_12529(list);
		}
	}

	private void method_12529(List<class_1511> list) {
		if (this.field_13115 && this.field_13120 == null) {
			for (class_2700.class_2702 lv = this.method_12531(); lv != null; lv = this.method_12531()) {
				for (int i = 0; i < this.field_13110.method_11710(); i++) {
					for (int j = 0; j < this.field_13110.method_11713(); j++) {
						for (int k = 0; k < this.field_13110.method_11712(); k++) {
							class_2694 lv2 = lv.method_11717(i, j, k);
							if (lv2.method_11681().method_11614() == class_2246.field_9987 || lv2.method_11681().method_11614() == class_2246.field_10027) {
								this.field_13108.method_8501(lv2.method_11683(), class_2246.field_10471.method_9564());
							}
						}
					}
				}
			}

			this.field_13120 = class_2876.field_13097;
			this.field_13118 = 0;
			this.method_12518(false);
			this.field_13109 = list;
		}
	}

	public void method_12524() {
		for (class_3310.class_3181 lv : class_3310.method_14506(this.field_13108)) {
			for (class_1511 lv2 : this.field_13108.method_8403(class_1511.class, lv.method_13968())) {
				lv2.method_5684(false);
				lv2.method_6837(null);
			}
		}
	}
}
