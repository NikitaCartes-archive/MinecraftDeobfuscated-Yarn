package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class ItemNbtFix extends DataFix {
	private final String name;
	private final Predicate<String> itemIdPredicate;

	public ItemNbtFix(Schema outputSchema, String name, Predicate<String> itemIdPredicate) {
		super(outputSchema, false);
		this.name = name;
		this.itemIdPredicate = itemIdPredicate;
	}

	@Override
	public final TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			this.name,
			type,
			typed -> {
				Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
				return optional.isPresent() && this.itemIdPredicate.test((String)((Pair)optional.get()).getSecond())
					? typed.updateTyped(opticFinder2, nbt -> nbt.update(DSL.remainderFinder(), this::fixNbt))
					: typed;
			}
		);
	}

	protected abstract <T> Dynamic<T> fixNbt(Dynamic<T> dynamic);
}
