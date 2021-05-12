package net.minecraft.client.sound;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Channel {
	private final Set<Channel.SourceManager> sources = Sets.newIdentityHashSet();
	final SoundEngine soundEngine;
	final Executor executor;

	public Channel(SoundEngine soundEngine, Executor executor) {
		this.soundEngine = soundEngine;
		this.executor = executor;
	}

	public CompletableFuture<Channel.SourceManager> createSource(SoundEngine.RunMode mode) {
		CompletableFuture<Channel.SourceManager> completableFuture = new CompletableFuture();
		this.executor.execute(() -> {
			Source source = this.soundEngine.createSource(mode);
			if (source != null) {
				Channel.SourceManager sourceManager = new Channel.SourceManager(source);
				this.sources.add(sourceManager);
				completableFuture.complete(sourceManager);
			} else {
				completableFuture.complete(null);
			}
		});
		return completableFuture;
	}

	public void execute(Consumer<Stream<Source>> consumer) {
		this.executor.execute(() -> consumer.accept(this.sources.stream().map(sourceManager -> sourceManager.source).filter(Objects::nonNull)));
	}

	public void tick() {
		this.executor.execute(() -> {
			Iterator<Channel.SourceManager> iterator = this.sources.iterator();

			while (iterator.hasNext()) {
				Channel.SourceManager sourceManager = (Channel.SourceManager)iterator.next();
				sourceManager.source.tick();
				if (sourceManager.source.isStopped()) {
					sourceManager.close();
					iterator.remove();
				}
			}
		});
	}

	public void close() {
		this.sources.forEach(Channel.SourceManager::close);
		this.sources.clear();
	}

	@Environment(EnvType.CLIENT)
	public class SourceManager {
		@Nullable
		Source source;
		private boolean stopped;

		public boolean isStopped() {
			return this.stopped;
		}

		public SourceManager(Source source) {
			this.source = source;
		}

		public void run(Consumer<Source> action) {
			Channel.this.executor.execute(() -> {
				if (this.source != null) {
					action.accept(this.source);
				}
			});
		}

		public void close() {
			this.stopped = true;
			Channel.this.soundEngine.release(this.source);
			this.source = null;
		}
	}
}
