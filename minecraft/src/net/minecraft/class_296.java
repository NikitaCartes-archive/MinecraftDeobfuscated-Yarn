package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_296 {
	private static final Logger field_1616 = LogManager.getLogger();
	private final class_296.class_297 field_1615;
	private final class_296.class_298 field_1614;
	private final int field_1613;
	private final int field_1612;

	public class_296(int i, class_296.class_297 arg, class_296.class_298 arg2, int j) {
		if (this.method_1383(i, arg2)) {
			this.field_1614 = arg2;
		} else {
			field_1616.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
			this.field_1614 = class_296.class_298.field_1636;
		}

		this.field_1615 = arg;
		this.field_1613 = i;
		this.field_1612 = j;
	}

	private final boolean method_1383(int i, class_296.class_298 arg) {
		return i == 0 || arg == class_296.class_298.field_1636;
	}

	public final class_296.class_297 method_1386() {
		return this.field_1615;
	}

	public final class_296.class_298 method_1382() {
		return this.field_1614;
	}

	public final int method_1384() {
		return this.field_1612;
	}

	public final int method_1385() {
		return this.field_1613;
	}

	public String toString() {
		return this.field_1612 + "," + this.field_1614.method_1392() + "," + this.field_1615.method_1389();
	}

	public final int method_1387() {
		return this.field_1615.method_1391() * this.field_1612;
	}

	public final boolean method_1388() {
		return this.field_1614 == class_296.class_298.field_1633;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_296 lv = (class_296)object;
			if (this.field_1612 != lv.field_1612) {
				return false;
			} else if (this.field_1613 != lv.field_1613) {
				return false;
			} else {
				return this.field_1615 != lv.field_1615 ? false : this.field_1614 == lv.field_1614;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.field_1615.hashCode();
		i = 31 * i + this.field_1614.hashCode();
		i = 31 * i + this.field_1613;
		return 31 * i + this.field_1612;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_297 {
		field_1623(4, "Float", 5126),
		field_1624(1, "Unsigned Byte", 5121),
		field_1621(1, "Byte", 5120),
		field_1622(2, "Unsigned Short", 5123),
		field_1625(2, "Short", 5122),
		field_1619(4, "Unsigned Int", 5125),
		field_1617(4, "Int", 5124);

		private final int field_1618;
		private final String field_1626;
		private final int field_1627;

		private class_297(int j, String string2, int k) {
			this.field_1618 = j;
			this.field_1626 = string2;
			this.field_1627 = k;
		}

		public int method_1391() {
			return this.field_1618;
		}

		public String method_1389() {
			return this.field_1626;
		}

		public int method_1390() {
			return this.field_1627;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_298 {
		field_1633("Position"),
		field_1635("Normal"),
		field_1632("Vertex Color"),
		field_1636("UV"),
		field_1634("Bone Matrix"),
		field_1628("Blend Weight"),
		field_1629("Padding");

		private final String field_1630;

		private class_298(String string2) {
			this.field_1630 = string2;
		}

		public String method_1392() {
			return this.field_1630;
		}
	}
}
