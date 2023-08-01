package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

/**
 * A set of utility functions for reading, writing, and scanning NBT files.
 */
public class NbtIo {
	/**
	 * Reads an NBT compound from Gzip-compressed {@code file}.
	 * 
	 * @return the NBT compound from the file
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 * @see #readCompressed(InputStream)
	 */
	public static NbtCompound readCompressed(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);

		NbtCompound var2;
		try {
			var2 = readCompressed(inputStream);
		} catch (Throwable var5) {
			try {
				inputStream.close();
			} catch (Throwable var4) {
				var5.addSuppressed(var4);
			}

			throw var5;
		}

		inputStream.close();
		return var2;
	}

	/**
	 * {@return a new input stream that decompresses the input {@code stream}}
	 */
	private static DataInputStream decompress(InputStream stream) throws IOException {
		return new DataInputStream(new FixedBufferInputStream(new GZIPInputStream(stream)));
	}

	/**
	 * Reads an NBT compound from Gzip-compressed {@code stream}.
	 * 
	 * @return the NBT compound from the stream
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 * @see #readCompressed(File)
	 */
	public static NbtCompound readCompressed(InputStream stream) throws IOException {
		DataInputStream dataInputStream = decompress(stream);

		NbtCompound var2;
		try {
			var2 = readCompound(dataInputStream, NbtTagSizeTracker.EMPTY);
		} catch (Throwable var5) {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (Throwable var4) {
					var5.addSuppressed(var4);
				}
			}

			throw var5;
		}

		if (dataInputStream != null) {
			dataInputStream.close();
		}

		return var2;
	}

	/**
	 * Scans the compressed NBT file using {@code scanner}.
	 * 
	 * @apiNote This method does not return the scan result; the user is expected
	 * to call the appropriate method of the {@link NbtScanner} subclasses, such as
	 * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #scanCompressed(InputStream, NbtScanner)
	 */
	public static void scanCompressed(File file, NbtScanner scanner) throws IOException {
		InputStream inputStream = new FileInputStream(file);

		try {
			scanCompressed(inputStream, scanner);
		} catch (Throwable var6) {
			try {
				inputStream.close();
			} catch (Throwable var5) {
				var6.addSuppressed(var5);
			}

			throw var6;
		}

		inputStream.close();
	}

	/**
	 * Scans the compressed NBT stream using {@code scanner}.
	 * 
	 * @apiNote This method does not return the scan result; the user is expected
	 * to call the appropriate method of the {@link NbtScanner} subclasses, such as
	 * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #scanCompressed(File, NbtScanner)
	 */
	public static void scanCompressed(InputStream stream, NbtScanner scanner) throws IOException {
		DataInputStream dataInputStream = decompress(stream);

		try {
			scan(dataInputStream, scanner);
		} catch (Throwable var6) {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (dataInputStream != null) {
			dataInputStream.close();
		}
	}

	/**
	 * Writes the Gzip-compressed {@code nbt} to {@code file}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #writeCompressed(NbtCompound, OutputStream)
	 */
	public static void writeCompressed(NbtCompound nbt, File file) throws IOException {
		OutputStream outputStream = new FileOutputStream(file);

		try {
			writeCompressed(nbt, outputStream);
		} catch (Throwable var6) {
			try {
				outputStream.close();
			} catch (Throwable var5) {
				var6.addSuppressed(var5);
			}

			throw var6;
		}

		outputStream.close();
	}

	/**
	 * Writes the Gzip-compressed {@code nbt} to {@code stream}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #writeCompressed(NbtCompound, File)
	 */
	public static void writeCompressed(NbtCompound nbt, OutputStream stream) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));

		try {
			writeCompound(nbt, dataOutputStream);
		} catch (Throwable var6) {
			try {
				dataOutputStream.close();
			} catch (Throwable var5) {
				var6.addSuppressed(var5);
			}

			throw var6;
		}

		dataOutputStream.close();
	}

	/**
	 * Writes the {@code nbt} to {@code file}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #write(NbtCompound, DataOutput)
	 */
	public static void write(NbtCompound nbt, File file) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		try {
			DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

			try {
				writeCompound(nbt, dataOutputStream);
			} catch (Throwable var8) {
				try {
					dataOutputStream.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}

				throw var8;
			}

			dataOutputStream.close();
		} catch (Throwable var9) {
			try {
				fileOutputStream.close();
			} catch (Throwable var6) {
				var9.addSuppressed(var6);
			}

			throw var9;
		}

		fileOutputStream.close();
	}

	/**
	 * Reads an NBT compound from {@code file}.
	 * 
	 * @return the NBT compound from the file, or {@code null} if the file does not exist
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 */
	@Nullable
	public static NbtCompound read(File file) throws IOException {
		if (!file.exists()) {
			return null;
		} else {
			FileInputStream fileInputStream = new FileInputStream(file);

			NbtCompound var3;
			try {
				DataInputStream dataInputStream = new DataInputStream(fileInputStream);

				try {
					var3 = readCompound(dataInputStream, NbtTagSizeTracker.EMPTY);
				} catch (Throwable var7) {
					try {
						dataInputStream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}

					throw var7;
				}

				dataInputStream.close();
			} catch (Throwable var8) {
				try {
					fileInputStream.close();
				} catch (Throwable var5) {
					var8.addSuppressed(var5);
				}

				throw var8;
			}

			fileInputStream.close();
			return var3;
		}
	}

	/**
	 * Reads an NBT compound from {@code input}.
	 * 
	 * @return the NBT compound from the input
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 */
	public static NbtCompound readCompound(DataInput input) throws IOException {
		return readCompound(input, NbtTagSizeTracker.EMPTY);
	}

	/**
	 * Reads an NBT compound from {@code input}.
	 * 
	 * @return the NBT compound from the input
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 */
	public static NbtCompound readCompound(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		NbtElement nbtElement = read(input, 0, tracker);
		if (nbtElement instanceof NbtCompound) {
			return (NbtCompound)nbtElement;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	/**
	 * Writes the {@code nbt} to {@code output}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #write(NbtCompound, File)
	 */
	public static void writeCompound(NbtCompound nbt, DataOutput output) throws IOException {
		write(nbt, output);
	}

	/**
	 * Scans the NBT input using {@code scanner}.
	 * 
	 * @apiNote This method does not return the scan result; the user is expected
	 * to call the appropriate method of the {@link NbtScanner} subclasses, such as
	 * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
	 * 
	 * @throws IOException if the IO operation fails
	 */
	public static void scan(DataInput input, NbtScanner scanner) throws IOException {
		NbtType<?> nbtType = NbtTypes.byId(input.readByte());
		if (nbtType == NbtEnd.TYPE) {
			if (scanner.start(NbtEnd.TYPE) == NbtScanner.Result.CONTINUE) {
				scanner.visitEnd();
			}
		} else {
			switch (scanner.start(nbtType)) {
				case HALT:
				default:
					break;
				case BREAK:
					NbtString.skip(input);
					nbtType.skip(input);
					break;
				case CONTINUE:
					NbtString.skip(input);
					nbtType.doAccept(input, scanner);
			}
		}
	}

	/**
	 * Reads an NBT element from {@code input}. Unlike {@link
	 * #readCompound(DataInput, NbtTagSizeTracker)}, the element does not have to
	 * be a compound.
	 * 
	 * @return the NBT element from the input
	 * @throws IOException if the IO operation fails
	 */
	public static NbtElement read(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		byte b = input.readByte();
		return (NbtElement)(b == 0 ? NbtEnd.INSTANCE : read(input, 0, tracker, b));
	}

	/**
	 * Writes the {@code nbt} to {@code output}. The output is the byte indicating
	 * the element type, followed by the NBT data.
	 * 
	 * @apiNote In vanilla, this is used exclusively in networking.
	 * @throws IOException if the IO operation fails
	 * @see #read(DataInput, NbtTagSizeTracker)
	 * @see #write(NbtElement, DataOutput)
	 */
	public static void writeForPacket(NbtElement nbt, DataOutput output) throws IOException {
		output.writeByte(nbt.getType());
		if (nbt.getType() != 0) {
			nbt.write(output);
		}
	}

	/**
	 * Writes the {@code nbt} to {@code output}. The output is the byte indicating
	 * the element type, followed by {@linkplain DataOutput#writeUTF an empty string}
	 * and the NBT data.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #read(DataInput, NbtTagSizeTracker)
	 * @see #writeForPacket(NbtElement, DataOutput)
	 */
	public static void write(NbtElement nbt, DataOutput output) throws IOException {
		output.writeByte(nbt.getType());
		if (nbt.getType() != 0) {
			output.writeUTF("");
			nbt.write(output);
		}
	}

	private static NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
		byte b = input.readByte();
		if (b == 0) {
			return NbtEnd.INSTANCE;
		} else {
			NbtString.skip(input);
			return read(input, depth, tracker, b);
		}
	}

	private static NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker, byte type) {
		try {
			return NbtTypes.byId(type).read(input, depth, tracker);
		} catch (IOException var7) {
			CrashReport crashReport = CrashReport.create(var7, "Loading NBT data");
			CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
			crashReportSection.add("Tag type", type);
			throw new CrashException(crashReport);
		}
	}
}
