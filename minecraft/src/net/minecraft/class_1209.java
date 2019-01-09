package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.Stream;

public class class_1209 extends DataFix {
	public class_1209(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.writeFixAndRead(
			"SavedDataVillageCropFix", this.getInputSchema().getType(class_1208.field_5724), this.getOutputSchema().getType(class_1208.field_5724), this::method_5152
		);
	}

	private <T> Dynamic<T> method_5152(Dynamic<T> dynamic) {
		return dynamic.update("Children", class_1209::method_5157);
	}

	private static <T> Dynamic<T> method_5157(Dynamic<T> dynamic) {
		return (Dynamic<T>)dynamic.asStreamOpt().map(class_1209::method_5151).map(dynamic::createList).orElse(dynamic);
	}

	private static Stream<? extends Dynamic<?>> method_5151(Stream<? extends Dynamic<?>> stream) {
		return stream.map(dynamic -> {
			String string = dynamic.get("id").asString("");
			if ("ViF".equals(string)) {
				return method_5154(dynamic);
			} else {
				return "ViDF".equals(string) ? method_5155(dynamic) : dynamic;
			}
		});
	}

	private static <T> Dynamic<T> method_5154(Dynamic<T> dynamic) {
		dynamic = method_5156(dynamic, "CA");
		return method_5156(dynamic, "CB");
	}

	private static <T> Dynamic<T> method_5155(Dynamic<T> dynamic) {
		dynamic = method_5156(dynamic, "CA");
		dynamic = method_5156(dynamic, "CB");
		dynamic = method_5156(dynamic, "CC");
		return method_5156(dynamic, "CD");
	}

	private static <T> Dynamic<T> method_5156(Dynamic<T> dynamic, String string) {
		return dynamic.get(string).asNumber().isPresent() ? dynamic.set(string, class_3580.method_15594(dynamic.get(string).asInt(0) << 4)) : dynamic;
	}
}
