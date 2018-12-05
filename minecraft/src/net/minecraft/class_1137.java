package net.minecraft;

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
public class class_1137 extends Channel {
	public IntBuffer field_5539;
	public int field_5541;
	public int field_5540;
	public float field_5538;

	public class_1137(int i, IntBuffer intBuffer) {
		super(i);
		this.libraryType = class_1138.class;
		this.field_5539 = intBuffer;
	}

	@Override
	public void cleanup() {
		if (this.field_5539 != null) {
			try {
				AL10.alSourceStop(this.field_5539.get(0));
				AL10.alGetError();
			} catch (Exception var3) {
			}

			try {
				AL10.alDeleteSources(this.field_5539);
				AL10.alGetError();
			} catch (Exception var2) {
			}

			this.field_5539.clear();
		}

		this.field_5539 = null;
		super.cleanup();
	}

	public boolean method_4827(IntBuffer intBuffer) {
		if (this.errorCheck(this.channelType != 0, "Sound buffers may only be attached to normal sources.")) {
			return false;
		} else {
			AL10.alSourcei(this.field_5539.get(0), 4105, intBuffer.get(0));
			if (this.attachedSource != null && this.attachedSource.soundBuffer != null && this.attachedSource.soundBuffer.audioFormat != null) {
				this.setAudioFormat(this.attachedSource.soundBuffer.audioFormat);
			}

			return this.method_4829();
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

		this.field_5541 = i;
		this.field_5540 = (int)audioFormat.getSampleRate();
	}

	public void method_4828(int i, int j) {
		this.field_5541 = i;
		this.field_5540 = j;
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
				AL10.alSourceStop(this.field_5539.get(0));
				this.method_4829();
			}

			int i = AL10.alGetSourcei(this.field_5539.get(0), 4118);
			if (i > 0) {
				IntBuffer intBuffer = BufferUtils.createIntBuffer(i);
				AL10.alGenBuffers(intBuffer);
				if (this.errorCheck(this.method_4829(), "Error clearing stream buffers in method 'preLoadBuffers'")) {
					return false;
				}

				AL10.alSourceUnqueueBuffers(this.field_5539.get(0), intBuffer);
				if (this.errorCheck(this.method_4829(), "Error unqueuing stream buffers in method 'preLoadBuffers'")) {
					return false;
				}
			}

			if (bl) {
				AL10.alSourcePlay(this.field_5539.get(0));
				this.method_4829();
			}

			IntBuffer intBufferx = BufferUtils.createIntBuffer(linkedList.size());
			AL10.alGenBuffers(intBufferx);
			if (this.errorCheck(this.method_4829(), "Error generating stream buffers in method 'preLoadBuffers'")) {
				return false;
			} else {
				for (int j = 0; j < linkedList.size(); j++) {
					ByteBuffer byteBuffer = (ByteBuffer)BufferUtils.createByteBuffer(((byte[])linkedList.get(j)).length).put((byte[])linkedList.get(j)).flip();

					try {
						AL10.alBufferData(intBufferx.get(j), this.field_5541, byteBuffer, this.field_5540);
					} catch (Exception var9) {
						this.errorMessage("Error creating buffers in method 'preLoadBuffers'");
						this.printStackTrace(var9);
						return false;
					}

					if (this.errorCheck(this.method_4829(), "Error creating buffers in method 'preLoadBuffers'")) {
						return false;
					}
				}

				try {
					AL10.alSourceQueueBuffers(this.field_5539.get(0), intBufferx);
				} catch (Exception var8) {
					this.errorMessage("Error queuing buffers in method 'preLoadBuffers'");
					this.printStackTrace(var8);
					return false;
				}

				if (this.errorCheck(this.method_4829(), "Error queuing buffers in method 'preLoadBuffers'")) {
					return false;
				} else {
					AL10.alSourcePlay(this.field_5539.get(0));
					return !this.errorCheck(this.method_4829(), "Error playing source in method 'preLoadBuffers'");
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
			AL10.alSourceUnqueueBuffers(this.field_5539.get(0), intBuffer);
			if (this.method_4829()) {
				return false;
			} else {
				if (AL10.alIsBuffer(intBuffer.get(0))) {
					this.field_5538 = this.field_5538 + this.method_4830(intBuffer.get(0));
				}

				this.method_4829();
				AL10.alBufferData(intBuffer.get(0), this.field_5541, byteBuffer, this.field_5540);
				if (this.method_4829()) {
					return false;
				} else {
					AL10.alSourceQueueBuffers(this.field_5539.get(0), intBuffer);
					return !this.method_4829();
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
			int i = AL10.alGetSourcei(this.field_5539.get(0), 4118);
			IntBuffer intBuffer;
			if (i > 0) {
				intBuffer = BufferUtils.createIntBuffer(i);
				AL10.alGenBuffers(intBuffer);
				if (this.errorCheck(this.method_4829(), "Error clearing stream buffers in method 'feedRawAudioData'")) {
					return -1;
				}

				AL10.alSourceUnqueueBuffers(this.field_5539.get(0), intBuffer);
				if (this.errorCheck(this.method_4829(), "Error unqueuing stream buffers in method 'feedRawAudioData'")) {
					return -1;
				}

				if (AL10.alIsBuffer(intBuffer.get(0))) {
					this.field_5538 = this.field_5538 + this.method_4830(intBuffer.get(0));
				}

				this.method_4829();
			} else {
				intBuffer = BufferUtils.createIntBuffer(1);
				AL10.alGenBuffers(intBuffer);
				if (this.errorCheck(this.method_4829(), "Error generating stream buffers in method 'preLoadBuffers'")) {
					return -1;
				}
			}

			AL10.alBufferData(intBuffer.get(0), this.field_5541, byteBuffer, this.field_5540);
			if (this.method_4829()) {
				return -1;
			} else {
				AL10.alSourceQueueBuffers(this.field_5539.get(0), intBuffer);
				if (this.method_4829()) {
					return -1;
				} else {
					if (this.attachedSource != null && this.attachedSource.channel == this && this.attachedSource.active() && !this.playing()) {
						AL10.alSourcePlay(this.field_5539.get(0));
						this.method_4829();
					}

					return i;
				}
			}
		}
	}

	public float method_4830(int i) {
		return (float)(1000 * AL10.alGetBufferi(i, 8196) / AL10.alGetBufferi(i, 8195)) / ((float)AL10.alGetBufferi(i, 8194) / 8.0F) / (float)this.field_5540;
	}

	@Override
	public float millisecondsPlayed() {
		float f = (float)AL10.alGetSourcei(this.field_5539.get(0), 4134);
		float g = 1.0F;
		switch (this.field_5541) {
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

		f = f / g / (float)this.field_5540 * 1000.0F;
		if (this.channelType == 1) {
			f += this.field_5538;
		}

		return f;
	}

	@Override
	public int buffersProcessed() {
		if (this.channelType != 1) {
			return 0;
		} else {
			int i = AL10.alGetSourcei(this.field_5539.get(0), 4118);
			return this.method_4829() ? 0 : i;
		}
	}

	@Override
	public void flush() {
		if (this.channelType == 1) {
			int i = AL10.alGetSourcei(this.field_5539.get(0), 4117);
			if (!this.method_4829()) {
				for (IntBuffer intBuffer = BufferUtils.createIntBuffer(1); i > 0; i--) {
					try {
						AL10.alSourceUnqueueBuffers(this.field_5539.get(0), intBuffer);
					} catch (Exception var4) {
						return;
					}

					if (this.method_4829()) {
						return;
					}
				}

				this.field_5538 = 0.0F;
			}
		}
	}

	@Override
	public void close() {
		try {
			AL10.alSourceStop(this.field_5539.get(0));
			AL10.alGetError();
		} catch (Exception var2) {
		}

		if (this.channelType == 1) {
			this.flush();
		}
	}

	@Override
	public void play() {
		AL10.alSourcePlay(this.field_5539.get(0));
		this.method_4829();
	}

	@Override
	public void pause() {
		AL10.alSourcePause(this.field_5539.get(0));
		this.method_4829();
	}

	@Override
	public void stop() {
		AL10.alSourceStop(this.field_5539.get(0));
		if (!this.method_4829()) {
			this.field_5538 = 0.0F;
		}
	}

	@Override
	public void rewind() {
		if (this.channelType != 1) {
			AL10.alSourceRewind(this.field_5539.get(0));
			if (!this.method_4829()) {
				this.field_5538 = 0.0F;
			}
		}
	}

	@Override
	public boolean playing() {
		int i = AL10.alGetSourcei(this.field_5539.get(0), 4112);
		return this.method_4829() ? false : i == 4114;
	}

	private boolean method_4829() {
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
