package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class BlockNameFlatteningFix extends DataFix {
	public BlockNameFlatteningFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.BLOCK_NAME);
		Type<Pair<String, Either<Integer, String>>> type3 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()));
		Type<Pair<String, String>> type4 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.namespacedString());
		if (Objects.equals(type, type3) && Objects.equals(type2, type4)) {
			return this.fixTypeEverywhere(
				"BlockNameFlatteningFix",
				type3,
				type4,
				dynamicOps -> pair -> pair.mapSecond(
							either -> either.map(BlockStateFlattening::lookup, string -> BlockStateFlattening.lookup(SchemaIdentifierNormalize.normalize(string)))
						)
			);
		} else {
			throw new IllegalStateException("Expected and actual types don't match.");
		}
	}
}
