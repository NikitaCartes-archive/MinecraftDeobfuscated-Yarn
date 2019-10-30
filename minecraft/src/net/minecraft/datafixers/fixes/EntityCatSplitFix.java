package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityCatSplitFix extends EntitySimpleTransformFix {
	public EntityCatSplitFix(Schema outputSchema, boolean changesType) {
		super("EntityCatSplitFix", outputSchema, changesType);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag) {
		if (Objects.equals("minecraft:ocelot", choice)) {
			int i = tag.get("CatType").asInt(0);
			if (i == 0) {
				String string = tag.get("Owner").asString("");
				String string2 = tag.get("OwnerUUID").asString("");
				if (string.length() > 0 || string2.length() > 0) {
					tag.set("Trusting", tag.createBoolean(true));
				}
			} else if (i > 0 && i < 4) {
				tag = tag.set("CatType", tag.createInt(i));
				tag = tag.set("OwnerUUID", tag.createString(tag.get("OwnerUUID").asString("")));
				return Pair.of("minecraft:cat", tag);
			}
		}

		return Pair.of(choice, tag);
	}
}
