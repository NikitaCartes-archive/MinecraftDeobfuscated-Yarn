package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

public abstract class EntitySimpleTransformFix extends EntityTransformFix {
	public EntitySimpleTransformFix(String string, Schema schema, boolean bl) {
		super(string, schema, bl);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String string, Typed<?> typed) {
		Pair<String, Dynamic<?>> pair = this.transform(string, typed.getOrCreate(DSL.remainderFinder()));
		return Pair.of(pair.getFirst(), typed.set(DSL.remainderFinder(), pair.getSecond()));
	}

	protected abstract Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic);
}
