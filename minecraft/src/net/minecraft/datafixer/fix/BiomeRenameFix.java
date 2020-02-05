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

public class BiomeRenameFix extends DataFix {
	public final Map<String, String> renames;

	public BiomeRenameFix(Schema outputSchema, boolean changesType, Map<String, String> renames) {
		super(outputSchema, changesType);
		this.renames = renames;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(TypeReferences.BIOME.typeName(), DSL.namespacedString());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.BIOME))) {
			throw new IllegalStateException("Biome type is not what was expected.");
		} else {
			return this.fixTypeEverywhere("Biomes fix", type, dynamicOps -> pair -> pair.mapSecond(string -> (String)this.renames.getOrDefault(string, string)));
		}
	}
}
