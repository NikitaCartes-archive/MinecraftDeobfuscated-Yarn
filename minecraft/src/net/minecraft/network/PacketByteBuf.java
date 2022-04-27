package net.minecraft.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

/**
 * A packet byte buf is a specialized byte buf with utility methods adapted
 * to Minecraft's protocol. It has serialization and deserialization of
 * custom objects.
 * 
 * <div class="fabric"><table border=1>
 * <caption>Custom object handling</caption>
 * <tr>
 *  <th><b>Object Type</b></th> <th><b>read method</b></th> <th><b>write method</b></th>
 * </tr>
 * <tr>
 *  <td>Codec-based</td><td>{@link #decode(Codec)}</td><td>{@link #encode(Codec, Object)}</td>
 * </tr>
 * <tr>
 *  <td>{@link net.minecraft.util.registry.Registry} value</td><td>{@link #readRegistryValue(IndexedIterable)}</td><td>{@link #writeRegistryValue(IndexedIterable, Object)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Collection}</td><td>{@link #readCollection(IntFunction, Function)}</td><td>{@link #writeCollection(Collection, BiConsumer)}</td>
 * </tr>
 * <tr>
 *  <td>{@link IntList}</td><td>{@link #readIntList()}</td><td>{@link #writeIntList(IntList)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Map}</td><td>{@link #readMap(IntFunction, Function, Function)}</td><td>{@link #writeMap(Map, BiConsumer, BiConsumer)}</td>
 * </tr>
 * <tr>
 *  <td>{@code byte[]}</td><td>{@link #readByteArray()}</td><td>{@link #writeByteArray(byte[])}</td>
 * </tr>
 * <tr>
 *  <td>{@code int[]}</td><td>{@link #readIntArray()}</td><td>{@link #writeIntArray(int[])}</td>
 * </tr>
 * <tr>
 *  <td>{@code long[]}</td><td>{@link #readLongArray()}</td><td>{@link #writeLongArray(long[])}</td>
 * </tr>
 * <tr>
 *  <td>{@link BlockPos}</td><td>{@link #readBlockPos()}</td><td>{@link #writeBlockPos(BlockPos)}</td>
 * </tr>
 * <tr>
 *  <td>{@link ChunkPos}</td><td>{@link #readChunkPos()}</td><td>{@link #writeChunkPos(ChunkPos)}</td>
 * </tr>
 * <tr>
 *  <td>{@link ChunkSectionPos}</td><td>{@link #readChunkSectionPos()}</td><td>{@link #writeChunkSectionPos(ChunkSectionPos)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Text}</td><td>{@link #readText()}</td><td>{@link #writeText(Text)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Enum}</td><td>{@link #readEnumConstant(Class)}</td><td>{@link #writeEnumConstant(Enum)}</td>
 * </tr>
 * <tr>
 *  <td>{@index VarInt}</td><td>{@link #readVarInt()}</td><td>{@link #writeVarInt(int)}</td>
 * </tr>
 * <tr>
 *  <td>{@index VarLong}</td><td>{@link #readVarLong()}</td><td>{@link #writeVarLong(long)}</td>
 * </tr>
 * <tr>
 *  <td>{@link UUID}</td><td>{@link #readUuid()}</td><td>{@link #writeUuid(UUID)}</td>
 * </tr>
 * <tr>
 *  <td>{@index GameProfile}</td><td>{@link #readGameProfile()}</td><td>{@link #writeGameProfile(GameProfile)}</td>
 * </tr>
 * <tr>
 *  <td>{@index Property}</td><td>{@link #readProperty()}</td><td>{@link #writeProperty(Property)}</td>
 * </tr>
 * <tr>
 *  <td>{@link NbtCompound}</td><td>{@link #readNbt()}</td><td>{@link #writeNbt(NbtCompound)}</td>
 * </tr>
 * <tr>
 *  <td>{@link ItemStack}</td><td>{@link #readItemStack()}</td><td>{@link #writeItemStack(ItemStack)}</td>
 * </tr>
 * <tr>
 *  <td>{@link String}</td><td>{@link #readString()}</td><td>{@link #writeString(String)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Identifier}</td><td>{@link #readIdentifier()}</td><td>{@link #writeIdentifier(Identifier)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Date}</td><td>{@link #readDate()}</td><td>{@link #writeDate(Date)}</td>
 * </tr>
 * <tr>
 *  <td>{@link BlockHitResult}</td><td>{@link #readBlockHitResult()}</td><td>{@link #writeBlockHitResult(BlockHitResult)}</td>
 * </tr>
 * <tr>
 *  <td>{@link BitSet}</td><td>{@link #readBitSet()}</td><td>{@link #writeBitSet(BitSet)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Optional}</td><td>{@link #readOptional(Function)}</td><td>{@link #writeOptional(Optional, BiConsumer)}</td>
 * </tr>
 * <tr>
 *  <td>{@index Either}</td><td>{@link #readEither(Function, Function)}</td><td>{@link #writeEither(Either, BiConsumer, BiConsumer)}</td>
 * </tr>
 * </table></div>
 * 
 * <p>All {@code read} and {@code write} methods throw {@link
 * IndexOutOfBoundsException} if there is not enough bytes to be read or
 * not enough space to write.
 */
public class PacketByteBuf extends ByteBuf {
	/**
	 * The max number of bytes an encoded var int value may use.
	 * 
	 * <p>Its value is {@value}. A regular int value always use 4 bytes in contrast.
	 * 
	 * @see #getVarIntLength(int)
	 */
	private static final int MAX_VAR_INT_LENGTH = 5;
	/**
	 * The max number of bytes an encoded var long value may use.
	 * 
	 * <p>Its value is {@value}. A regular long value always use 8 bytes in contrast.
	 * 
	 * @see #getVarLongLength(long)
	 */
	private static final int MAX_VAR_LONG_LENGTH = 10;
	/**
	 * The maximum size, in number of bytes, allowed of the NBT compound read by
	 * {@link #readNbt()}.
	 */
	private static final int MAX_READ_NBT_SIZE = 2097152;
	private final ByteBuf parent;
	/**
	 * The default max length of strings {@linkplain #readString() read} or {@linkplain
	 * #writeString(String) written}. This is also the max length of identifiers
	 * {@linkplain #readIdentifier() read} or {@linkplain #writeIdentifier(Identifier)
	 * written} in their string form.
	 */
	public static final short DEFAULT_MAX_STRING_LENGTH = 32767;
	/**
	 * The maximum size, in terms of JSON string length, allowed of the text read by
	 * {@link #readText()} or written by {@link #writeText(Text)}.
	 */
	public static final int MAX_TEXT_LENGTH = 262144;

	/**
	 * Creates a packet byte buf that delegates its operations to the {@code
	 * parent} buf.
	 * 
	 * @param parent the parent, or delegate, buf
	 */
	public PacketByteBuf(ByteBuf parent) {
		this.parent = parent;
	}

	/**
	 * Returns the number of bytes needed to encode {@code value} as a
	 * {@linkplain #writeVarInt(int) var int}. Guaranteed to be between {@code
	 * 1} and {@value #MAX_VAR_INT_LENGTH}.
	 * 
	 * @return the number of bytes a var int {@code value} uses
	 * 
	 * @param value the value to encode
	 */
	public static int getVarIntLength(int value) {
		for (int i = 1; i < MAX_VAR_INT_LENGTH; i++) {
			if ((value & -1 << i * 7) == 0) {
				return i;
			}
		}

		return MAX_VAR_INT_LENGTH;
	}

