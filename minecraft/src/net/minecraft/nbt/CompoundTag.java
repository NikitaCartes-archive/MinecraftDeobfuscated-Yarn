package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompoundTag implements Tag {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
	public static final TagReader<CompoundTag> READER = new TagReader<CompoundTag>() {
		public CompoundTag method_23240(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(384L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				Map<String, Tag> map = Maps.<String, Tag>newHashMap();

				byte b;
				while ((b = CompoundTag.readByte(dataInput, positionTracker)) != 0) {
					String string = CompoundTag.readString(dataInput, positionTracker);
					positionTracker.add((long)(224 + 16 * string.length()));
					Tag tag = CompoundTag.read(TagReaders.of(b), string, dataInput, i + 1, positionTracker);
					if (map.put(string, tag) != null) {
						positionTracker.add(288L);
					}
				}

				return new CompoundTag(map);
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
	private final Map<String, Tag> tags;

	private CompoundTag(Map<String, Tag> map) {
		this.tags = map;
	}

	public CompoundTag() {
		this(Maps.<String, Tag>newHashMap());
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		for (String string : this.tags.keySet()) {
			Tag tag = (Tag)this.tags.get(string);
			write(string, tag, dataOutput);
		}

		dataOutput.writeByte(0);
	}

	public Set<String> getKeys() {
		return this.tags.keySet();
	}

	@Override
	public byte getType() {
		return 10;
	}

	@Override
	public TagReader<CompoundTag> getReader() {
		return READER;
	}

	public int getSize() {
		return this.tags.size();
	}

	@Nullable
	public Tag put(String string, Tag tag) {
		return (Tag)this.tags.put(string, tag);
	}

	public void putByte(String string, byte b) {
		this.tags.put(string, ByteTag.of(b));
	}

	public void putShort(String string, short s) {
		this.tags.put(string, ShortTag.of(s));
	}

	public void putInt(String string, int i) {
		this.tags.put(string, IntTag.of(i));
	}

	public void putLong(String string, long l) {
		this.tags.put(string, LongTag.of(l));
	}

	public void putUuid(String string, UUID uUID) {
		this.putLong(string + "Most", uUID.getMostSignificantBits());
		this.putLong(string + "Least", uUID.getLeastSignificantBits());
	}

	public UUID getUuid(String string) {
		return new UUID(this.getLong(string + "Most"), this.getLong(string + "Least"));
	}

	public boolean containsUuid(String string) {
		return this.contains(string + "Most", 99) && this.contains(string + "Least", 99);
	}

	public void removeUuid(String string) {
		this.remove(string + "Most");
		this.remove(string + "Least");
	}

	public void putFloat(String string, float f) {
		this.tags.put(string, FloatTag.of(f));
	}

	public void putDouble(String string, double d) {
		this.tags.put(string, DoubleTag.of(d));
	}

	public void putString(String string, String string2) {
		this.tags.put(string, StringTag.of(string2));
	}

	public void putByteArray(String string, byte[] bs) {
		this.tags.put(string, new ByteArrayTag(bs));
	}

	public void putIntArray(String string, int[] is) {
		this.tags.put(string, new IntArrayTag(is));
	}

	public void putIntArray(String string, List<Integer> list) {
		this.tags.put(string, new IntArrayTag(list));
	}

	public void putLongArray(String string, long[] ls) {
		this.tags.put(string, new LongArrayTag(ls));
	}

	public void putLongArray(String string, List<Long> list) {
		this.tags.put(string, new LongArrayTag(list));
	}

	public void putBoolean(String string, boolean bl) {
		this.tags.put(string, ByteTag.of(bl));
	}

	@Nullable
	public Tag get(String string) {
		return (Tag)this.tags.get(string);
	}

	public byte getType(String string) {
		Tag tag = (Tag)this.tags.get(string);
		return tag == null ? 0 : tag.getType();
	}

	public boolean contains(String string) {
		return this.tags.containsKey(string);
	}

	public boolean contains(String string, int i) {
		int j = this.getType(string);
		if (j == i) {
			return true;
		} else {
			return i != 99 ? false : j == 1 || j == 2 || j == 3 || j == 4 || j == 5 || j == 6;
		}
	}

	public byte getByte(String string) {
		try {
			if (this.contains(string, 99)) {
				return ((AbstractNumberTag)this.tags.get(string)).getByte();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public short getShort(String string) {
		try {
			if (this.contains(string, 99)) {
				return ((AbstractNumberTag)this.tags.get(string)).getShort();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public int getInt(String string) {
		try {
			if (this.contains(string, 99)) {
				return ((AbstractNumberTag)this.tags.get(string)).getInt();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public long getLong(String string) {
		try {
			if (this.contains(string, 99)) {
				return ((AbstractNumberTag)this.tags.get(string)).getLong();
			}
		} catch (ClassCastException var3) {
		}

		return 0L;
	}

	public float getFloat(String string) {
		try {
			if (this.contains(string, 99)) {
				return ((AbstractNumberTag)this.tags.get(string)).getFloat();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0F;
	}

	public double getDouble(String string) {
		try {
			if (this.contains(string, 99)) {
				return ((AbstractNumberTag)this.tags.get(string)).getDouble();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0;
	}

	public String getString(String string) {
		try {
			if (this.contains(string, 8)) {
				return ((Tag)this.tags.get(string)).asString();
			}
		} catch (ClassCastException var3) {
		}

		return "";
	}

	public byte[] getByteArray(String string) {
		try {
			if (this.contains(string, 7)) {
				return ((ByteArrayTag)this.tags.get(string)).getByteArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(string, ByteArrayTag.READER, var3));
		}

		return new byte[0];
	}

	public int[] getIntArray(String string) {
		try {
			if (this.contains(string, 11)) {
				return ((IntArrayTag)this.tags.get(string)).getIntArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(string, IntArrayTag.READER, var3));
		}

		return new int[0];
	}

	public long[] getLongArray(String string) {
		try {
			if (this.contains(string, 12)) {
				return ((LongArrayTag)this.tags.get(string)).getLongArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(string, LongArrayTag.READER, var3));
		}

		return new long[0];
	}

	public CompoundTag getCompound(String string) {
		try {
			if (this.contains(string, 10)) {
				return (CompoundTag)this.tags.get(string);
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(string, READER, var3));
		}

		return new CompoundTag();
	}

	public ListTag getList(String string, int i) {
		try {
			if (this.getType(string) == 9) {
				ListTag listTag = (ListTag)this.tags.get(string);
				if (!listTag.isEmpty() && listTag.getElementType() != i) {
					return new ListTag();
				}

				return listTag;
			}
		} catch (ClassCastException var4) {
			throw new CrashException(this.createCrashReport(string, ListTag.READER, var4));
		}

		return new ListTag();
	}

	public boolean getBoolean(String string) {
		return this.getByte(string) != 0;
	}

	public void remove(String string) {
		this.tags.remove(string);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");
		Collection<String> collection = this.tags.keySet();
		if (LOGGER.isDebugEnabled()) {
			List<String> list = Lists.<String>newArrayList(this.tags.keySet());
			Collections.sort(list);
			collection = list;
		}

		for (String string : collection) {
			if (stringBuilder.length() != 1) {
				stringBuilder.append(',');
			}

			stringBuilder.append(escapeTagKey(string)).append(':').append(this.tags.get(string));
		}

		return stringBuilder.append('}').toString();
	}

	public boolean isEmpty() {
		return this.tags.isEmpty();
	}

	private CrashReport createCrashReport(String string, TagReader<?> tagReader, ClassCastException classCastException) {
		CrashReport crashReport = CrashReport.create(classCastException, "Reading NBT data");
		CrashReportSection crashReportSection = crashReport.addElement("Corrupt NBT tag", 1);
		crashReportSection.add("Tag type found", (CrashCallable<String>)(() -> ((Tag)this.tags.get(string)).getReader().getCrashReportName()));
		crashReportSection.add("Tag type expected", tagReader::getCrashReportName);
		crashReportSection.add("Tag name", string);
		return crashReport;
	}

	public CompoundTag method_10553() {
		Map<String, Tag> map = Maps.<String, Tag>newHashMap(Maps.transformValues(this.tags, Tag::copy));
		return new CompoundTag(map);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)object).tags);
	}

	public int hashCode() {
		return this.tags.hashCode();
	}

	private static void write(String string, Tag tag, DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(tag.getType());
		if (tag.getType() != 0) {
			dataOutput.writeUTF(string);
			tag.write(dataOutput);
		}
	}

	private static byte readByte(DataInput dataInput, PositionTracker positionTracker) throws IOException {
		return dataInput.readByte();
	}

	private static String readString(DataInput dataInput, PositionTracker positionTracker) throws IOException {
		return dataInput.readUTF();
	}

	private static Tag read(TagReader<?> tagReader, String string, DataInput dataInput, int i, PositionTracker positionTracker) {
		try {
			return tagReader.read(dataInput, i, positionTracker);
		} catch (IOException var8) {
			CrashReport crashReport = CrashReport.create(var8, "Loading NBT data");
			CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
			crashReportSection.add("Tag name", string);
			crashReportSection.add("Tag type", tagReader.getCrashReportName());
			throw new CrashException(crashReport);
		}
	}

	public CompoundTag copyFrom(CompoundTag compoundTag) {
		for (String string : compoundTag.tags.keySet()) {
			Tag tag = (Tag)compoundTag.tags.get(string);
			if (tag.getType() == 10) {
				if (this.contains(string, 10)) {
					CompoundTag compoundTag2 = this.getCompound(string);
					compoundTag2.copyFrom((CompoundTag)tag);
				} else {
					this.put(string, tag.copy());
				}
			} else {
				this.put(string, tag.copy());
			}
		}

		return this;
	}

	protected static String escapeTagKey(String string) {
		return PATTERN.matcher(string).matches() ? string : StringTag.escape(string);
	}

	protected static Text prettyPrintTagKey(String string) {
		if (PATTERN.matcher(string).matches()) {
			return new LiteralText(string).formatted(AQUA);
		} else {
			String string2 = StringTag.escape(string);
			String string3 = string2.substring(0, 1);
			Text text = new LiteralText(string2.substring(1, string2.length() - 1)).formatted(AQUA);
			return new LiteralText(string3).append(text).append(string3);
		}
	}

	@Override
	public Text toText(String string, int i) {
		if (this.tags.isEmpty()) {
			return new LiteralText("{}");
		} else {
			Text text = new LiteralText("{");
			Collection<String> collection = this.tags.keySet();
			if (LOGGER.isDebugEnabled()) {
				List<String> list = Lists.<String>newArrayList(this.tags.keySet());
				Collections.sort(list);
				collection = list;
			}

			if (!string.isEmpty()) {
				text.append("\n");
			}

			Iterator<String> iterator = collection.iterator();

			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				Text text2 = new LiteralText(Strings.repeat(string, i + 1))
					.append(prettyPrintTagKey(string2))
					.append(String.valueOf(':'))
					.append(" ")
					.append(((Tag)this.tags.get(string2)).toText(string, i + 1));
				if (iterator.hasNext()) {
					text2.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
				}

				text.append(text2);
			}

			if (!string.isEmpty()) {
				text.append("\n").append(Strings.repeat(string, i));
			}

			text.append("}");
			return text;
		}
	}
}
