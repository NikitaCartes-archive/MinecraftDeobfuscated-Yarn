package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;

@Environment(EnvType.CLIENT)
public class ChannelList implements FloatConsumer {
	private final List<ByteBuffer> buffers = Lists.<ByteBuffer>newArrayList();
	private final int size;
	private int currentBufferSize;
	private ByteBuffer buffer;

	public ChannelList(int size) {
		this.size = size + 1 & -2;
		this.buffer = BufferUtils.createByteBuffer(size);
	}

	@Override
	public void accept(float value) {
		if (this.buffer.remaining() == 0) {
			this.buffer.flip();
			this.buffers.add(this.buffer);
			this.buffer = BufferUtils.createByteBuffer(this.size);
		}

		int i = MathHelper.clamp((int)(value * 32767.5F - 0.5F), -32768, 32767);
		this.buffer.putShort((short)i);
		this.currentBufferSize += 2;
	}

	public ByteBuffer getBuffer() {
		this.buffer.flip();
		if (this.buffers.isEmpty()) {
			return this.buffer;
		} else {
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(this.currentBufferSize);
			this.buffers.forEach(byteBuffer::put);
			byteBuffer.put(this.buffer);
			byteBuffer.flip();
			return byteBuffer;
		}
	}

	public int getCurrentBufferSize() {
		return this.currentBufferSize;
	}
}
