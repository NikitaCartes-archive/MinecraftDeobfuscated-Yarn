package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class class_4744 extends DataFix {
	private final String field_21814;
	private final Function<String, String> field_21815;

	public class_4744(Schema schema, boolean bl, String string, Function<String, String> function) {
		super(schema, bl);
		this.field_21814 = string;
		this.field_21815 = function;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			this.field_21814,
			this.getInputSchema().getType(TypeReferences.ADVANCEMENTS),
			typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.updateMapValues(pair -> {
						String string = ((Dynamic)pair.getFirst()).asString("");
						return pair.mapFirst(dynamic2 -> dynamic.createString((String)this.field_21815.apply(string)));
					}))
		);
	}
}
