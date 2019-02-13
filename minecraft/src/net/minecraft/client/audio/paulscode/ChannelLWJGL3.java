package net.minecraft.client.audio.paulscode;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;

@Environment(EnvType.CLIENT)
public class ChannelLWJGL3 extends Channel {
	public IntBuffer alSource;
	public int alFormat;
	public int sampleRate;
	public float millisPreviouslyPlayed;

	public ChannelLWJGL3(int i, IntBuffer intBuffer) {
		super(i);
		this.libraryType = LibraryLWJGL3.class;
		this.alSource = intBuffer;
	}

	@Override
	public void cleanup() {
		if (this.alSource != null) {
			try {
				AL10.alSourceStop(this.alSource.get(0));
				AL10.alGetError();
			} catch (Exception var3) {
			}

			try {
				AL10.alDeleteSources(this.alSource);
				AL10.alGetError();
			} catch (Exception var2) {
			}

			this.alSource.clear();
		}

		this.alSource = null;
		super.cleanup();
	}

	public boolean attachBuffer(IntBuffer intBuffer) {
		if (this.errorCheck(this.channelType != 0, "Sound buffers may only be attached to normal sources.")) {
			return false;
		} else {
			AL10.alSourcei(this.alSource.get(0), 4105, intBuffer.get(0));
			if (this.attachedSource != null && this.attachedSource.soundBuffer != null && this.attachedSource.soundBuffer.audioFormat != null) {
				this.setAudioFormat(this.attachedSource.soundBuffer.audioFormat);
			}

			return this.checkALError();
		}
	}

	@Override
	public void setAudioFormat(AudioFormat audioFormat) {
		int i;
		if (audioFormat.getChannels() == 1) {
			if (audioFormat.getSampleSizeInBits() == 8) {
				i = 4352;
			} else {
				if (audioFormat.getSampleSizeInBits() != 16) {
					this.errorMessage("Illegal sample size in method 'setAudioFormat'");
					return;
				}

				i = 4353;
			}
		} else {
			if (audioFormat.getChannels() != 2) {
				this.errorMessage("Audio data neither mono nor stereo in method 'setAudioFormat'");
				return;
			}

			if (audioFormat.getSampleSizeInBits() == 8) {
				i = 4354;
			} else {
				if (audioFormat.getSampleSizeInBits() != 16) {
					this.errorMessage("Illegal sample size in method 'setAudioFormat'");
					return;
				}

				i = 4355;
			}
		}

		this.alFormat = i;
		this.sampleRate = (int)audioFormat.getSampleRate();
	}

	public void setFormat(int i, int j) {
		this.alFormat = i;
		this.sampleRate = j;
	}

