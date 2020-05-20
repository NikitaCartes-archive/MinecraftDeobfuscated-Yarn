package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class BiomeRenameFix extends DataFix {
	private final String name;
	private final Map<String, String> renames;

	public BiomeRenameFix(Schema outputSchema, boolean changesType, String name, Map<String, String> changes) {
		super(outputSchema, changesType);
		this.renames = changes;
		this.name = name;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(TypeReferences.BIOME.typeName(), IdentifierNormalizingSchema.method_28295());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.BIOME))) {
			throw new IllegalStateException("Biome type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(string -> (String)this.renames.getOrDefault(string, string)));
		}
	}
}
