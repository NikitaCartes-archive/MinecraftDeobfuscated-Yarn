package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class class_3607 extends class_1197 {
	private static final Map<String, String> field_15898 = DataFixUtils.make(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("donkeykong", "donkey_kong");
		hashMap.put("burningskull", "burning_skull");
		hashMap.put("skullandroses", "skull_and_roses");
	});

	public class_3607(Schema schema, boolean bl) {
		super(schema, bl, "EntityPaintingMotiveFix", class_1208.field_5729, "minecraft:painting");
	}

	public Dynamic<?> method_15723(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("Motive").asString();
		if (optional.isPresent()) {
			String string = ((String)optional.get()).toLowerCase(Locale.ROOT);
			return dynamic.set("Motive", dynamic.createString(new class_2960((String)field_15898.getOrDefault(string, string)).toString()));
		} else {
			return dynamic;
		}
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_15723);
	}
}
