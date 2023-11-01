package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

/**
 * A set of utility functions for reading, writing, and scanning NBT files.
 * Methods that do not require {@link NbtTagSizeTracker} accept any bytes of data,
 * provided that its depth does not exceed {@value NbtTagSizeTracker#DEFAULT_MAX_DEPTH}.
 */
public class NbtIo {
	/**
	 * Reads an NBT compound from Gzip-compressed file at {@code path}.
	 * 
	 * @return the NBT compound from the file
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 * @throws NbtSizeValidationException if the NBT is too deep
	 * @see #readCompressed(InputStream, NbtTagSizeTracker)
	 */
	public static NbtCompound readCompressed(Path path, NbtTagSizeTracker tagSizeTracker) throws IOException {
		InputStream inputStream = Files.newInputStream(path);

		NbtCompound var3;
		try {
			var3 = readCompressed(inputStream, tagSizeTracker);
		} catch (Throwable var6) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (inputStream != null) {
			inputStream.close();
		}

		return var3;
	}

	/**
	 * {@return a new input stream that decompresses the input {@code stream}}
	 */
	private static DataInputStream decompress(InputStream stream) throws IOException {
		return new DataInputStream(new FixedBufferInputStream(new GZIPInputStream(stream)));
	}

	/**
	 * {@return a new output stream that compresses the input {@code stream}}
	 */
	private static DataOutputStream compress(OutputStream stream) throws IOException {
		return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));
	}

	/**
	 * Reads an NBT compound from Gzip-compressed {@code stream}.
	 * 
	 * @return the NBT compound from the stream
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 * @throws NbtSizeValidationException if the NBT is too deep
	 * @see #readCompressed(Path, NbtTagSizeTracker)
	 */
	public static NbtCompound readCompressed(InputStream stream, NbtTagSizeTracker tagSizeTracker) throws IOException {
		DataInputStream dataInputStream = decompress(stream);

		NbtCompound var3;
		try {
			var3 = readCompound(dataInputStream, tagSizeTracker);
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

		return var3;
	}

	/**
	 * Scans the compressed NBT file using {@code scanner}.
	 * 
	 * @apiNote This method does not return the scan result; the user is expected
	 * to call the appropriate method of the {@link NbtScanner} subclasses, such as
	 * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @throws NbtSizeValidationException if the {@code tracker}'s validation fails
	 * @see #scanCompressed(InputStream, NbtScanner, NbtTagSizeTracker)
	 */
	public static void scanCompressed(Path path, NbtScanner scanner, NbtTagSizeTracker tracker) throws IOException {
		InputStream inputStream = Files.newInputStream(path);

		try {
			scanCompressed(inputStream, scanner, tracker);
		} catch (Throwable var7) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable var6) {
					var7.addSuppressed(var6);
				}
			}

			throw var7;
		}

		if (inputStream != null) {
			inputStream.close();
		}
	}

	/**
	 * Scans the compressed NBT stream using {@code scanner}.
	 * 
	 * @apiNote This method does not return the scan result; the user is expected
	 * to call the appropriate method of the {@link NbtScanner} subclasses, such as
	 * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @throws NbtSizeValidationException if the {@code tracker}'s validation fails
	 * @see #scanCompressed(Path, NbtScanner, NbtTagSizeTracker)
	 */
	public static void scanCompressed(InputStream stream, NbtScanner scanner, NbtTagSizeTracker tracker) throws IOException {
		DataInputStream dataInputStream = decompress(stream);

		try {
			scan(dataInputStream, scanner, tracker);
		} catch (Throwable var7) {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (Throwable var6) {
					var7.addSuppressed(var6);
				}
			}

			throw var7;
		}

		if (dataInputStream != null) {
			dataInputStream.close();
		}
	}

	/**
	 * {@return the compressed byte array serialization of {@code nbt}}
	 */
	public static byte[] toCompressedBytes(NbtCompound nbt) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = compress(byteArrayOutputStream);

		try {
			writeCompound(nbt, dataOutputStream);
		} catch (Throwable var6) {
			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (dataOutputStream != null) {
			dataOutputStream.close();
		}

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * {@return the byte array serialization of {@code nbt}}
	 */
	public static byte[] toBytes(NbtCompound nbt) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

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
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Writes the Gzip-compressed {@code nbt} to the file at {@code path}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #writeCompressed(NbtCompound, OutputStream)
	 */
	public static void writeCompressed(NbtCompound nbt, Path path) throws IOException {
		OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.SYNC);

		try {
			OutputStream outputStream2 = new BufferedOutputStream(outputStream);

			try {
				writeCompressed(nbt, outputStream2);
			} catch (Throwable var8) {
				try {
					outputStream2.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}

				throw var8;
			}

			outputStream2.close();
		} catch (Throwable var9) {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable var6) {
					var9.addSuppressed(var6);
				}
			}

			throw var9;
		}

		if (outputStream != null) {
			outputStream.close();
		}
	}

	/**
	 * Writes the Gzip-compressed {@code nbt} to {@code stream}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #writeCompressed(NbtCompound, Path)
	 */
	public static void writeCompressed(NbtCompound nbt, OutputStream stream) throws IOException {
		DataOutputStream dataOutputStream = compress(stream);

		try {
			writeCompound(nbt, dataOutputStream);
		} catch (Throwable var6) {
			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (dataOutputStream != null) {
			dataOutputStream.close();
		}
	}

	/**
	 * Writes the {@code nbt} to the file at {@code path}.
	 * 
	 * @throws IOException if the IO operation fails
	 * @see #write(NbtCompound, DataOutput)
	 */
	public static void write(NbtCompound nbt, Path path) throws IOException {
		OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.SYNC);

		try {
			OutputStream outputStream2 = new BufferedOutputStream(outputStream);

			try {
				DataOutputStream dataOutputStream = new DataOutputStream(outputStream2);

				try {
					writeCompound(nbt, dataOutputStream);
				} catch (Throwable var10) {
					try {
						dataOutputStream.close();
					} catch (Throwable var9) {
						var10.addSuppressed(var9);
					}

					throw var10;
				}

				dataOutputStream.close();
			} catch (Throwable var11) {
				try {
					outputStream2.close();
				} catch (Throwable var8) {
					var11.addSuppressed(var8);
				}

				throw var11;
			}

			outputStream2.close();
		} catch (Throwable var12) {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable var7) {
					var12.addSuppressed(var7);
				}
			}

			throw var12;
		}

		if (outputStream != null) {
			outputStream.close();
		}
	}

	/**
	 * Reads an NBT compound from the file at{@code path}.
	 * 
	 * @return the NBT compound from the file, or {@code null} if the file does not exist
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 * @throws NbtSizeValidationException if the NBT is too deep
	 */
	@Nullable
	public static NbtCompound read(Path path) throws IOException {
		if (Files.exists(path, new LinkOption[0])) {
			return null;
		} else {
			InputStream inputStream = Files.newInputStream(path);

			NbtCompound var3;
			try {
				DataInputStream dataInputStream = new DataInputStream(inputStream);

				try {
					var3 = readCompound(dataInputStream, NbtTagSizeTracker.ofUnlimitedBytes());
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
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var5) {
						var8.addSuppressed(var5);
					}
				}

				throw var8;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var3;
		}
	}

	/**
	 * Reads an NBT compound from {@code input}.
	 * 
	 * @return the NBT compound from the input
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 * @throws NbtSizeValidationException if the NBT is too deep
	 */
	public static NbtCompound readCompound(DataInput input) throws IOException {
		return readCompound(input, NbtTagSizeTracker.ofUnlimitedBytes());
	}

	/**
	 * Reads an NBT compound from {@code input}.
	 * 
	 * @return the NBT compound from the input
	 * @throws IOException if the IO operation fails or if the root NBT element is
	 * not a compound
	 * @throws NbtSizeValidationException if the {@code tracker}'s validation fails
	 */
	public static NbtCompound readCompound(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		NbtElement nbtElement = readElement(input, tracker);
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
	 * @see #write(NbtCompound, Path)
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
	 * @throws NbtSizeValidationException if the {@code tracker}'s validation fails
	 */
	public static void scan(DataInput input, NbtScanner scanner, NbtTagSizeTracker tracker) throws IOException {
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
					nbtType.skip(input, tracker);
					break;
				case CONTINUE:
					NbtString.skip(input);
					nbtType.doAccept(input, scanner, tracker);
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
	 * @throws NbtSizeValidationException if the {@code tracker}'s validation fails
	 */
	public static NbtElement read(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		byte b = input.readByte();
		return (NbtElement)(b == 0 ? NbtEnd.INSTANCE : readElement(input, tracker, b));
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

	private static NbtElement readElement(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		byte b = input.readByte();
		if (b == 0) {
			return NbtEnd.INSTANCE;
		} else {
			NbtString.skip(input);
			return readElement(input, tracker, b);
		}
	}

	private static NbtElement readElement(DataInput input, NbtTagSizeTracker tracker, byte typeId) {
		try {
			return NbtTypes.byId(typeId).read(input, tracker);
		} catch (IOException var6) {
			CrashReport crashReport = CrashReport.create(var6, "Loading NBT data");
			CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
			crashReportSection.add("Tag type", typeId);
			throw new NbtCrashException(crashReport);
		}
	}
}
