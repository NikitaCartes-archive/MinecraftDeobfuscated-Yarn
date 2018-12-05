package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public class HeightmapRenamingFix extends DataFix {
	public HeightmapRenamingFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		OpticFinder<?> opticFinder = type.findField("Level");
		return this.fixTypeEverywhereTyped(
			"HeightmapRenamingFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_4998))
		);
	}

	private Dynamic<?> method_4998(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("Heightmaps");
		if (!optional.isPresent()) {
			return dynamic;
		} else {
			Dynamic<?> dynamic2 = (Dynamic<?>)optional.get();
			Optional<? extends Dynamic<?>> optional2 = dynamic2.get("LIQUID");
			if (optional2.isPresent()) {
				dynamic2 = dynamic2.remove("LIQUID");
				dynamic2 = dynamic2.set("WORLD_SURFACE_WG", (Dynamic<?>)optional2.get());
			}

			Optional<? extends Dynamic<?>> optional3 = dynamic2.get("SOLID");
			if (optional3.isPresent()) {
				dynamic2 = dynamic2.remove("SOLID");
				dynamic2 = dynamic2.set("OCEAN_FLOOR_WG", (Dynamic<?>)optional3.get());
				dynamic2 = dynamic2.set("OCEAN_FLOOR", (Dynamic<?>)optional3.get());
			}

			Optional<? extends Dynamic<?>> optional4 = dynamic2.get("LIGHT");
			if (optional4.isPresent()) {
				dynamic2 = dynamic2.remove("LIGHT");
				dynamic2 = dynamic2.set("LIGHT_BLOCKING", (Dynamic<?>)optional4.get());
			}

			Optional<? extends Dynamic<?>> optional5 = dynamic2.get("RAIN");
			if (optional5.isPresent()) {
				dynamic2 = dynamic2.remove("RAIN");
				dynamic2 = dynamic2.set("MOTION_BLOCKING", (Dynamic<?>)optional5.get());
				dynamic2 = dynamic2.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic<?>)optional5.get());
			}

			return dynamic.set("Heightmaps", dynamic2);
		}
	}
}
