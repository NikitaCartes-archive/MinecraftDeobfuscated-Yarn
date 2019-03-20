package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Channel {
	private final Set<Channel.SourceManager> sourceLists = Sets.newIdentityHashSet();
	private final SoundEngine soundEngine;
	private final Executor executor;

	public Channel(SoundEngine soundEngine, Executor executor) {
		this.soundEngine = soundEngine;
		this.executor = executor;
	}

	public Channel.SourceManager method_19723(SoundEngine.RunMode runMode) {
		Channel.SourceManager sourceManager = new Channel.SourceManager();
		this.executor.execute(() -> {
			Source source = this.soundEngine.method_19663(runMode);
			if (source != null) {
				sourceManager.source = source;
				this.sourceLists.add(sourceManager);
			}
		});
		return sourceManager;
	}

	public void execute(Consumer<Stream<Source>> consumer) {
		this.executor.execute(() -> consumer.accept(this.sourceLists.stream().map(sourceManager -> sourceManager.source).filter(Objects::nonNull)));
	}

	public void tick() {
		this.executor.execute(() -> {
			Iterator<Channel.SourceManager> iterator = this.sourceLists.iterator();

			while (iterator.hasNext()) {
				Channel.SourceManager sourceManager = (Channel.SourceManager)iterator.next();
				sourceManager.source.method_19658();
				if (sourceManager.source.isStopped()) {
					sourceManager.close();
					iterator.remove();
				}
			}
		});
	}

	public void close() {
		this.sourceLists.forEach(Channel.SourceManager::close);
		this.sourceLists.clear();
	}

	@Environment(EnvType.CLIENT)
	public class SourceManager {
		private Source source;
		private boolean stopped;

		public boolean isStopped() {
			return this.stopped;
		}

		public void run(Consumer<Source> consumer) {
			Channel.this.executor.execute(() -> {
				if (this.source != null) {
					consumer.accept(this.source);
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
