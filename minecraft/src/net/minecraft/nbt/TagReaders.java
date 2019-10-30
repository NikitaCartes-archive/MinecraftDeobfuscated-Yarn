package net.minecraft.nbt;

public class TagReaders {
	private static final TagReader<?>[] VALUES = new TagReader[]{
		EndTag.READER,
		ByteTag.READER,
		ShortTag.READER,
		IntTag.READER,
		LongTag.READER,
		FloatTag.READER,
		DoubleTag.READER,
		ByteArrayTag.READER,
		StringTag.READER,
		ListTag.READER,
		CompoundTag.READER,
		IntArrayTag.READER,
		LongArrayTag.READER
	};

	public static TagReader<?> of(int type) {
		return type >= 0 && type < VALUES.length ? VALUES[type] : TagReader.createInvalid(type);
	}
}