	/**
	 * Returns the number of bytes needed to encode {@code value} as a
	 * {@linkplain #writeVarLong(int) var long}. Guaranteed to be between {@code
	 * 1} and {@value #MAX_VAR_LONG_LENGTH}.
	 * 
	 * @return the number of bytes a var long {@code value} uses
	 * 
	 * @param value the value to encode
	 */
	public static int getVarLongLength(long value) {
		for (int i = 1; i < MAX_VAR_LONG_LENGTH; i++) {
			if ((value & -1L << i * 7) == 0L) {
				return i;
			}
		}

		return MAX_VAR_LONG_LENGTH;
	}

	/**
	 * Reads an object from this buf as a compound NBT with the given codec.
	 * 
	 * @param <T> the decoded object's type
	 * @return the read object
	 * @throws io.netty.handler.codec.EncoderException if the {@code codec} fails
	 * to decode the compound NBT
	 * @see #encode(Codec, Object)
	 * 
	 * @param codec the codec to decode the object
	 */
	public <T> T decode(Codec<T> codec) {
		NbtCompound nbtCompound = this.readUnlimitedNbt();
		DataResult<T> dataResult = codec.parse(NbtOps.INSTANCE, nbtCompound);
		dataResult.error().ifPresent(partial -> {
			throw new EncoderException("Failed to decode: " + partial.message() + " " + nbtCompound);
		});
		return (T)dataResult.result().get();
	}

	/**
	 * Writes an object to this buf as a compound NBT with the given codec.
	 * 
	 * @param <T> the encoded object's type
	 * @throws io.netty.handler.codec.EncoderException if the {@code codec} fails
	 * to encode the compound NBT
	 * @see #decode(Codec)
	 * 
	 * @param codec the codec to encode the object
	 * @param object the object to write to this buf
	 */
	public <T> void encode(Codec<T> codec, T object) {
		DataResult<NbtElement> dataResult = codec.encodeStart(NbtOps.INSTANCE, object);
		dataResult.error().ifPresent(partial -> {
			throw new EncoderException("Failed to encode: " + partial.message() + " " + object);
		});
		this.writeNbt((NbtCompound)dataResult.result().get());
	}

