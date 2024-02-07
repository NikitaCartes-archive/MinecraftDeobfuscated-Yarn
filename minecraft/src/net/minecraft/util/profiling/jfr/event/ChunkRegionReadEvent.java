package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.ChunkCompressionFormat;
import net.minecraft.world.storage.StorageKey;

@Name("minecraft.ChunkRegionRead")
@Label("Region File Read")
@DontObfuscate
public class ChunkRegionReadEvent extends ChunkRegionEvent {
	public static final String EVENT_NAME = "minecraft.ChunkRegionRead";
	public static final EventType TYPE = EventType.getEventType(ChunkRegionReadEvent.class);

	public ChunkRegionReadEvent(StorageKey storageKey, ChunkPos chunkPos, ChunkCompressionFormat chunkCompressionFormat, int i) {
		super(storageKey, chunkPos, chunkCompressionFormat, i);
	}
}
