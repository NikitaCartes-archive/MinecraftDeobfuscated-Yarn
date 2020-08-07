package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class PlayerUuidFix extends AbstractUuidFix {
	public PlayerUuidFix(Schema outputSchema) {
		super(outputSchema, TypeReferences.PLAYER);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"PlayerUUIDFix",
			this.getInputSchema().getType(this.typeReference),
			typed -> {
				OpticFinder<?> opticFinder = typed.getType().findField("RootVehicle");
				return typed.updateTyped(
						opticFinder,
						opticFinder.type(),
						typedx -> typedx.update(DSL.remainderFinder(), dynamic -> (Dynamic)updateRegularMostLeast(dynamic, "Attach", "Attach").orElse(dynamic))
					)
					.update(DSL.remainderFinder(), dynamic -> EntityUuidFix.updateSelfUuid(EntityUuidFix.updateLiving(dynamic)));
			}
		);
	}
}
