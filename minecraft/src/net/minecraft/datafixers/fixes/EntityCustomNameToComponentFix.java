package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class EntityCustomNameToComponentFix extends DataFix {
	public EntityCustomNameToComponentFix(Schema schema, boolean bl) {
		super(schema, bl);
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

	public static Dynamic<?> fixCustomName(Dynamic<?> dynamic) {
		String string = dynamic.get("CustomName").asString("");
		return string.isEmpty()
			? dynamic.remove("CustomName")
			: dynamic.set("CustomName", dynamic.createString(Component.Serializer.toJsonString(new TextComponent(string))));
	}
}
