package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

@Environment(EnvType.CLIENT)
public class SoundEngine {
	private static final Logger LOGGER = LogManager.getLogger();
	private long field_18898;
	private long field_18899;
	private final SoundEngine.class_4226 field_18900 = new SoundEngine.class_4226(28);
	private final SoundEngine.class_4226 field_18901 = new SoundEngine.class_4226(8);
	private final Listener listener = new Listener();

	public void init() {
		this.field_18898 = ALC10.alcOpenDevice((ByteBuffer)null);
		if (this.field_18898 == 0L) {
			throw new IllegalStateException("Failed to open default device");
		} else {
			ALCCapabilities aLCCapabilities = ALC.createCapabilities(this.field_18898);
			if (!aLCCapabilities.OpenALC10) {
				throw new IllegalStateException("OpenAL 1.0 not supported");
			} else {
				this.field_18899 = ALC10.alcCreateContext(this.field_18898, (IntBuffer)null);
				ALC10.alcMakeContextCurrent(this.field_18899);
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
	}

	public void close() {
		this.field_18900.close();
		this.field_18901.close();
		ALC10.alcDestroyContext(this.field_18899);
		if (this.field_18898 != 0L) {
			ALC10.alcCloseDevice(this.field_18898);
		}
	}

	public Listener getListener() {
		return this.listener;
	}

	@Nullable
	public Source method_19663(SoundEngine.RunMode runMode) {
		return (runMode == SoundEngine.RunMode.field_18353 ? this.field_18901 : this.field_18900).method_19666();
	}

	public void release(Source source) {
		if (!this.field_18900.method_19667(source) && !this.field_18901.method_19667(source)) {
			throw new IllegalStateException("Tried to release unknown channel");
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum RunMode {
		field_18352,
		field_18353;
	}

	@Environment(EnvType.CLIENT)
	static class class_4226 {
		private final int field_18903;
		private final Set<Source> sources = Sets.newIdentityHashSet();

		public class_4226(int i) {
			this.field_18903 = i;
		}

		@Nullable
		public Source method_19666() {
			if (this.sources.size() >= this.field_18903) {
				return null;
			} else {
				Source source = Source.method_19638();
				if (source != null) {
					this.sources.add(source);
				}

				return source;
			}
		}

		public boolean method_19667(Source source) {
			if (!this.sources.remove(source)) {
				return false;
			} else {
				source.close();
				return true;
			}
		}

		public void close() {
			this.sources.forEach(Source::close);
			this.sources.clear();
		}
	}
}
