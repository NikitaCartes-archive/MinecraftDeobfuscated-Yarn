package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class MemoryExpiryDataFix extends ChoiceFix {
	public MemoryExpiryDataFix(Schema outputSchema, String choiceName) {
		super(outputSchema, false, "Memory expiry data fix (" + choiceName + ")", TypeReferences.ENTITY, choiceName);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::updateBrain);
	}

	public Dynamic<?> updateBrain(Dynamic<?> entityDynamic) {
		return entityDynamic.update("Brain", this::updateMemories);
	}

	private Dynamic<?> updateMemories(Dynamic<?> brainDynamic) {
		return brainDynamic.update("memories", this::updateMemoryMap);
	}

	private Dynamic<?> updateMemoryMap(Dynamic<?> memoriesDynamic) {
		return memoriesDynamic.updateMapValues(this::updateMemoryMapValues);
	}

	private Pair<Dynamic<?>, Dynamic<?>> updateMemoryMapValues(Pair<Dynamic<?>, Dynamic<?>> memoryKv) {
		return memoryKv.mapSecond(this::updateMemoryMapValueEntry);
	}

	private Dynamic<?> updateMemoryMapValueEntry(Dynamic<?> memoryValue) {
		return memoryValue.createMap(ImmutableMap.of(memoryValue.createString("value"), memoryValue));
	}
}
