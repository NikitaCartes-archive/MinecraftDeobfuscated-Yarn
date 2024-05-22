package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class EntityCustomNameToTextFix extends DataFix {
	public EntityCustomNameToTextFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		OpticFinder<String> opticFinder = DSL.fieldFinder("id", IdentifierNormalizingSchema.getIdentifierType());
		return this.fixTypeEverywhereTyped(
			"EntityCustomNameToComponentFix",
			this.getInputSchema().getType(TypeReferences.ENTITY),
			entityTyped -> entityTyped.update(DSL.remainderFinder(), entityDynamic -> {
					Optional<String> optional = entityTyped.getOptional(opticFinder);
					return optional.isPresent() && Objects.equals(optional.get(), "minecraft:commandblock_minecart") ? entityDynamic : fixCustomName(entityDynamic);
				})
		);
	}

	public static Dynamic<?> fixCustomName(Dynamic<?> entityDynamic) {
		String string = entityDynamic.get("CustomName").asString("");
		return string.isEmpty() ? entityDynamic.remove("CustomName") : entityDynamic.set("CustomName", TextFixes.text(entityDynamic.getOps(), string));
	}
}
