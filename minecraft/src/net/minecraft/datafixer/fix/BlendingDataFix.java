package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.math.ChunkSectionPos;

public class BlendingDataFix extends DataFix {
	private final String name;
	private static final Set<String> field_37415 = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:biomes");

	public BlendingDataFix(Schema schema, String name) {
		super(schema, false);
		this.name = name;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(TypeReferences.CHUNK);
		return this.fixTypeEverywhereTyped(this.name, type, typed -> typed.update(DSL.remainderFinder(), BlendingDataFix::method_41312));
	}

	private static Dynamic<?> method_41312(Dynamic<?> dynamic) {
		dynamic = dynamic.remove("blending_data");
		Optional<? extends Dynamic<?>> optional = dynamic.get("Status").result();
		if (optional.isPresent()) {
			String string = IdentifierNormalizingSchema.normalize(((Dynamic)optional.get()).asString("empty"));
			Optional<? extends Dynamic<?>> optional2 = dynamic.get("below_zero_retrogen").result();
			if (!field_37415.contains(string)) {
				dynamic = method_41313(dynamic, 384, -64);
			} else if (optional2.isPresent()) {
				Dynamic<?> dynamic2 = (Dynamic<?>)optional2.get();
				String string2 = IdentifierNormalizingSchema.normalize(dynamic2.get("target_status").asString("empty"));
				if (!field_37415.contains(string2)) {
					dynamic = method_41313(dynamic, 256, 0);
				}
			}
		}

		return dynamic;
	}

	private static Dynamic<?> method_41313(Dynamic<?> dynamic, int i, int j) {
		return dynamic.set(
			"blending_data",
			dynamic.createMap(
				Map.of(
					dynamic.createString("min_section"),
					dynamic.createInt(ChunkSectionPos.getSectionCoord(j)),
					dynamic.createString("max_section"),
					dynamic.createInt(ChunkSectionPos.getSectionCoord(j + i))
				)
			)
		);
	}
}
