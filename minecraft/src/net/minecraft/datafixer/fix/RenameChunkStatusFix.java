package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class RenameChunkStatusFix extends DataFix {
	private final String name;
	private final UnaryOperator<String> mapper;

	public RenameChunkStatusFix(Schema outputSchema, String name, UnaryOperator<String> mapper) {
		super(outputSchema, false);
		this.name = name;
		this.mapper = mapper;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			this.name,
			this.getInputSchema().getType(TypeReferences.CHUNK),
			typed -> typed.update(
					DSL.remainderFinder(),
					chunk -> chunk.update("Status", this::updateStatus).update("below_zero_retrogen", dynamic -> dynamic.update("target_status", this::updateStatus))
				)
		);
	}

	private <T> Dynamic<T> updateStatus(Dynamic<T> status) {
		Optional<Dynamic<T>> optional = status.asString().result().map(IdentifierNormalizingSchema::normalize).map(this.mapper).map(status::createString);
		return DataFixUtils.orElse(optional, status);
	}
}
