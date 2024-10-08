package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

@Name("minecraft.ChunkGeneration")
@Label("Chunk Generation")
@Category({"Minecraft", "World Generation"})
@StackTrace(false)
@Enabled(false)
@DontObfuscate
public class ChunkGenerationEvent extends Event {
	public static final String EVENT_NAME = "minecraft.ChunkGeneration";
	public static final EventType TYPE = EventType.getEventType(ChunkGenerationEvent.class);
	@Name("worldPosX")
	@Label("First Block X World Position")
	public final int worldPosX;
	@Name("worldPosZ")
	@Label("First Block Z World Position")
	public final int worldPosZ;
	@Name("chunkPosX")
	@Label("Chunk X Position")
	public final int chunkPosX;
	@Name("chunkPosZ")
	@Label("Chunk Z Position")
	public final int chunkPosZ;
	@Name("status")
	@Label("Status")
	public final String targetStatus;
	@Name("level")
	@Label("Level")
	public final String level;

	public ChunkGenerationEvent(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
		this.targetStatus = targetStatus;
		this.level = world.getValue().toString();
		this.chunkPosX = chunkPos.x;
		this.chunkPosZ = chunkPos.z;
		this.worldPosX = chunkPos.getStartX();
		this.worldPosZ = chunkPos.getStartZ();
	}

	public static class Names {
		public static final String WORLD_POS_X = "worldPosX";
		public static final String WORLD_POS_Z = "worldPosZ";
		public static final String CHUNK_POS_X = "chunkPosX";
		public static final String CHUNK_POS_Z = "chunkPosZ";
		public static final String STATUS = "status";
		public static final String LEVEL = "level";

		private Names() {
		}
	}
}
