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

	private Typed<?> fixEntities(Typed<?> entityTyped) {
		entityTyped = this.update(entityTyped, "minecraft:egg", this::moveOwnerToArray);
		entityTyped = this.update(entityTyped, "minecraft:ender_pearl", this::moveOwnerToArray);
		entityTyped = this.update(entityTyped, "minecraft:experience_bottle", this::moveOwnerToArray);
		entityTyped = this.update(entityTyped, "minecraft:snowball", this::moveOwnerToArray);
		entityTyped = this.update(entityTyped, "minecraft:potion", this::moveOwnerToArray);
		entityTyped = this.update(entityTyped, "minecraft:potion", this::renamePotionToItem);
		entityTyped = this.update(entityTyped, "minecraft:llama_spit", this::moveNestedOwnerMostLeastToArray);
		entityTyped = this.update(entityTyped, "minecraft:arrow", this::moveFlatOwnerMostLeastToArray);
		entityTyped = this.update(entityTyped, "minecraft:spectral_arrow", this::moveFlatOwnerMostLeastToArray);
		return this.update(entityTyped, "minecraft:trident", this::moveFlatOwnerMostLeastToArray);
	}

	private Dynamic<?> moveFlatOwnerMostLeastToArray(Dynamic<?> entityDynamic) {
		long l = entityDynamic.get("OwnerUUIDMost").asLong(0L);
		long m = entityDynamic.get("OwnerUUIDLeast").asLong(0L);
		return this.insertOwnerUuidArray(entityDynamic, l, m).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
	}

	private Dynamic<?> moveNestedOwnerMostLeastToArray(Dynamic<?> entityDynamic) {
		OptionalDynamic<?> optionalDynamic = entityDynamic.get("Owner");
		long l = optionalDynamic.get("OwnerUUIDMost").asLong(0L);
		long m = optionalDynamic.get("OwnerUUIDLeast").asLong(0L);
		return this.insertOwnerUuidArray(entityDynamic, l, m).remove("Owner");
	}

	private Dynamic<?> renamePotionToItem(Dynamic<?> entityDynamic) {
		OptionalDynamic<?> optionalDynamic = entityDynamic.get("Potion");
		return entityDynamic.set("Item", optionalDynamic.orElseEmptyMap()).remove("Potion");
	}

	private Dynamic<?> moveOwnerToArray(Dynamic<?> entityDynamic) {
		String string = "owner";
		OptionalDynamic<?> optionalDynamic = entityDynamic.get("owner");
		long l = optionalDynamic.get("M").asLong(0L);
		long m = optionalDynamic.get("L").asLong(0L);
		return this.insertOwnerUuidArray(entityDynamic, l, m).remove("owner");
	}

	private Dynamic<?> insertOwnerUuidArray(Dynamic<?> entityDynamic, long most, long least) {
		String string = "OwnerUUID";
		return most != 0L && least != 0L ? entityDynamic.set("OwnerUUID", entityDynamic.createIntList(Arrays.stream(makeUuidArray(most, least)))) : entityDynamic;
	}

	private static int[] makeUuidArray(long most, long least) {
		return new int[]{(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
	}

	private Typed<?> update(Typed<?> entityTyped, String matchId, Function<Dynamic<?>, Dynamic<?>> fixer) {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, matchId);
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, matchId);
		return entityTyped.updateTyped(DSL.namedChoice(matchId, type), type2, typed -> typed.update(DSL.remainderFinder(), fixer));
	}
}
