package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class class_1176 extends DataFix {
	public class_1176(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5724);
		Type<?> type2 = this.getOutputSchema().getType(class_1208.field_5724);
		return this.writeFixAndRead("IglooMetadataRemovalFix", type, type2, class_1176::method_4993);
	}

	private static <T> Dynamic<T> method_4993(Dynamic<T> dynamic) {
		boolean bl = (Boolean)dynamic.get("Children").asStreamOpt().map(stream -> stream.allMatch(class_1176::method_4997)).orElse(false);
		return bl ? dynamic.set("id", dynamic.createString("Igloo")).remove("Children") : dynamic.update("Children", class_1176::method_4996);
	}

	private static <T> Dynamic<T> method_4996(Dynamic<T> dynamic) {
		return (Dynamic<T>)dynamic.asStreamOpt().map(stream -> stream.filter(dynamicx -> !method_4997(dynamicx))).map(dynamic::createList).orElse(dynamic);
	}

	private static boolean method_4997(Dynamic<?> dynamic) {
		return dynamic.get("id").asString("").equals("Iglu");
	}
}