	@Override
	public boolean preLoadBuffers(LinkedList<byte[]> linkedList) {
		if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
			return false;
		} else if (this.errorCheck(linkedList == null, "Buffer List null in method 'preLoadBuffers'")) {
			return false;
		} else {
			boolean bl = this.playing();
			if (bl) {
				AL10.alSourceStop(this.alSource.get(0));
				this.checkALError();
			}

			int i = AL10.alGetSourcei(this.alSource.get(0), 4118);
			if (i > 0) {
				IntBuffer intBuffer = BufferUtils.createIntBuffer(i);
				AL10.alGenBuffers(intBuffer);
				if (this.errorCheck(this.checkALError(), "Error clearing stream buffers in method 'preLoadBuffers'")) {
					return false;
				}

				AL10.alSourceUnqueueBuffers(this.alSource.get(0), intBuffer);
				if (this.errorCheck(this.checkALError(), "Error unqueuing stream buffers in method 'preLoadBuffers'")) {
					return false;
				}
			}

			if (bl) {
				AL10.alSourcePlay(this.alSource.get(0));
				this.checkALError();
			}

			IntBuffer intBufferx = BufferUtils.createIntBuffer(linkedList.size());
			AL10.alGenBuffers(intBufferx);
			if (this.errorCheck(this.checkALError(), "Error generating stream buffers in method 'preLoadBuffers'")) {
				return false;
			} else {
				for (int j = 0; j < linkedList.size(); j++) {
					ByteBuffer byteBuffer = (ByteBuffer)BufferUtils.createByteBuffer(((byte[])linkedList.get(j)).length).put((byte[])linkedList.get(j)).flip();

					try {
						AL10.alBufferData(intBufferx.get(j), this.alFormat, byteBuffer, this.sampleRate);
					} catch (Exception var9) {
						this.errorMessage("Error creating buffers in method 'preLoadBuffers'");
						this.printStackTrace(var9);
						return false;
					}

					if (this.errorCheck(this.checkALError(), "Error creating buffers in method 'preLoadBuffers'")) {
						return false;
					}
				}

				try {
					AL10.alSourceQueueBuffers(this.alSource.get(0), intBufferx);
				} catch (Exception var8) {
					this.errorMessage("Error queuing buffers in method 'preLoadBuffers'");
					this.printStackTrace(var8);
					return false;
				}

				if (this.errorCheck(this.checkALError(), "Error queuing buffers in method 'preLoadBuffers'")) {
					return false;
				} else {
					AL10.alSourcePlay(this.alSource.get(0));
					return !this.errorCheck(this.checkALError(), "Error playing source in method 'preLoadBuffers'");
				}
			}
		}
	}

	@Override
	public boolean queueBuffer(byte[] bs) {
		if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
			return false;
		} else {
			ByteBuffer byteBuffer = (ByteBuffer)BufferUtils.createByteBuffer(bs.length).put(bs).flip();
			IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
			AL10.alSourceUnqueueBuffers(this.alSource.get(0), intBuffer);
			if (this.checkALError()) {
				return false;
			} else {
				if (AL10.alIsBuffer(intBuffer.get(0))) {
					this.millisPreviouslyPlayed = this.millisPreviouslyPlayed + this.millisInBuffer(intBuffer.get(0));
				}

				this.checkALError();
				AL10.alBufferData(intBuffer.get(0), this.alFormat, byteBuffer, this.sampleRate);
				if (this.checkALError()) {
					return false;
				} else {
					AL10.alSourceQueueBuffers(this.alSource.get(0), intBuffer);
					return !this.checkALError();
				}
			}
		}
	}

	@Override
	public int feedRawAudioData(byte[] bs) {
		if (this.errorCheck(this.channelType != 1, "Raw audio data can only be fed to streaming sources.")) {
			return -1;
		} else {
			ByteBuffer byteBuffer = (ByteBuffer)BufferUtils.createByteBuffer(bs.length).put(bs).flip();
			int i = AL10.alGetSourcei(this.alSource.get(0), 4118);
			IntBuffer intBuffer;
			if (i > 0) {
				intBuffer = BufferUtils.createIntBuffer(i);
				AL10.alGenBuffers(intBuffer);
				if (this.errorCheck(this.checkALError(), "Error clearing stream buffers in method 'feedRawAudioData'")) {
					return -1;
				}

				AL10.alSourceUnqueueBuffers(this.alSource.get(0), intBuffer);
				if (this.errorCheck(this.checkALError(), "Error unqueuing stream buffers in method 'feedRawAudioData'")) {
					return -1;
				}

				if (AL10.alIsBuffer(intBuffer.get(0))) {
					this.millisPreviouslyPlayed = this.millisPreviouslyPlayed + this.millisInBuffer(intBuffer.get(0));
				}

				this.checkALError();
			} else {
				intBuffer = BufferUtils.createIntBuffer(1);
				AL10.alGenBuffers(intBuffer);
				if (this.errorCheck(this.checkALError(), "Error generating stream buffers in method 'preLoadBuffers'")) {
					return -1;
				}
			}

			AL10.alBufferData(intBuffer.get(0), this.alFormat, byteBuffer, this.sampleRate);
			if (this.checkALError()) {
				return -1;
			} else {
				AL10.alSourceQueueBuffers(this.alSource.get(0), intBuffer);
				if (this.checkALError()) {
					return -1;
				} else {
					if (this.attachedSource != null && this.attachedSource.channel == this && this.attachedSource.active() && !this.playing()) {
						AL10.alSourcePlay(this.alSource.get(0));
						this.checkALError();
					}

					return i;
				}
			}
		}
	}

	public float millisInBuffer(int i) {
		return (float)(1000 * AL10.alGetBufferi(i, 8196) / AL10.alGetBufferi(i, 8195)) / ((float)AL10.alGetBufferi(i, 8194) / 8.0F) / (float)this.sampleRate;
	}

	@Override
	public float millisecondsPlayed() {
		float f = (float)AL10.alGetSourcei(this.alSource.get(0), 4134);
		float g = 1.0F;
		switch (this.alFormat) {
			case 4352:
				g = 1.0F;
				break;
			case 4353:
				g = 2.0F;
				break;
			case 4354:
				g = 2.0F;
				break;
			case 4355:
				g = 4.0F;
		}

		f = f / g / (float)this.sampleRate * 1000.0F;
		if (this.channelType == 1) {
			f += this.millisPreviouslyPlayed;
		}

		return f;
	}

	@Override
	public int buffersProcessed() {
		if (this.channelType != 1) {
			return 0;
		} else {
			int i = AL10.alGetSourcei(this.alSource.get(0), 4118);
			return this.checkALError() ? 0 : i;
		}
	}

	@Override
	public void flush() {
		if (this.channelType == 1) {
			int i = AL10.alGetSourcei(this.alSource.get(0), 4117);
			if (!this.checkALError()) {
				for (IntBuffer intBuffer = BufferUtils.createIntBuffer(1); i > 0; i--) {
					try {
						AL10.alSourceUnqueueBuffers(this.alSource.get(0), intBuffer);
					} catch (Exception var4) {
						return;
					}

					if (this.checkALError()) {
						return;
					}
				}

				this.millisPreviouslyPlayed = 0.0F;
			}
		}
	}

	@Override
	public void close() {
		try {
			AL10.alSourceStop(this.alSource.get(0));
			AL10.alGetError();
		} catch (Exception var2) {
		}

		if (this.channelType == 1) {
			this.flush();
		}
	}

	@Override
	public void play() {
		AL10.alSourcePlay(this.alSource.get(0));
		this.checkALError();
	}

	@Override
	public void pause() {
		AL10.alSourcePause(this.alSource.get(0));
		this.checkALError();
	}

	@Override
	public void stop() {
		AL10.alSourceStop(this.alSource.get(0));
		if (!this.checkALError()) {
			this.millisPreviouslyPlayed = 0.0F;
		}
	}

	@Override
	public void rewind() {
		if (this.channelType != 1) {
			AL10.alSourceRewind(this.alSource.get(0));
			if (!this.checkALError()) {
				this.millisPreviouslyPlayed = 0.0F;
			}
		}
	}

	@Override
	public boolean playing() {
		int i = AL10.alGetSourcei(this.alSource.get(0), 4112);
		return this.checkALError() ? false : i == 4114;
	}

	private boolean checkALError() {
		switch (AL10.alGetError()) {
			case 0:
				return false;
			case 40961:
				this.errorMessage("Invalid name parameter.");
				return true;
			case 40962:
				this.errorMessage("Invalid parameter.");
				return true;
			case 40963:
				this.errorMessage("Invalid enumerated parameter value.");
				return true;
			case 40964:
				this.errorMessage("Illegal call.");
				return true;
			case 40965:
				this.errorMessage("Unable to allocate memory.");
				return true;
			default:
				this.errorMessage("An unrecognized error occurred.");
				return true;
		}
	}
}
