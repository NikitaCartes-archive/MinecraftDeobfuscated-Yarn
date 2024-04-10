package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BufferedAudioStream extends NonRepeatingAudioStream {
	int CHUNK_SIZE = 8192;

	boolean read(FloatConsumer consumer) throws IOException;

	@Override
	default ByteBuffer read(int size) throws IOException {
		ChannelList channelList = new ChannelList(size + 8192);

		while (this.read(channelList) && channelList.getCurrentBufferSize() < size) {
		}

		return channelList.getBuffer();
	}

	@Override
	default ByteBuffer readAll() throws IOException {
		ChannelList channelList = new ChannelList(16384);

		while (this.read(channelList)) {
		}

		return channelList.getBuffer();
	}
}
