package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class class_3605 extends DataFix {
	private static final List<String> field_15896 = Lists.<String>newArrayList("MinecartRideable", "MinecartChest", "MinecartFurnace");

	public class_3605(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(class_1208.field_5729);
		TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(class_1208.field_5729);
		return this.fixTypeEverywhere(
			"EntityMinecartIdentifiersFix",
			taggedChoiceType,
			taggedChoiceType2,
			dynamicOps -> pair -> {
					if (!Objects.equals(pair.getFirst(), "Minecart")) {
						return pair;
					} else {
						Typed<? extends Pair<String, ?>> typed = (Typed<? extends Pair<String, ?>>)taggedChoiceType.point(dynamicOps, "Minecart", pair.getSecond())
							.orElseThrow(IllegalStateException::new);
						Dynamic<?> dynamic = typed.getOrCreate(DSL.remainderFinder());
						int i = dynamic.get("Type").asInt(0);
						String string;
						if (i > 0 && i < field_15896.size()) {
							string = (String)field_15896.get(i);
						} else {
							string = "MinecartRideable";
						}

						return Pair.of(
							string,
							((Optional)((Type)taggedChoiceType2.types().get(string)).read(typed.write()).getSecond())
								.orElseThrow(() -> new IllegalStateException("Could not read the new minecart."))
						);
					}
				}
		);
	}
}
