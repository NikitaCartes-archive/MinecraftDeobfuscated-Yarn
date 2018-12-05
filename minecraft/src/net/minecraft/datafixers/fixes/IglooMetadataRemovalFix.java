package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;

public class IglooMetadataRemovalFix extends DataFix {
	public IglooMetadataRemovalFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
		return this.writeFixAndRead("IglooMetadataRemovalFix", type, type2, IglooMetadataRemovalFix::method_4993);
	}

	private static <T> Dynamic<T> method_4993(Dynamic<T> dynamic) {
		boolean bl = (Boolean)dynamic.get("Children").flatMap(Dynamic::getStream).map(stream -> stream.allMatch(IglooMetadataRemovalFix::isIgloo)).orElse(false);
		return bl ? dynamic.set("id", dynamic.createString("Igloo")).remove("Children") : dynamic.update("Children", IglooMetadataRemovalFix::removeIgloos);
	}

	private static <T> Dynamic<T> removeIgloos(Dynamic<T> dynamic) {
		return (Dynamic<T>)dynamic.getStream().map(stream -> stream.filter(dynamicx -> !isIgloo(dynamicx))).map(dynamic::createList).orElse(dynamic);
	}

	private static boolean isIgloo(Dynamic<?> dynamic) {
		return dynamic.getString("id").equals("Iglu");
	}
}
