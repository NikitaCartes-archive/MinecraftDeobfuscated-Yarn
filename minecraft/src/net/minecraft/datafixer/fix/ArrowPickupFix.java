package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

/**
 * A fix that automatically renames the {@code player} byte in arrow data to
 * {@code pickup}, if there is not any existing {@code pickup} data.
 * 
 * <p>This is known as {@index AbstractArrowPickupFix} in the literal
 * string, though this fix is not abstract.
 */
public class ArrowPickupFix extends DataFix {
	public ArrowPickupFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		return this.fixTypeEverywhereTyped("AbstractArrowPickupFix", schema.getType(TypeReferences.ENTITY), this::update);
	}

	private Typed<?> update(Typed<?> typed) {
		typed = this.updateEntity(typed, "minecraft:arrow", ArrowPickupFix::update);
		typed = this.updateEntity(typed, "minecraft:spectral_arrow", ArrowPickupFix::update);
		return this.updateEntity(typed, "minecraft:trident", ArrowPickupFix::update);
	}

	/**
	 * When the {@code pickup} NBT byte of an arrow's data is absent, sets it
	 * from the arrow's {@code player} NBT byte.
	 */
	private static Dynamic<?> update(Dynamic<?> arrowData) {
		if (arrowData.get("pickup").result().isPresent()) {
			return arrowData;
		} else {
			boolean bl = arrowData.get("player").asBoolean(true);
			return arrowData.set("pickup", arrowData.createByte((byte)(bl ? 1 : 0))).remove("player");
		}
	}

	private Typed<?> updateEntity(Typed<?> typed, String choiceName, Function<Dynamic<?>, Dynamic<?>> updater) {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, choiceName);
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, choiceName);
		return typed.updateTyped(DSL.namedChoice(choiceName, type), type2, t -> t.update(DSL.remainderFinder(), updater));
	}
}
