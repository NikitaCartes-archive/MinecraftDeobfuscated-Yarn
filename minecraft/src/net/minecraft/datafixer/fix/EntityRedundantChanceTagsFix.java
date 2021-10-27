package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;

public class EntityRedundantChanceTagsFix extends DataFix {
	private static final Codec<List<Float>> field_25695 = Codec.FLOAT.listOf();

	public EntityRedundantChanceTagsFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityRedundantChanceTagsFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					if (hasZeroDropChance(dynamic.get("HandDropChances"), 2)) {
						dynamic = dynamic.remove("HandDropChances");
					}

					if (hasZeroDropChance(dynamic.get("ArmorDropChances"), 4)) {
						dynamic = dynamic.remove("ArmorDropChances");
					}

					return dynamic;
				})
		);
	}

	private static boolean hasZeroDropChance(OptionalDynamic<?> optionalDynamic, int i) {
		return (Boolean)optionalDynamic.flatMap(field_25695::parse)
			.map(list -> list.size() == i && list.stream().allMatch(float_ -> float_ == 0.0F))
			.result()
			.orElse(false);
	}
}
