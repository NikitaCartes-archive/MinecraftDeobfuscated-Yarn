package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;

public class Schema1451v6 extends IdentifierNormalizingSchema {
	public static final String field_34013 = "_special";
	protected static final HookFunction field_34014 = new HookFunction() {
		@Override
		public <T> T apply(DynamicOps<T> dynamicOps, T object) {
			Dynamic<T> dynamic = new Dynamic<>(dynamicOps, object);
			return DataFixUtils.orElse(
					dynamic.get("CriteriaName")
						.asString()
						.get()
						.left()
						.map(string -> {
							int i = string.indexOf(58);
							if (i < 0) {
								return Pair.of("_special", string);
							} else {
								try {
									Identifier identifier = Identifier.splitOn(string.substring(0, i), '.');
									Identifier identifier2 = Identifier.splitOn(string.substring(i + 1), '.');
									return Pair.of(identifier.toString(), identifier2.toString());
								} catch (Exception var4) {
									return Pair.of("_special", string);
								}
							}
						})
						.map(
							pair -> dynamic.set(
									"CriteriaType",
									dynamic.createMap(
										ImmutableMap.of(
											dynamic.createString("type"),
											dynamic.createString((String)pair.getFirst()),
											dynamic.createString("id"),
											dynamic.createString((String)pair.getSecond())
										)
									)
								)
						),
					dynamic
				)
				.getValue();
		}
	};
	protected static final HookFunction field_34015 = new HookFunction() {
		private String method_37399(String string) {
			Identifier identifier = Identifier.tryParse(string);
			return identifier != null ? identifier.getNamespace() + "." + identifier.getPath() : string;
		}

		@Override
		public <T> T apply(DynamicOps<T> dynamicOps, T object) {
			Dynamic<T> dynamic = new Dynamic<>(dynamicOps, object);
			Optional<Dynamic<T>> optional = dynamic.get("CriteriaType")
				.get()
				.get()
				.left()
				.flatMap(
					dynamic2 -> {
						Optional<String> optionalx = dynamic2.get("type").asString().get().left();
						Optional<String> optional2 = dynamic2.get("id").asString().get().left();
						if (optionalx.isPresent() && optional2.isPresent()) {
							String string = (String)optionalx.get();
							return string.equals("_special")
								? Optional.of(dynamic.createString((String)optional2.get()))
								: Optional.of(dynamic2.createString(this.method_37399(string) + ":" + this.method_37399((String)optional2.get())));
						} else {
							return Optional.empty();
						}
					}
				);
			return DataFixUtils.orElse(optional.map(dynamic2 -> dynamic.set("CriteriaName", dynamic2).remove("CriteriaType")), dynamic).getValue();
		}
	};

	public Schema1451v6(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		Supplier<TypeTemplate> supplier = () -> DSL.compoundList(TypeReferences.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
		schema.registerType(
			false,
			TypeReferences.STATS,
			() -> DSL.optionalFields(
					"stats",
					DSL.optionalFields(
						"minecraft:mined",
						DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())),
						"minecraft:crafted",
						(TypeTemplate)supplier.get(),
						"minecraft:used",
						(TypeTemplate)supplier.get(),
						"minecraft:broken",
						(TypeTemplate)supplier.get(),
						"minecraft:picked_up",
						(TypeTemplate)supplier.get(),
						DSL.optionalFields(
							"minecraft:dropped",
							(TypeTemplate)supplier.get(),
							"minecraft:killed",
							DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())),
							"minecraft:killed_by",
							DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())),
							"minecraft:custom",
							DSL.compoundList(DSL.constType(getIdentifierType()), DSL.constType(DSL.intType()))
						)
					)
				)
		);
		Map<String, Supplier<TypeTemplate>> map = method_37389(schema);
		schema.registerType(
			false,
			TypeReferences.OBJECTIVE,
			() -> DSL.hook(DSL.optionalFields("CriteriaType", DSL.taggedChoiceLazy("type", DSL.string(), map)), field_34014, field_34015)
		);
	}

	protected static Map<String, Supplier<TypeTemplate>> method_37389(Schema schema) {
		Supplier<TypeTemplate> supplier = () -> DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema));
		Supplier<TypeTemplate> supplier2 = () -> DSL.optionalFields("id", TypeReferences.BLOCK_NAME.in(schema));
		Supplier<TypeTemplate> supplier3 = () -> DSL.optionalFields("id", TypeReferences.ENTITY_NAME.in(schema));
		Map<String, Supplier<TypeTemplate>> map = Maps.<String, Supplier<TypeTemplate>>newHashMap();
		map.put("minecraft:mined", supplier2);
		map.put("minecraft:crafted", supplier);
		map.put("minecraft:used", supplier);
		map.put("minecraft:broken", supplier);
		map.put("minecraft:picked_up", supplier);
		map.put("minecraft:dropped", supplier);
		map.put("minecraft:killed", supplier3);
		map.put("minecraft:killed_by", supplier3);
		map.put("minecraft:custom", (Supplier)() -> DSL.optionalFields("id", DSL.constType(getIdentifierType())));
		map.put("_special", (Supplier)() -> DSL.optionalFields("id", DSL.constType(DSL.string())));
		return map;
	}
}
