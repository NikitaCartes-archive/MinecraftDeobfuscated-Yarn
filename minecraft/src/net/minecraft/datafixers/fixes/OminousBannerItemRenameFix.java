package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public class OminousBannerItemRenameFix extends DataFix {
	public OminousBannerItemRenameFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private Dynamic<?> fixBannerName(Dynamic<?> tag) {
		Optional<? extends Dynamic<?>> optional = tag.get("display").get();
		if (optional.isPresent()) {
			Dynamic<?> dynamic = (Dynamic<?>)optional.get();
			Optional<String> optional2 = dynamic.get("Name").asString();
			if (optional2.isPresent()) {
				String string = (String)optional2.get();
				string = string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
				dynamic = dynamic.set("Name", dynamic.createString(string));
			}

			return tag.set("display", dynamic);
		} else {
			return tag;
		}
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped("OminousBannerRenameFix", type, typed -> {
			Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
			if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:white_banner")) {
				Optional<? extends Typed<?>> optional2 = typed.getOptionalTyped(opticFinder2);
				if (optional2.isPresent()) {
					Typed<?> typed2 = (Typed<?>)optional2.get();
					Dynamic<?> dynamic = typed2.get(DSL.remainderFinder());
					return typed.set(opticFinder2, typed2.set(DSL.remainderFinder(), this.fixBannerName(dynamic)));
				}
			}

			return typed;
		});
	}
}
