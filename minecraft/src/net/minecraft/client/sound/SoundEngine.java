package net.minecraft.client.sound;

import com.google.common.collect.Sets;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public class SoundEngine {
	private static final Logger LOGGER = LogManager.getLogger();
	private long devicePointer;
	private long contextPointer;
	private static final SoundEngine.SourceSet EMPTY_SOURCE_SET = new SoundEngine.SourceSet() {
		@Nullable
		@Override
		public Source createSource() {
			return null;
		}

		@Override
		public boolean release(Source source) {
			return false;
		}

		@Override
		public void close() {
		}

		@Override
		public int getMaxSourceCount() {
			return 0;
		}

		@Override
		public int getSourceCount() {
			return 0;
		}
	};
	private SoundEngine.SourceSet streamingSources = EMPTY_SOURCE_SET;
	private SoundEngine.SourceSet staticSources = EMPTY_SOURCE_SET;
	private final Listener listener = new Listener();

	public void init() {
		this.devicePointer = openDevice();
		ALCCapabilities aLCCapabilities = ALC.createCapabilities(this.devicePointer);
		if (AlUtil.checkAlcErrors(this.devicePointer, "Get capabilities")) {
			throw new IllegalStateException("Failed to get OpenAL capabilities");
		} else if (!aLCCapabilities.OpenALC11) {
			throw new IllegalStateException("OpenAL 1.1 not supported");
		} else {
			this.contextPointer = ALC10.alcCreateContext(this.devicePointer, (IntBuffer)null);
			ALC10.alcMakeContextCurrent(this.contextPointer);
			int i = this.method_20297();
			int j = MathHelper.clamp((int)MathHelper.sqrt((float)i), 2, 8);
			int k = MathHelper.clamp(i - j, 8, 255);
			this.streamingSources = new SoundEngine.SourceSetImpl(k);
			this.staticSources = new SoundEngine.SourceSetImpl(j);
			ALCapabilities aLCapabilities = AL.createCapabilities(aLCCapabilities);
			AlUtil.checkErrors("Initialization");
			if (!aLCapabilities.AL_EXT_source_distance_model) {
				throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
			} else {
				AL10.alEnable(512);
				if (!aLCapabilities.AL_EXT_LINEAR_DISTANCE) {
					throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
				} else {
					AlUtil.checkErrors("Enable per-source distance models");
					LOGGER.info("OpenAL initialized.");
				}
			}
		}
	}

	private int method_20297() {
		int var8;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			int i = ALC10.alcGetInteger(this.devicePointer, 4098);
			if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes size")) {
				throw new IllegalStateException("Failed to get OpenAL attributes");
			}

			IntBuffer intBuffer = memoryStack.mallocInt(i);
			ALC10.alcGetIntegerv(this.devicePointer, 4099, intBuffer);
			if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes")) {
				throw new IllegalStateException("Failed to get OpenAL attributes");
			}

			int j = 0;

			int k;
			int l;
			do {
				if (j >= i) {
					return 30;
				}

				k = intBuffer.get(j++);
				if (k == 0) {
					return 30;
				}

				l = intBuffer.get(j++);
			} while (k != 4112);

			var8 = l;
		}

		return var8;
	}

	private static long openDevice() {
		for (int i = 0; i < 3; i++) {
			long l = ALC10.alcOpenDevice((ByteBuffer)null);
			if (l != 0L && !AlUtil.checkAlcErrors(l, "Open device")) {
				return l;
			}
		}

		throw new IllegalStateException("Failed to open OpenAL device");
	}

	public void close() {
		this.streamingSources.close();
		this.staticSources.close();
		ALC10.alcDestroyContext(this.contextPointer);
		if (this.devicePointer != 0L) {
			ALC10.alcCloseDevice(this.devicePointer);
		}
	}

	public Listener getListener() {
		return this.listener;
	}

	@Nullable
	public Source createSource(SoundEngine.RunMode runMode) {
		return (runMode == SoundEngine.RunMode.STREAMING ? this.staticSources : this.streamingSources).createSource();
	}

	public void release(Source source) {
		if (!this.streamingSources.release(source) && !this.staticSources.release(source)) {
			throw new IllegalStateException("Tried to release unknown channel");
		}
	}

	public String getDebugString() {
		return String.format(
			"Sounds: %d/%d + %d/%d",
			this.streamingSources.getSourceCount(),
			this.streamingSources.getMaxSourceCount(),
			this.staticSources.getSourceCount(),
			this.staticSources.getMaxSourceCount()
		);
	}

	@Environment(EnvType.CLIENT)
	public static enum RunMode {
		STATIC,
		STREAMING;
	}

	@Environment(EnvType.CLIENT)
	interface SourceSet {
		@Nullable
		Source createSource();

		boolean release(Source source);

		void close();

		int getMaxSourceCount();

		int getSourceCount();
	}

	@Environment(EnvType.CLIENT)
	static class SourceSetImpl implements SoundEngine.SourceSet {
		private final int maxSourceCount;
		private final Set<Source> sources = Sets.newIdentityHashSet();

		public SourceSetImpl(int i) {
			this.maxSourceCount = i;
		}

		@Nullable
		@Override
		public Source createSource() {
			if (this.sources.size() >= this.maxSourceCount) {
				return null;
			} else {
				Source source = Source.create();
				if (source != null) {
					this.sources.add(source);
				}

				return source;
			}
		}

		@Override
		public boolean release(Source source) {
			if (!this.sources.remove(source)) {
				return false;
			} else {
				source.close();
				return true;
			}
		}

		@Override
		public void close() {
			this.sources.forEach(Source::close);
			this.sources.clear();
		}

		@Override
		public int getMaxSourceCount() {
			return this.maxSourceCount;
		}

		@Override
		public int getSourceCount() {
			return this.sources.size();
		}
	}
}
