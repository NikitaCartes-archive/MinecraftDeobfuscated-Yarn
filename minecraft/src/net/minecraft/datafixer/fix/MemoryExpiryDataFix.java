package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import net.minecraft.datafixer.TypeReferences;

public class MemoryExpiryDataFix extends ChoiceFix {
	public MemoryExpiryDataFix(Schema schema, String choiceName) {
		super(schema, false, "Memory expiry data fix (" + choiceName + ")", TypeReferences.ENTITY, choiceName);
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::updateBrain);
	}

	public Dynamic<?> updateBrain(Dynamic<?> dynamic) {
		return dynamic.update("Brain", this::updateMemories);
	}

	private Dynamic<?> updateMemories(Dynamic<?> dynamic) {
		return dynamic.update("memories", this::updateMemoryMap);
	}

	private Dynamic<?> updateMemoryMap(Dynamic<?> dynamic) {
		return dynamic.updateMapValues(this::updateMemoryMapValues);
	}

	private Pair<Dynamic<?>, Dynamic<?>> updateMemoryMapValues(Pair<Dynamic<?>, Dynamic<?>> pair) {
		return pair.mapSecond(this::updateMemoryMapValueEntry);
	}

	private Dynamic<?> updateMemoryMapValueEntry(Dynamic<?> dynamic) {
		return dynamic.createMap(ImmutableMap.of(dynamic.createString("value"), dynamic));
	}
}
