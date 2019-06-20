package net.minecraft;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;

@Environment(EnvType.CLIENT)
public class class_4224 {
	private static final Logger field_18892 = LogManager.getLogger();
	private final int field_18893;
	private AtomicBoolean field_18894 = new AtomicBoolean(true);
	private int field_18895 = 16384;
	@Nullable
	private class_4234 field_18896;

	@Nullable
	static class_4224 method_19638() {
		int[] is = new int[1];
		AL10.alGenSources(is);
		return class_4230.method_19684("Allocate new source") ? null : new class_4224(is[0]);
	}

	private class_4224(int i) {
		this.field_18893 = i;
	}

	public void method_19646() {
		if (this.field_18894.compareAndSet(true, false)) {
			AL10.alSourceStop(this.field_18893);
			class_4230.method_19684("Stop");
			if (this.field_18896 != null) {
				try {
					this.field_18896.close();
				} catch (IOException var2) {
					field_18892.error("Failed to close audio stream", (Throwable)var2);
				}

				this.method_19660();
				this.field_18896 = null;
			}

			AL10.alDeleteSources(new int[]{this.field_18893});
			class_4230.method_19684("Cleanup");
		}
	}

	public void method_19650() {
		AL10.alSourcePlay(this.field_18893);
	}

	private int method_19659() {
		return !this.field_18894.get() ? 4116 : AL10.alGetSourcei(this.field_18893, 4112);
	}

	public void method_19653() {
		if (this.method_19659() == 4114) {
			AL10.alSourcePause(this.field_18893);
		}
	}

	public void method_19654() {
		if (this.method_19659() == 4115) {
			AL10.alSourcePlay(this.field_18893);
		}
	}

	public void method_19655() {
		if (this.field_18894.get()) {
			AL10.alSourceStop(this.field_18893);
			class_4230.method_19684("Stop");
		}
	}

	public boolean method_19656() {
		return this.method_19659() == 4116;
	}

	public void method_19641(class_243 arg) {
		AL10.alSourcefv(this.field_18893, 4100, new float[]{(float)arg.field_1352, (float)arg.field_1351, (float)arg.field_1350});
	}

	public void method_19639(float f) {
		AL10.alSourcef(this.field_18893, 4099, f);
	}

	public void method_19645(boolean bl) {
		AL10.alSourcei(this.field_18893, 4103, bl ? 1 : 0);
	}

	public void method_19647(float f) {
		AL10.alSourcef(this.field_18893, 4106, f);
	}

	public void method_19657() {
		AL10.alSourcei(this.field_18893, 53248, 0);
	}

	public void method_19651(float f) {
		AL10.alSourcei(this.field_18893, 53248, 53251);
		AL10.alSourcef(this.field_18893, 4131, f);
		AL10.alSourcef(this.field_18893, 4129, 1.0F);
		AL10.alSourcef(this.field_18893, 4128, 0.0F);
	}

	public void method_19649(boolean bl) {
		AL10.alSourcei(this.field_18893, 514, bl ? 1 : 0);
	}

	public void method_19642(class_4231 arg) {
		arg.method_19686().ifPresent(i -> AL10.alSourcei(this.field_18893, 4105, i));
	}

	public void method_19643(class_4234 arg) {
		this.field_18896 = arg;
		AudioFormat audioFormat = arg.method_19719();
		this.field_18895 = method_19644(audioFormat, 1);
		this.method_19640(4);
	}

	private static int method_19644(AudioFormat audioFormat, int i) {
		return (int)((float)(i * audioFormat.getSampleSizeInBits()) / 8.0F * (float)audioFormat.getChannels() * audioFormat.getSampleRate());
	}

	private void method_19640(int i) {
		if (this.field_18896 != null) {
			try {
				for (int j = 0; j < i; j++) {
					ByteBuffer byteBuffer = this.field_18896.method_19720(this.field_18895);
					if (byteBuffer != null) {
						new class_4231(byteBuffer, this.field_18896.method_19719()).method_19688().ifPresent(ix -> AL10.alSourceQueueBuffers(this.field_18893, new int[]{ix}));
					}
				}
			} catch (IOException var4) {
				field_18892.error("Failed to read from audio stream", (Throwable)var4);
			}
		}
	}

	public void method_19658() {
		if (this.field_18896 != null) {
			int i = this.method_19660();
			this.method_19640(i);
		}
	}

	private int method_19660() {
		int i = AL10.alGetSourcei(this.field_18893, 4118);
		if (i > 0) {
			int[] is = new int[i];
			AL10.alSourceUnqueueBuffers(this.field_18893, is);
			class_4230.method_19684("Unqueue buffers");
			AL10.alDeleteBuffers(is);
			class_4230.method_19684("Remove processed buffers");
		}

		return i;
	}
}
