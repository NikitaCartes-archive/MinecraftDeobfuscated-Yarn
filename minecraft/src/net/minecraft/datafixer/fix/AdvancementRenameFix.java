package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class AdvancementRenameFix extends DataFix {
	private final String name;
	private final Function<String, String> renamer;

	public AdvancementRenameFix(Schema outputSchema, boolean changesType, String name, Function<String, String> renamer) {
		super(outputSchema, changesType);
		this.name = name;
		this.renamer = renamer;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			this.name,
			this.getInputSchema().getType(TypeReferences.ADVANCEMENTS),
			typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.updateMapValues(pair -> {
						String string = ((Dynamic)pair.getFirst()).asString("");
						return pair.mapFirst(dynamic2 -> dynamic.createString((String)this.renamer.apply(string)));
					}))
		);
	}
}
