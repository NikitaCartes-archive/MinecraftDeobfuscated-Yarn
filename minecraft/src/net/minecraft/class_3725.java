package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class class_3725 extends class_1212 {
	public class_3725(Schema schema, boolean bl) {
		super("EntityCatSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> method_5164(String string, Dynamic<?> dynamic) {
		if (Objects.equals("minecraft:ocelot", string)) {
			int i = dynamic.get("CatType").asInt(0);
			if (i > 0 && i < 4) {
				dynamic = dynamic.set("CatType", dynamic.createInt(i));
				dynamic = dynamic.set("OwnerUUID", dynamic.createString(dynamic.get("OwnerUUID").asString("")));
				return Pair.of("minecraft:cat", dynamic);
			}
		}

		return Pair.of(string, dynamic);
	}
}
