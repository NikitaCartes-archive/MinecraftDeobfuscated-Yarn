package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemBannerColorFix extends DataFix {
	public ItemBannerColorFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		OpticFinder<?> opticFinder2 = type.findField("tag");
		OpticFinder<?> opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
		return this.fixTypeEverywhereTyped(
			"ItemBannerColorFix",
			type,
			itemStackTyped -> {
				Optional<Pair<String, String>> optional = itemStackTyped.getOptional(opticFinder);
				if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:banner")) {
					Dynamic<?> dynamic = itemStackTyped.get(DSL.remainderFinder());
					Optional<? extends Typed<?>> optional2 = itemStackTyped.getOptionalTyped(opticFinder2);
					if (optional2.isPresent()) {
						Typed<?> typed = (Typed<?>)optional2.get();
						Optional<? extends Typed<?>> optional3 = typed.getOptionalTyped(opticFinder3);
						if (optional3.isPresent()) {
							Typed<?> typed2 = (Typed<?>)optional3.get();
							Dynamic<?> dynamic2 = typed.get(DSL.remainderFinder());
							Dynamic<?> dynamic3 = typed2.getOrCreate(DSL.remainderFinder());
							if (dynamic3.get("Base").asNumber().result().isPresent()) {
								dynamic = dynamic.set("Damage", dynamic.createShort((short)(dynamic3.get("Base").asInt(0) & 15)));
								Optional<? extends Dynamic<?>> optional4 = dynamic2.get("display").result();
								if (optional4.isPresent()) {
									Dynamic<?> dynamic4 = (Dynamic<?>)optional4.get();
									Dynamic<?> dynamic5 = dynamic4.createMap(
										ImmutableMap.of(dynamic4.createString("Lore"), dynamic4.createList(Stream.of(dynamic4.createString("(+NBT"))))
									);
									if (Objects.equals(dynamic4, dynamic5)) {
										return itemStackTyped.set(DSL.remainderFinder(), dynamic);
									}
								}

								dynamic3.remove("Base");
								return itemStackTyped.set(DSL.remainderFinder(), dynamic).set(opticFinder2, typed.set(opticFinder3, typed2.set(DSL.remainderFinder(), dynamic3)));
							}
						}
					}

					return itemStackTyped.set(DSL.remainderFinder(), dynamic);
				} else {
					return itemStackTyped;
				}
			}
		);
	}
}
