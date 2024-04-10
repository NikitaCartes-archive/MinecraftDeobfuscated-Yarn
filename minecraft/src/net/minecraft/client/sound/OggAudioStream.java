package net.minecraft.client.sound;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OggAudioStream implements BufferedAudioStream {
	private static final int field_51442 = 8192;
	private static final int field_51443 = -1;
	private static final int field_51444 = 0;
	private static final int field_51445 = 1;
	private static final int field_51446 = -1;
	private static final int field_51447 = 0;
	private static final int field_51448 = 1;
	private final SyncState syncState = new SyncState();
	private final Page page = new Page();
	private final StreamState streamState = new StreamState();
	private final Packet packet = new Packet();
	private final Info info = new Info();
	private final DspState dspState = new DspState();
	private final Block block = new Block(this.dspState);
	private final AudioFormat format;
	private final InputStream inputStream;
	private long field_51456;
	private long field_51457 = Long.MAX_VALUE;

	public OggAudioStream(InputStream inputStream) throws IOException {
		this.inputStream = inputStream;
		Comment comment = new Comment();
		Page page = this.readPage();
		if (page == null) {
			throw new IOException("Invalid Ogg file - can't find first page");
		} else {
			Packet packet = this.readIdentificationPacket(page);
			if (isError(this.info.synthesis_headerin(comment, packet))) {
				throw new IOException("Invalid Ogg identification packet");
			} else {
				for (int i = 0; i < 2; i++) {
					packet = this.readPacket();
					if (packet == null) {
						throw new IOException("Unexpected end of Ogg stream");
					}

					if (isError(this.info.synthesis_headerin(comment, packet))) {
						throw new IOException("Invalid Ogg header packet " + i);
					}
				}

				this.dspState.synthesis_init(this.info);
				this.block.init(this.dspState);
				this.format = new AudioFormat((float)this.info.rate, 16, this.info.channels, true, false);
			}
		}
	}

	private static boolean isError(int code) {
		return code < 0;
	}

	@Override
	public AudioFormat getFormat() {
		return this.format;
	}

	private boolean read() throws IOException {
		int i = this.syncState.buffer(8192);
		byte[] bs = this.syncState.data;
		int j = this.inputStream.read(bs, i, 8192);
		if (j == -1) {
			return false;
		} else {
			this.syncState.wrote(j);
			return true;
		}
	}

	@Nullable
	private Page readPage() throws IOException {
		while (true) {
			int i = this.syncState.pageout(this.page);
			switch (i) {
				case -1:
					throw new IllegalStateException("Corrupt or missing data in bitstream");
				case 0:
					if (this.read()) {
						break;
					}

					return null;
				case 1:
					if (this.page.eos() != 0) {
						this.field_51457 = this.page.granulepos();
					}

					return this.page;
				default:
					throw new IllegalStateException("Unknown page decode result: " + i);
			}
		}
	}

	private Packet readIdentificationPacket(Page page) throws IOException {
		this.streamState.init(page.serialno());
		if (isError(this.streamState.pagein(page))) {
			throw new IOException("Failed to parse page");
		} else {
			int i = this.streamState.packetout(this.packet);
			if (i != 1) {
				throw new IOException("Failed to read identification packet: " + i);
			} else {
				return this.packet;
			}
		}
	}

	@Nullable
	private Packet readPacket() throws IOException {
		while (true) {
			int i = this.streamState.packetout(this.packet);
			switch (i) {
				case -1:
					throw new IOException("Failed to parse packet");
				case 0:
					Page page = this.readPage();
					if (page == null) {
						return null;
					}

					if (!isError(this.streamState.pagein(page))) {
						break;
					}

					throw new IOException("Failed to parse page");
				case 1:
					return this.packet;
				default:
					throw new IllegalStateException("Unknown packet decode result: " + i);
			}
		}
	}

	private long method_59765(int i) {
		long l = this.field_51456 + (long)i;
		long m;
		if (l > this.field_51457) {
			m = this.field_51457 - this.field_51456;
			this.field_51456 = this.field_51457;
		} else {
			this.field_51456 = l;
			m = (long)i;
		}

		return m;
	}

	@Override
	public boolean read(FloatConsumer consumer) throws IOException {
		float[][][] fs = new float[1][][];
		int[] is = new int[this.info.channels];
		Packet packet = this.readPacket();
		if (packet == null) {
			return false;
		} else if (isError(this.block.synthesis(packet))) {
			throw new IOException("Can't decode audio packet");
		} else {
			this.dspState.synthesis_blockin(this.block);

			int i;
			while ((i = this.dspState.synthesis_pcmout(fs, is)) > 0) {
				float[][] gs = fs[0];
				long l = this.method_59765(i);
				switch (this.info.channels) {
					case 1:
						method_59760(gs[0], is[0], l, consumer);
						break;
					case 2:
						method_59761(gs[0], is[0], gs[1], is[1], l, consumer);
						break;
					default:
						method_59762(gs, this.info.channels, is, l, consumer);
				}

				this.dspState.synthesis_read(i);
			}

			return true;
		}
	}

	private static void method_59762(float[][] fs, int i, int[] is, long l, FloatConsumer floatConsumer) {
		for (int j = 0; (long)j < l; j++) {
			for (int k = 0; k < i; k++) {
				int m = is[k];
				float f = fs[k][m + j];
				floatConsumer.accept(f);
			}
		}
	}

	private static void method_59760(float[] fs, int i, long l, FloatConsumer floatConsumer) {
		for (int j = i; (long)j < (long)i + l; j++) {
			floatConsumer.accept(fs[j]);
		}
	}

	private static void method_59761(float[] fs, int i, float[] gs, int j, long l, FloatConsumer floatConsumer) {
		for (int k = 0; (long)k < l; k++) {
			floatConsumer.accept(fs[i + k]);
			floatConsumer.accept(gs[j + k]);
		}
	}

	public void close() throws IOException {
		this.inputStream.close();
	}
}
