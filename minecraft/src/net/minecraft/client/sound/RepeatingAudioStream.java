package net.minecraft.client.sound;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RepeatingAudioStream implements AudioStream {
	private final RepeatingAudioStream.DelegateFactory delegateFactory;
	private AudioStream delegate;
	private final BufferedInputStream inputStream;

	public RepeatingAudioStream(RepeatingAudioStream.DelegateFactory delegateFactory, InputStream inputStream) throws IOException {
		this.delegateFactory = delegateFactory;
		this.inputStream = new BufferedInputStream(inputStream);
		this.inputStream.mark(Integer.MAX_VALUE);
		this.delegate = delegateFactory.create(new RepeatingAudioStream.ReusableInputStream(this.inputStream));
	}

	@Override
	public AudioFormat getFormat() {
		return this.delegate.getFormat();
	}

	@Override
	public ByteBuffer read(int size) throws IOException {
		ByteBuffer byteBuffer = this.delegate.read(size);
		if (!byteBuffer.hasRemaining()) {
			this.delegate.close();
			this.inputStream.reset();
			this.delegate = this.delegateFactory.create(new RepeatingAudioStream.ReusableInputStream(this.inputStream));
			byteBuffer = this.delegate.read(size);
		}

		return byteBuffer;
	}

	public void close() throws IOException {
		this.delegate.close();
		this.inputStream.close();
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface DelegateFactory {
		AudioStream create(InputStream stream) throws IOException;
	}

	@Environment(EnvType.CLIENT)
	static class ReusableInputStream extends FilterInputStream {
		ReusableInputStream(InputStream stream) {
			super(stream);
		}

		public void close() {
		}
	}
}
