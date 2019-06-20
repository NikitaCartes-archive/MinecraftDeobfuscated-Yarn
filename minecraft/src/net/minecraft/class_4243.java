package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class class_4243 extends class_4097<class_1646> {
	private static final Map<class_3852, class_2960> field_18984 = class_156.method_654(Maps.<class_3852, class_2960>newHashMap(), hashMap -> {
		hashMap.put(class_3852.field_17052, class_39.field_19062);
		hashMap.put(class_3852.field_17053, class_39.field_19063);
		hashMap.put(class_3852.field_17054, class_39.field_19064);
		hashMap.put(class_3852.field_17055, class_39.field_19065);
		hashMap.put(class_3852.field_17056, class_39.field_19066);
		hashMap.put(class_3852.field_17057, class_39.field_19067);
		hashMap.put(class_3852.field_17058, class_39.field_19068);
		hashMap.put(class_3852.field_17059, class_39.field_19069);
		hashMap.put(class_3852.field_17060, class_39.field_19070);
		hashMap.put(class_3852.field_17061, class_39.field_19071);
		hashMap.put(class_3852.field_17063, class_39.field_19072);
		hashMap.put(class_3852.field_17064, class_39.field_19073);
		hashMap.put(class_3852.field_17065, class_39.field_19074);
	});
	private int field_18985 = 600;
	private boolean field_18986;
	private long field_18987;

	public class_4243(int i) {
		super(
			ImmutableMap.of(
				class_4140.field_18445,
				class_4141.field_18458,
				class_4140.field_18446,
				class_4141.field_18458,
				class_4140.field_18447,
				class_4141.field_18458,
				class_4140.field_18444,
				class_4141.field_18456
			),
			i
		);
	}

	protected boolean method_19962(class_3218 arg, class_1646 arg2) {
		if (!this.method_19964(arg2)) {
			return false;
		} else if (this.field_18985 > 0) {
			this.field_18985--;
			return false;
		} else {
			return true;
		}
	}

	protected void method_19963(class_3218 arg, class_1646 arg2, long l) {
		this.field_18986 = false;
		this.field_18987 = l;
		class_1657 lv = (class_1657)this.method_19966(arg2).get();
		arg2.method_18868().method_18878(class_4140.field_18447, lv);
		class_4215.method_19554(arg2, lv);
	}

	protected boolean method_19965(class_3218 arg, class_1646 arg2, long l) {
		return this.method_19964(arg2) && !this.field_18986;
	}

	protected void method_19967(class_3218 arg, class_1646 arg2, long l) {
		class_1657 lv = (class_1657)this.method_19966(arg2).get();
		class_4215.method_19554(arg2, lv);
		if (this.method_19958(arg2, lv)) {
			if (l - this.field_18987 > 20L) {
				this.method_19957(arg2, lv);
				this.field_18986 = true;
			}
		} else {
			class_4215.method_19556(arg2, lv, 5);
		}
	}

	protected void method_19968(class_3218 arg, class_1646 arg2, long l) {
		this.field_18985 = method_19961(arg);
		arg2.method_18868().method_18875(class_4140.field_18447);
		arg2.method_18868().method_18875(class_4140.field_18445);
		arg2.method_18868().method_18875(class_4140.field_18446);
	}

	private void method_19957(class_1646 arg, class_1309 arg2) {
		for (class_1799 lv : this.method_19956(arg)) {
			class_4215.method_19949(arg, lv, arg2);
		}
	}

	private List<class_1799> method_19956(class_1646 arg) {
		if (arg.method_6109()) {
			return ImmutableList.of(new class_1799(class_1802.field_8880));
		} else {
			class_3852 lv = arg.method_7231().method_16924();
			if (field_18984.containsKey(lv)) {
				class_52 lv2 = arg.field_6002.method_8503().method_3857().method_367((class_2960)field_18984.get(lv));
				class_47.class_48 lv3 = new class_47.class_48((class_3218)arg.field_6002)
					.method_312(class_181.field_1232, new class_2338(arg))
					.method_312(class_181.field_1226, arg)
					.method_311(arg.method_6051());
				return lv2.method_319(lv3.method_309(class_173.field_16235));
			} else {
				return ImmutableList.of(new class_1799(class_1802.field_8317));
			}
		}
	}

	private boolean method_19964(class_1646 arg) {
		return this.method_19966(arg).isPresent();
	}

	private Optional<class_1657> method_19966(class_1646 arg) {
		return arg.method_18868().method_18904(class_4140.field_18444).filter(this::method_19959);
	}

	private boolean method_19959(class_1657 arg) {
		return arg.method_6059(class_1294.field_18980);
	}

	private boolean method_19958(class_1646 arg, class_1657 arg2) {
		class_2338 lv = new class_2338(arg2);
		class_2338 lv2 = new class_2338(arg);
		return lv2.method_19771(lv, 5.0);
	}

	private static int method_19961(class_3218 arg) {
		return 600 + arg.field_9229.nextInt(6001);
	}
}
