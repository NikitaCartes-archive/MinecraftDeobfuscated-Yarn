package net.minecraft.client.util;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.profiler.ReadableProfiler;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.util.profiler.Sampler;
import net.minecraft.util.profiler.SamplerFactory;
import net.minecraft.util.profiler.SamplerSource;
import net.minecraft.util.profiler.ServerSamplerSource;

@Environment(EnvType.CLIENT)
public class ClientSamplerSource implements SamplerSource {
	private final WorldRenderer renderer;
	private final Set<Sampler> samplers = new ObjectOpenHashSet<>();
	private final SamplerFactory factory = new SamplerFactory();

	public ClientSamplerSource(LongSupplier nanoTimeSupplier, WorldRenderer renderer) {
		this.renderer = renderer;
		this.samplers.add(ServerSamplerSource.createTickTimeTracker(nanoTimeSupplier));
		this.addInfoSamplers();
	}

	private void addInfoSamplers() {
		this.samplers.addAll(ServerSamplerSource.createSystemSamplers());
		this.samplers.add(Sampler.create("totalChunks", SampleType.CHUNK_RENDERING, this.renderer, WorldRenderer::getChunkCount));
		this.samplers.add(Sampler.create("renderedChunks", SampleType.CHUNK_RENDERING, this.renderer, WorldRenderer::getCompletedChunkCount));
		this.samplers.add(Sampler.create("lastViewDistance", SampleType.CHUNK_RENDERING, this.renderer, WorldRenderer::getViewDistance));
		ChunkBuilder chunkBuilder = this.renderer.getChunkBuilder();
		this.samplers.add(Sampler.create("toUpload", SampleType.CHUNK_RENDERING_DISPATCHING, chunkBuilder, ChunkBuilder::getChunksToUpload));
		this.samplers.add(Sampler.create("freeBufferCount", SampleType.CHUNK_RENDERING_DISPATCHING, chunkBuilder, ChunkBuilder::getFreeBufferCount));
		this.samplers.add(Sampler.create("toBatchCount", SampleType.CHUNK_RENDERING_DISPATCHING, chunkBuilder, ChunkBuilder::getToBatchCount));
		if (GlTimer.getInstance().isPresent()) {
			this.samplers.add(Sampler.create("gpuUtilization", SampleType.GPU, MinecraftClient.getInstance(), MinecraftClient::getGpuUtilizationPercentage));
		}
	}

	@Override
	public Set<Sampler> getSamplers(Supplier<ReadableProfiler> profilerSupplier) {
		this.samplers.addAll(this.factory.createSamplers(profilerSupplier));
		return this.samplers;
	}
}
