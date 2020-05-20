package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class EntityProjectileOwnerFix extends DataFix {
	public EntityProjectileOwnerFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		return this.fixTypeEverywhereTyped("EntityProjectileOwner", schema.getType(TypeReferences.ENTITY), this::fixEntities);
	}

	private Typed<?> fixEntities(Typed<?> typed) {
		typed = this.update(typed, "minecraft:egg", this::moveOwnerToArray);
		typed = this.update(typed, "minecraft:ender_pearl", this::moveOwnerToArray);
		typed = this.update(typed, "minecraft:experience_bottle", this::moveOwnerToArray);
		typed = this.update(typed, "minecraft:snowball", this::moveOwnerToArray);
		typed = this.update(typed, "minecraft:potion", this::moveOwnerToArray);
		typed = this.update(typed, "minecraft:potion", this::renamePotionToItem);
		typed = this.update(typed, "minecraft:llama_spit", this::moveNestedOwnerMostLeastToArray);
		typed = this.update(typed, "minecraft:arrow", this::moveFlatOwnerMostLeastToArray);
		typed = this.update(typed, "minecraft:spectral_arrow", this::moveFlatOwnerMostLeastToArray);
		return this.update(typed, "minecraft:trident", this::moveFlatOwnerMostLeastToArray);
	}

	private Dynamic<?> moveFlatOwnerMostLeastToArray(Dynamic<?> dynamic) {
		long l = dynamic.get("OwnerUUIDMost").asLong(0L);
		long m = dynamic.get("OwnerUUIDLeast").asLong(0L);
		return this.insertOwnerUuidArray(dynamic, l, m).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
	}

	private Dynamic<?> moveNestedOwnerMostLeastToArray(Dynamic<?> dynamic) {
		OptionalDynamic<?> optionalDynamic = dynamic.get("Owner");
		long l = optionalDynamic.get("OwnerUUIDMost").asLong(0L);
		long m = optionalDynamic.get("OwnerUUIDLeast").asLong(0L);
		return this.insertOwnerUuidArray(dynamic, l, m).remove("Owner");
	}

	private Dynamic<?> renamePotionToItem(Dynamic<?> dynamic) {
		OptionalDynamic<?> optionalDynamic = dynamic.get("Potion");
		return dynamic.set("Item", optionalDynamic.orElseEmptyMap()).remove("Potion");
	}

	private Dynamic<?> moveOwnerToArray(Dynamic<?> dynamic) {
		String string = "owner";
		OptionalDynamic<?> optionalDynamic = dynamic.get("owner");
		long l = optionalDynamic.get("M").asLong(0L);
		long m = optionalDynamic.get("L").asLong(0L);
		return this.insertOwnerUuidArray(dynamic, l, m).remove("owner");
	}

	private Dynamic<?> insertOwnerUuidArray(Dynamic<?> dynamic, long most, long least) {
		String string = "OwnerUUID";
		return most != 0L && least != 0L ? dynamic.set("OwnerUUID", dynamic.createIntList(Arrays.stream(makeUuidArray(most, least)))) : dynamic;
	}

	private static int[] makeUuidArray(long most, long least) {
		return new int[]{(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
	}

	private Typed<?> update(Typed<?> typed, String string, Function<Dynamic<?>, Dynamic<?>> function) {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, string);
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, string);
		return typed.updateTyped(DSL.namedChoice(string, type), type2, typedx -> typedx.update(DSL.remainderFinder(), function));
	}
}
