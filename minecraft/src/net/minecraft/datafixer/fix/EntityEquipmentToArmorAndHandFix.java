package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class EntityEquipmentToArmorAndHandFix extends DataFix {
	public EntityEquipmentToArmorAndHandFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixEquipment(this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK));
	}

	private <IS> TypeRewriteRule fixEquipment(Type<IS> itemStackType) {
		Type<Pair<Either<List<IS>, Unit>, Dynamic<?>>> type = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(itemStackType))), DSL.remainderType());
		Type<Pair<Either<List<IS>, Unit>, Pair<Either<List<IS>, Unit>, Pair<Either<IS, Unit>, Dynamic<?>>>>> type2 = DSL.and(
			DSL.optional(DSL.field("ArmorItems", DSL.list(itemStackType))),
			DSL.optional(DSL.field("HandItems", DSL.list(itemStackType))),
			DSL.optional(DSL.field("body_armor_item", itemStackType)),
			DSL.remainderType()
		);
		OpticFinder<Pair<Either<List<IS>, Unit>, Dynamic<?>>> opticFinder = DSL.typeFinder(type);
		OpticFinder<List<IS>> opticFinder2 = DSL.fieldFinder("Equipment", DSL.list(itemStackType));
		return this.fixTypeEverywhereTyped(
			"EntityEquipmentToArmorAndHandFix",
			this.getInputSchema().getType(TypeReferences.ENTITY),
			this.getOutputSchema().getType(TypeReferences.ENTITY),
			entityTyped -> {
				Either<List<IS>, Unit> either = Either.right(DSL.unit());
				Either<List<IS>, Unit> either2 = Either.right(DSL.unit());
				Either<IS, Unit> either3 = Either.right(DSL.unit());
				Dynamic<?> dynamic = entityTyped.getOrCreate(DSL.remainderFinder());
				Optional<List<IS>> optional = entityTyped.getOptional(opticFinder2);
				if (optional.isPresent()) {
					List<IS> list = (List<IS>)optional.get();
					IS object = (IS)((Pair)itemStackType.read(dynamic.emptyMap())
							.result()
							.orElseThrow(() -> new IllegalStateException("Could not parse newly created empty itemstack.")))
						.getFirst();
					if (!list.isEmpty()) {
						either = Either.left(Lists.<IS>newArrayList((IS[])(new Object[]{list.get(0), object})));
					}

					if (list.size() > 1) {
						List<IS> list2 = Lists.<IS>newArrayList(object, object, object, object);

						for (int i = 1; i < Math.min(list.size(), 5); i++) {
							list2.set(i - 1, list.get(i));
						}

						either2 = Either.left(list2);
					}
				}

				Dynamic<?> dynamic2 = dynamic;
				Optional<? extends Stream<? extends Dynamic<?>>> optional2 = dynamic.get("DropChances").asStreamOpt().result();
				if (optional2.isPresent()) {
					Iterator<? extends Dynamic<?>> iterator = Stream.concat((Stream)optional2.get(), Stream.generate(() -> dynamic2.createInt(0))).iterator();
					float f = ((Dynamic)iterator.next()).asFloat(0.0F);
					if (dynamic.get("HandDropChances").result().isEmpty()) {
						Dynamic<?> dynamic3 = dynamic.createList(Stream.of(f, 0.0F).map(dynamic::createFloat));
						dynamic = dynamic.set("HandDropChances", dynamic3);
					}

					if (dynamic.get("ArmorDropChances").result().isEmpty()) {
						Dynamic<?> dynamic3 = dynamic.createList(
							Stream.of(
									((Dynamic)iterator.next()).asFloat(0.0F),
									((Dynamic)iterator.next()).asFloat(0.0F),
									((Dynamic)iterator.next()).asFloat(0.0F),
									((Dynamic)iterator.next()).asFloat(0.0F)
								)
								.map(dynamic::createFloat)
						);
						dynamic = dynamic.set("ArmorDropChances", dynamic3);
					}

					dynamic = dynamic.remove("DropChances");
				}

				return entityTyped.set(opticFinder, type2, Pair.of(either, Pair.of(either2, Pair.of(either3, dynamic))));
			}
		);
	}
}
