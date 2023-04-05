package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class SculkSensorRemoveCooldownPhaseFix extends DataFix {
	public SculkSensorRemoveCooldownPhaseFix(Schema outputSchema, boolean bl) {
		super(outputSchema, bl);
	}

	private static Dynamic<?> removeCooldownPhase(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("Name").asString().result();
		return !optional.equals(Optional.of("minecraft:sculk_sensor")) && !optional.equals(Optional.of("minecraft:calibrated_sculk_sensor"))
			? dynamic
			: dynamic.update("Properties", dynamicx -> {
				String string = dynamicx.get("sculk_sensor_phase").asString("");
				return string.equals("cooldown") ? dynamicx.set("sculk_sensor_phase", dynamicx.createString("inactive")) : dynamicx;
			});
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"sculk_sensor_remove_cooldown_phase_fix",
			this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
			typed -> typed.update(DSL.remainderFinder(), SculkSensorRemoveCooldownPhaseFix::removeCooldownPhase)
		);
	}
}
