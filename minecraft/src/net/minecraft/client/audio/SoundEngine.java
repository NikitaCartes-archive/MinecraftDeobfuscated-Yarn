package net.minecraft.client.audio;

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
	private long field_18898;
	private long field_18899;
	private static final SoundEngine.class_4276 field_19183 = new SoundEngine.class_4276() {
		@Nullable
		@Override
		public Source method_19666() {
			return null;
		}

		@Override
		public boolean method_19667(Source source) {
			return false;
		}

		@Override
		public void method_19668() {
		}

		@Override
		public int method_20298() {
			return 0;
		}

		@Override
		public int method_20299() {
			return 0;
		}
	};
	private SoundEngine.class_4276 field_19184 = field_19183;
	private SoundEngine.class_4276 field_19185 = field_19183;
	private final Listener listener = new Listener();

	public void init() {
		this.field_18898 = method_20050();
		ALCCapabilities aLCCapabilities = ALC.createCapabilities(this.field_18898);
		if (AlUtil.method_20051(this.field_18898, "Get capabilities")) {
			throw new IllegalStateException("Failed to get OpenAL capabilities");
		} else if (!aLCCapabilities.OpenALC11) {
			throw new IllegalStateException("OpenAL 1.1 not supported");
		} else {
			this.field_18899 = ALC10.alcCreateContext(this.field_18898, (IntBuffer)null);
			ALC10.alcMakeContextCurrent(this.field_18899);
			int i = this.method_20297();
			int j = MathHelper.clamp((int)MathHelper.sqrt((float)i), 2, 8);
			int k = MathHelper.clamp(i - j, 8, 255);
			this.field_19184 = new SoundEngine.class_4226(k);
			this.field_19185 = new SoundEngine.class_4226(j);
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
			int i = ALC10.alcGetInteger(this.field_18898, 4098);
			if (AlUtil.method_20051(this.field_18898, "Get attributes size")) {
				throw new IllegalStateException("Failed to get OpenAL attributes");
			}

			IntBuffer intBuffer = memoryStack.mallocInt(i);
			ALC10.alcGetIntegerv(this.field_18898, 4099, intBuffer);
			if (AlUtil.method_20051(this.field_18898, "Get attributes")) {
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

	private static long method_20050() {
		for (int i = 0; i < 3; i++) {
			long l = ALC10.alcOpenDevice((ByteBuffer)null);
			if (l != 0L && !AlUtil.method_20051(l, "Open device")) {
				return l;
			}
		}

		throw new IllegalStateException("Failed to open OpenAL device");
	}

	public void close() {
		this.field_19184.method_19668();
		this.field_19185.method_19668();
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
		return (runMode == SoundEngine.RunMode.field_18353 ? this.field_19185 : this.field_19184).method_19666();
	}

	public void release(Source source) {
		if (!this.field_19184.method_19667(source) && !this.field_19185.method_19667(source)) {
			throw new IllegalStateException("Tried to release unknown channel");
		}
	}

	public String method_20296() {
		return String.format(
			"Sounds: %d/%d + %d/%d", this.field_19184.method_20299(), this.field_19184.method_20298(), this.field_19185.method_20299(), this.field_19185.method_20298()
		);
	}

	@Environment(EnvType.CLIENT)
	public static enum RunMode {
		field_18352,
		field_18353;
	}

	@Environment(EnvType.CLIENT)
	static class class_4226 implements SoundEngine.class_4276 {
		private final int field_18903;
		private final Set<Source> sources = Sets.newIdentityHashSet();

		public class_4226(int i) {
			this.field_18903 = i;
		}

		@Nullable
		@Override
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

		@Override
		public boolean method_19667(Source source) {
			if (!this.sources.remove(source)) {
				return false;
			} else {
				source.close();
				return true;
			}
		}

		@Override
		public void method_19668() {
			this.sources.forEach(Source::close);
			this.sources.clear();
		}

		@Override
		public int method_20298() {
			return this.field_18903;
		}

		@Override
		public int method_20299() {
			return this.sources.size();
		}
	}

	@Environment(EnvType.CLIENT)
	interface class_4276 {
		@Nullable
		Source method_19666();

		boolean method_19667(Source source);

		void method_19668();

		int method_20298();

		int method_20299();
	}
}
