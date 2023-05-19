package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.datafixer.TypeReferences;

public class EntityStringUuidFix extends DataFix {
	public EntityStringUuidFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityStringUuidFix",
			this.getInputSchema().getType(TypeReferences.ENTITY),
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> {
						Optional<String> optional = dynamic.get("UUID").asString().result();
						if (optional.isPresent()) {
							UUID uUID = UUID.fromString((String)optional.get());
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
