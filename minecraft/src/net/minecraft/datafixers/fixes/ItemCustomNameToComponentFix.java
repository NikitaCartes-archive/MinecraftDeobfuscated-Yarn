package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ItemCustomNameToComponentFix extends DataFix {
	public ItemCustomNameToComponentFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private Dynamic<?> fixCustomName(Dynamic<?> tag) {
		Optional<? extends Dynamic<?>> optional = tag.get("display").get();
		if (optional.isPresent()) {
			Dynamic<?> dynamic = (Dynamic<?>)optional.get();
			Optional<String> optional2 = dynamic.get("Name").asString();
			if (optional2.isPresent()) {
				dynamic = dynamic.set("Name", dynamic.createString(Text.Serializer.toJson(new LiteralText((String)optional2.get()))));
			} else {
				Optional<String> optional3 = dynamic.get("LocName").asString();
				if (optional3.isPresent()) {
					dynamic = dynamic.set("Name", dynamic.createString(Text.Serializer.toJson(new TranslatableText((String)optional3.get()))));
					dynamic = dynamic.remove("LocName");
				}
			}

			return tag.set("display", dynamic);
		} else {
			return tag;
		}
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemCustomNameToComponentFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::fixCustomName))
		);
	}
}
