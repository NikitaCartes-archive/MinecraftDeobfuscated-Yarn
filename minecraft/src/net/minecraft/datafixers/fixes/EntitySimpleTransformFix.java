package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

public abstract class EntitySimpleTransformFix extends EntityTransformFix {
	public EntitySimpleTransformFix(String name, Schema oldSchema, boolean bl) {
		super(name, oldSchema, bl);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
		Pair<String, Dynamic<?>> pair = this.transform(choice, typed.getOrCreate(DSL.remainderFinder()));
		return Pair.of(pair.getFirst(), typed.set(DSL.remainderFinder(), pair.getSecond()));
	}

	protected abstract Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag);
}
