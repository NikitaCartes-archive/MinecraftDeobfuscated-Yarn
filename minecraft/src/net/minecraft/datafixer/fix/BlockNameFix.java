package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class BlockNameFix extends DataFix {
	private final String name;

	public BlockNameFix(Schema outputSchema, String name) {
		super(outputSchema, false);
		this.name = name;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
		Type<Pair<String, String>> type2 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType());
		if (!Objects.equals(type, type2)) {
			throw new IllegalStateException("block type is not what was expected.");
		} else {
			TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.name + " for block", type2, dynamicOps -> pair -> pair.mapSecond(this::rename));
			TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(
				this.name + " for block_state",
				this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
				typed -> typed.update(DSL.remainderFinder(), this::fixBlockState)
			);
			TypeRewriteRule typeRewriteRule3 = this.fixTypeEverywhereTyped(
				this.name + " for flat_block_state",
				this.getInputSchema().getType(TypeReferences.FLAT_BLOCK_STATE),
				typed -> typed.update(
						DSL.remainderFinder(), dynamic -> DataFixUtils.orElse(dynamic.asString().result().map(this::fixFlatBlockState).map(dynamic::createString), dynamic)
					)
			);
			return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2, typeRewriteRule3);
		}
	}

	private Dynamic<?> fixBlockState(Dynamic<?> blockStateDynamic) {
		Optional<String> optional = blockStateDynamic.get("Name").asString().result();
		return optional.isPresent() ? blockStateDynamic.set("Name", blockStateDynamic.createString(this.rename((String)optional.get()))) : blockStateDynamic;
	}

	/**
	 * @implNote Replaces the part of {@code flatBlockState} before the first [ or {.
	 */
	private String fixFlatBlockState(String flatBlockState) {
		int i = flatBlockState.indexOf(91);
		int j = flatBlockState.indexOf(123);
		int k = flatBlockState.length();
		if (i > 0) {
			k = i;
		}

		if (j > 0) {
			k = Math.min(k, j);
		}

		String string = flatBlockState.substring(0, k);
		String string2 = this.rename(string);
		return string2 + flatBlockState.substring(k);
	}

	protected abstract String rename(String oldName);

	public static DataFix create(Schema outputSchema, String name, Function<String, String> rename) {
		return new BlockNameFix(outputSchema, name) {
			@Override
			protected String rename(String oldName) {
				return (String)rename.apply(oldName);
			}
		};
	}
}
