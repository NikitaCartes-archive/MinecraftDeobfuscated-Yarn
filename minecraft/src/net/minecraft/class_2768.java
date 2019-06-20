package net.minecraft;

public enum class_2768 implements class_3542 {
	field_12665(0, "north_south"),
	field_12674(1, "east_west"),
	field_12667(2, "ascending_east"),
	field_12666(3, "ascending_west"),
	field_12670(4, "ascending_north"),
	field_12668(5, "ascending_south"),
	field_12664(6, "south_east"),
	field_12671(7, "south_west"),
	field_12672(8, "north_west"),
	field_12663(9, "north_east");

	private final int field_12675;
	private final String field_12669;

	private class_2768(int j, String string2) {
		this.field_12675 = j;
		this.field_12669 = string2;
	}

	public int method_11896() {
		return this.field_12675;
	}

	public String toString() {
		return this.field_12669;
	}

	public boolean method_11897() {
		return this == field_12670 || this == field_12667 || this == field_12668 || this == field_12666;
	}

	@Override
	public String method_15434() {
		return this.field_12669;
	}
}
