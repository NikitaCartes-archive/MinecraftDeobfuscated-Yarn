package net.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.profiler.SamplingChannel;
import net.minecraft.client.util.profiler.SamplingRecorder;
import net.minecraft.util.profiler.ReadableProfiler;

@Environment(EnvType.CLIENT)
public class class_6412 implements class_6400 {
	private final WorldRenderer field_33959;
	private final Set<SamplingRecorder> field_33960 = new ObjectOpenHashSet<>();
	private final class_6401 field_33961 = new class_6401();

	public class_6412(LongSupplier longSupplier, WorldRenderer worldRenderer) {
		this.field_33959 = worldRenderer;
		this.field_33960.add(class_6402.method_37202(longSupplier));
		this.method_37309();
	}

	private void method_37309() {
		this.field_33960.addAll(class_6402.method_37199());
		this.field_33960.add(SamplingRecorder.create("totalChunks", SamplingChannel.CHUNK_RENDERING, this.field_33959, WorldRenderer::getChunkCount));
		this.field_33960.add(SamplingRecorder.create("renderedChunks", SamplingChannel.CHUNK_RENDERING, this.field_33959, WorldRenderer::getCompletedChunkCount));
		this.field_33960.add(SamplingRecorder.create("lastViewDistance", SamplingChannel.CHUNK_RENDERING, this.field_33959, WorldRenderer::getViewDistance));
		ChunkBuilder chunkBuilder = this.field_33959.getChunkBuilder();
		this.field_33960.add(SamplingRecorder.create("toUpload", SamplingChannel.CHUNK_RENDERING_DISPATCHING, chunkBuilder, ChunkBuilder::getChunksToUpload));
		this.field_33960.add(SamplingRecorder.create("freeBufferCount", SamplingChannel.CHUNK_RENDERING_DISPATCHING, chunkBuilder, ChunkBuilder::getFreeBufferCount));
		this.field_33960.add(SamplingRecorder.create("toBatchCount", SamplingChannel.CHUNK_RENDERING_DISPATCHING, chunkBuilder, ChunkBuilder::getToBatchCount));
	}

	@Override
	public Set<SamplingRecorder> method_37189(Supplier<ReadableProfiler> supplier) {
		this.field_33960.addAll(this.field_33961.method_37194(supplier));
		return this.field_33960;
	}
}
