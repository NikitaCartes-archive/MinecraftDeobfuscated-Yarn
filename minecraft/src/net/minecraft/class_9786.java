package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class class_9786 extends ChoiceFix {
	public class_9786(Schema schema) {
		super(schema, false, "JukeboxTicksSinceSongStartedFix", TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
	}

	public Dynamic<?> method_60696(Dynamic<?> dynamic) {
		long l = dynamic.get("TickCount").asLong(0L) - dynamic.get("RecordStartTick").asLong(0L);
		Dynamic<?> dynamic2 = dynamic.remove("IsPlaying").remove("TickCount").remove("RecordStartTick");
		return l > 0L ? dynamic2.set("ticks_since_song_started", dynamic.createLong(l)) : dynamic2;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::method_60696);
	}
}
