package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

public abstract class class_1212 extends class_1167 {
	public class_1212(String string, Schema schema, boolean bl) {
		super(string, schema, bl);
	}

	@Override
	protected Pair<String, Typed<?>> method_4982(String string, Typed<?> typed) {
		Pair<String, Dynamic<?>> pair = this.method_5164(string, typed.getOrCreate(DSL.remainderFinder()));
		return Pair.of(pair.getFirst(), typed.set(DSL.remainderFinder(), pair.getSecond()));
	}

	protected abstract Pair<String, Dynamic<?>> method_5164(String string, Dynamic<?> dynamic);
}
