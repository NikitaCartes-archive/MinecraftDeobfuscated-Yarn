package net.minecraft.world.chunk;

import java.util.concurrent.Executor;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record ChunkGenerationContext(
	ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureManager, ServerLightingProvider lightingProvider, Executor mainThreadExecutor
) {
}
