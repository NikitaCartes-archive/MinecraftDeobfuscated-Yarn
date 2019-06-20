package net.minecraft;

import com.google.common.collect.ImmutableSet;

public class class_3852 {
	public static final class_3852 field_17051 = method_16926("none", class_4158.field_18502);
	public static final class_3852 field_17052 = method_16926("armorer", class_4158.field_18503);
	public static final class_3852 field_17053 = method_16926("butcher", class_4158.field_18504);
	public static final class_3852 field_17054 = method_16926("cartographer", class_4158.field_18505);
	public static final class_3852 field_17055 = method_16926("cleric", class_4158.field_18506);
	public static final class_3852 field_17056 = method_19197(
		"farmer",
		class_4158.field_18507,
		ImmutableSet.of(class_1802.field_8861, class_1802.field_8317, class_1802.field_8309),
		ImmutableSet.of(class_2246.field_10362)
	);
	public static final class_3852 field_17057 = method_16926("fisherman", class_4158.field_18508);
	public static final class_3852 field_17058 = method_16926("fletcher", class_4158.field_18509);
	public static final class_3852 field_17059 = method_16926("leatherworker", class_4158.field_18510);
	public static final class_3852 field_17060 = method_16926("librarian", class_4158.field_18511);
	public static final class_3852 field_17061 = method_16926("mason", class_4158.field_18512);
	public static final class_3852 field_17062 = method_16926("nitwit", class_4158.field_18513);
	public static final class_3852 field_17063 = method_16926("shepherd", class_4158.field_18514);
	public static final class_3852 field_17064 = method_16926("toolsmith", class_4158.field_18515);
	public static final class_3852 field_17065 = method_16926("weaponsmith", class_4158.field_18516);
	private final String field_18541;
	private final class_4158 field_18542;
	private final ImmutableSet<class_1792> field_18543;
	private final ImmutableSet<class_2248> field_18880;

	private class_3852(String string, class_4158 arg, ImmutableSet<class_1792> immutableSet, ImmutableSet<class_2248> immutableSet2) {
		this.field_18541 = string;
		this.field_18542 = arg;
		this.field_18543 = immutableSet;
		this.field_18880 = immutableSet2;
	}

	public class_4158 method_19198() {
		return this.field_18542;
	}

	public ImmutableSet<class_1792> method_19199() {
		return this.field_18543;
	}

	public ImmutableSet<class_2248> method_19630() {
		return this.field_18880;
	}

	public String toString() {
		return this.field_18541;
	}

	static class_3852 method_16926(String string, class_4158 arg) {
		return method_19197(string, arg, ImmutableSet.of(), ImmutableSet.of());
	}

	static class_3852 method_19197(String string, class_4158 arg, ImmutableSet<class_1792> immutableSet, ImmutableSet<class_2248> immutableSet2) {
		return class_2378.method_10230(class_2378.field_17167, new class_2960(string), new class_3852(string, arg, immutableSet, immutableSet2));
	}
}
