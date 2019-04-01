package net.minecraft;

import java.util.EnumSet;

public abstract class class_1352 {
	private final EnumSet<class_1352.class_4134> field_6451 = EnumSet.noneOf(class_1352.class_4134.class);

	public abstract boolean method_6264();

	public boolean method_6266() {
		return this.method_6264();
	}

	public boolean method_6267() {
		return true;
	}

	public void method_6269() {
	}

	public void method_6270() {
	}

	public void method_6268() {
	}

	public void method_6265(EnumSet<class_1352.class_4134> enumSet) {
		this.field_6451.clear();
		this.field_6451.addAll(enumSet);
	}

	public EnumSet<class_1352.class_4134> method_6271() {
		return this.field_6451;
	}

	public static enum class_4134 {
		field_18405,
		field_18406,
		field_18407,
		field_18408;
	}
}
