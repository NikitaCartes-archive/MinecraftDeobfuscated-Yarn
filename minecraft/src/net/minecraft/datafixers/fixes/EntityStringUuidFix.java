package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.UUID;
import net.minecraft.datafixers.TypeReferences;

public class EntityStringUuidFix extends DataFix {
	public EntityStringUuidFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityStringUuidFix",
			this.getInputSchema().getType(TypeReferences.ENTITY),
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> {
						if (dynamic.get("UUID").flatMap(Dynamic::getStringValue).isPresent()) {
							UUID uUID = UUID.fromString(dynamic.getString("UUID"));
							return dynamic.remove("UUID")
								.set("UUIDMost", dynamic.createLong(uUID.getMostSignificantBits()))
								.set("UUIDLeast", dynamic.createLong(uUID.getLeastSignificantBits()));
						} else {
							return dynamic;
						}
					}
				)
		);
	}
}
