package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;

public class IglooMetadataRemovalFix extends DataFix {
	public IglooMetadataRemovalFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
		return this.writeFixAndRead("IglooMetadataRemovalFix", type, type2, IglooMetadataRemovalFix::removeMetadata);
	}

	private static <T> Dynamic<T> removeMetadata(Dynamic<T> tag) {
		boolean bl = (Boolean)tag.get("Children").asStreamOpt().map(stream -> stream.allMatch(IglooMetadataRemovalFix::isIgloo)).orElse(false);
		return bl ? tag.set("id", tag.createString("Igloo")).remove("Children") : tag.update("Children", IglooMetadataRemovalFix::removeIgloos);
	}

	private static <T> Dynamic<T> removeIgloos(Dynamic<T> tag) {
		return (Dynamic<T>)tag.asStreamOpt().map(stream -> stream.filter(dynamic -> !isIgloo(dynamic))).map(tag::createList).orElse(tag);
	}

	private static boolean isIgloo(Dynamic<?> tag) {
		return tag.get("id").asString("").equals("Iglu");
	}
}
