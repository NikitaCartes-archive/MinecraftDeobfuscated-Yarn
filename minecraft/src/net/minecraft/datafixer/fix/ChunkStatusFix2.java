package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class ChunkStatusFix2 extends DataFix {
	private static final Map<String, String> statusMap = ImmutableMap.<String, String>builder()
		.put("structure_references", "empty")
		.put("biomes", "empty")
		.put("base", "surface")
		.put("carved", "carvers")
		.put("liquid_carved", "liquid_carvers")
		.put("decorated", "features")
		.put("lighted", "light")
		.put("mobs_spawned", "spawn")
		.put("finalized", "heightmaps")
		.put("fullchunk", "full")
		.build();

	public ChunkStatusFix2(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = type.findFieldType("Level");
		OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type2);
		return this.fixTypeEverywhereTyped(
			"ChunkStatusFix2", type, this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> typed.updateTyped(opticFinder, typedx -> {
					Dynamic<?> dynamic = typedx.get(DSL.remainderFinder());
					String string = dynamic.get("Status").asString("empty");
					String string2 = (String)statusMap.getOrDefault(string, "empty");
					return Objects.equals(string, string2) ? typedx : typedx.set(DSL.remainderFinder(), dynamic.set("Status", dynamic.createString(string2)));
				})
		);
	}
}
