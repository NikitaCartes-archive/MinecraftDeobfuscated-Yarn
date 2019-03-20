package net.minecraft.client.audio;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;

@Environment(EnvType.CLIENT)
public class Source {
	private static final Logger LOGGER = LogManager.getLogger();
	private final int pointer;
	private boolean playing;
	private int field_18895 = 16384;
	@Nullable
	private AudioStream field_18896;

	@Nullable
	static Source method_19638() {
		int[] is = new int[1];
		AL10.alGenSources(is);
		return AlUtil.checkErrors("Allocate new source") ? null : new Source(is[0]);
	}

	private Source(int i) {
		this.pointer = i;
		this.playing = true;
	}

	public void close() {
		if (this.playing) {
			AL10.alSourceStop(this.pointer);
			if (this.field_18896 != null) {
				try {
					this.field_18896.close();
				} catch (IOException var2) {
					LOGGER.error("Failed to close audio stream", (Throwable)var2);
				}

				this.method_19660();
				this.field_18896 = null;
			}

			AL10.alDeleteSources(new int[]{this.pointer});
			AlUtil.checkErrors("Cleanup");
			this.playing = false;
		}
	}

	public void method_19650() {
		AL10.alSourcePlay(this.pointer);
	}

	private int getSourceState() {
		return AL10.alGetSourcei(this.pointer, 4112);
	}

	public void pause() {
		if (this.getSourceState() == 4114) {
			AL10.alSourcePause(this.pointer);
		}
	}

	public void play() {
		if (this.getSourceState() == 4115) {
			AL10.alSourcePlay(this.pointer);
		}
	}

	public void stop() {
		AL10.alSourceStop(this.pointer);
	}

	public boolean isStopped() {
		return this.getSourceState() == 4116;
	}

	public void setPosition(Vec3d vec3d) {
		AL10.alSourcefv(this.pointer, 4100, new float[]{(float)vec3d.x, (float)vec3d.y, (float)vec3d.z});
	}

	public void setPitch(float f) {
		AL10.alSourcef(this.pointer, 4099, f);
	}

	public void setLooping(boolean bl) {
		AL10.alSourcei(this.pointer, 4103, bl ? 1 : 0);
	}

	public void setVolume(float f) {
		AL10.alSourcef(this.pointer, 4106, f);
	}

	public void method_19657() {
		AL10.alSourcei(this.pointer, 53248, 0);
	}

	public void method_19651(float f) {
		AL10.alSourcei(this.pointer, 53248, 53251);
		AL10.alSourcef(this.pointer, 4131, f);
		AL10.alSourcef(this.pointer, 4129, 1.0F);
		AL10.alSourcef(this.pointer, 4128, 0.0F);
	}

	public void setRelative(boolean bl) {
		AL10.alSourcei(this.pointer, 514, bl ? 1 : 0);
	}

	public void method_19642(SoundData soundData) {
		soundData.getBufferPointer().ifPresent(i -> AL10.alSourcei(this.pointer, 4105, i));
	}

	public void method_19643(AudioStream audioStream) {
		this.field_18896 = audioStream;
		AudioFormat audioFormat = audioStream.getFormat();
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
						new SoundData(byteBuffer, this.field_18896.getFormat()).method_19688().ifPresent(ix -> AL10.alSourceQueueBuffers(this.pointer, new int[]{ix}));
					}
				}
			} catch (IOException var4) {
				LOGGER.error("Failed to read from audio stream", (Throwable)var4);
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
		int i = AL10.alGetSourcei(this.pointer, 4118);
		if (i > 0) {
			int[] is = new int[i];
			AL10.alSourceUnqueueBuffers(this.pointer, is);
			AlUtil.checkErrors("Unqueue buffers");
			AL10.alDeleteBuffers(is);
			AlUtil.checkErrors("Remove processed buffers");
		}

		return i;
	}
}
