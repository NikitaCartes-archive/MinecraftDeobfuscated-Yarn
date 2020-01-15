package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class EntityRidingToPassengerFix extends DataFix {
	public EntityRidingToPassengerFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		Schema schema2 = this.getOutputSchema();
		Type<?> type = schema.getTypeRaw(TypeReferences.ENTITY_TREE);
		Type<?> type2 = schema2.getTypeRaw(TypeReferences.ENTITY_TREE);
		Type<?> type3 = schema.getTypeRaw(TypeReferences.ENTITY);
		return this.method_4978(schema, schema2, type, type2, type3);
	}

	private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule method_4978(
		Schema schema, Schema schema2, Type<OldEntityTree> type, Type<NewEntityTree> type2, Type<Entity> type3
	) {
		Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> type4 = DSL.named(
			TypeReferences.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", type)), type3)
		);
		Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> type5 = DSL.named(
			TypeReferences.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(type2))), type3)
		);
		Type<?> type6 = schema.getType(TypeReferences.ENTITY_TREE);
		Type<?> type7 = schema2.getType(TypeReferences.ENTITY_TREE);
		if (!Objects.equals(type6, type4)) {
			throw new IllegalStateException("Old entity type is not what was expected.");
		} else if (!type7.equals(type5, true, true)) {
			throw new IllegalStateException("New entity type is not what was expected.");
		} else {
			OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> opticFinder = DSL.typeFinder(type4);
			OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> opticFinder2 = DSL.typeFinder(type5);
			OpticFinder<NewEntityTree> opticFinder3 = DSL.typeFinder(type2);
			Type<?> type8 = schema.getType(TypeReferences.PLAYER);
			Type<?> type9 = schema2.getType(TypeReferences.PLAYER);
			return TypeRewriteRule.seq(
				this.fixTypeEverywhere(
					"EntityRidingToPassengerFix",
					type4,
					type5,
					dynamicOps -> pair -> {
							Optional<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> optional = Optional.empty();
							Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>> pair2 = pair;

							while (true) {
								Either<List<NewEntityTree>, Unit> either = DataFixUtils.orElse(
									optional.map(
										pairx -> {
											Typed<NewEntityTree> typed = (Typed<NewEntityTree>)type2.pointTyped(dynamicOps)
												.orElseThrow(() -> new IllegalStateException("Could not create new entity tree"));
											NewEntityTree object = (NewEntityTree)typed.set(opticFinder2, pairx)
												.getOptional(opticFinder3)
												.orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
											return Either.left(ImmutableList.of(object));
										}
									),
									Either.right(DSL.unit())
								);
								optional = Optional.of(Pair.of(TypeReferences.ENTITY_TREE.typeName(), Pair.of(either, pair2.getSecond().getSecond())));
								Optional<OldEntityTree> optional2 = pair2.getSecond().getFirst().left();
								if (!optional2.isPresent()) {
									return (Pair)optional.orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
								}

								pair2 = (Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>)new Typed<>(type, dynamicOps, (OldEntityTree)optional2.get())
									.getOptional(opticFinder)
									.orElseThrow(() -> new IllegalStateException("Should always have an entity here"));
							}
						}
				),
				this.writeAndRead("player RootVehicle injecter", type8, type9)
			);
		}
	}
}
