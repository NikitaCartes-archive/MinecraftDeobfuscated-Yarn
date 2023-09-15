package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

/**
 * Represents an NBT compound object. This mutable object holds unordered key-value pairs
 * with distinct case-sensitive string keys. This can effectively be used like a
 * {@code HashMap<String, NbtElement>}. Note that this <strong>does not</strong> implement
 * {@link java.util.Map}. Its type is {@value NbtElement#COMPOUND_TYPE}. To get the compound
 * as a map, use {@link #toMap()}.
 * 
 * <p>There are two ways to use this compound; one is to create NBT instances yourself and use
 * {@link #get(String)} or {@link #put(String, NbtElement)}. Manual casting is required in
 * this case. The other, easier way is to use methods with type names, such as
 * {@link #getInt(String)} or {@link #putInt(String, int)}. Where applicable, these methods
 * return and accept Java types (e.g. {@code int}, {@code long[]}) instead of {@link NbtElement}
 * subclasses. Note that there is no {@code putCompound} method, since you can just use the
 * put method. These getters also have the advantage of providing type safety, because if
 * type mismatch occurs or there is no such element in the compound, it returns the default
 * value for that type instead of throwing or returning {@code null}.
 */
public class NbtCompound implements NbtElement {
	public static final Codec<NbtCompound> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
		NbtElement nbtElement = dynamic.convert(NbtOps.INSTANCE).getValue();
		return nbtElement instanceof NbtCompound ? DataResult.success((NbtCompound)nbtElement) : DataResult.error(() -> "Not a compound tag: " + nbtElement);
	}, nbt -> new Dynamic<>(NbtOps.INSTANCE, nbt));
	private static final int SIZE = 48;
	private static final int field_41719 = 32;
	public static final NbtType<NbtCompound> TYPE = new NbtType.OfVariableSize<NbtCompound>() {
		public NbtCompound read(DataInput dataInput, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.pushStack();

			NbtCompound var3;
			try {
				var3 = readCompound(dataInput, nbtTagSizeTracker);
			} finally {
				nbtTagSizeTracker.popStack();
			}

			return var3;
		}

		private static NbtCompound readCompound(DataInput input, NbtTagSizeTracker tracker) throws IOException {
			tracker.add(48L);
			Map<String, NbtElement> map = Maps.<String, NbtElement>newHashMap();

			byte b;
			while ((b = input.readByte()) != 0) {
				String string = readString(input, tracker);
				NbtElement nbtElement = NbtCompound.read(NbtTypes.byId(b), string, input, tracker);
				if (map.put(string, nbtElement) == null) {
					tracker.add(36L);
				}
			}

			return new NbtCompound(map);
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtTagSizeTracker tracker) throws IOException {
			tracker.pushStack();

			NbtScanner.Result var4;
			try {
				var4 = scanCompound(input, visitor, tracker);
			} finally {
				tracker.popStack();
			}

			return var4;
		}

		private static NbtScanner.Result scanCompound(DataInput input, NbtScanner visitor, NbtTagSizeTracker tracker) throws IOException {
			tracker.add(48L);

			byte b;
			label35:
			while ((b = input.readByte()) != 0) {
				NbtType<?> nbtType = NbtTypes.byId(b);
				switch (visitor.visitSubNbtType(nbtType)) {
					case HALT:
						return NbtScanner.Result.HALT;
					case BREAK:
						NbtString.skip(input);
						nbtType.skip(input, tracker);
						break label35;
					case SKIP:
						NbtString.skip(input);
						nbtType.skip(input, tracker);
						break;
					default:
						String string = readString(input, tracker);
						switch (visitor.startSubNbt(nbtType, string)) {
							case HALT:
								return NbtScanner.Result.HALT;
							case BREAK:
								nbtType.skip(input, tracker);
								break label35;
							case SKIP:
								nbtType.skip(input, tracker);
								break;
							default:
								tracker.add(36L);
								switch (nbtType.doAccept(input, visitor, tracker)) {
									case HALT:
										return NbtScanner.Result.HALT;
									case BREAK:
								}
						}
				}
			}

			if (b != 0) {
				while ((b = input.readByte()) != 0) {
					NbtString.skip(input);
					NbtTypes.byId(b).skip(input, tracker);
				}
			}

			return visitor.endNested();
		}

		private static String readString(DataInput input, NbtTagSizeTracker tracker) throws IOException {
			String string = input.readUTF();
			tracker.add(28L);
			tracker.add(2L, (long)string.length());
			return string;
		}

		@Override
		public void skip(DataInput input, NbtTagSizeTracker tracker) throws IOException {
			tracker.pushStack();

			byte b;
			try {
				while ((b = input.readByte()) != 0) {
					NbtString.skip(input);
					NbtTypes.byId(b).skip(input, tracker);
				}
			} finally {
				tracker.popStack();
			}
		}

		@Override
		public String getCrashReportName() {
			return "COMPOUND";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Compound";
		}
	};
	private final Map<String, NbtElement> entries;

	protected NbtCompound(Map<String, NbtElement> entries) {
		this.entries = entries;
	}

	public NbtCompound() {
		this(Maps.<String, NbtElement>newHashMap());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		for (String string : this.entries.keySet()) {
			NbtElement nbtElement = (NbtElement)this.entries.get(string);
			write(string, nbtElement, output);
		}

		output.writeByte(0);
	}

	@Override
	public int getSizeInBytes() {
		int i = 48;

		for (Entry<String, NbtElement> entry : this.entries.entrySet()) {
			i += 28 + 2 * ((String)entry.getKey()).length();
			i += 36;
			i += ((NbtElement)entry.getValue()).getSizeInBytes();
		}

		return i;
	}

	/**
	 * {@return the set of keys in this compound}
	 */
	public Set<String> getKeys() {
		return this.entries.keySet();
	}

	@Override
	public byte getType() {
		return NbtElement.COMPOUND_TYPE;
	}

	@Override
	public NbtType<NbtCompound> getNbtType() {
		return TYPE;
	}

	/**
	 * {@return the size of this compound}
	 */
	public int getSize() {
		return this.entries.size();
	}

	/**
	 * Puts an element to this compound.
	 * 
	 * @return the previous value, or {@code null} if there was none
	 * @see #get(String)
	 */
	@Nullable
	public NbtElement put(String key, NbtElement element) {
		return (NbtElement)this.entries.put(key, element);
	}

	/**
	 * Puts a {@code byte} to this compound.
	 * 
	 * @see #getByte(String)
	 */
	public void putByte(String key, byte value) {
		this.entries.put(key, NbtByte.of(value));
	}

	/**
	 * Puts a {@code short} to this compound.
	 * 
	 * @see #getShort(String)
	 */
	public void putShort(String key, short value) {
		this.entries.put(key, NbtShort.of(value));
	}

	/**
	 * Puts an {@code int} to this compound.
	 * 
	 * @see #getInt(String)
	 */
	public void putInt(String key, int value) {
		this.entries.put(key, NbtInt.of(value));
	}

	/**
	 * Puts a {@code long} to this compound.
	 * 
	 * @see #getLong(String)
	 */
	public void putLong(String key, long value) {
		this.entries.put(key, NbtLong.of(value));
	}

	/**
	 * Puts a {@link UUID}'s NBT representation to this compound.
	 * 
	 * @see NbtHelper#fromUuid(UUID)
	 * @see #containsUuid(String)
	 * @see #getUuid(String)
	 */
	public void putUuid(String key, UUID value) {
		this.entries.put(key, NbtHelper.fromUuid(value));
	}

	/**
	 * {@return a {@link UUID} from its NBT representation in this compound}
	 * 
	 * @apiNote Unlike other specialized getters, this method can throw unchecked exceptions.
	 * It is therefore recommended to call {@link #containsUuid(String)} before getting the
	 * UUID.
	 * 
	 * @throws IllegalArgumentException if there is no value with the key or the value
	 * associated with the key is not a valid
	 * NBT representation of a UUID
	 * @see NbtHelper#toUuid(NbtIntArray)
	 * @see #containsUuid(String)
	 * @see #putUuid(String, UUID)
	 */
	public UUID getUuid(String key) {
		return NbtHelper.toUuid(this.get(key));
	}

	/**
	 * Returns {@code true} if this {@code NbtCompound} contains a valid UUID representation associated with the given key.
	 * A valid UUID is represented by an int array of length 4.
	 */
	public boolean containsUuid(String key) {
		NbtElement nbtElement = this.get(key);
		return nbtElement != null && nbtElement.getNbtType() == NbtIntArray.TYPE && ((NbtIntArray)nbtElement).getIntArray().length == 4;
	}

	/**
	 * Puts a {@code float} to this compound.
	 * 
	 * @see #getFloat(String)
	 */
	public void putFloat(String key, float value) {
		this.entries.put(key, NbtFloat.of(value));
	}

	/**
	 * Puts a {@code double} to this compound.
	 * 
	 * @see #getDouble(String)
	 */
	public void putDouble(String key, double value) {
		this.entries.put(key, NbtDouble.of(value));
	}

	/**
	 * Puts a {@link String} to this compound.
	 * 
	 * @see #getString(String)
	 */
	public void putString(String key, String value) {
		this.entries.put(key, NbtString.of(value));
	}

	/**
	 * Puts a byte array to this compound. This does not copy the array.
	 * 
	 * @see #getByteArray(String)
	 * @see #putByteArray(String, List)
	 */
	public void putByteArray(String key, byte[] value) {
		this.entries.put(key, new NbtByteArray(value));
	}

	/**
	 * Puts a list of bytes to this compound. This copies the list.
	 * 
	 * @see #getByteArray(String)
	 * @see #putByteArray(String, byte[])
	 */
	public void putByteArray(String key, List<Byte> value) {
		this.entries.put(key, new NbtByteArray(value));
	}

	/**
	 * Puts an int array to this compound. This does not copy the array.
	 * 
	 * @see #getIntArray(String)
	 * @see #putIntArray(String, List)
	 */
	public void putIntArray(String key, int[] value) {
		this.entries.put(key, new NbtIntArray(value));
	}

	/**
	 * Puts a list of integers to this compound. This copies the list.
	 * 
	 * @see #getIntArray(String)
	 * @see #putIntArray(String, int[])
	 */
	public void putIntArray(String key, List<Integer> value) {
		this.entries.put(key, new NbtIntArray(value));
	}

	/**
	 * Puts a long array to this compound. This does not copy the array.
	 * 
	 * @see #getLongArray(String)
	 * @see #putLongArray(String, List)
	 */
	public void putLongArray(String key, long[] value) {
		this.entries.put(key, new NbtLongArray(value));
	}

	/**
	 * Puts a list of longs to this compound. This copies the list.
	 * 
	 * @see #getLongArray(String)
	 * @see #putLongArray(String, long[])
	 */
	public void putLongArray(String key, List<Long> value) {
		this.entries.put(key, new NbtLongArray(value));
	}

	/**
	 * Puts a {@code boolean} to this compound. The value is stored as {@link NbtByte}.
	 * 
	 * @see #getBoolean(String)
	 */
	public void putBoolean(String key, boolean value) {
		this.entries.put(key, NbtByte.of(value));
	}

	/**
	 * {@return the element associated with the key from this compound, or
	 * {@code null} if there is none}
	 * 
	 * @apiNote This method does not provide type safety; if the type is known, it is
	 * recommended to use other type-specific methods instead.
	 * 
	 * @see #put(String, NbtElement)
	 */
	@Nullable
	public NbtElement get(String key) {
		return (NbtElement)this.entries.get(key);
	}

	/**
	 * Gets the {@linkplain NbtElement#getType NBT type} of the element stored at the specified key.
	 * 
	 * @return the element NBT type, or {@link NbtElement#END_TYPE} if it does not exist
	 */
	public byte getType(String key) {
		NbtElement nbtElement = (NbtElement)this.entries.get(key);
		return nbtElement == null ? NbtElement.END_TYPE : nbtElement.getType();
	}

	/**
	 * Determines whether the NBT compound object contains the specified key.
	 * 
	 * @return {@code true} if the key exists, else {@code false}
	 */
	public boolean contains(String key) {
		return this.entries.containsKey(key);
	}

	/**
	 * Returns whether the NBT compound object contains an element of the specified type at the specified key.
	 * <p>
	 * The type restriction can also be {@link NbtElement#NUMBER_TYPE NUMBER_TYPE}, which only allows any type of number.
	 * 
	 * @return {@code true} if the key exists and the element type is equivalent to the given {@code type}, else {@code false}
	 */
	public boolean contains(String key, int type) {
		int i = this.getType(key);
		if (i == type) {
			return true;
		} else {
			return type != NbtElement.NUMBER_TYPE
				? false
				: i == NbtElement.BYTE_TYPE
					|| i == NbtElement.SHORT_TYPE
					|| i == NbtElement.INT_TYPE
					|| i == NbtElement.LONG_TYPE
					|| i == NbtElement.FLOAT_TYPE
					|| i == NbtElement.DOUBLE_TYPE;
		}
	}

	/**
	 * {@return the {@code byte} associated with {@code key}, or {@code 0} if there is no number
	 * stored with the key}
	 * 
	 * <p>If a non-byte numeric value is stored, this will cast the value.
	 * 
	 * @see #putByte(String, byte)
	 * @see AbstractNbtNumber#byteValue()
	 */
	public byte getByte(String key) {
		try {
			if (this.contains(key, NbtElement.NUMBER_TYPE)) {
				return ((AbstractNbtNumber)this.entries.get(key)).byteValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	/**
	 * {@return the {@code short} associated with {@code key}, or {@code 0} if there is no number
	 * stored with the key}
	 * 
	 * <p>If a non-short numeric value is stored, this will cast the value.
	 * 
	 * @see #putShort(String, short)
	 * @see AbstractNbtNumber#shortValue()
	 */
	public short getShort(String key) {
		try {
			if (this.contains(key, NbtElement.NUMBER_TYPE)) {
				return ((AbstractNbtNumber)this.entries.get(key)).shortValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	/**
	 * {@return the {@code int} associated with {@code key}, or {@code 0} if there is no number
	 * stored with the key}
	 * 
	 * <p>If a non-integer numeric value is stored, this will cast the value.
	 * 
	 * @see #putInt(String, int)
	 * @see AbstractNbtNumber#intValue()
	 */
	public int getInt(String key) {
		try {
			if (this.contains(key, NbtElement.NUMBER_TYPE)) {
				return ((AbstractNbtNumber)this.entries.get(key)).intValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	/**
	 * {@return the {@code long} associated with {@code key}, or {@code 0L} if there is no number
	 * stored with the key}
	 * 
	 * <p>If a non-long numeric value is stored, this will cast the value.
	 * 
	 * @see #putLong(String, long)
	 * @see AbstractNbtNumber#longValue()
	 */
	public long getLong(String key) {
		try {
			if (this.contains(key, NbtElement.NUMBER_TYPE)) {
				return ((AbstractNbtNumber)this.entries.get(key)).longValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0L;
	}

	/**
	 * {@return the {@code float} associated with {@code key}, or {@code 0.0f} if there is
	 * no number stored with the key}
	 * 
	 * <p>If a non-float numeric value is stored, this will cast the value.
	 * 
	 * @see #putFloat(String, float)
	 * @see AbstractNbtNumber#floatValue()
	 */
	public float getFloat(String key) {
		try {
			if (this.contains(key, NbtElement.NUMBER_TYPE)) {
				return ((AbstractNbtNumber)this.entries.get(key)).floatValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0F;
	}

	/**
	 * {@return the {@code double} associated with {@code key}, or {@code 0.0} if there is
	 * no number stored with the key}
	 * 
	 * <p>If a non-double numeric value is stored, this will cast the value.
	 * 
	 * @see #putDouble(String, double)
	 * @see AbstractNbtNumber#doubleValue()
	 */
	public double getDouble(String key) {
		try {
			if (this.contains(key, NbtElement.NUMBER_TYPE)) {
				return ((AbstractNbtNumber)this.entries.get(key)).doubleValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0;
	}

	/**
	 * {@return the {@link String} associated with {@code key}, or an empty string if there is no
	 * string stored with the key}
	 * 
	 * @see #putString(String, String)
	 * @see NbtElement#asString()
	 */
	public String getString(String key) {
		try {
			if (this.contains(key, NbtElement.STRING_TYPE)) {
				return ((NbtElement)this.entries.get(key)).asString();
			}
		} catch (ClassCastException var3) {
		}

		return "";
	}

	/**
	 * {@return the byte array associated with {@code key}, or an empty byte array if there is no
	 * byte array stored with the key}
	 * 
	 * @apiNote Modifying the returned array also modifies the NBT byte array.
	 * 
	 * @see #putByteArray(String, byte[])
	 * @see NbtByteArray#getByteArray()
	 */
	public byte[] getByteArray(String key) {
		try {
			if (this.contains(key, NbtElement.BYTE_ARRAY_TYPE)) {
				return ((NbtByteArray)this.entries.get(key)).getByteArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, NbtByteArray.TYPE, var3));
		}

		return new byte[0];
	}

	/**
	 * {@return the int array associated with {@code key}, or an empty int array if there is no
	 * int array stored with the key}
	 * 
	 * @apiNote Modifying the returned array also modifies the NBT int array.
	 * 
	 * @see #putIntArray(String, int[])
	 * @see NbtIntArray#getIntArray()
	 */
	public int[] getIntArray(String key) {
		try {
			if (this.contains(key, NbtElement.INT_ARRAY_TYPE)) {
				return ((NbtIntArray)this.entries.get(key)).getIntArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, NbtIntArray.TYPE, var3));
		}

		return new int[0];
	}

	/**
	 * {@return the long array associated with {@code key}, or an empty long array if there is no
	 * long array stored with the key}
	 * 
	 * @apiNote Modifying the returned array also modifies the NBT long array.
	 * 
	 * @see #putLongArray(String, long[])
	 * @see NbtLongArray#getLongArray()
	 */
	public long[] getLongArray(String key) {
		try {
			if (this.contains(key, NbtElement.LONG_ARRAY_TYPE)) {
				return ((NbtLongArray)this.entries.get(key)).getLongArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, NbtLongArray.TYPE, var3));
		}

		return new long[0];
	}

	/**
	 * {@return the compound associated with {@code key}, or an empty compound if there is no
	 * compound stored with the key}
	 * 
	 * @see #put(String, NbtElement)
	 */
	public NbtCompound getCompound(String key) {
		try {
			if (this.contains(key, NbtElement.COMPOUND_TYPE)) {
				return (NbtCompound)this.entries.get(key);
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, TYPE, var3));
		}

		return new NbtCompound();
	}

	/**
	 * {@return the list associated with {@code key}, or an empty list if there is no
	 * list stored with the key and the type}
	 * 
	 * @see #put(String, NbtElement)
	 * 
	 * @param type the expected held type of the list
	 */
	public NbtList getList(String key, int type) {
		try {
			if (this.getType(key) == NbtElement.LIST_TYPE) {
				NbtList nbtList = (NbtList)this.entries.get(key);
				if (!nbtList.isEmpty() && nbtList.getHeldType() != type) {
					return new NbtList();
				}

				return nbtList;
			}
		} catch (ClassCastException var4) {
			throw new CrashException(this.createCrashReport(key, NbtList.TYPE, var4));
		}

		return new NbtList();
	}

	/**
	 * {@return the boolean value stored with the {@code key}}
	 * 
	 * @implNote Since NBT does not have a boolean type, {@link NbtByte} is used instead. This
	 * method returns {@code true} for any values which, after casting to {@code byte} as
	 * described at {@link #getByte(String)}, is not {@code 0}. Since all non-numeric values
	 * become {@code 0} during casting to bytes, this method returns {@code false} for those
	 * as well. This includes values often considered truthy in other languages, such as a
	 * non-empty string or list.
	 */
	public boolean getBoolean(String key) {
		return this.getByte(key) != 0;
	}

	/**
	 * Removes the entry with the specified {@code key}. Does nothing if there is none.
	 */
	public void remove(String key) {
		this.entries.remove(key);
	}

	@Override
	public String toString() {
		return this.asString();
	}

	/**
	 * {@return whether the compound has no entries}
	 */
	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	private CrashReport createCrashReport(String key, NbtType<?> reader, ClassCastException exception) {
		CrashReport crashReport = CrashReport.create(exception, "Reading NBT data");
		CrashReportSection crashReportSection = crashReport.addElement("Corrupt NBT tag", 1);
		crashReportSection.add("Tag type found", (CrashCallable<String>)(() -> ((NbtElement)this.entries.get(key)).getNbtType().getCrashReportName()));
		crashReportSection.add("Tag type expected", reader::getCrashReportName);
		crashReportSection.add("Tag name", key);
		return crashReport;
	}

	public NbtCompound copy() {
		Map<String, NbtElement> map = Maps.<String, NbtElement>newHashMap(Maps.transformValues(this.entries, NbtElement::copy));
		return new NbtCompound(map);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtCompound && Objects.equals(this.entries, ((NbtCompound)o).entries);
	}

	public int hashCode() {
		return this.entries.hashCode();
	}

	private static void write(String key, NbtElement element, DataOutput output) throws IOException {
		output.writeByte(element.getType());
		if (element.getType() != 0) {
			output.writeUTF(key);
			element.write(output);
		}
	}

	static NbtElement read(NbtType<?> reader, String key, DataInput input, NbtTagSizeTracker nbtTagSizeTracker) {
		try {
			return reader.read(input, nbtTagSizeTracker);
		} catch (IOException var7) {
			CrashReport crashReport = CrashReport.create(var7, "Loading NBT data");
			CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
			crashReportSection.add("Tag name", key);
			crashReportSection.add("Tag type", reader.getCrashReportName());
			throw new CrashException(crashReport);
		}
	}

	/**
	 * Merges the entries of {@code source} to this compound. The passed compound will not
	 * be modified. If both compounds contain a compound with the same key, they will be
	 * merged; otherwise the values of this compound will be overwritten.
	 * 
	 * @return this compound with entries merged
	 */
	public NbtCompound copyFrom(NbtCompound source) {
		for (String string : source.entries.keySet()) {
			NbtElement nbtElement = (NbtElement)source.entries.get(string);
			if (nbtElement.getType() == NbtElement.COMPOUND_TYPE) {
				if (this.contains(string, NbtElement.COMPOUND_TYPE)) {
					NbtCompound nbtCompound = this.getCompound(string);
					nbtCompound.copyFrom((NbtCompound)nbtElement);
				} else {
					this.put(string, nbtElement.copy());
				}
			} else {
				this.put(string, nbtElement.copy());
			}
		}

		return this;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitCompound(this);
	}

	/**
	 * {@return the compound as an unmodifiable map}
	 * 
	 * <p>Changes to this compound will be propagated to the returned map.
	 */
	protected Map<String, NbtElement> toMap() {
		return Collections.unmodifiableMap(this.entries);
	}

	@Override
	public NbtScanner.Result doAccept(NbtScanner visitor) {
		for (Entry<String, NbtElement> entry : this.entries.entrySet()) {
			NbtElement nbtElement = (NbtElement)entry.getValue();
			NbtType<?> nbtType = nbtElement.getNbtType();
			NbtScanner.NestedResult nestedResult = visitor.visitSubNbtType(nbtType);
			switch (nestedResult) {
				case HALT:
					return NbtScanner.Result.HALT;
				case BREAK:
					return visitor.endNested();
				case SKIP:
					break;
				default:
					nestedResult = visitor.startSubNbt(nbtType, (String)entry.getKey());
					switch (nestedResult) {
						case HALT:
							return NbtScanner.Result.HALT;
						case BREAK:
							return visitor.endNested();
						case SKIP:
							break;
						default:
							NbtScanner.Result result = nbtElement.doAccept(visitor);
							switch (result) {
								case HALT:
									return NbtScanner.Result.HALT;
								case BREAK:
									return visitor.endNested();
							}
					}
			}
		}

		return visitor.endNested();
	}
}
