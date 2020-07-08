package net.minecraft.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
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
import net.minecraft.datafixer.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PacketByteBuf extends ByteBuf {
	private final ByteBuf parent;

	public PacketByteBuf(ByteBuf byteBuf) {
		this.parent = byteBuf;
	}

	public static int getVarIntSizeBytes(int i) {
		for (int j = 1; j < 5; j++) {
			if ((i & -1 << j * 7) == 0) {
				return j;
			}
		}

		return 5;
	}

	public <T> T decode(Codec<T> codec) throws IOException {
		CompoundTag compoundTag = this.method_30617();
		DataResult<T> dataResult = codec.parse(NbtOps.INSTANCE, compoundTag);
		if (dataResult.error().isPresent()) {
			throw new IOException("Failed to decode: " + ((PartialResult)dataResult.error().get()).message() + " " + compoundTag);
		} else {
			return (T)dataResult.result().get();
		}
	}

	public <T> void encode(Codec<T> codec, T object) throws IOException {
		DataResult<Tag> dataResult = codec.encodeStart(NbtOps.INSTANCE, object);
		if (dataResult.error().isPresent()) {
			throw new IOException("Failed to encode: " + ((PartialResult)dataResult.error().get()).message() + " " + object);
		} else {
			this.writeCompoundTag((CompoundTag)dataResult.result().get());
		}
	}

	public PacketByteBuf writeByteArray(byte[] bs) {
		this.writeVarInt(bs.length);
		this.writeBytes(bs);
		return this;
	}

	public byte[] readByteArray() {
		return this.readByteArray(this.readableBytes());
	}

	public byte[] readByteArray(int i) {
		int j = this.readVarInt();
		if (j > i) {
			throw new DecoderException("ByteArray with size " + j + " is bigger than allowed " + i);
		} else {
			byte[] bs = new byte[j];
			this.readBytes(bs);
			return bs;
		}
	}

	public PacketByteBuf writeIntArray(int[] is) {
		this.writeVarInt(is.length);

		for (int i : is) {
			this.writeVarInt(i);
		}

		return this;
	}

	public int[] readIntArray() {
		return this.readIntArray(this.readableBytes());
	}

	public int[] readIntArray(int i) {
		int j = this.readVarInt();
		if (j > i) {
			throw new DecoderException("VarIntArray with size " + j + " is bigger than allowed " + i);
		} else {
			int[] is = new int[j];

			for (int k = 0; k < is.length; k++) {
				is[k] = this.readVarInt();
			}

			return is;
		}
	}

	public PacketByteBuf writeLongArray(long[] ls) {
		this.writeVarInt(ls.length);

		for (long l : ls) {
			this.writeLong(l);
		}

		return this;
	}

	@Environment(EnvType.CLIENT)
	public long[] readLongArray(@Nullable long[] ls) {
		return this.readLongArray(ls, this.readableBytes() / 8);
	}

	@Environment(EnvType.CLIENT)
	public long[] readLongArray(@Nullable long[] toArray, int i) {
		int j = this.readVarInt();
		if (toArray == null || toArray.length != j) {
			if (j > i) {
				throw new DecoderException("LongArray with size " + j + " is bigger than allowed " + i);
			}

			toArray = new long[j];
		}

		for (int k = 0; k < toArray.length; k++) {
			toArray[k] = this.readLong();
		}

		return toArray;
	}

	public BlockPos readBlockPos() {
		return BlockPos.fromLong(this.readLong());
	}

	public PacketByteBuf writeBlockPos(BlockPos blockPos) {
		this.writeLong(blockPos.asLong());
		return this;
	}

	@Environment(EnvType.CLIENT)
	public ChunkSectionPos readChunkSectionPos() {
		return ChunkSectionPos.from(this.readLong());
	}

	public Text readText() {
		return Text.Serializer.fromJson(this.readString(262144));
	}

	public PacketByteBuf writeText(Text text) {
		return this.writeString(Text.Serializer.toJson(text), 262144);
	}

	public <T extends Enum<T>> T readEnumConstant(Class<T> class_) {
		return (T)class_.getEnumConstants()[this.readVarInt()];
	}

	public PacketByteBuf writeEnumConstant(Enum<?> enum_) {
		return this.writeVarInt(enum_.ordinal());
	}

	public int readVarInt() {
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

	public long readVarLong() {
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

	public PacketByteBuf writeUuid(UUID uUID) {
		this.writeLong(uUID.getMostSignificantBits());
		this.writeLong(uUID.getLeastSignificantBits());
		return this;
	}

	public UUID readUuid() {
		return new UUID(this.readLong(), this.readLong());
	}

	public PacketByteBuf writeVarInt(int i) {
		while ((i & -128) != 0) {
			this.writeByte(i & 127 | 128);
			i >>>= 7;
		}

		this.writeByte(i);
		return this;
	}

	public PacketByteBuf writeVarLong(long l) {
		while ((l & -128L) != 0L) {
			this.writeByte((int)(l & 127L) | 128);
			l >>>= 7;
		}

		this.writeByte((int)l);
		return this;
	}

	public PacketByteBuf writeCompoundTag(@Nullable CompoundTag compoundTag) {
		if (compoundTag == null) {
			this.writeByte(0);
		} else {
			try {
				NbtIo.write(compoundTag, new ByteBufOutputStream(this));
			} catch (IOException var3) {
				throw new EncoderException(var3);
			}
		}

		return this;
	}

	@Nullable
	public CompoundTag readCompoundTag() {
		return this.method_30616(new PositionTracker(2097152L));
	}

	@Nullable
	public CompoundTag method_30617() {
		return this.method_30616(PositionTracker.DEFAULT);
	}

	@Nullable
	public CompoundTag method_30616(PositionTracker positionTracker) {
		int i = this.readerIndex();
		byte b = this.readByte();
		if (b == 0) {
			return null;
		} else {
			this.readerIndex(i);

			try {
				return NbtIo.read(new ByteBufInputStream(this), positionTracker);
			} catch (IOException var5) {
				throw new EncoderException(var5);
			}
		}
	}

	public PacketByteBuf writeItemStack(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			this.writeBoolean(false);
		} else {
			this.writeBoolean(true);
			Item item = itemStack.getItem();
			this.writeVarInt(Item.getRawId(item));
			this.writeByte(itemStack.getCount());
			CompoundTag compoundTag = null;
			if (item.isDamageable() || item.shouldSyncTagToClient()) {
				compoundTag = itemStack.getTag();
			}

			this.writeCompoundTag(compoundTag);
		}

		return this;
	}

	public ItemStack readItemStack() {
		if (!this.readBoolean()) {
			return ItemStack.EMPTY;
		} else {
			int i = this.readVarInt();
			int j = this.readByte();
			ItemStack itemStack = new ItemStack(Item.byRawId(i), j);
			itemStack.setTag(this.readCompoundTag());
			return itemStack;
		}
	}

	@Environment(EnvType.CLIENT)
	public String readString() {
		return this.readString(32767);
	}

	public String readString(int i) {
		int j = this.readVarInt();
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

	public PacketByteBuf writeString(String string) {
		return this.writeString(string, 32767);
	}

	public PacketByteBuf writeString(String string, int i) {
		byte[] bs = string.getBytes(StandardCharsets.UTF_8);
		if (bs.length > i) {
			throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + i + ")");
		} else {
			this.writeVarInt(bs.length);
			this.writeBytes(bs);
			return this;
		}
	}

	public Identifier readIdentifier() {
		return new Identifier(this.readString(32767));
	}

	public PacketByteBuf writeIdentifier(Identifier identifier) {
		this.writeString(identifier.toString());
		return this;
	}

	public Date readDate() {
		return new Date(this.readLong());
	}

	public PacketByteBuf writeDate(Date date) {
		this.writeLong(date.getTime());
		return this;
	}

	public BlockHitResult readBlockHitResult() {
		BlockPos blockPos = this.readBlockPos();
		Direction direction = this.readEnumConstant(Direction.class);
		float f = this.readFloat();
		float g = this.readFloat();
		float h = this.readFloat();
		boolean bl = this.readBoolean();
		return new BlockHitResult(
			new Vec3d((double)blockPos.getX() + (double)f, (double)blockPos.getY() + (double)g, (double)blockPos.getZ() + (double)h), direction, blockPos, bl
		);
	}

	public void writeBlockHitResult(BlockHitResult blockHitResult) {
		BlockPos blockPos = blockHitResult.getBlockPos();
		this.writeBlockPos(blockPos);
		this.writeEnumConstant(blockHitResult.getSide());
		Vec3d vec3d = blockHitResult.getPos();
		this.writeFloat((float)(vec3d.x - (double)blockPos.getX()));
		this.writeFloat((float)(vec3d.y - (double)blockPos.getY()));
		this.writeFloat((float)(vec3d.z - (double)blockPos.getZ()));
		this.writeBoolean(blockHitResult.isInsideBlock());
	}

	@Override
	public int capacity() {
		return this.parent.capacity();
	}

	@Override
	public ByteBuf capacity(int i) {
		return this.parent.capacity(i);
	}

	@Override
	public int maxCapacity() {
		return this.parent.maxCapacity();
	}

	@Override
	public ByteBufAllocator alloc() {
		return this.parent.alloc();
	}

	@Override
	public ByteOrder order() {
		return this.parent.order();
	}

	@Override
	public ByteBuf order(ByteOrder byteOrder) {
		return this.parent.order(byteOrder);
	}

	@Override
	public ByteBuf unwrap() {
		return this.parent.unwrap();
	}

	@Override
	public boolean isDirect() {
		return this.parent.isDirect();
	}

	@Override
	public boolean isReadOnly() {
		return this.parent.isReadOnly();
	}

	@Override
	public ByteBuf asReadOnly() {
		return this.parent.asReadOnly();
	}

	@Override
	public int readerIndex() {
		return this.parent.readerIndex();
	}

	@Override
	public ByteBuf readerIndex(int i) {
		return this.parent.readerIndex(i);
	}

	@Override
	public int writerIndex() {
		return this.parent.writerIndex();
	}

	@Override
	public ByteBuf writerIndex(int i) {
		return this.parent.writerIndex(i);
	}

	@Override
	public ByteBuf setIndex(int i, int j) {
		return this.parent.setIndex(i, j);
	}

	@Override
	public int readableBytes() {
		return this.parent.readableBytes();
	}

	@Override
	public int writableBytes() {
		return this.parent.writableBytes();
	}

	@Override
	public int maxWritableBytes() {
		return this.parent.maxWritableBytes();
	}

	@Override
	public boolean isReadable() {
		return this.parent.isReadable();
	}

	@Override
	public boolean isReadable(int i) {
		return this.parent.isReadable(i);
	}

	@Override
	public boolean isWritable() {
		return this.parent.isWritable();
	}

	@Override
	public boolean isWritable(int i) {
		return this.parent.isWritable(i);
	}

	@Override
	public ByteBuf clear() {
		return this.parent.clear();
	}

	@Override
	public ByteBuf markReaderIndex() {
		return this.parent.markReaderIndex();
	}

	@Override
	public ByteBuf resetReaderIndex() {
		return this.parent.resetReaderIndex();
	}

	@Override
	public ByteBuf markWriterIndex() {
		return this.parent.markWriterIndex();
	}

	@Override
	public ByteBuf resetWriterIndex() {
		return this.parent.resetWriterIndex();
	}

	@Override
	public ByteBuf discardReadBytes() {
		return this.parent.discardReadBytes();
	}

	@Override
	public ByteBuf discardSomeReadBytes() {
		return this.parent.discardSomeReadBytes();
	}

	@Override
	public ByteBuf ensureWritable(int i) {
		return this.parent.ensureWritable(i);
	}

	@Override
	public int ensureWritable(int i, boolean bl) {
		return this.parent.ensureWritable(i, bl);
	}

	@Override
	public boolean getBoolean(int i) {
		return this.parent.getBoolean(i);
	}

	@Override
	public byte getByte(int i) {
		return this.parent.getByte(i);
	}

	@Override
	public short getUnsignedByte(int i) {
		return this.parent.getUnsignedByte(i);
	}

	@Override
	public short getShort(int i) {
		return this.parent.getShort(i);
	}

	@Override
	public short getShortLE(int i) {
		return this.parent.getShortLE(i);
	}

	@Override
	public int getUnsignedShort(int i) {
		return this.parent.getUnsignedShort(i);
	}

	@Override
	public int getUnsignedShortLE(int i) {
		return this.parent.getUnsignedShortLE(i);
	}

	@Override
	public int getMedium(int i) {
		return this.parent.getMedium(i);
	}

	@Override
	public int getMediumLE(int i) {
		return this.parent.getMediumLE(i);
	}

	@Override
	public int getUnsignedMedium(int i) {
		return this.parent.getUnsignedMedium(i);
	}

	@Override
	public int getUnsignedMediumLE(int i) {
		return this.parent.getUnsignedMediumLE(i);
	}

	@Override
	public int getInt(int i) {
		return this.parent.getInt(i);
	}

	@Override
	public int getIntLE(int i) {
		return this.parent.getIntLE(i);
	}

	@Override
	public long getUnsignedInt(int i) {
		return this.parent.getUnsignedInt(i);
	}

	@Override
	public long getUnsignedIntLE(int i) {
		return this.parent.getUnsignedIntLE(i);
	}

	@Override
	public long getLong(int i) {
		return this.parent.getLong(i);
	}

	@Override
	public long getLongLE(int i) {
		return this.parent.getLongLE(i);
	}

	@Override
	public char getChar(int i) {
		return this.parent.getChar(i);
	}

	@Override
	public float getFloat(int i) {
		return this.parent.getFloat(i);
	}

	@Override
	public double getDouble(int i) {
		return this.parent.getDouble(i);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf byteBuf) {
		return this.parent.getBytes(i, byteBuf);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
		return this.parent.getBytes(i, byteBuf, j);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
		return this.parent.getBytes(i, byteBuf, j, k);
	}

	@Override
	public ByteBuf getBytes(int i, byte[] bs) {
		return this.parent.getBytes(i, bs);
	}

	@Override
	public ByteBuf getBytes(int i, byte[] bs, int j, int k) {
		return this.parent.getBytes(i, bs, j, k);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
		return this.parent.getBytes(i, byteBuffer);
	}

	@Override
	public ByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
		return this.parent.getBytes(i, outputStream, j);
	}

	@Override
	public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int j) throws IOException {
		return this.parent.getBytes(i, gatheringByteChannel, j);
	}

	@Override
	public int getBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
		return this.parent.getBytes(i, fileChannel, l, j);
	}

	@Override
	public CharSequence getCharSequence(int i, int j, Charset charset) {
		return this.parent.getCharSequence(i, j, charset);
	}

	@Override
	public ByteBuf setBoolean(int i, boolean bl) {
		return this.parent.setBoolean(i, bl);
	}

	@Override
	public ByteBuf setByte(int i, int j) {
		return this.parent.setByte(i, j);
	}

	@Override
	public ByteBuf setShort(int i, int j) {
		return this.parent.setShort(i, j);
	}

	@Override
	public ByteBuf setShortLE(int i, int j) {
		return this.parent.setShortLE(i, j);
	}

	@Override
	public ByteBuf setMedium(int i, int j) {
		return this.parent.setMedium(i, j);
	}

	@Override
	public ByteBuf setMediumLE(int i, int j) {
		return this.parent.setMediumLE(i, j);
	}

	@Override
	public ByteBuf setInt(int i, int j) {
		return this.parent.setInt(i, j);
	}

	@Override
	public ByteBuf setIntLE(int i, int j) {
		return this.parent.setIntLE(i, j);
	}

	@Override
	public ByteBuf setLong(int i, long l) {
		return this.parent.setLong(i, l);
	}

	@Override
	public ByteBuf setLongLE(int i, long l) {
		return this.parent.setLongLE(i, l);
	}

	@Override
	public ByteBuf setChar(int i, int j) {
		return this.parent.setChar(i, j);
	}

	@Override
	public ByteBuf setFloat(int i, float f) {
		return this.parent.setFloat(i, f);
	}

	@Override
	public ByteBuf setDouble(int i, double d) {
		return this.parent.setDouble(i, d);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf byteBuf) {
		return this.parent.setBytes(i, byteBuf);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
		return this.parent.setBytes(i, byteBuf, j);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
		return this.parent.setBytes(i, byteBuf, j, k);
	}

	@Override
	public ByteBuf setBytes(int i, byte[] bs) {
		return this.parent.setBytes(i, bs);
	}

	@Override
	public ByteBuf setBytes(int i, byte[] bs, int j, int k) {
		return this.parent.setBytes(i, bs, j, k);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
		return this.parent.setBytes(i, byteBuffer);
	}

	@Override
	public int setBytes(int i, InputStream inputStream, int j) throws IOException {
		return this.parent.setBytes(i, inputStream, j);
	}

	@Override
	public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int j) throws IOException {
		return this.parent.setBytes(i, scatteringByteChannel, j);
	}

	@Override
	public int setBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
		return this.parent.setBytes(i, fileChannel, l, j);
	}

	@Override
	public ByteBuf setZero(int i, int j) {
		return this.parent.setZero(i, j);
	}

	@Override
	public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
		return this.parent.setCharSequence(i, charSequence, charset);
	}

	@Override
	public boolean readBoolean() {
		return this.parent.readBoolean();
	}

	@Override
	public byte readByte() {
		return this.parent.readByte();
	}

	@Override
	public short readUnsignedByte() {
		return this.parent.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return this.parent.readShort();
	}

	@Override
	public short readShortLE() {
		return this.parent.readShortLE();
	}

	@Override
	public int readUnsignedShort() {
		return this.parent.readUnsignedShort();
	}

	@Override
	public int readUnsignedShortLE() {
		return this.parent.readUnsignedShortLE();
	}

	@Override
	public int readMedium() {
		return this.parent.readMedium();
	}

	@Override
	public int readMediumLE() {
		return this.parent.readMediumLE();
	}

	@Override
	public int readUnsignedMedium() {
		return this.parent.readUnsignedMedium();
	}

	@Override
	public int readUnsignedMediumLE() {
		return this.parent.readUnsignedMediumLE();
	}

	@Override
	public int readInt() {
		return this.parent.readInt();
	}

	@Override
	public int readIntLE() {
		return this.parent.readIntLE();
	}

	@Override
	public long readUnsignedInt() {
		return this.parent.readUnsignedInt();
	}

	@Override
	public long readUnsignedIntLE() {
		return this.parent.readUnsignedIntLE();
	}

	@Override
	public long readLong() {
		return this.parent.readLong();
	}

	@Override
	public long readLongLE() {
		return this.parent.readLongLE();
	}

	@Override
	public char readChar() {
		return this.parent.readChar();
	}

	@Override
	public float readFloat() {
		return this.parent.readFloat();
	}

	@Override
	public double readDouble() {
		return this.parent.readDouble();
	}

	@Override
	public ByteBuf readBytes(int i) {
		return this.parent.readBytes(i);
	}

	@Override
	public ByteBuf readSlice(int i) {
		return this.parent.readSlice(i);
	}

	@Override
	public ByteBuf readRetainedSlice(int i) {
		return this.parent.readRetainedSlice(i);
	}

	@Override
	public ByteBuf readBytes(ByteBuf byteBuf) {
		return this.parent.readBytes(byteBuf);
	}

	@Override
	public ByteBuf readBytes(ByteBuf byteBuf, int i) {
		return this.parent.readBytes(byteBuf, i);
	}

	@Override
	public ByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
		return this.parent.readBytes(byteBuf, i, j);
	}

	@Override
	public ByteBuf readBytes(byte[] bs) {
		return this.parent.readBytes(bs);
	}

	@Override
	public ByteBuf readBytes(byte[] bs, int i, int j) {
		return this.parent.readBytes(bs, i, j);
	}

	@Override
	public ByteBuf readBytes(ByteBuffer byteBuffer) {
		return this.parent.readBytes(byteBuffer);
	}

	@Override
	public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
		return this.parent.readBytes(outputStream, i);
	}

	@Override
	public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
		return this.parent.readBytes(gatheringByteChannel, i);
	}

	@Override
	public CharSequence readCharSequence(int i, Charset charset) {
		return this.parent.readCharSequence(i, charset);
	}

	@Override
	public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
		return this.parent.readBytes(fileChannel, l, i);
	}

	@Override
	public ByteBuf skipBytes(int i) {
		return this.parent.skipBytes(i);
	}

	@Override
	public ByteBuf writeBoolean(boolean bl) {
		return this.parent.writeBoolean(bl);
	}

	@Override
	public ByteBuf writeByte(int i) {
		return this.parent.writeByte(i);
	}

	@Override
	public ByteBuf writeShort(int i) {
		return this.parent.writeShort(i);
	}

	@Override
	public ByteBuf writeShortLE(int i) {
		return this.parent.writeShortLE(i);
	}

	@Override
	public ByteBuf writeMedium(int i) {
		return this.parent.writeMedium(i);
	}

	@Override
	public ByteBuf writeMediumLE(int i) {
		return this.parent.writeMediumLE(i);
	}

	@Override
	public ByteBuf writeInt(int i) {
		return this.parent.writeInt(i);
	}

	@Override
	public ByteBuf writeIntLE(int i) {
		return this.parent.writeIntLE(i);
	}

	@Override
	public ByteBuf writeLong(long l) {
		return this.parent.writeLong(l);
	}

	@Override
	public ByteBuf writeLongLE(long l) {
		return this.parent.writeLongLE(l);
	}

	@Override
	public ByteBuf writeChar(int i) {
		return this.parent.writeChar(i);
	}

	@Override
	public ByteBuf writeFloat(float f) {
		return this.parent.writeFloat(f);
	}

	@Override
	public ByteBuf writeDouble(double d) {
		return this.parent.writeDouble(d);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf byteBuf) {
		return this.parent.writeBytes(byteBuf);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
		return this.parent.writeBytes(byteBuf, i);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
		return this.parent.writeBytes(byteBuf, i, j);
	}

	@Override
	public ByteBuf writeBytes(byte[] bs) {
		return this.parent.writeBytes(bs);
	}

	@Override
	public ByteBuf writeBytes(byte[] bs, int i, int j) {
		return this.parent.writeBytes(bs, i, j);
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer byteBuffer) {
		return this.parent.writeBytes(byteBuffer);
	}

	@Override
	public int writeBytes(InputStream inputStream, int i) throws IOException {
		return this.parent.writeBytes(inputStream, i);
	}

	@Override
	public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
		return this.parent.writeBytes(scatteringByteChannel, i);
	}

	@Override
	public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
		return this.parent.writeBytes(fileChannel, l, i);
	}

	@Override
	public ByteBuf writeZero(int i) {
		return this.parent.writeZero(i);
	}

	@Override
	public int writeCharSequence(CharSequence charSequence, Charset charset) {
		return this.parent.writeCharSequence(charSequence, charset);
	}

	@Override
	public int indexOf(int i, int j, byte b) {
		return this.parent.indexOf(i, j, b);
	}

	@Override
	public int bytesBefore(byte b) {
		return this.parent.bytesBefore(b);
	}

	@Override
	public int bytesBefore(int i, byte b) {
		return this.parent.bytesBefore(i, b);
	}

	@Override
	public int bytesBefore(int i, int j, byte b) {
		return this.parent.bytesBefore(i, j, b);
	}

	@Override
	public int forEachByte(ByteProcessor byteProcessor) {
		return this.parent.forEachByte(byteProcessor);
	}

	@Override
	public int forEachByte(int i, int j, ByteProcessor byteProcessor) {
		return this.parent.forEachByte(i, j, byteProcessor);
	}

	@Override
	public int forEachByteDesc(ByteProcessor byteProcessor) {
		return this.parent.forEachByteDesc(byteProcessor);
	}

	@Override
	public int forEachByteDesc(int i, int j, ByteProcessor byteProcessor) {
		return this.parent.forEachByteDesc(i, j, byteProcessor);
	}

	@Override
	public ByteBuf copy() {
		return this.parent.copy();
	}

	@Override
	public ByteBuf copy(int i, int j) {
		return this.parent.copy(i, j);
	}

	@Override
	public ByteBuf slice() {
		return this.parent.slice();
	}

	@Override
	public ByteBuf retainedSlice() {
		return this.parent.retainedSlice();
	}

	@Override
	public ByteBuf slice(int i, int j) {
		return this.parent.slice(i, j);
	}

	@Override
	public ByteBuf retainedSlice(int i, int j) {
		return this.parent.retainedSlice(i, j);
	}

	@Override
	public ByteBuf duplicate() {
		return this.parent.duplicate();
	}

	@Override
	public ByteBuf retainedDuplicate() {
		return this.parent.retainedDuplicate();
	}

	@Override
	public int nioBufferCount() {
		return this.parent.nioBufferCount();
	}

	@Override
	public ByteBuffer nioBuffer() {
		return this.parent.nioBuffer();
	}

	@Override
	public ByteBuffer nioBuffer(int i, int j) {
		return this.parent.nioBuffer(i, j);
	}

	@Override
	public ByteBuffer internalNioBuffer(int i, int j) {
		return this.parent.internalNioBuffer(i, j);
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return this.parent.nioBuffers();
	}

	@Override
	public ByteBuffer[] nioBuffers(int i, int j) {
		return this.parent.nioBuffers(i, j);
	}

	@Override
	public boolean hasArray() {
		return this.parent.hasArray();
	}

	@Override
	public byte[] array() {
		return this.parent.array();
	}

	@Override
	public int arrayOffset() {
		return this.parent.arrayOffset();
	}

	@Override
	public boolean hasMemoryAddress() {
		return this.parent.hasMemoryAddress();
	}

	@Override
	public long memoryAddress() {
		return this.parent.memoryAddress();
	}

	@Override
	public String toString(Charset charset) {
		return this.parent.toString(charset);
	}

	@Override
	public String toString(int i, int j, Charset charset) {
		return this.parent.toString(i, j, charset);
	}

	@Override
	public int hashCode() {
		return this.parent.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this.parent.equals(object);
	}

	@Override
	public int compareTo(ByteBuf byteBuf) {
		return this.parent.compareTo(byteBuf);
	}

	@Override
	public String toString() {
		return this.parent.toString();
	}

	@Override
	public ByteBuf retain(int i) {
		return this.parent.retain(i);
	}

	@Override
	public ByteBuf retain() {
		return this.parent.retain();
	}

	@Override
	public ByteBuf touch() {
		return this.parent.touch();
	}

	@Override
	public ByteBuf touch(Object object) {
		return this.parent.touch(object);
	}

	@Override
	public int refCnt() {
		return this.parent.refCnt();
	}

	@Override
	public boolean release() {
		return this.parent.release();
	}

	@Override
	public boolean release(int i) {
		return this.parent.release(i);
	}
}
