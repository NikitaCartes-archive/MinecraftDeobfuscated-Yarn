package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class CauldronRenameFix extends DataFix {
	public CauldronRenameFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private static Dynamic<?> rename(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("Name").asString().result();
		if (optional.equals(Optional.of("minecraft:cauldron"))) {
			Dynamic<?> dynamic2 = dynamic.get("Properties").orElseEmptyMap();
			return dynamic2.get("level").asString("0").equals("0")
				? dynamic.remove("Properties")
				: dynamic.set("Name", dynamic.createString("minecraft:water_cauldron"));
		} else {
			return dynamic;
		}
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"cauldron_rename_fix", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), CauldronRenameFix::rename)
		);
	}
}
