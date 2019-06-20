package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2540 extends ByteBuf {
	private final ByteBuf field_11695;

	public class_2540(ByteBuf byteBuf) {
		this.field_11695 = byteBuf;
	}

	public static int method_10815(int i) {
		for (int j = 1; j < 5; j++) {
			if ((i & -1 << j * 7) == 0) {
				return j;
			}
		}

		return 5;
	}

	public class_2540 method_10813(byte[] bs) {
		this.method_10804(bs.length);
		this.writeBytes(bs);
		return this;
	}

	public byte[] method_10795() {
		return this.method_10803(this.readableBytes());
	}

	public byte[] method_10803(int i) {
		int j = this.method_10816();
		if (j > i) {
			throw new DecoderException("ByteArray with size " + j + " is bigger than allowed " + i);
		} else {
			byte[] bs = new byte[j];
			this.readBytes(bs);
			return bs;
		}
	}

	public class_2540 method_10806(int[] is) {
		this.method_10804(is.length);

		for (int i : is) {
			this.method_10804(i);
		}

		return this;
	}

	public int[] method_10787() {
		return this.method_10799(this.readableBytes());
	}

	public int[] method_10799(int i) {
		int j = this.method_10816();
		if (j > i) {
			throw new DecoderException("VarIntArray with size " + j + " is bigger than allowed " + i);
		} else {
			int[] is = new int[j];

			for (int k = 0; k < is.length; k++) {
				is[k] = this.method_10816();
			}

			return is;
		}
	}

	public class_2540 method_10789(long[] ls) {
		this.method_10804(ls.length);

		for (long l : ls) {
			this.writeLong(l);
		}

		return this;
	}

	@Environment(EnvType.CLIENT)
	public long[] method_10801(@Nullable long[] ls) {
		return this.method_10809(ls, this.readableBytes() / 8);
	}

	@Environment(EnvType.CLIENT)
	public long[] method_10809(@Nullable long[] ls, int i) {
		int j = this.method_10816();
		if (ls == null || ls.length != j) {
			if (j > i) {
				throw new DecoderException("LongArray with size " + j + " is bigger than allowed " + i);
			}

			ls = new long[j];
		}

		for (int k = 0; k < ls.length; k++) {
			ls[k] = this.readLong();
		}

		return ls;
	}

	public class_2338 method_10811() {
		return class_2338.method_10092(this.readLong());
	}

	public class_2540 method_10807(class_2338 arg) {
		this.writeLong(arg.method_10063());
		return this;
	}

	@Environment(EnvType.CLIENT)
	public class_4076 method_19456() {
		return class_4076.method_18677(this.readLong());
	}

	public class_2561 method_10808() {
		return class_2561.class_2562.method_10877(this.method_10800(262144));
	}

	public class_2540 method_10805(class_2561 arg) {
		return this.method_10788(class_2561.class_2562.method_10867(arg), 262144);
	}

	public <T extends Enum<T>> T method_10818(Class<T> class_) {
		return (T)class_.getEnumConstants()[this.method_10816()];
	}

	public class_2540 method_10817(Enum<?> enum_) {
		return this.method_10804(enum_.ordinal());
	}

	public int method_10816() {
		int i = 0;
		int j = 0;

		byte b;
		do {
			b = this.readByte();
			i |= (b & 127) << j++ * 7;
			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((b & 128) == 128);

		return i;
	}

	public long method_10792() {
		long l = 0L;
		int i = 0;

		byte b;
		do {
			b = this.readByte();
			l |= (long)(b & 127) << i++ * 7;
			if (i > 10) {
				throw new RuntimeException("VarLong too big");
			}
		} while ((b & 128) == 128);

		return l;
	}

	public class_2540 method_10797(UUID uUID) {
		this.writeLong(uUID.getMostSignificantBits());
		this.writeLong(uUID.getLeastSignificantBits());
		return this;
	}

	public UUID method_10790() {
		return new UUID(this.readLong(), this.readLong());
	}

	public class_2540 method_10804(int i) {
		while ((i & -128) != 0) {
			this.writeByte(i & 127 | 128);
			i >>>= 7;
		}

		this.writeByte(i);
		return this;
	}

	public class_2540 method_10791(long l) {
		while ((l & -128L) != 0L) {
			this.writeByte((int)(l & 127L) | 128);
			l >>>= 7;
		}

		this.writeByte((int)l);
		return this;
	}

	public class_2540 method_10794(@Nullable class_2487 arg) {
		if (arg == null) {
			this.writeByte(0);
		} else {
			try {
				class_2507.method_10628(arg, new ByteBufOutputStream(this));
			} catch (IOException var3) {
				throw new EncoderException(var3);
			}
		}

		return this;
	}

	@Nullable
	public class_2487 method_10798() {
		int i = this.readerIndex();
		byte b = this.readByte();
		if (b == 0) {
			return null;
		} else {
			this.readerIndex(i);

			try {
				return class_2507.method_10625(new ByteBufInputStream(this), new class_2505(2097152L));
			} catch (IOException var4) {
				throw new EncoderException(var4);
			}
		}
	}

	public class_2540 method_10793(class_1799 arg) {
		if (arg.method_7960()) {
			this.writeBoolean(false);
		} else {
			this.writeBoolean(true);
			class_1792 lv = arg.method_7909();
			this.method_10804(class_1792.method_7880(lv));
			this.writeByte(arg.method_7947());
			class_2487 lv2 = null;
			if (lv.method_7846() || lv.method_7887()) {
				lv2 = arg.method_7969();
			}

			this.method_10794(lv2);
		}

		return this;
	}

	public class_1799 method_10819() {
		if (!this.readBoolean()) {
			return class_1799.field_8037;
		} else {
			int i = this.method_10816();
			int j = this.readByte();
			class_1799 lv = new class_1799(class_1792.method_7875(i), j);
			lv.method_7980(this.method_10798());
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public String method_19772() {
		return this.method_10800(32767);
	}

	public String method_10800(int i) {
		int j = this.method_10816();
		if (j > i * 4) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
		} else if (j < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			String string = this.toString(this.readerIndex(), j, StandardCharsets.UTF_8);
			this.readerIndex(this.readerIndex() + j);
			if (string.length() > i) {
				throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
			} else {
				return string;
			}
		}
	}

	public class_2540 method_10814(String string) {
		return this.method_10788(string, 32767);
	}

	public class_2540 method_10788(String string, int i) {
		byte[] bs = string.getBytes(StandardCharsets.UTF_8);
		if (bs.length > i) {
			throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + i + ")");
		} else {
			this.method_10804(bs.length);
			this.writeBytes(bs);
			return this;
		}
	}

	public class_2960 method_10810() {
		return new class_2960(this.method_10800(32767));
	}

	public class_2540 method_10812(class_2960 arg) {
		this.method_10814(arg.toString());
		return this;
	}

	public Date method_10802() {
		return new Date(this.readLong());
	}

	public class_2540 method_10796(Date date) {
		this.writeLong(date.getTime());
		return this;
	}

	public class_3965 method_17814() {
		class_2338 lv = this.method_10811();
		class_2350 lv2 = this.method_10818(class_2350.class);
		float f = this.readFloat();
		float g = this.readFloat();
		float h = this.readFloat();
		boolean bl = this.readBoolean();
		return new class_3965(
			new class_243((double)((float)lv.method_10263() + f), (double)((float)lv.method_10264() + g), (double)((float)lv.method_10260() + h)), lv2, lv, bl
		);
	}

	public void method_17813(class_3965 arg) {
		class_2338 lv = arg.method_17777();
		this.method_10807(lv);
		this.method_10817(arg.method_17780());
		class_243 lv2 = arg.method_17784();
		this.writeFloat((float)(lv2.field_1352 - (double)lv.method_10263()));
		this.writeFloat((float)(lv2.field_1351 - (double)lv.method_10264()));
		this.writeFloat((float)(lv2.field_1350 - (double)lv.method_10260()));
		this.writeBoolean(arg.method_17781());
	}

	@Override
	public int capacity() {
		return this.field_11695.capacity();
	}

	@Override
	public ByteBuf capacity(int i) {
		return this.field_11695.capacity(i);
	}

	@Override
	public int maxCapacity() {
		return this.field_11695.maxCapacity();
	}

	@Override
	public ByteBufAllocator alloc() {
		return this.field_11695.alloc();
	}

	@Override
	public ByteOrder order() {
		return this.field_11695.order();
	}

	@Override
	public ByteBuf order(ByteOrder byteOrder) {
		return this.field_11695.order(byteOrder);
	}

	@Override
	public ByteBuf unwrap() {
		return this.field_11695.unwrap();
	}

	@Override
	public boolean isDirect() {
		return this.field_11695.isDirect();
	}

	@Override
	public boolean isReadOnly() {
		return this.field_11695.isReadOnly();
	}

	@Override
	public ByteBuf asReadOnly() {
		return this.field_11695.asReadOnly();
	}

	@Override
	public int readerIndex() {
		return this.field_11695.readerIndex();
	}

	@Override
	public ByteBuf readerIndex(int i) {
		return this.field_11695.readerIndex(i);
	}

	@Override
	public int writerIndex() {
		return this.field_11695.writerIndex();
	}

	@Override
	public ByteBuf writerIndex(int i) {
		return this.field_11695.writerIndex(i);
	}

	@Override
	public ByteBuf setIndex(int i, int j) {
		return this.field_11695.setIndex(i, j);
	}

	@Override
	public int readableBytes() {
		return this.field_11695.readableBytes();
	}

	@Override
	public int writableBytes() {
		return this.field_11695.writableBytes();
	}

	@Override
	public int maxWritableBytes() {
		return this.field_11695.maxWritableBytes();
	}

	@Override
	public boolean isReadable() {
		return this.field_11695.isReadable();
	}

	@Override
	public boolean isReadable(int i) {
		return this.field_11695.isReadable(i);
	}

	@Override
	public boolean isWritable() {
		return this.field_11695.isWritable();
	}

	@Override
	public boolean isWritable(int i) {
		return this.field_11695.isWritable(i);
	}

	@Override
	public ByteBuf clear() {
		return this.field_11695.clear();
	}

	@Override
	public ByteBuf markReaderIndex() {
		return this.field_11695.markReaderIndex();
	}

	@Override
	public ByteBuf resetReaderIndex() {
		return this.field_11695.resetReaderIndex();
	}

	@Override
	public ByteBuf markWriterIndex() {
		return this.field_11695.markWriterIndex();
	}

	@Override
	public ByteBuf resetWriterIndex() {
		return this.field_11695.resetWriterIndex();
	}

	@Override
	public ByteBuf discardReadBytes() {
		return this.field_11695.discardReadBytes();
	}

	@Override
	public ByteBuf discardSomeReadBytes() {
		return this.field_11695.discardSomeReadBytes();
	}

	@Override
	public ByteBuf ensureWritable(int i) {
		return this.field_11695.ensureWritable(i);
	}

	@Override
	public int ensureWritable(int i, boolean bl) {
		return this.field_11695.ensureWritable(i, bl);
	}

	@Override
	public boolean getBoolean(int i) {
		return this.field_11695.getBoolean(i);
	}

	@Override
	public byte getByte(int i) {
		return this.field_11695.getByte(i);
	}

	@Override
	public short getUnsignedByte(int i) {
		return this.field_11695.getUnsignedByte(i);
	}

	@Override
	public short getShort(int i) {
		return this.field_11695.getShort(i);
	}

	@Override
	public short getShortLE(int i) {
		return this.field_11695.getShortLE(i);
	}

	@Override
	public int getUnsignedShort(int i) {
		return this.field_11695.getUnsignedShort(i);
	}

	@Override
	public int getUnsignedShortLE(int i) {
		return this.field_11695.getUnsignedShortLE(i);
	}

	@Override
	public int getMedium(int i) {
		return this.field_11695.getMedium(i);
	}

	@Override
	public int getMediumLE(int i) {
		return this.field_11695.getMediumLE(i);
	}

	@Override
	public int getUnsignedMedium(int i) {
		return this.field_11695.getUnsignedMedium(i);
	}

	@Override
	public int getUnsignedMediumLE(int i) {
		return this.field_11695.getUnsignedMediumLE(i);
	}

	@Override
	public int getInt(int i) {
		return this.field_11695.getInt(i);
	}

	@Override
	public int getIntLE(int i) {
		return this.field_11695.getIntLE(i);
	}

	@Override
	public long getUnsignedInt(int i) {
		return this.field_11695.getUnsignedInt(i);
	}

	@Override
	public long getUnsignedIntLE(int i) {
		return this.field_11695.getUnsignedIntLE(i);
	}

	@Override
	public long getLong(int i) {
		return this.field_11695.getLong(i);
	}

	@Override
	public long getLongLE(int i) {
		return this.field_11695.getLongLE(i);
	}

	@Override
	public char getChar(int i) {
		return this.field_11695.getChar(i);
	}

	@Override
	public float getFloat(int i) {
		return this.field_11695.getFloat(i);
	}

	@Override
	public double getDouble(int i) {
		return this.field_11695.getDouble(i);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf byteBuf) {
		return this.field_11695.getBytes(i, byteBuf);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
		return this.field_11695.getBytes(i, byteBuf, j);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
		return this.field_11695.getBytes(i, byteBuf, j, k);
	}

	@Override
	public ByteBuf getBytes(int i, byte[] bs) {
		return this.field_11695.getBytes(i, bs);
	}

	@Override
	public ByteBuf getBytes(int i, byte[] bs, int j, int k) {
		return this.field_11695.getBytes(i, bs, j, k);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
		return this.field_11695.getBytes(i, byteBuffer);
	}

	@Override
	public ByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
		return this.field_11695.getBytes(i, outputStream, j);
	}

	@Override
	public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int j) throws IOException {
		return this.field_11695.getBytes(i, gatheringByteChannel, j);
	}

	@Override
	public int getBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
		return this.field_11695.getBytes(i, fileChannel, l, j);
	}

	@Override
	public CharSequence getCharSequence(int i, int j, Charset charset) {
		return this.field_11695.getCharSequence(i, j, charset);
	}

	@Override
	public ByteBuf setBoolean(int i, boolean bl) {
		return this.field_11695.setBoolean(i, bl);
	}

	@Override
	public ByteBuf setByte(int i, int j) {
		return this.field_11695.setByte(i, j);
	}

	@Override
	public ByteBuf setShort(int i, int j) {
		return this.field_11695.setShort(i, j);
	}

	@Override
	public ByteBuf setShortLE(int i, int j) {
		return this.field_11695.setShortLE(i, j);
	}

	@Override
	public ByteBuf setMedium(int i, int j) {
		return this.field_11695.setMedium(i, j);
	}

	@Override
	public ByteBuf setMediumLE(int i, int j) {
		return this.field_11695.setMediumLE(i, j);
	}

	@Override
	public ByteBuf setInt(int i, int j) {
		return this.field_11695.setInt(i, j);
	}

	@Override
	public ByteBuf setIntLE(int i, int j) {
		return this.field_11695.setIntLE(i, j);
	}

	@Override
	public ByteBuf setLong(int i, long l) {
		return this.field_11695.setLong(i, l);
	}

	@Override
	public ByteBuf setLongLE(int i, long l) {
		return this.field_11695.setLongLE(i, l);
	}

	@Override
	public ByteBuf setChar(int i, int j) {
		return this.field_11695.setChar(i, j);
	}

	@Override
	public ByteBuf setFloat(int i, float f) {
		return this.field_11695.setFloat(i, f);
	}

	@Override
	public ByteBuf setDouble(int i, double d) {
		return this.field_11695.setDouble(i, d);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf byteBuf) {
		return this.field_11695.setBytes(i, byteBuf);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
		return this.field_11695.setBytes(i, byteBuf, j);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
		return this.field_11695.setBytes(i, byteBuf, j, k);
	}

	@Override
	public ByteBuf setBytes(int i, byte[] bs) {
		return this.field_11695.setBytes(i, bs);
	}

	@Override
	public ByteBuf setBytes(int i, byte[] bs, int j, int k) {
		return this.field_11695.setBytes(i, bs, j, k);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
		return this.field_11695.setBytes(i, byteBuffer);
	}

	@Override
	public int setBytes(int i, InputStream inputStream, int j) throws IOException {
		return this.field_11695.setBytes(i, inputStream, j);
	}

	@Override
	public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int j) throws IOException {
		return this.field_11695.setBytes(i, scatteringByteChannel, j);
	}

	@Override
	public int setBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
		return this.field_11695.setBytes(i, fileChannel, l, j);
	}

	@Override
	public ByteBuf setZero(int i, int j) {
		return this.field_11695.setZero(i, j);
	}

	@Override
	public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
		return this.field_11695.setCharSequence(i, charSequence, charset);
	}

	@Override
	public boolean readBoolean() {
		return this.field_11695.readBoolean();
	}

	@Override
	public byte readByte() {
		return this.field_11695.readByte();
	}

	@Override
	public short readUnsignedByte() {
		return this.field_11695.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return this.field_11695.readShort();
	}

	@Override
	public short readShortLE() {
		return this.field_11695.readShortLE();
	}

	@Override
	public int readUnsignedShort() {
		return this.field_11695.readUnsignedShort();
	}

	@Override
	public int readUnsignedShortLE() {
		return this.field_11695.readUnsignedShortLE();
	}

	@Override
	public int readMedium() {
		return this.field_11695.readMedium();
	}

	@Override
	public int readMediumLE() {
		return this.field_11695.readMediumLE();
	}

	@Override
	public int readUnsignedMedium() {
		return this.field_11695.readUnsignedMedium();
	}

	@Override
	public int readUnsignedMediumLE() {
		return this.field_11695.readUnsignedMediumLE();
	}

	@Override
	public int readInt() {
		return this.field_11695.readInt();
	}

	@Override
	public int readIntLE() {
		return this.field_11695.readIntLE();
	}

	@Override
	public long readUnsignedInt() {
		return this.field_11695.readUnsignedInt();
	}

	@Override
	public long readUnsignedIntLE() {
		return this.field_11695.readUnsignedIntLE();
	}

	@Override
	public long readLong() {
		return this.field_11695.readLong();
	}

	@Override
	public long readLongLE() {
		return this.field_11695.readLongLE();
	}

	@Override
	public char readChar() {
		return this.field_11695.readChar();
	}

	@Override
	public float readFloat() {
		return this.field_11695.readFloat();
	}

	@Override
	public double readDouble() {
		return this.field_11695.readDouble();
	}

	@Override
	public ByteBuf readBytes(int i) {
		return this.field_11695.readBytes(i);
	}

	@Override
	public ByteBuf readSlice(int i) {
		return this.field_11695.readSlice(i);
	}

	@Override
	public ByteBuf readRetainedSlice(int i) {
		return this.field_11695.readRetainedSlice(i);
	}

	@Override
	public ByteBuf readBytes(ByteBuf byteBuf) {
		return this.field_11695.readBytes(byteBuf);
	}

	@Override
	public ByteBuf readBytes(ByteBuf byteBuf, int i) {
		return this.field_11695.readBytes(byteBuf, i);
	}

	@Override
	public ByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
		return this.field_11695.readBytes(byteBuf, i, j);
	}

	@Override
	public ByteBuf readBytes(byte[] bs) {
		return this.field_11695.readBytes(bs);
	}

	@Override
	public ByteBuf readBytes(byte[] bs, int i, int j) {
		return this.field_11695.readBytes(bs, i, j);
	}

	@Override
	public ByteBuf readBytes(ByteBuffer byteBuffer) {
		return this.field_11695.readBytes(byteBuffer);
	}

	@Override
	public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
		return this.field_11695.readBytes(outputStream, i);
	}

	@Override
	public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
		return this.field_11695.readBytes(gatheringByteChannel, i);
	}

	@Override
	public CharSequence readCharSequence(int i, Charset charset) {
		return this.field_11695.readCharSequence(i, charset);
	}

	@Override
	public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
		return this.field_11695.readBytes(fileChannel, l, i);
	}

	@Override
	public ByteBuf skipBytes(int i) {
		return this.field_11695.skipBytes(i);
	}

	@Override
	public ByteBuf writeBoolean(boolean bl) {
		return this.field_11695.writeBoolean(bl);
	}

	@Override
	public ByteBuf writeByte(int i) {
		return this.field_11695.writeByte(i);
	}

	@Override
	public ByteBuf writeShort(int i) {
		return this.field_11695.writeShort(i);
	}

	@Override
	public ByteBuf writeShortLE(int i) {
		return this.field_11695.writeShortLE(i);
	}

	@Override
	public ByteBuf writeMedium(int i) {
		return this.field_11695.writeMedium(i);
	}

	@Override
	public ByteBuf writeMediumLE(int i) {
		return this.field_11695.writeMediumLE(i);
	}

	@Override
	public ByteBuf writeInt(int i) {
		return this.field_11695.writeInt(i);
	}

	@Override
	public ByteBuf writeIntLE(int i) {
		return this.field_11695.writeIntLE(i);
	}

	@Override
	public ByteBuf writeLong(long l) {
		return this.field_11695.writeLong(l);
	}

	@Override
	public ByteBuf writeLongLE(long l) {
		return this.field_11695.writeLongLE(l);
	}

	@Override
	public ByteBuf writeChar(int i) {
		return this.field_11695.writeChar(i);
	}

	@Override
	public ByteBuf writeFloat(float f) {
		return this.field_11695.writeFloat(f);
	}

	@Override
	public ByteBuf writeDouble(double d) {
		return this.field_11695.writeDouble(d);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf byteBuf) {
		return this.field_11695.writeBytes(byteBuf);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
		return this.field_11695.writeBytes(byteBuf, i);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
		return this.field_11695.writeBytes(byteBuf, i, j);
	}

	@Override
	public ByteBuf writeBytes(byte[] bs) {
		return this.field_11695.writeBytes(bs);
	}

	@Override
	public ByteBuf writeBytes(byte[] bs, int i, int j) {
		return this.field_11695.writeBytes(bs, i, j);
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer byteBuffer) {
		return this.field_11695.writeBytes(byteBuffer);
	}

	@Override
	public int writeBytes(InputStream inputStream, int i) throws IOException {
		return this.field_11695.writeBytes(inputStream, i);
	}

	@Override
	public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
		return this.field_11695.writeBytes(scatteringByteChannel, i);
	}

	@Override
	public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
		return this.field_11695.writeBytes(fileChannel, l, i);
	}

	@Override
	public ByteBuf writeZero(int i) {
		return this.field_11695.writeZero(i);
	}

	@Override
	public int writeCharSequence(CharSequence charSequence, Charset charset) {
		return this.field_11695.writeCharSequence(charSequence, charset);
	}

	@Override
	public int indexOf(int i, int j, byte b) {
		return this.field_11695.indexOf(i, j, b);
	}

	@Override
	public int bytesBefore(byte b) {
		return this.field_11695.bytesBefore(b);
	}

	@Override
	public int bytesBefore(int i, byte b) {
		return this.field_11695.bytesBefore(i, b);
	}

	@Override
	public int bytesBefore(int i, int j, byte b) {
		return this.field_11695.bytesBefore(i, j, b);
	}

	@Override
	public int forEachByte(ByteProcessor byteProcessor) {
		return this.field_11695.forEachByte(byteProcessor);
	}

	@Override
	public int forEachByte(int i, int j, ByteProcessor byteProcessor) {
		return this.field_11695.forEachByte(i, j, byteProcessor);
	}

	@Override
	public int forEachByteDesc(ByteProcessor byteProcessor) {
		return this.field_11695.forEachByteDesc(byteProcessor);
	}

	@Override
	public int forEachByteDesc(int i, int j, ByteProcessor byteProcessor) {
		return this.field_11695.forEachByteDesc(i, j, byteProcessor);
	}

	@Override
	public ByteBuf copy() {
		return this.field_11695.copy();
	}

	@Override
	public ByteBuf copy(int i, int j) {
		return this.field_11695.copy(i, j);
	}

	@Override
	public ByteBuf slice() {
		return this.field_11695.slice();
	}

	@Override
	public ByteBuf retainedSlice() {
		return this.field_11695.retainedSlice();
	}

	@Override
	public ByteBuf slice(int i, int j) {
		return this.field_11695.slice(i, j);
	}

	@Override
	public ByteBuf retainedSlice(int i, int j) {
		return this.field_11695.retainedSlice(i, j);
	}

	@Override
	public ByteBuf duplicate() {
		return this.field_11695.duplicate();
	}

	@Override
	public ByteBuf retainedDuplicate() {
		return this.field_11695.retainedDuplicate();
	}

	@Override
	public int nioBufferCount() {
		return this.field_11695.nioBufferCount();
	}

	@Override
	public ByteBuffer nioBuffer() {
		return this.field_11695.nioBuffer();
	}

	@Override
	public ByteBuffer nioBuffer(int i, int j) {
		return this.field_11695.nioBuffer(i, j);
	}

	@Override
	public ByteBuffer internalNioBuffer(int i, int j) {
		return this.field_11695.internalNioBuffer(i, j);
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return this.field_11695.nioBuffers();
	}

	@Override
	public ByteBuffer[] nioBuffers(int i, int j) {
		return this.field_11695.nioBuffers(i, j);
	}

	@Override
	public boolean hasArray() {
		return this.field_11695.hasArray();
	}

	@Override
	public byte[] array() {
		return this.field_11695.array();
	}

	@Override
	public int arrayOffset() {
		return this.field_11695.arrayOffset();
	}

	@Override
	public boolean hasMemoryAddress() {
		return this.field_11695.hasMemoryAddress();
	}

	@Override
	public long memoryAddress() {
		return this.field_11695.memoryAddress();
	}

	@Override
	public String toString(Charset charset) {
		return this.field_11695.toString(charset);
	}

	@Override
	public String toString(int i, int j, Charset charset) {
		return this.field_11695.toString(i, j, charset);
	}

	@Override
	public int hashCode() {
		return this.field_11695.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this.field_11695.equals(object);
	}

	@Override
	public int compareTo(ByteBuf byteBuf) {
		return this.field_11695.compareTo(byteBuf);
	}

	@Override
	public String toString() {
		return this.field_11695.toString();
	}

	@Override
	public ByteBuf retain(int i) {
		return this.field_11695.retain(i);
	}

	@Override
	public ByteBuf retain() {
		return this.field_11695.retain();
	}

	@Override
	public ByteBuf touch() {
		return this.field_11695.touch();
	}

	@Override
	public ByteBuf touch(Object object) {
		return this.field_11695.touch(object);
	}

	@Override
	public int refCnt() {
		return this.field_11695.refCnt();
	}

	@Override
	public boolean release() {
		return this.field_11695.release();
	}

	@Override
	public boolean release(int i) {
		return this.field_11695.release(i);
	}
}
