package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public abstract class ComponentFix extends DataFix {
	private final String name;
	private final String oldComponentId;
	private final String newComponentId;

	public ComponentFix(Schema outputSchema, String name, String componentId) {
		this(outputSchema, name, componentId, componentId);
	}

	public ComponentFix(Schema outputSchema, String name, String oldComponentId, String newComponentId) {
		super(outputSchema, false);
		this.name = name;
		this.oldComponentId = oldComponentId;
		this.newComponentId = newComponentId;
	}

	@Override
	public final TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("components");
		return this.fixTypeEverywhereTyped(
			this.name,
			type,
			typed -> typed.updateTyped(
					opticFinder,
					typedx -> typedx.update(DSL.remainderFinder(), dynamic -> FixUtil.replaceKey(dynamic, this.oldComponentId, this.newComponentId, this::fixComponent))
				)
		);
	}

	protected abstract <T> Dynamic<T> fixComponent(Dynamic<T> dynamic);
}
