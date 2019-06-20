package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;

public class class_4299 extends class_1197 {
	public class_4299(Schema schema, boolean bl) {
		super(schema, bl, "OminousBannerBlockEntityRenameFix", class_1208.field_5727, "minecraft:banner");
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_20481);
	}

	private Dynamic<?> method_20481(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("CustomName").asString();
		if (optional.isPresent()) {
			String string = (String)optional.get();
			string = string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
			return dynamic.set("CustomName", dynamic.createString(string));
		} else {
			return dynamic;
		}
	}
}
