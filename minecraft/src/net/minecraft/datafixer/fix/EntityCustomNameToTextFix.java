package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class EntityCustomNameToTextFix extends DataFix {
	public EntityCustomNameToTextFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		OpticFinder<String> opticFinder = DSL.fieldFinder("id", DSL.namespacedString());
		return this.fixTypeEverywhereTyped(
			"EntityCustomNameToComponentFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Optional<String> optional = typed.getOptional(opticFinder);
					return optional.isPresent() && Objects.equals(optional.get(), "minecraft:commandblock_minecart") ? dynamic : fixCustomName(dynamic);
				})
		);
	}

	public static Dynamic<?> fixCustomName(Dynamic<?> tag) {
		String string = tag.get("CustomName").asString("");
		return string.isEmpty() ? tag.remove("CustomName") : tag.set("CustomName", tag.createString(Text.Serializer.toJson(new LiteralText(string))));
	}
}
