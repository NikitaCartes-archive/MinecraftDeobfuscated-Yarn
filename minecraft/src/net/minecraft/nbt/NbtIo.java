package net.minecraft.nbt;

import java.io.BufferedInputStream;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class NbtIo {
	public static CompoundTag readCompressed(InputStream stream) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(stream)));
		Throwable var2 = null;

		CompoundTag var3;
		try {
			var3 = read(dataInputStream, PositionTracker.DEFAULT);
		} catch (Throwable var12) {
			var2 = var12;
			throw var12;
		} finally {
			if (dataInputStream != null) {
				if (var2 != null) {
					try {
						dataInputStream.close();
					} catch (Throwable var11) {
						var2.addSuppressed(var11);
					}
				} else {
					dataInputStream.close();
				}
			}
		}

		return var3;
	}

	public static void writeCompressed(CompoundTag tag, OutputStream stream) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));
		Throwable var3 = null;

		try {
			write(tag, dataOutputStream);
		} catch (Throwable var12) {
			var3 = var12;
			throw var12;
		} finally {
			if (dataOutputStream != null) {
				if (var3 != null) {
					try {
						dataOutputStream.close();
					} catch (Throwable var11) {
						var3.addSuppressed(var11);
					}
				} else {
					dataOutputStream.close();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static void safeWrite(CompoundTag tag, File file) throws IOException {
		File file2 = new File(file.getAbsolutePath() + "_tmp");
		if (file2.exists()) {
			file2.delete();
		}

		write(tag, file2);
		if (file.exists()) {
			file.delete();
		}

		if (file.exists()) {
			throw new IOException("Failed to delete " + file);
		} else {
			file2.renameTo(file);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void write(CompoundTag tag, File file) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

		try {
			write(tag, dataOutputStream);
		} finally {
			dataOutputStream.close();
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static CompoundTag read(File file) throws IOException {
		if (!file.exists()) {
			return null;
		} else {
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

			CompoundTag var2;
			try {
				var2 = read(dataInputStream, PositionTracker.DEFAULT);
			} finally {
				dataInputStream.close();
			}

			return var2;
		}
	}

	public static CompoundTag read(DataInputStream stream) throws IOException {
		return read(stream, PositionTracker.DEFAULT);
	}

	public static CompoundTag read(DataInput input, PositionTracker trakcer) throws IOException {
		Tag tag = read(input, 0, trakcer);
		if (tag instanceof CompoundTag) {
			return (CompoundTag)tag;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void write(CompoundTag tag, DataOutput output) throws IOException {
		write((Tag)tag, output);
	}

	private static void write(Tag tag, DataOutput output) throws IOException {
		output.writeByte(tag.getType());
		if (tag.getType() != 0) {
			output.writeUTF("");
			tag.write(output);
		}
	}

	private static Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		byte b = input.readByte();
		if (b == 0) {
			return EndTag.INSTANCE;
		} else {
			input.readUTF();

			try {
				return TagReaders.of(b).read(input, depth, tracker);
			} catch (IOException var7) {
				CrashReport crashReport = CrashReport.create(var7, "Loading NBT data");
				CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
				crashReportSection.add("Tag type", b);
				throw new CrashException(crashReport);
			}
		}
	}
}
