package net.minecraft.util.profiling.jfr.event.worldgen;

import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.util.profiling.jfr.event.StackTracelessEvent;

@Name("minecraft.WorldLoadFinishedEvent")
@Label("WorldLoadFinished")
@Category({"Minecraft", "World Generation"})
@DontObfuscate
public class WorldLoadFinishedEvent extends StackTracelessEvent {
	public static final String NAME = "minecraft.WorldLoadFinishedEvent";
}
