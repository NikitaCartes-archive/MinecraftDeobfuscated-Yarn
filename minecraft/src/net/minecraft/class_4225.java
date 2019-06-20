package net.minecraft;

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
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public class class_4225 {
	private static final Logger field_18897 = LogManager.getLogger();
	private long field_18898;
	private long field_18899;
	private static final class_4225.class_4276 field_19183 = new class_4225.class_4276() {
		@Nullable
		@Override
		public class_4224 method_19666() {
			return null;
		}

		@Override
		public boolean method_19667(class_4224 arg) {
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
	private class_4225.class_4276 field_19184 = field_19183;
	private class_4225.class_4276 field_19185 = field_19183;
	private final class_4227 field_18902 = new class_4227();

	public void method_19661() {
		this.field_18898 = method_20050();
		ALCCapabilities aLCCapabilities = ALC.createCapabilities(this.field_18898);
		if (class_4230.method_20051(this.field_18898, "Get capabilities")) {
			throw new IllegalStateException("Failed to get OpenAL capabilities");
		} else if (!aLCCapabilities.OpenALC11) {
			throw new IllegalStateException("OpenAL 1.1 not supported");
		} else {
			this.field_18899 = ALC10.alcCreateContext(this.field_18898, (IntBuffer)null);
			ALC10.alcMakeContextCurrent(this.field_18899);
			int i = this.method_20297();
			int j = class_3532.method_15340((int)class_3532.method_15355((float)i), 2, 8);
			int k = class_3532.method_15340(i - j, 8, 255);
			this.field_19184 = new class_4225.class_4226(k);
			this.field_19185 = new class_4225.class_4226(j);
			ALCapabilities aLCapabilities = AL.createCapabilities(aLCCapabilities);
			class_4230.method_19684("Initialization");
			if (!aLCapabilities.AL_EXT_source_distance_model) {
				throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
			} else {
				AL10.alEnable(512);
				if (!aLCapabilities.AL_EXT_LINEAR_DISTANCE) {
					throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
				} else {
					class_4230.method_19684("Enable per-source distance models");
					field_18897.info("OpenAL initialized.");
				}
			}
		}
	}

	private int method_20297() {
		int var8;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			int i = ALC10.alcGetInteger(this.field_18898, 4098);
			if (class_4230.method_20051(this.field_18898, "Get attributes size")) {
				throw new IllegalStateException("Failed to get OpenAL attributes");
			}

			IntBuffer intBuffer = memoryStack.mallocInt(i);
			ALC10.alcGetIntegerv(this.field_18898, 4099, intBuffer);
			if (class_4230.method_20051(this.field_18898, "Get attributes")) {
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
			if (l != 0L && !class_4230.method_20051(l, "Open device")) {
				return l;
			}
		}

		throw new IllegalStateException("Failed to open OpenAL device");
	}

	public void method_19664() {
		this.field_19184.method_19668();
		this.field_19185.method_19668();
		ALC10.alcDestroyContext(this.field_18899);
		if (this.field_18898 != 0L) {
			ALC10.alcCloseDevice(this.field_18898);
		}
	}

	public class_4227 method_19665() {
		return this.field_18902;
	}

	@Nullable
	public class_4224 method_19663(class_4225.class_4105 arg) {
		return (arg == class_4225.class_4105.field_18353 ? this.field_19185 : this.field_19184).method_19666();
	}

	public void method_19662(class_4224 arg) {
		if (!this.field_19184.method_19667(arg) && !this.field_19185.method_19667(arg)) {
			throw new IllegalStateException("Tried to release unknown channel");
		}
	}

	public String method_20296() {
		return String.format(
			"Sounds: %d/%d + %d/%d", this.field_19184.method_20299(), this.field_19184.method_20298(), this.field_19185.method_20299(), this.field_19185.method_20298()
		);
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4105 {
		field_18352,
		field_18353;
	}

	@Environment(EnvType.CLIENT)
	static class class_4226 implements class_4225.class_4276 {
		private final int field_18903;
		private final Set<class_4224> field_18904 = Sets.newIdentityHashSet();

		public class_4226(int i) {
			this.field_18903 = i;
		}

		@Nullable
		@Override
		public class_4224 method_19666() {
			if (this.field_18904.size() >= this.field_18903) {
				return null;
			} else {
				class_4224 lv = class_4224.method_19638();
				if (lv != null) {
					this.field_18904.add(lv);
				}

				return lv;
			}
		}

		@Override
		public boolean method_19667(class_4224 arg) {
			if (!this.field_18904.remove(arg)) {
				return false;
			} else {
				arg.method_19646();
				return true;
			}
		}

		@Override
		public void method_19668() {
			this.field_18904.forEach(class_4224::method_19646);
			this.field_18904.clear();
		}

		@Override
		public int method_20298() {
			return this.field_18903;
		}

		@Override
		public int method_20299() {
			return this.field_18904.size();
		}
	}

	@Environment(EnvType.CLIENT)
	interface class_4276 {
		@Nullable
		class_4224 method_19666();

		boolean method_19667(class_4224 arg);

		void method_19668();

		int method_20298();

		int method_20299();
	}
}
