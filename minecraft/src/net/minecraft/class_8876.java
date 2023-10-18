package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class class_8876 extends DataFix {
	private static final String field_46771 = "WorldGenSettings";
	private static final List<String> field_46772 = List.of(
		"RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest"
	);

	public class_8876(Schema schema) {
		super(schema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"LevelLegacyWorldGenSettingsFix", this.getInputSchema().getType(TypeReferences.LEVEL), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Dynamic<?> dynamic2 = dynamic.get("WorldGenSettings").orElseEmptyMap();

					for (String string : field_46772) {
						Optional<? extends Dynamic<?>> optional = dynamic.get(string).result();
						if (optional.isPresent()) {
							dynamic = dynamic.remove(string);
							dynamic2 = dynamic2.set(string, (Dynamic<?>)optional.get());
						}
					}

					return dynamic.set("WorldGenSettings", dynamic2);
				})
		);
	}
}
