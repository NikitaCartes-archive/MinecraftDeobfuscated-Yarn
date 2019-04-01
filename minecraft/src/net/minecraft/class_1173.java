package net.minecraft;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class class_1173 extends class_1211 {
	public class_1173(Schema schema, boolean bl) {
		super("EntityTippedArrowFix", schema, bl);
	}

	@Override
	protected String method_5163(String string) {
		return Objects.equals(string, "TippedArrow") ? "Arrow" : string;
	}
}
