package net.minecraft;

import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_29 implements class_35 {
	private static final Logger field_149 = LogManager.getLogger();
	private final File field_145;
	private final File field_144;
	private final long field_146 = class_156.method_658();
	private final String field_150;
	private final class_3485 field_147;
	protected final DataFixer field_148;

	public class_29(File file, String string, @Nullable MinecraftServer minecraftServer, DataFixer dataFixer) {
		this.field_148 = dataFixer;
		this.field_145 = new File(file, string);
		this.field_145.mkdirs();
		this.field_144 = new File(this.field_145, "playerdata");
		this.field_150 = string;
		if (minecraftServer != null) {
			this.field_144.mkdirs();
			this.field_147 = new class_3485(minecraftServer, this.field_145, dataFixer);
		} else {
			this.field_147 = null;
		}

		this.method_128();
	}

	public void method_131(class_31 arg, @Nullable class_2487 arg2) {
		arg.method_142(19133);
		class_2487 lv = arg.method_163(arg2);
		class_2487 lv2 = new class_2487();
		lv2.method_10566("Data", lv);

		try {
			File file = new File(this.field_145, "level.dat_new");
			File file2 = new File(this.field_145, "level.dat_old");
			File file3 = new File(this.field_145, "level.dat");
			class_2507.method_10634(lv2, new FileOutputStream(file));
			if (file2.exists()) {
				file2.delete();
			}

			file3.renameTo(file2);
			if (file3.exists()) {
				file3.delete();
			}

			file.renameTo(file3);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception var8) {
			var8.printStackTrace();
		}
	}

	private void method_128() {
		try {
			File file = new File(this.field_145, "session.lock");
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

			try {
				dataOutputStream.writeLong(this.field_146);
			} finally {
				dataOutputStream.close();
			}
		} catch (IOException var7) {
			var7.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
	}

	public File method_132() {
		return this.field_145;
	}

	public void method_137() throws class_1939 {
		try {
			File file = new File(this.field_145, "session.lock");
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

			try {
				if (dataInputStream.readLong() != this.field_146) {
					throw new class_1939("The save is being accessed from another location, aborting");
				}
			} finally {
				dataInputStream.close();
			}
		} catch (IOException var7) {
			throw new class_1939("Failed to check session lock, aborting");
		}
	}

	@Nullable
	public class_31 method_133() {
		File file = new File(this.field_145, "level.dat");
		if (file.exists()) {
			class_31 lv = class_32.method_17926(file, this.field_148);
			if (lv != null) {
				return lv;
			}
		}

		file = new File(this.field_145, "level.dat_old");
		return file.exists() ? class_32.method_17926(file, this.field_148) : null;
	}

	public void method_136(class_31 arg) {
		this.method_131(arg, null);
	}

	@Override
	public void method_262(class_1657 arg) {
		try {
			class_2487 lv = arg.method_5647(new class_2487());
			File file = new File(this.field_144, arg.method_5845() + ".dat.tmp");
			File file2 = new File(this.field_144, arg.method_5845() + ".dat");
			class_2507.method_10634(lv, new FileOutputStream(file));
			if (file2.exists()) {
				file2.delete();
			}

			file.renameTo(file2);
		} catch (Exception var5) {
			field_149.warn("Failed to save player data for {}", arg.method_5477().getString());
		}
	}

	@Nullable
	@Override
	public class_2487 method_261(class_1657 arg) {
		class_2487 lv = null;

		try {
			File file = new File(this.field_144, arg.method_5845() + ".dat");
			if (file.exists() && file.isFile()) {
				lv = class_2507.method_10629(new FileInputStream(file));
			}
		} catch (Exception var4) {
			field_149.warn("Failed to load player data for {}", arg.method_5477().getString());
		}

		if (lv != null) {
			int i = lv.method_10573("DataVersion", 3) ? lv.method_10550("DataVersion") : -1;
			arg.method_5651(class_2512.method_10688(this.field_148, class_4284.field_19213, lv, i));
		}

		return lv;
	}

	public String[] method_263() {
		String[] strings = this.field_144.list();
		if (strings == null) {
			strings = new String[0];
		}

		for (int i = 0; i < strings.length; i++) {
			if (strings[i].endsWith(".dat")) {
				strings[i] = strings[i].substring(0, strings[i].length() - 4);
			}
		}

		return strings;
	}

	public class_3485 method_134() {
		return this.field_147;
	}

	public DataFixer method_130() {
		return this.field_148;
	}
}
