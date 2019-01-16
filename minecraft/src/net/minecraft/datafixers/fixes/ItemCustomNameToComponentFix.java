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
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class ItemCustomNameToComponentFix extends DataFix {
	public ItemCustomNameToComponentFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private Dynamic<?> method_5001(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("display").get();
		if (optional.isPresent()) {
			Dynamic<?> dynamic2 = (Dynamic<?>)optional.get();
			Optional<String> optional2 = dynamic2.get("Name").asString();
			if (optional2.isPresent()) {
				dynamic2 = dynamic2.set("Name", dynamic2.createString(TextComponent.Serializer.toJsonString(new StringTextComponent((String)optional2.get()))));
			} else {
				Optional<String> optional3 = dynamic2.get("LocName").asString();
				if (optional3.isPresent()) {
					dynamic2 = dynamic2.set("Name", dynamic2.createString(TextComponent.Serializer.toJsonString(new TranslatableTextComponent((String)optional3.get()))));
					dynamic2 = dynamic2.remove("LocName");
				}
			}

			return dynamic.set("display", dynamic2);
		} else {
			return dynamic;
		}
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemCustomNameToComponentFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_5001))
		);
	}
}
