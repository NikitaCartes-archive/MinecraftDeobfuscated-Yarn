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
	private static final Set<String> SKIP_BLENDING_STATUSES = Set.of(
		"minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:biomes"
	);

	public BlendingDataFix(Schema schema) {
		super(schema, false);
		this.name = "Blending Data Fix v" + schema.getVersionKey();
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(TypeReferences.CHUNK);
		return this.fixTypeEverywhereTyped(this.name, type, typed -> typed.update(DSL.remainderFinder(), BlendingDataFix::update));
	}

	private static Dynamic<?> update(Dynamic<?> dynamic) {
		dynamic = dynamic.remove("blending_data");
		Optional<? extends Dynamic<?>> optional = dynamic.get("Status").result();
		if (optional.isPresent()) {
			String string = IdentifierNormalizingSchema.normalize(((Dynamic)optional.get()).asString("empty"));
			Optional<? extends Dynamic<?>> optional2 = dynamic.get("below_zero_retrogen").result();
			if (!SKIP_BLENDING_STATUSES.contains(string)) {
				dynamic = setSections(dynamic, 384, -64);
			} else if (optional2.isPresent()) {
				Dynamic<?> dynamic2 = (Dynamic<?>)optional2.get();
				String string2 = IdentifierNormalizingSchema.normalize(dynamic2.get("target_status").asString("empty"));
				if (!SKIP_BLENDING_STATUSES.contains(string2)) {
					dynamic = setSections(dynamic, 256, 0);
				}
			}
		}

		return dynamic;
	}

	private static Dynamic<?> setSections(Dynamic<?> dynamic, int height, int minY) {
		return dynamic.set(
			"blending_data",
			dynamic.createMap(
				Map.of(
					dynamic.createString("min_section"),
					dynamic.createInt(ChunkSectionPos.getSectionCoord(minY)),
					dynamic.createString("max_section"),
					dynamic.createInt(ChunkSectionPos.getSectionCoord(minY + height))
				)
			)
		);
	}
}
