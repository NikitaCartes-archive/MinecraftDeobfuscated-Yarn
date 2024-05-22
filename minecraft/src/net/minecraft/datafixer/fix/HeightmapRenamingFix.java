package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class HeightmapRenamingFix extends DataFix {
	public HeightmapRenamingFix(Schema outputSchema, boolean changesTyped) {
		super(outputSchema, changesTyped);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		OpticFinder<?> opticFinder = type.findField("Level");
		return this.fixTypeEverywhereTyped(
			"HeightmapRenamingFix",
			type,
			chunkTyped -> chunkTyped.updateTyped(opticFinder, levelTyped -> levelTyped.update(DSL.remainderFinder(), this::renameHeightmapTags))
		);
	}

	private Dynamic<?> renameHeightmapTags(Dynamic<?> levelDynamic) {
		Optional<? extends Dynamic<?>> optional = levelDynamic.get("Heightmaps").result();
		if (optional.isEmpty()) {
			return levelDynamic;
		} else {
			Dynamic<?> dynamic = (Dynamic<?>)optional.get();
			Optional<? extends Dynamic<?>> optional2 = dynamic.get("LIQUID").result();
			if (optional2.isPresent()) {
				dynamic = dynamic.remove("LIQUID");
				dynamic = dynamic.set("WORLD_SURFACE_WG", (Dynamic<?>)optional2.get());
			}

			Optional<? extends Dynamic<?>> optional3 = dynamic.get("SOLID").result();
			if (optional3.isPresent()) {
				dynamic = dynamic.remove("SOLID");
				dynamic = dynamic.set("OCEAN_FLOOR_WG", (Dynamic<?>)optional3.get());
				dynamic = dynamic.set("OCEAN_FLOOR", (Dynamic<?>)optional3.get());
			}

			Optional<? extends Dynamic<?>> optional4 = dynamic.get("LIGHT").result();
			if (optional4.isPresent()) {
				dynamic = dynamic.remove("LIGHT");
				dynamic = dynamic.set("LIGHT_BLOCKING", (Dynamic<?>)optional4.get());
			}

			Optional<? extends Dynamic<?>> optional5 = dynamic.get("RAIN").result();
			if (optional5.isPresent()) {
				dynamic = dynamic.remove("RAIN");
				dynamic = dynamic.set("MOTION_BLOCKING", (Dynamic<?>)optional5.get());
				dynamic = dynamic.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic<?>)optional5.get());
			}

			return levelDynamic.set("Heightmaps", dynamic);
		}
	}
}
