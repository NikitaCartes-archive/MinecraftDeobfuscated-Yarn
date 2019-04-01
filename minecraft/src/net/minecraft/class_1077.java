package net.minecraft;

import com.mojang.bridge.game.Language;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1077 implements Language, Comparable<class_1077> {
	private final String field_5326;
	private final String field_5329;
	private final String field_5327;
	private final boolean field_5328;

	public class_1077(String string, String string2, String string3, boolean bl) {
		this.field_5326 = string;
		this.field_5329 = string2;
		this.field_5327 = string3;
		this.field_5328 = bl;
	}

	@Override
	public String getCode() {
		return this.field_5326;
	}

	@Override
	public String getName() {
		return this.field_5327;
	}

	@Override
	public String getRegion() {
		return this.field_5329;
	}

	public boolean method_4672() {
		return this.field_5328;
	}

	public String toString() {
		return String.format("%s (%s)", this.field_5327, this.field_5329);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return !(object instanceof class_1077) ? false : this.field_5326.equals(((class_1077)object).field_5326);
		}
	}

	public int hashCode() {
		return this.field_5326.hashCode();
	}

	public int method_4673(class_1077 arg) {
		return this.field_5326.compareTo(arg.field_5326);
	}
}
