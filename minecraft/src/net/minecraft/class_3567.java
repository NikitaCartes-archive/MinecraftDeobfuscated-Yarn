package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;

public class class_3567 extends DataFix {
	public class_3567(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		OpticFinder<String> opticFinder = DSL.fieldFinder("id", DSL.namespacedString());
		return this.fixTypeEverywhereTyped(
			"BlockEntityCustomNameToComponentFix", this.getInputSchema().getType(class_1208.field_5727), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Optional<String> optional = typed.getOptional(opticFinder);
					return optional.isPresent() && Objects.equals(optional.get(), "minecraft:command_block") ? dynamic : class_3597.method_15697(dynamic);
				})
		);
	}
}
