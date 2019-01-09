package net.minecraft;

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

public class class_2507 {
	public static class_2487 method_10629(InputStream inputStream) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));

		class_2487 var2;
		try {
			var2 = method_10625(dataInputStream, class_2505.field_11556);
		} finally {
			dataInputStream.close();
		}

		return var2;
	}

	public static void method_10634(class_2487 arg, OutputStream outputStream) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

		try {
			method_10628(arg, dataOutputStream);
		} finally {
			dataOutputStream.close();
		}
	}

	@Environment(EnvType.CLIENT)
	public static void method_10632(class_2487 arg, File file) throws IOException {
		File file2 = new File(file.getAbsolutePath() + "_tmp");
		if (file2.exists()) {
			file2.delete();
		}

		method_10630(arg, file2);
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
	public static void method_10630(class_2487 arg, File file) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

		try {
			method_10628(arg, dataOutputStream);
		} finally {
			dataOutputStream.close();
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_2487 method_10633(File file) throws IOException {
		if (!file.exists()) {
			return null;
		} else {
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

			class_2487 var2;
			try {
				var2 = method_10625(dataInputStream, class_2505.field_11556);
			} finally {
				dataInputStream.close();
			}

			return var2;
		}
	}

	public static class_2487 method_10627(DataInputStream dataInputStream) throws IOException {
		return method_10625(dataInputStream, class_2505.field_11556);
	}

	public static class_2487 method_10625(DataInput dataInput, class_2505 arg) throws IOException {
		class_2520 lv = method_10626(dataInput, 0, arg);
		if (lv instanceof class_2487) {
			return (class_2487)lv;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void method_10628(class_2487 arg, DataOutput dataOutput) throws IOException {
		method_10631(arg, dataOutput);
	}

	private static void method_10631(class_2520 arg, DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(arg.method_10711());
		if (arg.method_10711() != 0) {
			dataOutput.writeUTF("");
			arg.method_10713(dataOutput);
		}
	}

	private static class_2520 method_10626(DataInput dataInput, int i, class_2505 arg) throws IOException {
		byte b = dataInput.readByte();
		if (b == 0) {
			return new class_2491();
		} else {
			dataInput.readUTF();
			class_2520 lv = class_2520.method_10708(b);

			try {
				lv.method_10709(dataInput, i, arg);
				return lv;
			} catch (IOException var8) {
				class_128 lv2 = class_128.method_560(var8, "Loading NBT data");
				class_129 lv3 = lv2.method_562("NBT Tag");
				lv3.method_578("Tag type", b);
				throw new class_148(lv2);
			}
		}
	}
}
