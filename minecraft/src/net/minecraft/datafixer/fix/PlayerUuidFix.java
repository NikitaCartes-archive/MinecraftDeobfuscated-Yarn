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
			playerTyped -> {
				OpticFinder<?> opticFinder = playerTyped.getType().findField("RootVehicle");
				return playerTyped.updateTyped(
						opticFinder,
						opticFinder.type(),
						rootVehicleTyped -> rootVehicleTyped.update(
								DSL.remainderFinder(), rootVehicleDynamic -> (Dynamic)updateRegularMostLeast(rootVehicleDynamic, "Attach", "Attach").orElse(rootVehicleDynamic)
							)
					)
					.update(DSL.remainderFinder(), playerDynamic -> EntityUuidFix.updateSelfUuid(EntityUuidFix.updateLiving(playerDynamic)));
			}
		);
	}
}
