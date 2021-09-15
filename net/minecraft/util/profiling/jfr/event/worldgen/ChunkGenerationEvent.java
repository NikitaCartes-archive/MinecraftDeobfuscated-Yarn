/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr.event.worldgen;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@Name(value="minecraft.ChunkGeneration")
@Label(value="Chunk generation duration")
@Category(value={"Minecraft", "World Generation"})
@StackTrace(value=false)
@DontObfuscate
public class ChunkGenerationEvent
extends Event {
    public static final String EVENT_NAME = "minecraft.ChunkGeneration";
    public static final EventType TYPE = EventType.getEventType(ChunkGenerationEvent.class);
    @Name(value="worldPosX")
    @Label(value="First block x world position")
    public final int worldPosX;
    @Name(value="worldPosZ")
    @Label(value="First block z world position")
    public final int worldPosZ;
    @Name(value="chunkPosX")
    @Label(value="Chunk x position")
    public final int chunkPosX;
    @Name(value="chunkPosZ")
    @Label(value="Chunk z position")
    public final int chunkPosZ;
    @Name(value="status")
    public final String targetStatus;
    @Name(value="level")
    public final String level;
    @Name(value="success")
    public boolean success;

    public ChunkGenerationEvent(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
        this.targetStatus = targetStatus;
        this.level = world.toString();
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
        public static final String SUCCESS = "success";
        public static final String LEVEL = "level";

        private Names() {
        }
    }
}

