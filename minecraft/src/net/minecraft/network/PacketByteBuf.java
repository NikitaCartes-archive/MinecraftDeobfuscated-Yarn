package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
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
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.encoding.VarLongs;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
 *  <td>Codec-based (NBT)</td><td>{@link #decode(DynamicOps, Codec, NbtTagSizeTracker)}</td><td>{@link #encode(DynamicOps, Codec, Object)}</td>
 * </tr>
 * <tr>
 *  <td>Codec-based (JSON)</td><td>{@link #decodeAsJson(Codec)}</td><td>{@link #encodeAsJson(Codec, Object)}</td>
 * </tr>
 * <tr>
 *  <td>{@link net.minecraft.registry.Registry} value</td><td>{@link #readRegistryValue(IndexedIterable)}</td><td>{@link #writeRegistryValue(IndexedIterable, Object)}</td>
 * </tr>
 * <tr>
 *  <td>Integer-identified value</td><td>{@link #decode(IntFunction)}</td><td>{@link #encode(ToIntFunction, Object)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Collection}</td><td>{@link #readCollection(IntFunction, PacketByteBuf.PacketReader)}</td><td>{@link #writeCollection(Collection, PacketByteBuf.PacketWriter)}</td>
 * </tr>
 * <tr>
 *  <td>{@link IntList}</td><td>{@link #readIntList()}</td><td>{@link #writeIntList(IntList)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Map}</td><td>{@link #readMap(IntFunction, PacketByteBuf.PacketReader, PacketByteBuf.PacketReader)}</td><td>{@link #writeMap(Map, PacketByteBuf.PacketWriter, PacketByteBuf.PacketWriter)}</td>
 * </tr>
 * <tr>
 *  <td>{@link EnumSet}</td><td>{@link #readEnumSet(Class)}</td><td>{@link #writeEnumSet(EnumSet, Class)}</td>
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
 *  <td>{@link GlobalPos}</td><td>{@link #readGlobalPos()}</td><td>{@link #writeGlobalPos(GlobalPos)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Vector3f}</td><td>{@link #readVector3f()}</td><td>{@link #writeVector3f(Vector3f)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Vec3d}</td><td>{@link #readVec3d()}</td><td>{@link #writeVec3d(Vec3d)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Quaternionf}</td><td>{@link #readQuaternionf()}</td><td>{@link #writeQuaternionf(Quaternionf)}</td>
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
 *  <td>{@index PropertyMap}</td><td>{@link #readPropertyMap()}</td><td>{@link #writePropertyMap(PropertyMap)}</td>
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
 *  <td>{@link RegistryKey}</td><td>{@link #readRegistryKey(RegistryKey)}</td><td>{@link #writeRegistryKey(RegistryKey)}</td>
 * </tr>
 * <tr>
 *  <td>{@link RegistryKey} of a registry</td><td>{@link #readRegistryRef()}</td><td>{@link #writeRegistryKey(RegistryKey)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Date}</td><td>{@link #readDate()}</td><td>{@link #writeDate(Date)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Instant}</td><td>{@link #readInstant()}</td><td>{@link #writeInstant(Instant)}</td>
 * </tr>
 * <tr>
 *  <td>{@link PublicKey}</td><td>{@link #readPublicKey()}</td><td>{@link #writePublicKey(PublicKey)}</td>
 * </tr>
 * <tr>
 *  <td>{@link BlockHitResult}</td><td>{@link #readBlockHitResult()}</td><td>{@link #writeBlockHitResult(BlockHitResult)}</td>
 * </tr>
 * <tr>
 *  <td>{@link BitSet}</td><td>{@link #readBitSet()}</td><td>{@link #writeBitSet(BitSet)}</td>
 * </tr>
 * <tr>
 *  <td>{@link Optional}</td><td>{@link #readOptional(PacketByteBuf.PacketReader)}</td><td>{@link #writeOptional(Optional, PacketByteBuf.PacketWriter)}</td>
 * </tr>
 * <tr>
 *  <td>Nullable value</td><td>{@link #readNullable(PacketByteBuf.PacketReader)}</td><td>{@link #writeNullable(Object, PacketByteBuf.PacketWriter)}</td>
 * </tr>
 * <tr>
 *  <td>{@index Either}</td><td>{@link #readEither(PacketByteBuf.PacketReader, PacketByteBuf.PacketReader)}</td><td>{@link #writeEither(Either, PacketByteBuf.PacketWriter, PacketByteBuf.PacketWriter)}</td>
 * </tr>
 * </table></div>
 * 
 * <p>All {@code read} and {@code write} methods throw {@link
 * IndexOutOfBoundsException} if there is not enough bytes to be read or
 * not enough space to write.
 */
public class PacketByteBuf extends ByteBuf {
	/**
	 * The maximum size, in number of bytes, allowed of the NBT compound read by
	 * {@link #readNbt()}.
	 */
	public static final int MAX_READ_NBT_SIZE = 2097152;
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
	private static final int field_39381 = 256;
	private static final int field_39382 = 256;
	private static final int field_39383 = 512;
	private static final Gson GSON = new Gson();

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
	 * Reads an object from this buf as a compound NBT with the given codec.
	 * 
	 * @param <T> the decoded object's type
	 * @return the read object
	 * @throws io.netty.handler.codec.EncoderException if the {@code codec} fails
	 * to decode the compound NBT
	 * @see #encode(DynamicOps, Codec, Object)
	 */
	@Deprecated
	public <T> T decode(DynamicOps<NbtElement> ops, Codec<T> codec) {
		return this.decode(ops, codec, NbtTagSizeTracker.EMPTY);
	}

	/**
	 * Reads an object from this buf as a compound NBT with the given codec.
	 * 
	 * @param <T> the decoded object's type
	 * @return the read object
	 * @throws io.netty.handler.codec.EncoderException if the {@code codec} fails
	 * to decode the compound NBT
	 * @see #encode(DynamicOps, Codec, Object)
	 */
	@Deprecated
	public <T> T decode(DynamicOps<NbtElement> ops, Codec<T> codec, NbtTagSizeTracker sizeTracker) {
		NbtElement nbtElement = this.readNbt(sizeTracker);
		return Util.getResult(codec.parse(ops, nbtElement), error -> new DecoderException("Failed to decode: " + error + " " + nbtElement));
	}

	/**
	 * Writes an object to this buf as a compound NBT with the given codec.
	 * 
	 * @param <T> the encoded object's type
	 * @throws io.netty.handler.codec.EncoderException if the {@code codec} fails
	 * to encode the compound NBT
	 * @see #decode(DynamicOps, Codec, NbtTagSizeTracker)
	 */
	@Deprecated
	public <T> PacketByteBuf encode(DynamicOps<NbtElement> ops, Codec<T> codec, T value) {
		NbtElement nbtElement = Util.getResult(codec.encodeStart(ops, value), error -> new EncoderException("Failed to encode: " + error + " " + value));
		this.writeNbt(nbtElement);
		return this;
	}

	/**
	 * Reads an object from this buf as a JSON element with the given codec.
	 * 
	 * @param <T> the decoded object's type
	 * @return the read object
	 * @throws io.netty.handler.codec.EncoderException if the {@code codec} fails
	 * to decode the JSON element
	 * @see #encodeAsJson(Codec, Object)
	 */
	public <T> T decodeAsJson(Codec<T> codec) {
		JsonElement jsonElement = JsonHelper.deserialize(GSON, this.readString(), JsonElement.class);
		DataResult<T> dataResult = codec.parse(JsonOps.INSTANCE, jsonElement);
		return Util.getResult(dataResult, error -> new DecoderException("Failed to decode json: " + error));
	}

	/**
	 * Writes an object to this buf as a JSON element with the given codec.
	 * 
	 * @param <T> the encoded object's type
	 * @throws io.netty.handler.codec.EncoderException if the {@code codec} fails
	 * to encode the JSON element
	 * @see #decodeAsJson(Codec)
	 */
	public <T> void encodeAsJson(Codec<T> codec, T value) {
		DataResult<JsonElement> dataResult = codec.encodeStart(JsonOps.INSTANCE, value);
		this.writeString(GSON.toJson(Util.getResult(dataResult, error -> new EncoderException("Failed to encode: " + error + " " + value))));
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

	public <T> void writeRegistryEntry(IndexedIterable<RegistryEntry<T>> registryEntries, RegistryEntry<T> entry, PacketByteBuf.PacketWriter<T> writer) {
		switch (entry.getType()) {
			case REFERENCE:
				int i = registryEntries.getRawId(entry);
				if (i == -1) {
					throw new IllegalArgumentException("Can't find id for '" + entry.value() + "' in map " + registryEntries);
				}

				this.writeVarInt(i + 1);
				break;
			case DIRECT:
				this.writeVarInt(0);
				writer.accept(this, entry.value());
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

	public <T> RegistryEntry<T> readRegistryEntry(IndexedIterable<RegistryEntry<T>> registryEntries, PacketByteBuf.PacketReader<T> reader) {
		int i = this.readVarInt();
		if (i == 0) {
			return RegistryEntry.of((T)reader.apply(this));
		} else {
			RegistryEntry<T> registryEntry = registryEntries.get(i - 1);
			if (registryEntry == null) {
				throw new IllegalArgumentException("Can't find element with id " + i);
			} else {
				return registryEntry;
			}
		}
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
	 * @see #writeCollection(Collection, PacketByteBuf.PacketWriter)
	 * @see #readList(PacketByteBuf.PacketReader)
	 * 
	 * @param collectionFactory a factory that creates a collection with a given size
	 */
	public <T, C extends Collection<T>> C readCollection(IntFunction<C> collectionFactory, PacketByteBuf.PacketReader<T> reader) {
		int i = this.readVarInt();
		C collection = (C)collectionFactory.apply(i);

		for (int j = 0; j < i; j++) {
			collection.add(reader.apply(this));
		}

		return collection;
	}

	/**
	 * Writes a collection to this buf. The collection is stored as a leading
	 * {@linkplain #readVarInt() var int} size followed by the entries
	 * sequentially.
	 * 
	 * @param <T> the list's entry type
	 * @see #readCollection(IntFunction, PacketByteBuf.PacketReader)
	 * 
	 * @param collection the collection to write
	 */
	public <T> void writeCollection(Collection<T> collection, PacketByteBuf.PacketWriter<T> writer) {
		this.writeVarInt(collection.size());

		for (T object : collection) {
			writer.accept(this, object);
		}
	}

	/**
	 * Reads a collection from this buf as an array list.
	 * 
	 * @param <T> the list's entry type
	 * @return the read list
	 * @see #readCollection(IntFunction, PacketByteBuf.PacketReader)
	 */
	public <T> List<T> readList(PacketByteBuf.PacketReader<T> reader) {
		return this.readCollection(Lists::newArrayListWithCapacity, reader);
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
	 * @see #writeMap(Map, PacketByteBuf.PacketWriter, PacketByteBuf.PacketWriter)
	 * @see #readMap(PacketByteBuf.PacketReader, PacketByteBuf.PacketReader)
	 * 
	 * @param mapFactory a factory that creates a map with a given size
	 */
	public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> mapFactory, PacketByteBuf.PacketReader<K> keyReader, PacketByteBuf.PacketReader<V> valueReader) {
		int i = this.readVarInt();
		M map = (M)mapFactory.apply(i);

		for (int j = 0; j < i; j++) {
			K object = (K)keyReader.apply(this);
			V object2 = (V)valueReader.apply(this);
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
	 * @see #readMap(IntFunction, PacketByteBuf.PacketReader, PacketByteBuf.PacketReader)
	 */
	public <K, V> Map<K, V> readMap(PacketByteBuf.PacketReader<K> keyReader, PacketByteBuf.PacketReader<V> valueReader) {
		return this.readMap(Maps::newHashMapWithExpectedSize, keyReader, valueReader);
	}

	/**
	 * Writes a map to this buf. The map is stored as a leading
	 * {@linkplain #readVarInt() var int} size followed by each key and value
	 * pair.
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 * @see #readMap(IntFunction, PacketByteBuf.PacketReader, PacketByteBuf.PacketReader)
	 * 
	 * @param map the map to write
	 */
	public <K, V> void writeMap(Map<K, V> map, PacketByteBuf.PacketWriter<K> keyWriter, PacketByteBuf.PacketWriter<V> valueWriter) {
		this.writeVarInt(map.size());
		map.forEach((key, value) -> {
			keyWriter.accept(this, key);
			valueWriter.accept(this, value);
		});
	}

	/**
	 * Iterates a collection from this buf. The collection is stored as a leading
	 * {@linkplain #readVarInt() var int} {@code size} followed by the entries
	 * sequentially. The {@code consumer} will be called {@code size} times.
	 * 
	 * @see #readCollection(IntFunction, PacketByteBuf.PacketReader)
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
	 * Writes an enum set to this buf. An enum set is represented by a bit set that indicates
	 * whether each element is in the set.
	 * 
	 * @see #readEnumSet
	 * 
	 * @param type the type of the enum
	 */
	public <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumSet, Class<E> type) {
		E[] enums = (E[])type.getEnumConstants();
		BitSet bitSet = new BitSet(enums.length);

		for (int i = 0; i < enums.length; i++) {
			bitSet.set(i, enumSet.contains(enums[i]));
		}

		this.writeBitSet(bitSet, enums.length);
	}

	/**
	 * Reads an enum set from this buf. An enum set is represented by a bit set that indicates
	 * whether each element is in the set.
	 * 
	 * @see #writeEnumSet
	 * 
	 * @param type the type of the enum
	 */
	public <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> type) {
		E[] enums = (E[])type.getEnumConstants();
		BitSet bitSet = this.readBitSet(enums.length);
		EnumSet<E> enumSet = EnumSet.noneOf(type);

		for (int i = 0; i < enums.length; i++) {
			if (bitSet.get(i)) {
				enumSet.add(enums[i]);
			}
		}

		return enumSet;
	}

	/**
	 * Writes an optional value to this buf. An optional value is represented by
	 * a boolean indicating if the value is present, followed by the value only if
	 * the value is present.
	 * 
	 * @see #readOptional(PacketByteBuf.PacketReader)
	 */
	public <T> void writeOptional(Optional<T> value, PacketByteBuf.PacketWriter<T> writer) {
		if (value.isPresent()) {
			this.writeBoolean(true);
			writer.accept(this, value.get());
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
	 * @see #writeOptional(Optional, PacketByteBuf.PacketWriter)
	 */
	public <T> Optional<T> readOptional(PacketByteBuf.PacketReader<T> reader) {
		return this.readBoolean() ? Optional.of(reader.apply(this)) : Optional.empty();
	}

	/**
	 * Reads a nullable value from this buf. A nullable value is represented by
	 * a boolean indicating if the value is not null, followed by the value only if
	 * the value is not null.
	 * 
	 * @return the read nullable value
	 * @see #writeNullable(Object, PacketByteBuf.PacketWriter)
	 */
	@Nullable
	public <T> T readNullable(PacketByteBuf.PacketReader<T> reader) {
		return (T)(this.readBoolean() ? reader.apply(this) : null);
	}

	/**
	 * Writes a nullable value to this buf. A nullable value is represented by
	 * a boolean indicating if the value is not null, followed by the value only if
	 * the value is not null.
	 * 
	 * @see #readNullable(PacketByteBuf.PacketReader)
	 */
	public <T> void writeNullable(@Nullable T value, PacketByteBuf.PacketWriter<T> writer) {
		if (value != null) {
			this.writeBoolean(true);
			writer.accept(this, value);
		} else {
			this.writeBoolean(false);
		}
	}

	/**
	 * Writes an {@code Either} to this buf. An either is represented by
	 * a boolean indicating if the left side or the right side of the either,
	 * followed by the value.
	 * 
	 * @see #readEither(PacketByteBuf.PacketReader, PacketByteBuf.PacketReader)
	 */
	public <L, R> void writeEither(Either<L, R> either, PacketByteBuf.PacketWriter<L> leftWriter, PacketByteBuf.PacketWriter<R> rightWriter) {
		either.ifLeft(object -> {
			this.writeBoolean(true);
			leftWriter.accept(this, object);
		}).ifRight(object -> {
			this.writeBoolean(false);
			rightWriter.accept(this, object);
		});
	}

	/**
	 * Reads an {@code Either} from this buf. An either is represented by
	 * a boolean indicating if the left side or the right side of the either,
	 * followed by the value.
	 * 
	 * @return the read either
	 * @see #writeEither(Either, PacketByteBuf.PacketWriter, PacketByteBuf.PacketWriter)
	 */
	public <L, R> Either<L, R> readEither(PacketByteBuf.PacketReader<L> leftReader, PacketByteBuf.PacketReader<R> rightReader) {
		return this.readBoolean() ? Either.left((L)leftReader.apply(this)) : Either.right((R)rightReader.apply(this));
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
	 * @param maxSize the max length of the read array
	 * @param toArray the array to reuse
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
	 * Reads a global position from this buf. A global position is represented by
	 * {@linkplain #readRegistryKey the registry key} of the dimension followed by
	 * {@linkplain #readBlockPos the block position}.
	 * 
	 * @return the read global pos
	 * @see #writeGlobalPos(GlobalPos)
	 */
	public GlobalPos readGlobalPos() {
		RegistryKey<World> registryKey = this.readRegistryKey(RegistryKeys.WORLD);
		BlockPos blockPos = this.readBlockPos();
		return GlobalPos.create(registryKey, blockPos);
	}

	/**
	 * Writes a global position to this buf. A global position is represented by
	 * {@linkplain #writeRegistryKey the registry key} of the dimension followed by
	 * {@linkplain #writeBlockPos the block position}.
	 * 
	 * @see #readGlobalPos()
	 */
	public void writeGlobalPos(GlobalPos pos) {
		this.writeRegistryKey(pos.getDimension());
		this.writeBlockPos(pos.getPos());
	}

	/**
	 * Reads a {@link Vector3f} from this buf. A {@link Vector3f} is represented by
	 * three {@code float}s.
	 * 
	 * @see #writeVector3f(Vector3f)
	 */
	public Vector3f readVector3f() {
		return new Vector3f(this.readFloat(), this.readFloat(), this.readFloat());
	}

	/**
	 * Writes a {@link Vector3f} to this buf. A {@link Vector3f} is represented by
	 * three {@code float}s.
	 * 
	 * @see #readVector3f()
	 */
	public void writeVector3f(Vector3f vector3f) {
		this.writeFloat(vector3f.x());
		this.writeFloat(vector3f.y());
		this.writeFloat(vector3f.z());
	}

	/**
	 * Reads a {@link Quaternionf} from this buf. A {@link Quaternionf} is represented
	 * by four {@code float}s.
	 * 
	 * @see #writeQuaternionf(Quaternionf)
	 */
	public Quaternionf readQuaternionf() {
		return new Quaternionf(this.readFloat(), this.readFloat(), this.readFloat(), this.readFloat());
	}

	/**
	 * Writes a {@link Quaternionf} to this buf. A {@link Quaternionf} is represented
	 * by four {@code float}s.
	 * 
	 * @see #readQuaternionf()
	 */
	public void writeQuaternionf(Quaternionf quaternionf) {
		this.writeFloat(quaternionf.x);
		this.writeFloat(quaternionf.y);
		this.writeFloat(quaternionf.z);
		this.writeFloat(quaternionf.w);
	}

	/**
	 * Reads a {@link Vec3d} from this buf. A {@link Vec3d} is represented
	 * by four {@code double}s.
	 * 
	 * @see #writeVec3d(Vec3d)
	 */
	public Vec3d readVec3d() {
		return new Vec3d(this.readDouble(), this.readDouble(), this.readDouble());
	}

	/**
	 * Writes a {@link Vec3d} to this buf. A {@link Vec3d} is represented
	 * by four {@code double}s.
	 * 
	 * @see #readVec3d()
	 */
	public void writeVec3d(Vec3d vec) {
		this.writeDouble(vec.getX());
		this.writeDouble(vec.getY());
		this.writeDouble(vec.getZ());
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
	 * Reads a {@linkplain #readVarInt var int} representing an ID, then
	 * returns the value converted by {@code idToValue}.
	 * 
	 * @see #encode(ToIntFunction, Object)
	 * 
	 * @param idToValue a function that gets the value from the integer ID
	 */
	public <T> T decode(IntFunction<T> idToValue) {
		int i = this.readVarInt();
		return (T)idToValue.apply(i);
	}

	/**
	 * Converts {@code value} to an integer representing its ID, then
	 * writes a {@linkplain #readVarInt var int} representation of such ID.
	 * 
	 * @see #decode(IntFunction)
	 * 
	 * @param valueToId a function that gets the value's integer ID
	 */
	public <T> PacketByteBuf encode(ToIntFunction<T> valueToId, T value) {
		int i = valueToId.applyAsInt(value);
		return this.writeVarInt(i);
	}

	/**
	 * Reads a single var int from this buf.
	 * 
	 * @return the value read
	 * @see #writeVarInt(int)
	 */
	public int readVarInt() {
		return VarInts.read(this.parent);
	}

	/**
	 * Reads a single var long from this buf.
	 * 
	 * @return the value read
	 * @see #writeVarLong(long)
	 */
	public long readVarLong() {
		return VarLongs.read(this.parent);
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
	 * @see net.minecraft.network.encoding.VarInts
	 * 
	 * @param value the value to write
	 */
	public PacketByteBuf writeVarInt(int value) {
		VarInts.write(this.parent, value);
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
	 * @see net.minecraft.network.encoding.VarLong
	 * 
	 * @param value the value to write
	 */
	public PacketByteBuf writeVarLong(long value) {
		VarLongs.write(this.parent, value);
		return this;
	}

	/**
	 * Writes an NBT element to this buf. The binary representation of NBT is
	 * handled by {@link net.minecraft.nbt.NbtIo}. If {@code nbt} is {@code
	 * null}, it is treated as an NBT null.
	 * 
	 * @return this buf, for chaining
	 * @throws io.netty.handler.codec.EncoderException if the NBT cannot be
	 * written
	 * @see #readNbt()
	 * @see #readNbt(NbtTagSizeTracker)
	 */
	public PacketByteBuf writeNbt(@Nullable NbtElement nbt) {
		if (nbt == null) {
			nbt = NbtEnd.INSTANCE;
		}

		try {
			NbtIo.writeForPacket(nbt, new ByteBufOutputStream(this));
			return this;
		} catch (IOException var3) {
			throw new EncoderException(var3);
		}
	}

	/**
	 * Reads an NBT compound from this buf. The binary representation of NBT is
	 * handled by {@link net.minecraft.nbt.NbtIo}. If an NBT null is encountered,
	 * this method returns {@code null}. The compound can have a maximum size of
	 * {@value #MAX_READ_NBT_SIZE} bytes.
	 * 
	 * <p>Note that unlike {@link #readNbt(NbtTagSizeTracker)}, this can only
	 * read compounds.
	 * 
	 * @return the read compound, may be {@code null}
	 * @throws io.netty.handler.codec.EncoderException if the NBT cannot be read
	 * @throws RuntimeException if the compound exceeds the allowed maximum size
	 * @see #writeNbt(NbtCompound)
	 * @see #readNbt(NbtTagSizeTracker)
	 * @see #MAX_READ_NBT_SIZE
	 */
	@Nullable
	public NbtCompound readNbt() {
		NbtElement nbtElement = this.readNbt(new NbtTagSizeTracker(2097152L));
		if (nbtElement != null && !(nbtElement instanceof NbtCompound)) {
			throw new DecoderException("Not a compound tag: " + nbtElement);
		} else {
			return (NbtCompound)nbtElement;
		}
	}

	/**
	 * Reads an NBT element from this buf. The binary representation of NBT is
	 * handled by {@link net.minecraft.nbt.NbtIo}. If an NBT null is encountered,
	 * this method returns {@code null}. The element can have a maximum size
	 * controlled by the {@code sizeTracker}.
	 * 
	 * @return the read element, may be {@code null}
	 * @throws io.netty.handler.codec.EncoderException if the NBT cannot be read
	 * @throws RuntimeException if the element exceeds the allowed maximum size
	 * @see #writeNbt(NbtElement)
	 * @see #readNbt()
	 */
	@Nullable
	public NbtElement readNbt(NbtTagSizeTracker sizeTracker) {
		try {
			NbtElement nbtElement = NbtIo.read(new ByteBufInputStream(this), sizeTracker);
			return nbtElement.getType() == 0 ? null : nbtElement;
		} catch (IOException var3) {
			throw new EncoderException(var3);
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
			this.writeRegistryValue(Registries.ITEM, item);
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
			Item item = this.readRegistryValue(Registries.ITEM);
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
		return StringEncoding.decode(this.parent, maxLength);
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
	 * @param maxLength the max length of the byte array
	 */
	public PacketByteBuf writeString(String string, int maxLength) {
		StringEncoding.encode(this.parent, string, maxLength);
		return this;
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
	 * @return this buf, for chaining
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
	 * Reads a registry key from this buf. A registry key is represented by its
	 * {@linkplain #readIdentifier value as an identifier}.
	 * 
	 * @return the read registry key
	 * @see #writeRegistryKey(RegistryKey)
	 * 
	 * @param registryRef the registry key of the registry the read registry key belongs to
	 */
	public <T> RegistryKey<T> readRegistryKey(RegistryKey<? extends Registry<T>> registryRef) {
		Identifier identifier = this.readIdentifier();
		return RegistryKey.of(registryRef, identifier);
	}

	/**
	 * Writes a registry key to this buf. A registry key is represented by its
	 * {@linkplain #writeIdentifier value as an identifier}.
	 * 
	 * @see #readRegistryKey(RegistryKey)
	 */
	public void writeRegistryKey(RegistryKey<?> key) {
		this.writeIdentifier(key.getValue());
	}

	/**
	 * Reads a registry key referencing another registry key from this buf.
	 * Such key is represented by its {@linkplain #readIdentifier value as an identifier}.
	 * 
	 * <p>This is the same as {@code readRegistryKey(Registries.ROOT)}.
	 * To read a registry key of a registered object (such as biomes),
	 * use {@link #readRegistryKey(RegistryKey)}.
	 * 
	 * @return the read registry key
	 * @see #readRegistryKey(RegistryKey)
	 * @see #writeRegistryKey(RegistryKey)
	 */
	public <T> RegistryKey<? extends Registry<T>> readRegistryRefKey() {
		Identifier identifier = this.readIdentifier();
		return RegistryKey.ofRegistry(identifier);
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
	 * Reads an instant from this buf. An instant is represented by the milliseconds
	 * since the epoch.
	 * 
	 * @return the read instant
	 * @see #writeInstant(Instant)
	 */
	public Instant readInstant() {
		return Instant.ofEpochMilli(this.readLong());
	}

	/**
	 * Writes an instant to this buf. An instant is represented by the milliseconds
	 * since the epoch.
	 * 
	 * @see #readInstant()
	 */
	public void writeInstant(Instant instant) {
		this.writeLong(instant.toEpochMilli());
	}

	/**
	 * Reads a public key from this buf. A public key is represented by a {@linkplain
	 * #readByteArray byte array} of X.509-encoded payload.
	 * 
	 * @return the read public key
	 * @throws io.netty.handler.codec.DecoderException if the public key is malformed
	 * @see #writePublicKey(PublicKey)
	 */
	public PublicKey readPublicKey() {
		try {
			return NetworkEncryptionUtils.decodeEncodedRsaPublicKey(this.readByteArray(512));
		} catch (NetworkEncryptionException var2) {
			throw new DecoderException("Malformed public key bytes", var2);
		}
	}

	/**
	 * Writes a public key to this buf. A public key is represented by a {@linkplain
	 * #writeByteArray byte array} of X.509-encoded payload.
	 * 
	 * @return this buf, for chaining
	 * @see #readPublicKey()
	 */
	public PacketByteBuf writePublicKey(PublicKey publicKey) {
		this.writeByteArray(publicKey.getEncoded());
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
	 * Reads a bit set from this buf. A bit set is represented using its byte array representation.
	 * 
	 * @see BitSet#valueOf
	 * @see #writeBitSet
	 * 
	 * @param size the maximum size of the bit set
	 */
	public BitSet readBitSet(int size) {
		byte[] bs = new byte[MathHelper.ceilDiv(size, 8)];
		this.readBytes(bs);
		return BitSet.valueOf(bs);
	}

	/**
	 * Writes a bit set to this buf. A bit set is represented using its byte array representation.
	 * 
	 * @throws io.netty.handler.codec.EncoderException if the bit set's length is above {@code size}
	 * 
	 * @see BitSet#toByteArray
	 * @see #readBitSet
	 * 
	 * @param size the maximum size of the bit set
	 */
	public void writeBitSet(BitSet bitSet, int size) {
		if (bitSet.length() > size) {
			throw new EncoderException("BitSet is larger than expected size (" + bitSet.length() + ">" + size + ")");
		} else {
			byte[] bs = bitSet.toByteArray();
			this.writeBytes(Arrays.copyOf(bs, MathHelper.ceilDiv(size, 8)));
		}
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
		gameProfile.getProperties().putAll(this.readPropertyMap());
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
		this.writePropertyMap(gameProfile.getProperties());
	}

	/**
	 * Reads an authlib property map from this buf. A property map is represented as a
	 * collection of properties.
	 * 
	 * @see #writePropertyMap
	 */
	public PropertyMap readPropertyMap() {
		PropertyMap propertyMap = new PropertyMap();
		this.forEachInCollection(buf -> {
			Property property = this.readProperty();
			propertyMap.put(property.name(), property);
		});
		return propertyMap;
	}

	/**
	 * Writes an authlib property map to this buf. A property map is represented as a
	 * collection of properties.
	 * 
	 * @see #readPropertyMap
	 */
	public void writePropertyMap(PropertyMap propertyMap) {
		this.writeCollection(propertyMap.values(), PacketByteBuf::writeProperty);
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
		String string3 = this.readNullable(PacketByteBuf::readString);
		return new Property(string, string2, string3);
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
		this.writeString(property.name());
		this.writeString(property.value());
		this.writeNullable(property.signature(), PacketByteBuf::writeString);
	}

	@Override
	public boolean isContiguous() {
		return this.parent.isContiguous();
	}

	@Override
	public int maxFastWritableBytes() {
		return this.parent.maxFastWritableBytes();
	}

	@Override
	public int capacity() {
		return this.parent.capacity();
	}

	public PacketByteBuf capacity(int i) {
		this.parent.capacity(i);
		return this;
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
		return this.parent;
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

	public PacketByteBuf readerIndex(int i) {
		this.parent.readerIndex(i);
		return this;
	}

	@Override
	public int writerIndex() {
		return this.parent.writerIndex();
	}

	public PacketByteBuf writerIndex(int i) {
		this.parent.writerIndex(i);
		return this;
	}

	public PacketByteBuf setIndex(int i, int j) {
		this.parent.setIndex(i, j);
		return this;
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

	public PacketByteBuf clear() {
		this.parent.clear();
		return this;
	}

	public PacketByteBuf markReaderIndex() {
		this.parent.markReaderIndex();
		return this;
	}

	public PacketByteBuf resetReaderIndex() {
		this.parent.resetReaderIndex();
		return this;
	}

	public PacketByteBuf markWriterIndex() {
		this.parent.markWriterIndex();
		return this;
	}

	public PacketByteBuf resetWriterIndex() {
		this.parent.resetWriterIndex();
		return this;
	}

	public PacketByteBuf discardReadBytes() {
		this.parent.discardReadBytes();
		return this;
	}

	public PacketByteBuf discardSomeReadBytes() {
		this.parent.discardSomeReadBytes();
		return this;
	}

	public PacketByteBuf ensureWritable(int i) {
		this.parent.ensureWritable(i);
		return this;
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

	public PacketByteBuf getBytes(int i, ByteBuf byteBuf) {
		this.parent.getBytes(i, byteBuf);
		return this;
	}

	public PacketByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
		this.parent.getBytes(i, byteBuf, j);
		return this;
	}

	public PacketByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
		this.parent.getBytes(i, byteBuf, j, k);
		return this;
	}

	public PacketByteBuf getBytes(int i, byte[] bs) {
		this.parent.getBytes(i, bs);
		return this;
	}

	public PacketByteBuf getBytes(int i, byte[] bs, int j, int k) {
		this.parent.getBytes(i, bs, j, k);
		return this;
	}

	public PacketByteBuf getBytes(int i, ByteBuffer byteBuffer) {
		this.parent.getBytes(i, byteBuffer);
		return this;
	}

	public PacketByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
		this.parent.getBytes(i, outputStream, j);
		return this;
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

	public PacketByteBuf setBoolean(int i, boolean bl) {
		this.parent.setBoolean(i, bl);
		return this;
	}

	public PacketByteBuf setByte(int i, int j) {
		this.parent.setByte(i, j);
		return this;
	}

	public PacketByteBuf setShort(int i, int j) {
		this.parent.setShort(i, j);
		return this;
	}

	public PacketByteBuf setShortLE(int i, int j) {
		this.parent.setShortLE(i, j);
		return this;
	}

	public PacketByteBuf setMedium(int i, int j) {
		this.parent.setMedium(i, j);
		return this;
	}

	public PacketByteBuf setMediumLE(int i, int j) {
		this.parent.setMediumLE(i, j);
		return this;
	}

	public PacketByteBuf setInt(int i, int j) {
		this.parent.setInt(i, j);
		return this;
	}

	public PacketByteBuf setIntLE(int i, int j) {
		this.parent.setIntLE(i, j);
		return this;
	}

	public PacketByteBuf setLong(int i, long l) {
		this.parent.setLong(i, l);
		return this;
	}

	public PacketByteBuf setLongLE(int i, long l) {
		this.parent.setLongLE(i, l);
		return this;
	}

	public PacketByteBuf setChar(int i, int j) {
		this.parent.setChar(i, j);
		return this;
	}

	public PacketByteBuf setFloat(int i, float f) {
		this.parent.setFloat(i, f);
		return this;
	}

	public PacketByteBuf setDouble(int i, double d) {
		this.parent.setDouble(i, d);
		return this;
	}

	public PacketByteBuf setBytes(int i, ByteBuf byteBuf) {
		this.parent.setBytes(i, byteBuf);
		return this;
	}

	public PacketByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
		this.parent.setBytes(i, byteBuf, j);
		return this;
	}

	public PacketByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
		this.parent.setBytes(i, byteBuf, j, k);
		return this;
	}

	public PacketByteBuf setBytes(int i, byte[] bs) {
		this.parent.setBytes(i, bs);
		return this;
	}

	public PacketByteBuf setBytes(int i, byte[] bs, int j, int k) {
		this.parent.setBytes(i, bs, j, k);
		return this;
	}

	public PacketByteBuf setBytes(int i, ByteBuffer byteBuffer) {
		this.parent.setBytes(i, byteBuffer);
		return this;
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

	public PacketByteBuf setZero(int i, int j) {
		this.parent.setZero(i, j);
		return this;
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

	public PacketByteBuf readBytes(ByteBuf byteBuf) {
		this.parent.readBytes(byteBuf);
		return this;
	}

	public PacketByteBuf readBytes(ByteBuf byteBuf, int i) {
		this.parent.readBytes(byteBuf, i);
		return this;
	}

	public PacketByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
		this.parent.readBytes(byteBuf, i, j);
		return this;
	}

	public PacketByteBuf readBytes(byte[] bs) {
		this.parent.readBytes(bs);
		return this;
	}

	public PacketByteBuf readBytes(byte[] bs, int i, int j) {
		this.parent.readBytes(bs, i, j);
		return this;
	}

	public PacketByteBuf readBytes(ByteBuffer byteBuffer) {
		this.parent.readBytes(byteBuffer);
		return this;
	}

	public PacketByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
		this.parent.readBytes(outputStream, i);
		return this;
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

	public PacketByteBuf skipBytes(int i) {
		this.parent.skipBytes(i);
		return this;
	}

	public PacketByteBuf writeBoolean(boolean bl) {
		this.parent.writeBoolean(bl);
		return this;
	}

	public PacketByteBuf writeByte(int i) {
		this.parent.writeByte(i);
		return this;
	}

	public PacketByteBuf writeShort(int i) {
		this.parent.writeShort(i);
		return this;
	}

	public PacketByteBuf writeShortLE(int i) {
		this.parent.writeShortLE(i);
		return this;
	}

	public PacketByteBuf writeMedium(int i) {
		this.parent.writeMedium(i);
		return this;
	}

	public PacketByteBuf writeMediumLE(int i) {
		this.parent.writeMediumLE(i);
		return this;
	}

	public PacketByteBuf writeInt(int i) {
		this.parent.writeInt(i);
		return this;
	}

	public PacketByteBuf writeIntLE(int i) {
		this.parent.writeIntLE(i);
		return this;
	}

	public PacketByteBuf writeLong(long l) {
		this.parent.writeLong(l);
		return this;
	}

	public PacketByteBuf writeLongLE(long l) {
		this.parent.writeLongLE(l);
		return this;
	}

	public PacketByteBuf writeChar(int i) {
		this.parent.writeChar(i);
		return this;
	}

	public PacketByteBuf writeFloat(float f) {
		this.parent.writeFloat(f);
		return this;
	}

	public PacketByteBuf writeDouble(double d) {
		this.parent.writeDouble(d);
		return this;
	}

	public PacketByteBuf writeBytes(ByteBuf byteBuf) {
		this.parent.writeBytes(byteBuf);
		return this;
	}

	public PacketByteBuf writeBytes(ByteBuf byteBuf, int i) {
		this.parent.writeBytes(byteBuf, i);
		return this;
	}

	public PacketByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
		this.parent.writeBytes(byteBuf, i, j);
		return this;
	}

	public PacketByteBuf writeBytes(byte[] bs) {
		this.parent.writeBytes(bs);
		return this;
	}

	public PacketByteBuf writeBytes(byte[] bs, int i, int j) {
		this.parent.writeBytes(bs, i, j);
		return this;
	}

	public PacketByteBuf writeBytes(ByteBuffer byteBuffer) {
		this.parent.writeBytes(byteBuffer);
		return this;
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

	public PacketByteBuf writeZero(int i) {
		this.parent.writeZero(i);
		return this;
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

	public PacketByteBuf retain(int i) {
		this.parent.retain(i);
		return this;
	}

	public PacketByteBuf retain() {
		this.parent.retain();
		return this;
	}

	public PacketByteBuf touch() {
		this.parent.touch();
		return this;
	}

	public PacketByteBuf touch(Object object) {
		this.parent.touch(object);
		return this;
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

	/**
	 * A functional interface to read a value from {@link PacketByteBuf}.
	 */
	@FunctionalInterface
	public interface PacketReader<T> extends Function<PacketByteBuf, T> {
		default PacketByteBuf.PacketReader<Optional<T>> asOptional() {
			return buf -> buf.readOptional(this);
		}
	}

	/**
	 * A functional interface to write a value to {@link PacketByteBuf}.
	 */
	@FunctionalInterface
	public interface PacketWriter<T> extends BiConsumer<PacketByteBuf, T> {
		default PacketByteBuf.PacketWriter<Optional<T>> asOptional() {
			return (buf, value) -> buf.writeOptional(value, this);
		}
	}
}
