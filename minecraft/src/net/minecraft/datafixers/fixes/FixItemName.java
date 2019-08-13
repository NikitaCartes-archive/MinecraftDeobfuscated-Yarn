package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixers.TypeReferences;

public abstract class FixItemName extends DataFix {
	private final String name;

	public FixItemName(Schema schema, String string) {
		super(schema, false);
		this.name = string;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString());
		if (!Objects.equals(this.getInputSchema().getType(TypeReferences.ITEM_NAME), type)) {
			throw new IllegalStateException("item name type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this::rename));
		}
	}

	protected abstract String rename(String string);

	public static DataFix create(Schema schema, String string, Function<String, String> function) {
		return new FixItemName(schema, string) {
			@Override
			protected String rename(String string) {
				return (String)function.apply(string);
			}
		};
	}
}
