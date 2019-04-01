package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Supplier;

public class class_1221 extends Schema {
	public class_1221(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			true,
			class_1208.field_5712,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						class_1208.field_5713.in(schema),
						"tag",
						DSL.optionalFields(
							"EntityTag",
							class_1208.field_5723.in(schema),
							"BlockEntityTag",
							class_1208.field_5727.in(schema),
							"CanDestroy",
							DSL.list(class_1208.field_5731.in(schema)),
							"CanPlaceOn",
							DSL.list(class_1208.field_5731.in(schema))
						)
					),
					class_1254.field_5747,
					HookFunction.IDENTITY
				)
		);
	}
}
