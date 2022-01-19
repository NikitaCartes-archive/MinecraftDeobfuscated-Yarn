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

public class NbtIo {
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

	private static DataInputStream method_40059(InputStream inputStream) throws IOException {
		return new DataInputStream(new FixedBufferInputStream(new GZIPInputStream(inputStream)));
	}

	public static NbtCompound readCompressed(InputStream stream) throws IOException {
		DataInputStream dataInputStream = method_40059(stream);

		NbtCompound var2;
		try {
			var2 = read(dataInputStream, NbtTagSizeTracker.EMPTY);
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

	public static void method_40057(File file, NbtScanner nbtScanner) throws IOException {
		InputStream inputStream = new FileInputStream(file);

		try {
			method_40058(inputStream, nbtScanner);
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

	public static void method_40058(InputStream inputStream, NbtScanner nbtScanner) throws IOException {
		DataInputStream dataInputStream = method_40059(inputStream);

		try {
			read(dataInputStream, nbtScanner);
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

	public static void writeCompressed(NbtCompound compound, File file) throws IOException {
		OutputStream outputStream = new FileOutputStream(file);

		try {
			writeCompressed(compound, outputStream);
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

	public static void writeCompressed(NbtCompound compound, OutputStream stream) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));

		try {
			write(compound, dataOutputStream);
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

	public static void write(NbtCompound compound, File file) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		try {
			DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

			try {
				write(compound, dataOutputStream);
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
					var3 = read(dataInputStream, NbtTagSizeTracker.EMPTY);
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

	public static NbtCompound read(DataInput input) throws IOException {
		return read(input, NbtTagSizeTracker.EMPTY);
	}

	public static NbtCompound read(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		NbtElement nbtElement = read(input, 0, tracker);
		if (nbtElement instanceof NbtCompound) {
			return (NbtCompound)nbtElement;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void write(NbtCompound compound, DataOutput output) throws IOException {
		write((NbtElement)compound, output);
	}

	public static void read(DataInput input, NbtScanner visitor) throws IOException {
		NbtType<?> nbtType = NbtTypes.byId(input.readByte());
		if (nbtType == NbtEnd.TYPE) {
			if (visitor.start(NbtEnd.TYPE) == NbtScanner.Result.CONTINUE) {
				visitor.visitEnd();
			}
		} else {
			switch (visitor.start(nbtType)) {
				case HALT:
				default:
					break;
				case BREAK:
					NbtString.skip(input);
					nbtType.skip(input);
					break;
				case CONTINUE:
					NbtString.skip(input);
					nbtType.doAccept(input, visitor);
			}
		}
	}

	public static void write(NbtElement element, DataOutput output) throws IOException {
		output.writeByte(element.getType());
		if (element.getType() != 0) {
			output.writeUTF("");
			element.write(output);
		}
	}

	private static NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
		byte b = input.readByte();
		if (b == 0) {
			return NbtEnd.INSTANCE;
		} else {
			NbtString.skip(input);

			try {
				return NbtTypes.byId(b).read(input, depth, tracker);
			} catch (IOException var7) {
				CrashReport crashReport = CrashReport.create(var7, "Loading NBT data");
				CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
				crashReportSection.add("Tag type", b);
				throw new CrashException(crashReport);
			}
		}
	}
}