	/**
	 * Writes a value from a registry (or other {@link IndexedIterable}s). The value
	 * is stored using its raw ID as a {@linkplain #readVarInt() var int}.
	 * 
	 * <p>Callers must ensure that <strong>the registry (or the indexed iterable) is
	 * properly synchronized</strong> between the client and the server.
	 * 
	 * @throws IllegalArgumentException if {@code value} is not in {@code registry}
	 * @see #readRegistryValue(IndexedIterable)
	 * 
	 * @param registry the registry (or an indexed iterable) that contains the value
	 * @param value a value to write, must be in {@code registry}
	 */
	public <T> void writeRegistryValue(IndexedIterable<T> registry, T value) {
		int i = registry.getRawId(value);
		if (i == -1) {
			throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + registry);
		} else {
			this.writeVarInt(i);
		}
	}

	/**
	 * Reads a value from a registry (or other {@link IndexedIterable}s). The value
	 * is stored using its raw ID as a {@linkplain #readVarInt() var int}.
	 * 
	 * <p>Callers must ensure that <strong>the registry (or the indexed iterable) is
	 * properly synchronized</strong> between the client and the server.
	 * 
	 * @return the value, or {@code null} if it is missing from {@code registry}
	 * @see #writeRegistryValue(IndexedIterable, Object)
	 * 
	 * @param registry the registry (or an indexed iterable) that contains the value
	 */
	@Nullable
	public <T> T readRegistryValue(IndexedIterable<T> registry) {
		int i = this.readVarInt();
		return registry.get(i);
	}

	public static <T> IntFunction<T> getMaxValidator(IntFunction<T> applier, int max) {
		return value -> {
			if (value > max) {
				throw new DecoderException("Value " + value + " is larger than limit " + max);
			} else {
				return applier.apply(value);
			}
		};
	}

	/**
	 * Reads a collection from this buf. The collection is stored as a leading
	 * {@linkplain #readVarInt() var int} size followed by the entries
	 * sequentially.
	 * 
	 * @param <T> the collection's entry type
	 * @param <C> the collection's type
	 * @return the read collection
	 * @see #writeCollection(Collection, BiConsumer)
	 * @see #readList(Function)
	 * 
	 * @param collectionFactory a factory that creates a collection with a given size
	 * @param entryParser a parser that parses each entry for the collection given this buf
	 */
	public <T, C extends Collection<T>> C readCollection(IntFunction<C> collectionFactory, Function<PacketByteBuf, T> entryParser) {
		int i = this.readVarInt();
		C collection = (C)collectionFactory.apply(i);

		for (int j = 0; j < i; j++) {
			collection.add(entryParser.apply(this));
		}

		return collection;
	}

	/**
	 * Writes a collection to this buf. The collection is stored as a leading
	 * {@linkplain #readVarInt() var int} size followed by the entries
	 * sequentially.
	 * 
	 * @param <T> the list's entry type
	 * @see #readCollection(IntFunction, Function)
	 * 
	 * @param collection the collection to write
	 * @param entrySerializer a serializer that writes each entry to this buf
	 */
	public <T> void writeCollection(Collection<T> collection, BiConsumer<PacketByteBuf, T> entrySerializer) {
		this.writeVarInt(collection.size());

		for (T object : collection) {
			entrySerializer.accept(this, object);
		}
	}

	/**
	 * Reads a collection from this buf as an array list.
	 * 
	 * @param <T> the list's entry type
	 * @return the read list
	 * @see #readCollection(IntFunction, Function)
	 * 
	 * @param entryParser a parser that parses each entry for the collection given this buf
	 */
	public <T> List<T> readList(Function<PacketByteBuf, T> entryParser) {
		return this.readCollection(Lists::newArrayListWithCapacity, entryParser);
	}

	/**
	 * Reads a list of primitive ints from this buf. The ints are stored as var
	 * ints, with an extra var int in the beginning indicating the size.
	 * 
	 * @apiNote To limit the length of the list or array read, use
	 * {@link #readIntArray(int)}.
	 * 
	 * @implNote A list of ints has the same format as an int array.
	 * 
	 * @return the read list
	 * @see #writeIntList(IntList)
	 * @see #readIntArray()
	 */
	public IntList readIntList() {
		int i = this.readVarInt();
		IntList intList = new IntArrayList();

		for (int j = 0; j < i; j++) {
			intList.add(this.readVarInt());
		}

		return intList;
	}

	/**
	 * Writes a list of primitive ints from this buf. The ints are stored as var
	 * ints, with an extra var int in the beginning indicating the size.
	 * 
	 * @implNote A list of ints has the same format as an int array.
	 * 
	 * @see #readIntList()
	 * @see #writeIntArray(int[])
	 * 
	 * @param list the list to write
	 */
	public void writeIntList(IntList list) {
		this.writeVarInt(list.size());
		list.forEach(this::writeVarInt);
	}

	/**
	 * Reads a map from this buf. The map is stored as a leading
	 * {@linkplain #readVarInt() var int} size followed by each key and value
	 * pair.
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 * @param <M> the map type
	 * @return the read map
	 * @see #writeMap(Map, BiConsumer, BiConsumer)
	 * @see #readMap(Function, Function)
	 * 
	 * @param mapFactory a factory that creates a map with a given size
	 * @param keyParser a parser that parses each key for the map given this buf
	 * @param valueParser a parser that parses each value for the map given this buf
	 */
	public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> mapFactory, Function<PacketByteBuf, K> keyParser, Function<PacketByteBuf, V> valueParser) {
		int i = this.readVarInt();
		M map = (M)mapFactory.apply(i);

		for (int j = 0; j < i; j++) {
			K object = (K)keyParser.apply(this);
			V object2 = (V)valueParser.apply(this);
			map.put(object, object2);
		}

		return map;
	}

	/**
	 * Reads a map from this buf as a hash map.
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return the read map
	 * @see #readMap(IntFunction, Function, Function)
	 * 
	 * @param keyParser a parser that parses each key for the map given this buf
	 * @param valueParser a parser that parses each value for the map given this buf
	 */
	public <K, V> Map<K, V> readMap(Function<PacketByteBuf, K> keyParser, Function<PacketByteBuf, V> valueParser) {
		return this.readMap(Maps::newHashMapWithExpectedSize, keyParser, valueParser);
	}

	/**
	 * Writes a map to this buf. The map is stored as a leading
	 * {@linkplain #readVarInt() var int} size followed by each key and value
	 * pair.
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 * @see #readMap(IntFunction, Function, Function)
	 * 
	 * @param map the map to write
	 * @param keySerializer a serializer that writes each key in the map to this buf
	 * @param valueSerializer a serializer that writes each value in the map to this buf
	 */
	public <K, V> void writeMap(Map<K, V> map, BiConsumer<PacketByteBuf, K> keySerializer, BiConsumer<PacketByteBuf, V> valueSerializer) {
		this.writeVarInt(map.size());
		map.forEach((key, value) -> {
			keySerializer.accept(this, key);
			valueSerializer.accept(this, value);
		});
	}

	/**
	 * Iterates a collection from this buf. The collection is stored as a leading
	 * {@linkplain #readVarInt() var int} {@code size} followed by the entries
	 * sequentially. The {@code consumer} will be called {@code size} times.
	 * 
	 * @see #readCollection(IntFunction, Function)
	 * 
	 * @param consumer the consumer to read entries
	 */
	public void forEachInCollection(Consumer<PacketByteBuf> consumer) {
		int i = this.readVarInt();

		for (int j = 0; j < i; j++) {
			consumer.accept(this);
		}
	}

	/**
	 * Writes an optional value to this buf. An optional value is represented by
	 * a boolean indicating if the value is present, followed by the value only if
	 * the value is present.
	 * 
	 * @see #readOptional(Function)
	 */
	public <T> void writeOptional(Optional<T> value, BiConsumer<PacketByteBuf, T> serializer) {
		if (value.isPresent()) {
			this.writeBoolean(true);
			serializer.accept(this, value.get());
		} else {
			this.writeBoolean(false);
		}
	}

	/**
	 * Reads an optional value from this buf. An optional value is represented by
	 * a boolean indicating if the value is present, followed by the value only if
	 * the value is present.
	 * 
	 * @return the read optional value
	 * @see #writeOptional(Optional, BiConsumer)
	 */
	public <T> Optional<T> readOptional(Function<PacketByteBuf, T> parser) {
		return this.readBoolean() ? Optional.of(parser.apply(this)) : Optional.empty();
	}

	/**
	 * Writes an {@code Either} to this buf. An either is represented by
	 * a boolean indicating if the left side or the right side of the either,
	 * followed by the value.
	 * 
	 * @see #readEither(Function, Function)
	 */
	public <L, R> void writeEither(Either<L, R> either, BiConsumer<PacketByteBuf, L> leftSerializer, BiConsumer<PacketByteBuf, R> rightSerializer) {
		either.ifLeft(object -> {
			this.writeBoolean(true);
			leftSerializer.accept(this, object);
		}).ifRight(object -> {
			this.writeBoolean(false);
			rightSerializer.accept(this, object);
		});
	}

	/**
	 * Reads an {@code Either} from this buf. An either is represented by
	 * a boolean indicating if the left side or the right side of the either,
	 * followed by the value.
	 * 
	 * @return the read either
	 * @see #writeEither(Either, BiConsumer, BiConsumer)
	 */
	public <L, R> Either<L, R> readEither(Function<PacketByteBuf, L> leftParser, Function<PacketByteBuf, R> rightParser) {
		return this.readBoolean() ? Either.left((L)leftParser.apply(this)) : Either.right((R)rightParser.apply(this));
	}

	/**
	 * Reads an array of primitive bytes from this buf. The array first has a
	 * var int indicating its length, followed by the actual bytes. The array
	 * does not have a length limit.
	 * 
	 * @see #readByteArray(int)
	 * @see #writeByteArray(byte[])
	 * @return the read byte array
	 */
	public byte[] readByteArray() {
		return this.readByteArray(this.readableBytes());
	}

	/**
	 * Writes an array of primitive bytes to this buf. The array first has a
	 * var int indicating its length, followed by the actual bytes.
	 * 
	 * @see #readByteArray()
	 * @return this buf, for chaining
	 * 
	 * @param array the array to write
	 */
	public PacketByteBuf writeByteArray(byte[] array) {
		this.writeVarInt(array.length);
		this.writeBytes(array);
		return this;
	}

	/**
	 * Reads an array of primitive bytes from this buf. The array first has a
	 * var int indicating its length, followed by the actual bytes. The array
	 * has a length limit given by {@code maxSize}.
	 * 
	 * @see #readByteArray()
	 * @see #writeByteArray(byte[])
	 * @return the read byte array
	 * @throws io.netty.handler.codec.DecoderException if the read array has a
	 * length over {@code maxSize}
	 * 
	 * @param maxSize the max length of the read array
	 */
	public byte[] readByteArray(int maxSize) {
		int i = this.readVarInt();
		if (i > maxSize) {
			throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + maxSize);
		} else {
			byte[] bs = new byte[i];
			this.readBytes(bs);
			return bs;
		}
	}

	/**
	 * Writes an array of primitive ints to this buf. The array first has a
	 * var int indicating its length, followed by the var int entries.
	 * 
	 * @implNote An int array has the same format as a list of ints.
	 * 
	 * @see #readIntArray(int)
	 * @see #writeIntArray(int[])
	 * @see #writeIntList(IntList)
	 * @return this buf, for chaining
	 * 
	 * @param array the array to write
	 */
	public PacketByteBuf writeIntArray(int[] array) {
		this.writeVarInt(array.length);

		for (int i : array) {
			this.writeVarInt(i);
		}

		return this;
	}

	/**
	 * Reads an array of primitive ints from this buf. The array first has a
	 * var int indicating its length, followed by the var int entries. The array
	 * does not have a length limit.
	 * 
	 * @implNote An int array has the same format as a list of ints.
	 * 
	 * @see #readIntArray(int)
	 * @see #writeIntArray(int[])
	 * @see #readIntList()
	 * @return the read byte array
	 */
	public int[] readIntArray() {
		return this.readIntArray(this.readableBytes());
	}

	/**
	 * Reads an array of primitive ints from this buf. The array first has a
	 * var int indicating its length, followed by the var int entries. The array
	 * has a length limit given by {@code maxSize}.
	 * 
	 * @implNote An int array has the same format as a list of ints.
	 * 
	 * @see #readIntArray()
	 * @see #writeIntArray(int[])
	 * @return the read byte array
	 * @throws io.netty.handler.codec.DecoderException if the read array has a
	 * length over {@code maxSize}
	 * 
	 * @param maxSize the max length of the read array
	 */
	public int[] readIntArray(int maxSize) {
		int i = this.readVarInt();
		if (i > maxSize) {
			throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxSize);
		} else {
			int[] is = new int[i];

			for (int j = 0; j < is.length; j++) {
				is[j] = this.readVarInt();
			}

			return is;
		}
	}

	/**
	 * Writes an array of primitive longs to this buf. The array first has a
	 * var int indicating its length, followed by the regular long (not var
	 * long) values.
	 * 
	 * @see #readLongArray()
	 * @return this buf, for chaining
	 * 
	 * @param array the array to write
	 */
	public PacketByteBuf writeLongArray(long[] array) {
		this.writeVarInt(array.length);

		for (long l : array) {
			this.writeLong(l);
		}

		return this;
	}

	/**
	 * Reads an array of primitive longs from this buf. The array first has a
	 * var int indicating its length, followed by the regular long (not var
	 * long) values. The array does not have a length limit.
	 * 
	 * @see #writeLongArray(long[])
	 * @see #readLongArray(long[])
	 * @see #readLongArray(long[], int)
	 * @return the read long array
	 */
	public long[] readLongArray() {
		return this.readLongArray(null);
	}

	/**
	 * Reads an array of primitive longs from this buf. The array first has a
	 * var int indicating its length, followed by the regular long (not var
	 * long) values. The array does not have a length limit.
	 * 
	 * <p>Only when {@code toArray} is not {@code null} and {@code
	 * toArray.length} equals to the length var int read will the {@code
	 * toArray} be reused and returned; otherwise, a new array
	 * of proper size is created.
	 * 
	 * @see #writeLongArray(long[])
	 * @see #readLongArray()
	 * @see #readLongArray(long[], int)
	 * @return the read long array
	 * 
	 * @param toArray the array to reuse
	 */
	public long[] readLongArray(@Nullable long[] toArray) {
		return this.readLongArray(toArray, this.readableBytes() / 8);
	}

	/**
	 * Reads an array of primitive longs from this buf. The array first has a
	 * var int indicating its length, followed by the regular long (not var
	 * long) values. The array has a length limit of {@code maxSize}.
	 * 
	 * <p>Only when {@code toArray} is not {@code null} and {@code
	 * toArray.length} equals to the length var int read will the {@code
	 * toArray} be reused and returned; otherwise, a new array
	 * of proper size is created.
	 * 
	 * @see #writeLongArray(long[])
	 * @see #readLongArray()
	 * @see #readLongArray(long[])
	 * @return the read long array
	 * @throws io.netty.handler.codec.DecoderException if the read array has a
	 * length over {@code maxSize}
	 * 
	 * @param toArray the array to reuse
	 * @param maxSize the max length of the read array
	 */
	public long[] readLongArray(@Nullable long[] toArray, int maxSize) {
		int i = this.readVarInt();
		if (toArray == null || toArray.length != i) {
			if (i > maxSize) {
				throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + maxSize);
			}

			toArray = new long[i];
		}

		for (int j = 0; j < toArray.length; j++) {
			toArray[j] = this.readLong();
		}

		return toArray;
	}

	/**
	 * Returns an array of bytes of contents in this buf between index {@code 0} and
	 * the {@link #writerIndex()}.
	 */
	@VisibleForTesting
	public byte[] getWrittenBytes() {
		int i = this.writerIndex();
		byte[] bs = new byte[i];
		this.getBytes(0, bs);
		return bs;
	}

	/**
	 * Reads a block position from this buf. A block position is represented by
	 * a regular long.
	 * 
	 * @return the read block pos
	 * @see #writeBlockPos(BlockPos)
	 */
	public BlockPos readBlockPos() {
		return BlockPos.fromLong(this.readLong());
	}

	/**
	 * Writes a block position to this buf. A block position is represented by
	 * a regular long.
	 * 
	 * @return this buf, for chaining
	 * @see #readBlockPos()
	 * 
	 * @param pos the pos to write
	 */
	public PacketByteBuf writeBlockPos(BlockPos pos) {
		this.writeLong(pos.asLong());
		return this;
	}

	/**
	 * Reads a chunk position from this buf. A chunk position is represented by
	 * a regular long.
	 * 
	 * @return the read chunk position
	 * @see #writeChunkPos(ChunkPos)
	 */
	public ChunkPos readChunkPos() {
		return new ChunkPos(this.readLong());
	}

	/**
	 * Writes a chunk position to this buf. A chunk position is represented by
	 * a regular long.
	 * 
	 * @return this buf, for chaining
	 * @see #readChunkPos()
	 * 
	 * @param pos the chunk position to write
	 */
	public PacketByteBuf writeChunkPos(ChunkPos pos) {
		this.writeLong(pos.toLong());
		return this;
	}

	/**
	 * Reads a chunk section position from this buf. A chunk section position is
	 * represented by a regular long.
	 * 
	 * @return the read chunk section pos
	 * @see #writeChunkSectionPos(ChunkSectionPos)
	 */
	public ChunkSectionPos readChunkSectionPos() {
		return ChunkSectionPos.from(this.readLong());
	}

	/**
	 * Reads a chunk section position from this buf. A chunk section position is
	 * represented by a regular long.
	 * 
	 * @return this buf, for chaining
	 * @see #readChunkSectionPos()
	 * 
	 * @param pos the section position to write
	 */
	public PacketByteBuf writeChunkSectionPos(ChunkSectionPos pos) {
		this.writeLong(pos.asLong());
		return this;
	}

	/**
	 * Reads a text from this buf. A text is represented by a JSON string with
	 * max length {@value #MAX_TEXT_LENGTH}.
	 * 
	 * @return the read text
	 * @throws io.netty.handler.codec.DecoderException if the JSON string read
	 * exceeds {@value #MAX_TEXT_LENGTH} in length
	 * @see #writeText(Text)
	 * @see #MAX_TEXT_LENGTH
	 */
	public Text readText() {
		Text text = Text.Serializer.fromJson(this.readString(MAX_TEXT_LENGTH));
		if (text == null) {
			throw new DecoderException("Received unexpected null component");
		} else {
			return text;
		}
	}

	/**
	 * Writes a text to this buf. A text is represented by a JSON string with
	 * max length {@value #MAX_TEXT_LENGTH}.
	 * 
	 * @return this buf, for chaining
	 * @throws io.netty.handler.codec.EncoderException if the JSON string
	 * written exceeds {@value #MAX_TEXT_LENGTH} in length
	 * @see #readText()
	 * @see #MAX_TEXT_LENGTH
	 * 
	 * @param text the text to write
	 */
	public PacketByteBuf writeText(Text text) {
		return this.writeString(Text.Serializer.toJson(text), MAX_TEXT_LENGTH);
	}

	/**
	 * Reads an enum constant from this buf. An enum constant is represented
	 * by a var int indicating its ordinal.
	 * 
	 * @return the read enum constant
	 * @see #writeEnumConstant(Enum)
	 * 
	 * @param enumClass the enum class, for constant lookup
	 */
	public <T extends Enum<T>> T readEnumConstant(Class<T> enumClass) {
		return (T)enumClass.getEnumConstants()[this.readVarInt()];
	}

	/**
	 * Writes an enum constant to this buf. An enum constant is represented
	 * by a var int indicating its ordinal.
	 * 
	 * @return this buf, for chaining
	 * @see #readEnumConstant(Class)
	 * 
	 * @param instance the enum constant to write
	 */
	public PacketByteBuf writeEnumConstant(Enum<?> instance) {
		return this.writeVarInt(instance.ordinal());
	}

	/**
	 * Reads a single var int from this buf.
	 * 
	 * @return the value read
	 * @see #writeVarInt(int)
	 */
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

	/**
	 * Reads a single var long from this buf.
	 * 
	 * @return the value read
	 * @see #writeVarLong(long)
	 */
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

	/**
	 * Writes a UUID (universally unique identifier) to this buf. A UUID is
	 * represented by two regular longs.
	 * 
	 * @return this buf, for chaining
	 * @see #readUuid()
	 * 
	 * @param uuid the UUID to write
	 */
	public PacketByteBuf writeUuid(UUID uuid) {
		this.writeLong(uuid.getMostSignificantBits());
		this.writeLong(uuid.getLeastSignificantBits());
		return this;
	}

	/**
	 * Reads a UUID (universally unique identifier) from this buf. A UUID is
	 * represented by two regular longs.
	 * 
	 * @return the read UUID
	 * @see #writeUuid(UUID)
	 */
	public UUID readUuid() {
		return new UUID(this.readLong(), this.readLong());
	}

	/**
	 * Writes a single var int to this buf.
	 * 
	 * <p>Compared to regular ints, var ints may use less bytes (ranging from 1
	 * to 5, where regular ints use 4) when representing smaller positive
	 * numbers.
	 * 
	 * @return this buf, for chaining
	 * @see #readVarInt()
	 * @see #getVarIntLength(int)
	 * 
	 * @param value the value to write
	 */
	public PacketByteBuf writeVarInt(int value) {
		while ((value & -128) != 0) {
			this.writeByte(value & 127 | 128);
			value >>>= 7;
		}

		this.writeByte(value);
		return this;
	}

	/**
	 * Writes a single var long to this buf.
	 * 
	 * <p>Compared to regular longs, var longs may use less bytes when
	 * representing smaller positive numbers.
	 * 
	 * @return this buf, for chaining
	 * @see #readVarLong()
	 * @see #getVarLongLength(long)
	 * 
	 * @param value the value to write
	 */
	public PacketByteBuf writeVarLong(long value) {
		while ((value & -128L) != 0L) {
			this.writeByte((int)(value & 127L) | 128);
			value >>>= 7;
		}

		this.writeByte((int)value);
		return this;
	}

	/**
	 * Writes an NBT compound to this buf. The binary representation of NBT is
	 * handled by {@link net.minecraft.nbt.NbtIo}. If {@code compound} is {@code
	 * null}, it is treated as an NBT null.
	 * 
	 * @return this buf, for chaining
	 * @throws io.netty.handler.codec.EncoderException if the NBT cannot be
	 * written
	 * @see #readNbt()
	 * @see #readUnlimitedNbt()
	 * @see #readNbt(NbtTagSizeTracker)
	 * 
	 * @param compound the compound to write
	 */
	public PacketByteBuf writeNbt(@Nullable NbtCompound compound) {
		if (compound == null) {
			this.writeByte(0);
		} else {
			try {
				NbtIo.write(compound, new ByteBufOutputStream(this));
			} catch (IOException var3) {
				throw new EncoderException(var3);
			}
		}

		return this;
	}

	/**
	 * Reads an NBT compound from this buf. The binary representation of NBT is
	 * handled by {@link net.minecraft.nbt.NbtIo}. If an NBT null is encountered,
	 * this method returns {@code null}. The compound can have a maximum size of
	 * {@value #MAX_READ_NBT_SIZE} bytes.
	 * 
	 * @return the read compound, may be {@code null}
	 * @throws io.netty.handler.codec.EncoderException if the NBT cannot be read
	 * @throws RuntimeException if the compound exceeds the allowed maximum size
	 * @see #writeNbt(NbtCompound)
	 * @see #readUnlimitedNbt()
	 * @see #readNbt(NbtTagSizeTracker)
	 * @see #MAX_READ_NBT_SIZE
	 */
	@Nullable
	public NbtCompound readNbt() {
		return this.readNbt(new NbtTagSizeTracker(2097152L));
	}

	/**
	 * Reads an NBT compound from this buf. The binary representation of NBT is
	 * handled by {@link net.minecraft.nbt.NbtIo}. If an NBT null is encountered,
	 * this method returns {@code null}. The compound does not have a size limit.
	 * 
	 * @apiNote Since this version does not have a size limit, it may be
	 * vulnerable to malicious NBT spam attacks.
	 * 
	 * @return the read compound, may be {@code null}
	 * @throws io.netty.handler.codec.EncoderException if the NBT cannot be read
	 * @see #writeNbt(NbtCompound)
	 * @see #readNbt()
	 * @see #readNbt(NbtTagSizeTracker)
	 */
	@Nullable
	public NbtCompound readUnlimitedNbt() {
		return this.readNbt(NbtTagSizeTracker.EMPTY);
	}

	/**
	 * Reads an NBT compound from this buf. The binary representation of NBT is
	 * handled by {@link net.minecraft.nbt.NbtIo}. If an NBT null is encountered,
	 * this method returns {@code null}. The compound can have a maximum size
	 * controlled by the {@code sizeTracker}.
	 * 
	 * @return the read compound, may be {@code null}
	 * @throws io.netty.handler.codec.EncoderException if the NBT cannot be read
	 * @throws RuntimeException if the compound exceeds the allowed maximum size
	 * @see #writeNbt(NbtCompound)
	 * @see #readNbt()
	 * @see #readUnlimitedNbt()
	 */
	@Nullable
	public NbtCompound readNbt(NbtTagSizeTracker sizeTracker) {
		int i = this.readerIndex();
		byte b = this.readByte();
		if (b == 0) {
			return null;
		} else {
			this.readerIndex(i);

			try {
				return NbtIo.read(new ByteBufInputStream(this), sizeTracker);
			} catch (IOException var5) {
				throw new EncoderException(var5);
			}
		}
	}

	/**
	 * Writes an item stack to this buf. An item stack is represented by a
	 * boolean indicating whether it exists; if it exists, it is followed by
	 * a var int for its raw id, a byte for its count, and an NBT compound for
	 * its tag.
	 * 
	 * @return this buf, for chaining
	 * @see #readItemStack()
	 * 
	 * @param stack the stack to write
	 */
	public PacketByteBuf writeItemStack(ItemStack stack) {
		if (stack.isEmpty()) {
			this.writeBoolean(false);
		} else {
			this.writeBoolean(true);
			Item item = stack.getItem();
			this.writeRegistryValue(Registry.ITEM, item);
			this.writeByte(stack.getCount());
			NbtCompound nbtCompound = null;
			if (item.isDamageable() || item.isNbtSynced()) {
				nbtCompound = stack.getNbt();
			}

			this.writeNbt(nbtCompound);
		}

		return this;
	}

	/**
	 * Reads an item stack from this buf. An item stack is represented by a
	 * boolean indicating whether it exists; if it exists, it is followed by
	 * a var int for its raw id, a byte for its count, and an NBT compound for
	 * its tag.
	 * 
	 * @return the read item stack
	 * @see #writeItemStack(ItemStack)
	 */
	public ItemStack readItemStack() {
		if (!this.readBoolean()) {
			return ItemStack.EMPTY;
		} else {
			Item item = this.readRegistryValue(Registry.ITEM);
			int i = this.readByte();
			ItemStack itemStack = new ItemStack(item, i);
			itemStack.setNbt(this.readNbt());
			return itemStack;
		}
	}

	/**
	 * Reads a string from this buf. A string is represented by a byte array of
	 * its UTF-8 data. The string can have a maximum length of {@value
	 * #DEFAULT_MAX_STRING_LENGTH}.
	 * 
	 * @return the string read
	 * @throws io.netty.handler.codec.DecoderException if the string read
	 * exceeds the maximum length
	 * @see #readString(int)
	 * @see #writeString(String)
	 * @see #writeString(String, int)
	 */
	public String readString() {
		return this.readString(DEFAULT_MAX_STRING_LENGTH);
	}

	/**
	 * Reads a string from this buf. A string is represented by a byte array of
	 * its UTF-8 data. The string can have a maximum length of {@code maxLength}.
	 * 
	 * @return the string read
	 * @throws io.netty.handler.codec.DecoderException if the string read
	 * is longer than {@code maxLength}
	 * @see #readString()
	 * @see #writeString(String)
	 * @see #writeString(String, int)
	 * 
	 * @param maxLength the maximum length of the string read
	 */
	public String readString(int maxLength) {
		int i = this.readVarInt();
		if (i > maxLength * 4) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
		} else if (i < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			String string = this.toString(this.readerIndex(), i, StandardCharsets.UTF_8);
			this.readerIndex(this.readerIndex() + i);
			if (string.length() > maxLength) {
				throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
			} else {
				return string;
			}
		}
	}

	/**
	 * Writes a string to this buf. A string is represented by a byte array of
	 * its UTF-8 data. That byte array can have a maximum length of
	 * {@value #DEFAULT_MAX_STRING_LENGTH}.
	 * 
	 * @return this buf, for chaining
	 * @throws io.netty.handler.codec.EncoderException if the byte array of the
	 * string to write is longer than {@value #DEFAULT_MAX_STRING_LENGTH}
	 * @see #readString()
	 * @see #readString(int)
	 * @see #writeString(String, int)
	 * 
	 * @param string the string to write
	 */
	public PacketByteBuf writeString(String string) {
		return this.writeString(string, DEFAULT_MAX_STRING_LENGTH);
	}

	/**
	 * Writes a string to this buf. A string is represented by a byte array of
	 * its UTF-8 data. That byte array can have a maximum length of
	 * {@code maxLength}.
	 * 
	 * @return this buf, for chaining
	 * @throws io.netty.handler.codec.EncoderException if the byte array of the
	 * string to write is longer than {@code maxLength}
	 * @see #readString()
	 * @see #readString(int)
	 * @see #writeString(String)
	 * 
	 * @param string the string to write
	 * @param maxLength the max length of the byte array
	 */
	public PacketByteBuf writeString(String string, int maxLength) {
		byte[] bs = string.getBytes(StandardCharsets.UTF_8);
		if (bs.length > maxLength) {
			throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + maxLength + ")");
		} else {
			this.writeVarInt(bs.length);
			this.writeBytes(bs);
			return this;
		}
	}

	/**
	 * Reads an identifier from this buf. An identifier is represented by its
	 * string form. The read identifier's string form can have a max length of
	 * {@value #DEFAULT_MAX_STRING_LENGTH}.
	 * 
	 * @return the read identifier
	 * @throws io.netty.handler.codec.DecoderException if the identifier's
	 * string form is longer than {@value #DEFAULT_MAX_STRING_LENGTH}
	 * @see #writeIdentifier(Identifier)
	 */
	public Identifier readIdentifier() {
		return new Identifier(this.readString(DEFAULT_MAX_STRING_LENGTH));
	}

	/**
	 * Writes an identifier to this buf. An identifier is represented by its
	 * string form. The written identifier's byte array can have a max length of
	 * {@value #DEFAULT_MAX_STRING_LENGTH}.
	 * 
	 * @return the read identifier
	 * @throws io.netty.handler.codec.EncoderException if the {@code id}'s
	 * byte array is longer than {@value #DEFAULT_MAX_STRING_LENGTH}
	 * @see #readIdentifier()
	 * 
	 * @param id the identifier to write
	 */
	public PacketByteBuf writeIdentifier(Identifier id) {
		this.writeString(id.toString());
		return this;
	}

	/**
	 * Reads a date from this buf. A date is represented by its time, a regular
	 * long.
	 * 
	 * @return the read date
	 * @see #writeDate(Date)
	 */
	public Date readDate() {
		return new Date(this.readLong());
	}

	/**
	 * Writes a date to this buf. A date is represented by its time, a regular
	 * long.
	 * 
	 * @return this buf, for chaining
	 * @see #readDate()
	 * 
	 * @param date the date to write
	 */
	public PacketByteBuf writeDate(Date date) {
		this.writeLong(date.getTime());
		return this;
	}

	/**
	 * Reads a block hit result from this buf. A block hit result is represented
	 * by a block position, a direction enum constant, 3 floats for the hit
	 * offset position, and a boolean for whether the hit was inside a block.
	 * 
	 * @return the read block hit result
	 * @see #writeBlockHitResult(BlockHitResult)
	 */
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

	/**
	 * Writes a block hit result to this buf. A block hit result is represented
	 * by a block position, a direction enum constant, 3 floats for the hit
	 * offset position, and a boolean for whether the hit was inside a block.
	 * 
	 * @see #readBlockHitResult()
	 * 
	 * @param hitResult the block hit result to write
	 */
	public void writeBlockHitResult(BlockHitResult hitResult) {
		BlockPos blockPos = hitResult.getBlockPos();
		this.writeBlockPos(blockPos);
		this.writeEnumConstant(hitResult.getSide());
		Vec3d vec3d = hitResult.getPos();
		this.writeFloat((float)(vec3d.x - (double)blockPos.getX()));
		this.writeFloat((float)(vec3d.y - (double)blockPos.getY()));
		this.writeFloat((float)(vec3d.z - (double)blockPos.getZ()));
		this.writeBoolean(hitResult.isInsideBlock());
	}

	/**
	 * Reads a bit set from this buf. A bit set is represented by a long array.
	 * 
	 * @return the read bit set
	 * @see #writeBitSet(BitSet)
	 */
	public BitSet readBitSet() {
		return BitSet.valueOf(this.readLongArray());
	}

	/**
	 * Writes a bit set to this buf. A bit set is represented by a long array.
	 * 
	 * @see #readBitSet()
	 * 
	 * @param bitSet the bit set to write
	 */
	public void writeBitSet(BitSet bitSet) {
		this.writeLongArray(bitSet.toLongArray());
	}

	/**
	 * Reads a game profile from this buf. A game profile is represented by a
	 * {@linkplain #readUuid() uuid}, a username string, and a collection of
	 * {@linkplain #readProperty() properties}.
	 * 
	 * @return the game profile
	 * @see #writeGameProfile(GameProfile)
	 */
	public GameProfile readGameProfile() {
		UUID uUID = this.readUuid();
		String string = this.readString(16);
		GameProfile gameProfile = new GameProfile(uUID, string);
		PropertyMap propertyMap = gameProfile.getProperties();
		this.forEachInCollection(buf -> {
			Property property = this.readProperty();
			propertyMap.put(property.getName(), property);
		});
		return gameProfile;
	}

	/**
	 * Writes a game profile to this buf. A game profile is represented by a
	 * {@linkplain #writeUuid(UUID) uuid}, a username string, and a collection of
	 * {@linkplain #writeProperty(Property) properties}.
	 * 
	 * @see #readGameProfile()
	 */
	public void writeGameProfile(GameProfile gameProfile) {
		this.writeUuid(gameProfile.getId());
		this.writeString(gameProfile.getName());
		this.writeCollection(gameProfile.getProperties().values(), PacketByteBuf::writeProperty);
	}

	/**
	 * Reads a property from this buf. A property is represented by a string representing
	 * the property key, a string representing the property value, a boolean indicating
	 * whether the property is signed, and a string representing the signature (only
	 * exists if signed).
	 * 
	 * @return the property
	 * @see #writeProperty(Property)
	 */
	public Property readProperty() {
		String string = this.readString();
		String string2 = this.readString();
		if (this.readBoolean()) {
			String string3 = this.readString();
			return new Property(string, string2, string3);
		} else {
			return new Property(string, string2);
		}
	}

	/**
	 * Writes a property to this buf. A property is represented by a string representing
	 * the property key, a string representing the property value, a boolean indicating
	 * whether the property is signed, and a string representing the signature (only
	 * exists if signed).
	 * 
	 * @see #readProperty()
	 */
	public void writeProperty(Property property) {
		this.writeString(property.getName());
		this.writeString(property.getValue());
		if (property.hasSignature()) {
			this.writeBoolean(true);
			this.writeString(property.getSignature());
		} else {
			this.writeBoolean(false);
		}
	}

	@Override
	public int capacity() {
		return this.parent.capacity();
	}

	@Override
	public ByteBuf capacity(int capacity) {
		return this.parent.capacity(capacity);
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
	public ByteBuf readerIndex(int index) {
		return this.parent.readerIndex(index);
	}

	@Override
	public int writerIndex() {
		return this.parent.writerIndex();
	}

	@Override
	public ByteBuf writerIndex(int index) {
		return this.parent.writerIndex(index);
	}

	@Override
	public ByteBuf setIndex(int readerIndex, int writerIndex) {
		return this.parent.setIndex(readerIndex, writerIndex);
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
	public boolean isReadable(int size) {
		return this.parent.isReadable(size);
	}

	@Override
	public boolean isWritable() {
		return this.parent.isWritable();
	}

	@Override
	public boolean isWritable(int size) {
		return this.parent.isWritable(size);
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
	public ByteBuf ensureWritable(int minBytes) {
		return this.parent.ensureWritable(minBytes);
	}

	@Override
	public int ensureWritable(int minBytes, boolean force) {
		return this.parent.ensureWritable(minBytes, force);
	}

	@Override
	public boolean getBoolean(int index) {
		return this.parent.getBoolean(index);
	}

	@Override
	public byte getByte(int index) {
		return this.parent.getByte(index);
	}

	@Override
	public short getUnsignedByte(int index) {
		return this.parent.getUnsignedByte(index);
	}

	@Override
	public short getShort(int index) {
		return this.parent.getShort(index);
	}

	@Override
	public short getShortLE(int index) {
		return this.parent.getShortLE(index);
	}

	@Override
	public int getUnsignedShort(int index) {
		return this.parent.getUnsignedShort(index);
	}

	@Override
	public int getUnsignedShortLE(int index) {
		return this.parent.getUnsignedShortLE(index);
	}

	@Override
	public int getMedium(int index) {
		return this.parent.getMedium(index);
	}

	@Override
	public int getMediumLE(int index) {
		return this.parent.getMediumLE(index);
	}

	@Override
	public int getUnsignedMedium(int index) {
		return this.parent.getUnsignedMedium(index);
	}

	@Override
	public int getUnsignedMediumLE(int index) {
		return this.parent.getUnsignedMediumLE(index);
	}

	@Override
	public int getInt(int index) {
		return this.parent.getInt(index);
	}

	@Override
	public int getIntLE(int index) {
		return this.parent.getIntLE(index);
	}

	@Override
	public long getUnsignedInt(int index) {
		return this.parent.getUnsignedInt(index);
	}

	@Override
	public long getUnsignedIntLE(int index) {
		return this.parent.getUnsignedIntLE(index);
	}

	@Override
	public long getLong(int index) {
		return this.parent.getLong(index);
	}

	@Override
	public long getLongLE(int index) {
		return this.parent.getLongLE(index);
	}

	@Override
	public char getChar(int index) {
		return this.parent.getChar(index);
	}

	@Override
	public float getFloat(int index) {
		return this.parent.getFloat(index);
	}

	@Override
	public double getDouble(int index) {
		return this.parent.getDouble(index);
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf buf) {
		return this.parent.getBytes(index, buf);
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf buf, int length) {
		return this.parent.getBytes(index, buf, length);
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf buf, int outputIndex, int length) {
		return this.parent.getBytes(index, buf, outputIndex, length);
	}

	@Override
	public ByteBuf getBytes(int index, byte[] bytes) {
		return this.parent.getBytes(index, bytes);
	}

	@Override
	public ByteBuf getBytes(int index, byte[] bytes, int outputIndex, int length) {
		return this.parent.getBytes(index, bytes, outputIndex, length);
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuffer buf) {
		return this.parent.getBytes(index, buf);
	}

	@Override
	public ByteBuf getBytes(int index, OutputStream stream, int length) throws IOException {
		return this.parent.getBytes(index, stream, length);
	}

	@Override
	public int getBytes(int index, GatheringByteChannel channel, int length) throws IOException {
		return this.parent.getBytes(index, channel, length);
	}

	@Override
	public int getBytes(int index, FileChannel channel, long pos, int length) throws IOException {
		return this.parent.getBytes(index, channel, pos, length);
	}

	@Override
	public CharSequence getCharSequence(int index, int length, Charset charset) {
		return this.parent.getCharSequence(index, length, charset);
	}

	@Override
	public ByteBuf setBoolean(int index, boolean value) {
		return this.parent.setBoolean(index, value);
	}

	@Override
	public ByteBuf setByte(int index, int value) {
		return this.parent.setByte(index, value);
	}

	@Override
	public ByteBuf setShort(int index, int value) {
		return this.parent.setShort(index, value);
	}

	@Override
	public ByteBuf setShortLE(int index, int value) {
		return this.parent.setShortLE(index, value);
	}

	@Override
	public ByteBuf setMedium(int index, int value) {
		return this.parent.setMedium(index, value);
	}

	@Override
	public ByteBuf setMediumLE(int index, int value) {
		return this.parent.setMediumLE(index, value);
	}

	@Override
	public ByteBuf setInt(int index, int value) {
		return this.parent.setInt(index, value);
	}

	@Override
	public ByteBuf setIntLE(int index, int value) {
		return this.parent.setIntLE(index, value);
	}

	@Override
	public ByteBuf setLong(int index, long value) {
		return this.parent.setLong(index, value);
	}

	@Override
	public ByteBuf setLongLE(int index, long value) {
		return this.parent.setLongLE(index, value);
	}

	@Override
	public ByteBuf setChar(int index, int value) {
		return this.parent.setChar(index, value);
	}

	@Override
	public ByteBuf setFloat(int index, float value) {
		return this.parent.setFloat(index, value);
	}

	@Override
	public ByteBuf setDouble(int index, double value) {
		return this.parent.setDouble(index, value);
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf buf) {
		return this.parent.setBytes(index, buf);
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf buf, int length) {
		return this.parent.setBytes(index, buf, length);
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf buf, int sourceIndex, int length) {
		return this.parent.setBytes(index, buf, sourceIndex, length);
	}

	@Override
	public ByteBuf setBytes(int index, byte[] bytes) {
		return this.parent.setBytes(index, bytes);
	}

	@Override
	public ByteBuf setBytes(int index, byte[] bytes, int sourceIndex, int length) {
		return this.parent.setBytes(index, bytes, sourceIndex, length);
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuffer buf) {
		return this.parent.setBytes(index, buf);
	}

	@Override
	public int setBytes(int index, InputStream stream, int length) throws IOException {
		return this.parent.setBytes(index, stream, length);
	}

	@Override
	public int setBytes(int index, ScatteringByteChannel channel, int length) throws IOException {
		return this.parent.setBytes(index, channel, length);
	}

	@Override
	public int setBytes(int index, FileChannel channel, long pos, int length) throws IOException {
		return this.parent.setBytes(index, channel, pos, length);
	}

	@Override
	public ByteBuf setZero(int index, int length) {
		return this.parent.setZero(index, length);
	}

	@Override
	public int setCharSequence(int index, CharSequence sequence, Charset charset) {
		return this.parent.setCharSequence(index, sequence, charset);
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
	public ByteBuf readBytes(int length) {
		return this.parent.readBytes(length);
	}

	@Override
	public ByteBuf readSlice(int length) {
		return this.parent.readSlice(length);
	}

	@Override
	public ByteBuf readRetainedSlice(int length) {
		return this.parent.readRetainedSlice(length);
	}

	@Override
	public ByteBuf readBytes(ByteBuf buf) {
		return this.parent.readBytes(buf);
	}

	@Override
	public ByteBuf readBytes(ByteBuf buf, int length) {
		return this.parent.readBytes(buf, length);
	}

	@Override
	public ByteBuf readBytes(ByteBuf buf, int outputIndex, int length) {
		return this.parent.readBytes(buf, outputIndex, length);
	}

	@Override
	public ByteBuf readBytes(byte[] bytes) {
		return this.parent.readBytes(bytes);
	}

	@Override
	public ByteBuf readBytes(byte[] bytes, int outputIndex, int length) {
		return this.parent.readBytes(bytes, outputIndex, length);
	}

	@Override
	public ByteBuf readBytes(ByteBuffer buf) {
		return this.parent.readBytes(buf);
	}

	@Override
	public ByteBuf readBytes(OutputStream stream, int length) throws IOException {
		return this.parent.readBytes(stream, length);
	}

	@Override
	public int readBytes(GatheringByteChannel channel, int length) throws IOException {
		return this.parent.readBytes(channel, length);
	}

	@Override
	public CharSequence readCharSequence(int length, Charset charset) {
		return this.parent.readCharSequence(length, charset);
	}

	@Override
	public int readBytes(FileChannel channel, long pos, int length) throws IOException {
		return this.parent.readBytes(channel, pos, length);
	}

	@Override
	public ByteBuf skipBytes(int length) {
		return this.parent.skipBytes(length);
	}

	@Override
	public ByteBuf writeBoolean(boolean value) {
		return this.parent.writeBoolean(value);
	}

	@Override
	public ByteBuf writeByte(int value) {
		return this.parent.writeByte(value);
	}

	@Override
	public ByteBuf writeShort(int value) {
		return this.parent.writeShort(value);
	}

	@Override
	public ByteBuf writeShortLE(int value) {
		return this.parent.writeShortLE(value);
	}

	@Override
	public ByteBuf writeMedium(int value) {
		return this.parent.writeMedium(value);
	}

	@Override
	public ByteBuf writeMediumLE(int value) {
		return this.parent.writeMediumLE(value);
	}

	@Override
	public ByteBuf writeInt(int value) {
		return this.parent.writeInt(value);
	}

	@Override
	public ByteBuf writeIntLE(int value) {
		return this.parent.writeIntLE(value);
	}

	@Override
	public ByteBuf writeLong(long value) {
		return this.parent.writeLong(value);
	}

	@Override
	public ByteBuf writeLongLE(long value) {
		return this.parent.writeLongLE(value);
	}

	@Override
	public ByteBuf writeChar(int value) {
		return this.parent.writeChar(value);
	}

	@Override
	public ByteBuf writeFloat(float value) {
		return this.parent.writeFloat(value);
	}

	@Override
	public ByteBuf writeDouble(double value) {
		return this.parent.writeDouble(value);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf buf) {
		return this.parent.writeBytes(buf);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf buf, int length) {
		return this.parent.writeBytes(buf, length);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf buf, int sourceIndex, int length) {
		return this.parent.writeBytes(buf, sourceIndex, length);
	}

	@Override
	public ByteBuf writeBytes(byte[] bytes) {
		return this.parent.writeBytes(bytes);
	}

	@Override
	public ByteBuf writeBytes(byte[] bytes, int sourceIndex, int length) {
		return this.parent.writeBytes(bytes, sourceIndex, length);
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer buf) {
		return this.parent.writeBytes(buf);
	}

	@Override
	public int writeBytes(InputStream stream, int length) throws IOException {
		return this.parent.writeBytes(stream, length);
	}

	@Override
	public int writeBytes(ScatteringByteChannel channel, int length) throws IOException {
		return this.parent.writeBytes(channel, length);
	}

	@Override
	public int writeBytes(FileChannel channel, long pos, int length) throws IOException {
		return this.parent.writeBytes(channel, pos, length);
	}

	@Override
	public ByteBuf writeZero(int length) {
		return this.parent.writeZero(length);
	}

	@Override
	public int writeCharSequence(CharSequence sequence, Charset charset) {
		return this.parent.writeCharSequence(sequence, charset);
	}

	@Override
	public int indexOf(int from, int to, byte value) {
		return this.parent.indexOf(from, to, value);
	}

	@Override
	public int bytesBefore(byte value) {
		return this.parent.bytesBefore(value);
	}

	@Override
	public int bytesBefore(int length, byte value) {
		return this.parent.bytesBefore(length, value);
	}

	@Override
	public int bytesBefore(int index, int length, byte value) {
		return this.parent.bytesBefore(index, length, value);
	}

	@Override
	public int forEachByte(ByteProcessor byteProcessor) {
		return this.parent.forEachByte(byteProcessor);
	}

	@Override
	public int forEachByte(int index, int length, ByteProcessor byteProcessor) {
		return this.parent.forEachByte(index, length, byteProcessor);
	}

	@Override
	public int forEachByteDesc(ByteProcessor byteProcessor) {
		return this.parent.forEachByteDesc(byteProcessor);
	}

	@Override
	public int forEachByteDesc(int index, int length, ByteProcessor byteProcessor) {
		return this.parent.forEachByteDesc(index, length, byteProcessor);
	}

	@Override
	public ByteBuf copy() {
		return this.parent.copy();
	}

	@Override
	public ByteBuf copy(int index, int length) {
		return this.parent.copy(index, length);
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
	public ByteBuf slice(int index, int length) {
		return this.parent.slice(index, length);
	}

	@Override
	public ByteBuf retainedSlice(int index, int length) {
		return this.parent.retainedSlice(index, length);
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
	public ByteBuffer nioBuffer(int index, int length) {
		return this.parent.nioBuffer(index, length);
	}

	@Override
	public ByteBuffer internalNioBuffer(int index, int length) {
		return this.parent.internalNioBuffer(index, length);
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return this.parent.nioBuffers();
	}

	@Override
	public ByteBuffer[] nioBuffers(int index, int length) {
		return this.parent.nioBuffers(index, length);
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
	public String toString(int index, int length, Charset charset) {
		return this.parent.toString(index, length, charset);
	}

	@Override
	public int hashCode() {
		return this.parent.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return this.parent.equals(o);
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
	public boolean release(int decrement) {
		return this.parent.release(decrement);
	}
}
