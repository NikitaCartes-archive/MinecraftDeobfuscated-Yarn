package net.minecraft.datafixers.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.util.Identifier;

public class EntityPaintingMotiveFix extends ChoiceFix {
	private static final Map<String, String> paintings = DataFixUtils.make(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("donkeykong", "donkey_kong");
		hashMap.put("burningskull", "burning_skull");
		hashMap.put("skullandroses", "skull_and_roses");
	});

	public EntityPaintingMotiveFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityPaintingMotiveFix", TypeReferences.ENTITY, "minecraft:painting");
	}

	public Dynamic<?> method_15723(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("Motive").flatMap(Dynamic::getStringValue);
		if (optional.isPresent()) {
			String string = ((String)optional.get()).toLowerCase(Locale.ROOT);
			return dynamic.set("Motive", dynamic.createString(new Identifier((String)paintings.getOrDefault(string, string)).toString()));
		} else {
			return dynamic;
		}
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_15723);
	}
}
