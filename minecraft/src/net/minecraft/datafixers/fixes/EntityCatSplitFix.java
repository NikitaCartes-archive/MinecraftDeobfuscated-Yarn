package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityCatSplitFix extends EntitySimpleTransformFix {
	public EntityCatSplitFix(Schema schema, boolean bl) {
		super("EntityCatSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic) {
		if (Objects.equals("minecraft:ocelot", string)) {
			int i = dynamic.getInt("CatType");
			if (i > 0 && i < 4) {
				dynamic = dynamic.set("CatType", dynamic.createInt(i));
				dynamic = dynamic.set("OwnerUUID", dynamic.createString(dynamic.getString("OwnerUUID")));
				return Pair.of("minecraft:cat", dynamic);
			}
		}

		return Pair.of(string, dynamic);
	}
}
