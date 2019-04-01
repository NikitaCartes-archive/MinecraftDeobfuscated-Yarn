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

@Environment(EnvType.CLIENT)
public class class_4225 {
	private static final Logger field_18897 = LogManager.getLogger();
	private long field_18898;
	private long field_18899;
	private final class_4225.class_4226 field_18900 = new class_4225.class_4226(28);
	private final class_4225.class_4226 field_18901 = new class_4225.class_4226(8);
	private final class_4227 field_18902 = new class_4227();

	public void method_19661() {
		this.field_18898 = method_20050();
		ALCCapabilities aLCCapabilities = ALC.createCapabilities(this.field_18898);
		if (class_4230.method_20051(this.field_18898, "Get capabilities")) {
			throw new IllegalStateException("Failed to get OpenAL capabilities");
		} else if (!aLCCapabilities.OpenALC10) {
			throw new IllegalStateException("OpenAL 1.0 not supported");
		} else {
			this.field_18899 = ALC10.alcCreateContext(this.field_18898, (IntBuffer)null);
			ALC10.alcMakeContextCurrent(this.field_18899);
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
		this.field_18900.method_19668();
		this.field_18901.method_19668();
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
		return (arg == class_4225.class_4105.field_18353 ? this.field_18901 : this.field_18900).method_19666();
	}

	public void method_19662(class_4224 arg) {
		if (!this.field_18900.method_19667(arg) && !this.field_18901.method_19667(arg)) {
			throw new IllegalStateException("Tried to release unknown channel");
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4105 {
		field_18352,
		field_18353;
	}

	@Environment(EnvType.CLIENT)
	static class class_4226 {
		private final int field_18903;
		private final Set<class_4224> field_18904 = Sets.newIdentityHashSet();

		public class_4226(int i) {
			this.field_18903 = i;
		}

		@Nullable
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

		public boolean method_19667(class_4224 arg) {
			if (!this.field_18904.remove(arg)) {
				return false;
			} else {
				arg.method_19646();
				return true;
			}
		}

		public void method_19668() {
			this.field_18904.forEach(class_4224::method_19646);
			this.field_18904.clear();
		}
	}
}
