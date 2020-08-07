package net.minecraft.client.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
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
	private final AtomicBoolean playing = new AtomicBoolean(true);
	private int bufferSize = 16384;
	@Nullable
	private AudioStream stream;

	@Nullable
	static Source create() {
		int[] is = new int[1];
		AL10.alGenSources(is);
		return AlUtil.checkErrors("Allocate new source") ? null : new Source(is[0]);
	}

	private Source(int pointer) {
		this.pointer = pointer;
	}

	public void close() {
		if (this.playing.compareAndSet(true, false)) {
			AL10.alSourceStop(this.pointer);
			AlUtil.checkErrors("Stop");
			if (this.stream != null) {
				try {
					this.stream.close();
				} catch (IOException var2) {
					LOGGER.error("Failed to close audio stream", (Throwable)var2);
				}

				this.removeProcessedBuffers();
				this.stream = null;
			}

			AL10.alDeleteSources(new int[]{this.pointer});
			AlUtil.checkErrors("Cleanup");
		}
	}

	public void play() {
		AL10.alSourcePlay(this.pointer);
	}

	private int getSourceState() {
		return !this.playing.get() ? 4116 : AL10.alGetSourcei(this.pointer, 4112);
	}

	public void pause() {
		if (this.getSourceState() == 4114) {
			AL10.alSourcePause(this.pointer);
		}
	}

	public void resume() {
		if (this.getSourceState() == 4115) {
			AL10.alSourcePlay(this.pointer);
		}
	}

	public void stop() {
		if (this.playing.get()) {
			AL10.alSourceStop(this.pointer);
			AlUtil.checkErrors("Stop");
		}
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

	public void disableAttenuation() {
		AL10.alSourcei(this.pointer, 53248, 0);
	}

	public void setAttenuation(float f) {
		AL10.alSourcei(this.pointer, 53248, 53251);
		AL10.alSourcef(this.pointer, 4131, f);
		AL10.alSourcef(this.pointer, 4129, 1.0F);
		AL10.alSourcef(this.pointer, 4128, 0.0F);
	}

	public void setRelative(boolean bl) {
		AL10.alSourcei(this.pointer, 514, bl ? 1 : 0);
	}

	public void setBuffer(StaticSound staticSound) {
		staticSound.getStreamBufferPointer().ifPresent(i -> AL10.alSourcei(this.pointer, 4105, i));
	}

	public void setStream(AudioStream stream) {
		this.stream = stream;
		AudioFormat audioFormat = stream.getFormat();
		this.bufferSize = getBufferSize(audioFormat, 1);
		this.method_19640(4);
	}

	private static int getBufferSize(AudioFormat format, int time) {
		return (int)((float)(time * format.getSampleSizeInBits()) / 8.0F * (float)format.getChannels() * format.getSampleRate());
	}

	private void method_19640(int i) {
		if (this.stream != null) {
			try {
				for (int j = 0; j < i; j++) {
					ByteBuffer byteBuffer = this.stream.getBuffer(this.bufferSize);
					if (byteBuffer != null) {
						new StaticSound(byteBuffer, this.stream.getFormat()).takeStreamBufferPointer().ifPresent(ix -> AL10.alSourceQueueBuffers(this.pointer, new int[]{ix}));
					}
				}
			} catch (IOException var4) {
				LOGGER.error("Failed to read from audio stream", (Throwable)var4);
			}
		}
	}

	public void tick() {
		if (this.stream != null) {
			int i = this.removeProcessedBuffers();
			this.method_19640(i);
		}
	}

	private int removeProcessedBuffers() {
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
