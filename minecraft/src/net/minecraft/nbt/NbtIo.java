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
import net.minecraft.util.crash.CrashReportElement;

public class NbtIo {
	public static CompoundTag readCompressed(InputStream inputStream) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));

		CompoundTag var2;
		try {
			var2 = read(dataInputStream, PositionTracker.DEFAULT);
		} finally {
			dataInputStream.close();
		}

		return var2;
	}

	public static void writeCompressed(CompoundTag compoundTag, OutputStream outputStream) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

		try {
			write(compoundTag, dataOutputStream);
		} finally {
			dataOutputStream.close();
		}
	}

	@Environment(EnvType.CLIENT)
	public static void safeWrite(CompoundTag compoundTag, File file) throws IOException {
		File file2 = new File(file.getAbsolutePath() + "_tmp");
		if (file2.exists()) {
			file2.delete();
		}

		write(compoundTag, file2);
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
	public static void write(CompoundTag compoundTag, File file) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

		try {
			write(compoundTag, dataOutputStream);
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

	public static CompoundTag read(DataInputStream dataInputStream) throws IOException {
		return read(dataInputStream, PositionTracker.DEFAULT);
	}

	public static CompoundTag read(DataInput dataInput, PositionTracker positionTracker) throws IOException {
		Tag tag = read(dataInput, 0, positionTracker);
		if (tag instanceof CompoundTag) {
			return (CompoundTag)tag;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void write(CompoundTag compoundTag, DataOutput dataOutput) throws IOException {
		write((Tag)compoundTag, dataOutput);
	}

	private static void write(Tag tag, DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(tag.getType());
		if (tag.getType() != 0) {
			dataOutput.writeUTF("");
			tag.write(dataOutput);
		}
	}

	private static Tag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		byte b = dataInput.readByte();
		if (b == 0) {
			return new EndTag();
		} else {
			dataInput.readUTF();
			Tag tag = Tag.createTag(b);

			try {
				tag.read(dataInput, i, positionTracker);
				return tag;
			} catch (IOException var8) {
				CrashReport crashReport = CrashReport.create(var8, "Loading NBT data");
				CrashReportElement crashReportElement = crashReport.addElement("NBT Tag");
				crashReportElement.add("Tag type", b);
				throw new CrashException(crashReport);
			}
		}
	}
}
