package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;

public class class_3597 extends DataFix {
	public class_3597(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		OpticFinder<String> opticFinder = DSL.fieldFinder("id", DSL.namespacedString());
		return this.fixTypeEverywhereTyped(
			"EntityCustomNameToComponentFix", this.getInputSchema().getType(class_1208.field_5729), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Optional<String> optional = typed.getOptional(opticFinder);
					return optional.isPresent() && Objects.equals(optional.get(), "minecraft:commandblock_minecart") ? dynamic : method_15697(dynamic);
				})
		);
	}

	public static Dynamic<?> method_15697(Dynamic<?> dynamic) {
		String string = dynamic.get("CustomName").asString("");
		return string.isEmpty()
			? dynamic.remove("CustomName")
			: dynamic.set("CustomName", dynamic.createString(class_2561.class_2562.method_10867(new class_2585(string))));
	}
}
