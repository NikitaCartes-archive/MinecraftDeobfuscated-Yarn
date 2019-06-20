package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum class_2355 {
	field_11069(class_2350.field_11043),
	field_11074(class_2350.field_11043, class_2350.field_11034),
	field_11075(class_2350.field_11034),
	field_11070(class_2350.field_11035, class_2350.field_11034),
	field_11073(class_2350.field_11035),
	field_11068(class_2350.field_11035, class_2350.field_11039),
	field_11072(class_2350.field_11039),
	field_11076(class_2350.field_11043, class_2350.field_11039);

	private static final int field_11067 = 1 << field_11076.ordinal();
	private static final int field_11084 = 1 << field_11072.ordinal();
	private static final int field_11083 = 1 << field_11068.ordinal();
	private static final int field_11082 = 1 << field_11073.ordinal();
	private static final int field_11081 = 1 << field_11070.ordinal();
	private static final int field_11080 = 1 << field_11075.ordinal();
	private static final int field_11079 = 1 << field_11074.ordinal();
	private static final int field_11077 = 1 << field_11069.ordinal();
	private final Set<class_2350> field_11078;

	private class_2355(class_2350... args) {
		this.field_11078 = Sets.immutableEnumSet(Arrays.asList(args));
	}

	public Set<class_2350> method_10186() {
		return this.field_11078;
	}
}
