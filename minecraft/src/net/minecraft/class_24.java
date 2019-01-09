package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_24 extends class_28 {
	private static final Logger field_133 = LogManager.getLogger();

	public class_24(Path path, Path path2, DataFixer dataFixer) {
		super(path, path2, dataFixer);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_232() {
		return "Anvil";
	}

	@Environment(EnvType.CLIENT)
	@Override
	public List<class_34> method_235() throws class_33 {
		if (!Files.isDirectory(this.field_142, new LinkOption[0])) {
			throw new class_33(new class_2588("selectWorld.load_folder_access").getString());
		} else {
			List<class_34> list = Lists.<class_34>newArrayList();
			File[] files = this.field_142.toFile().listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					String string = file.getName();
					class_31 lv = this.method_238(string);
					if (lv != null && (lv.method_168() == 19132 || lv.method_168() == 19133)) {
						boolean bl = lv.method_168() != this.method_115();
						String string2 = lv.method_150();
						if (StringUtils.isEmpty(string2)) {
							string2 = string;
						}

						long l = 0L;
						list.add(new class_34(lv, string, string2, 0L, bl));
					}
				}
			}

			return list;
		}
	}

	protected int method_115() {
		return 19133;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_245() {
		class_2867.method_12438();
	}

	@Override
	public class_30 method_242(String string, @Nullable MinecraftServer minecraftServer) {
		return new class_25(this.field_142.toFile(), string, minecraftServer, this.field_143);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_244(String string) {
		class_31 lv = this.method_238(string);
		return lv != null && lv.method_168() == 19132;
	}

	@Override
	public boolean method_231(String string) {
		class_31 lv = this.method_238(string);
		return lv != null && lv.method_168() != this.method_115();
	}

	@Override
	public boolean method_234(String string, class_3536 arg) {
		arg.method_15410(0);
		List<File> list = Lists.<File>newArrayList();
		List<File> list2 = Lists.<File>newArrayList();
		List<File> list3 = Lists.<File>newArrayList();
		File file = new File(this.field_142.toFile(), string);
		File file2 = class_2874.field_13076.method_12488(file);
		File file3 = class_2874.field_13078.method_12488(file);
		field_133.info("Scanning folders...");
		this.method_117(file, list);
		if (file2.exists()) {
			this.method_117(file2, list2);
		}

		if (file3.exists()) {
			this.method_117(file3, list3);
		}

		int i = list.size() + list2.size() + list3.size();
		field_133.info("Total conversion count is {}", i);
		class_31 lv = this.method_238(string);
		class_1969<class_1991, class_1992> lv2 = class_1969.field_9401;
		class_1969<class_2084, class_2088> lv3 = class_1969.field_9402;
		class_1966 lv4;
		if (lv != null && lv.method_153() == class_1942.field_9277) {
			lv4 = lv2.method_8772(lv2.method_8774().method_8782(class_1972.field_9451));
		} else {
			lv4 = lv3.method_8772(lv3.method_8774().method_9002(lv).method_9004(class_2798.field_12769.method_12117()));
		}

		this.method_113(new File(file, "region"), list, lv4, 0, i, arg);
		this.method_113(new File(file2, "region"), list2, lv2.method_8772(lv2.method_8774().method_8782(class_1972.field_9461)), list.size(), i, arg);
		this.method_113(new File(file3, "region"), list3, lv2.method_8772(lv2.method_8774().method_8782(class_1972.field_9411)), list.size() + list2.size(), i, arg);
		lv.method_142(19133);
		if (lv.method_153() == class_1942.field_9268) {
			lv.method_225(class_1942.field_9265);
		}

		this.method_118(string);
		class_30 lv5 = this.method_242(string, null);
		lv5.method_136(lv);
		return true;
	}

	private void method_118(String string) {
		File file = new File(this.field_142.toFile(), string);
		if (!file.exists()) {
			field_133.warn("Unable to create level.dat_mcr backup");
		} else {
			File file2 = new File(file, "level.dat");
			if (!file2.exists()) {
				field_133.warn("Unable to create level.dat_mcr backup");
			} else {
				File file3 = new File(file, "level.dat_mcr");
				if (!file2.renameTo(file3)) {
					field_133.warn("Unable to create level.dat_mcr backup");
				}
			}
		}
	}

	private void method_113(File file, Iterable<File> iterable, class_1966 arg, int i, int j, class_3536 arg2) {
		for (File file2 : iterable) {
			this.method_116(file, file2, arg, i, j, arg2);
			i++;
			int k = (int)Math.round(100.0 * (double)i / (double)j);
			arg2.method_15410(k);
		}
	}

	private void method_116(File file, File file2, class_1966 arg, int i, int j, class_3536 arg2) {
		try {
			String string = file2.getName();
			class_2861 lv = new class_2861(file2);
			class_2861 lv2 = new class_2861(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"));

			for (int k = 0; k < 32; k++) {
				for (int l = 0; l < 32; l++) {
					if (lv.method_12423(k, l) && !lv2.method_12423(k, l)) {
						DataInputStream dataInputStream = lv.method_12421(k, l);
						if (dataInputStream == null) {
							field_133.warn("Failed to fetch input stream");
						} else {
							class_2487 lv3 = class_2507.method_10627(dataInputStream);
							dataInputStream.close();
							class_2487 lv4 = lv3.method_10562("Level");
							class_2864.class_2865 lv5 = class_2864.method_12433(lv4);
							class_2487 lv6 = new class_2487();
							class_2487 lv7 = new class_2487();
							lv6.method_10566("Level", lv7);
							class_2864.method_12432(lv5, lv7, arg);
							DataOutputStream dataOutputStream = lv2.method_12425(k, l);
							class_2507.method_10628(lv6, dataOutputStream);
							dataOutputStream.close();
						}
					}
				}

				int lx = (int)Math.round(100.0 * (double)(i * 1024) / (double)(j * 1024));
				int m = (int)Math.round(100.0 * (double)((k + 1) * 32 + i * 1024) / (double)(j * 1024));
				if (m > lx) {
					arg2.method_15410(m);
				}
			}

			lv.method_12429();
			lv2.method_12429();
		} catch (IOException var19) {
			var19.printStackTrace();
		}
	}

	private void method_117(File file, Collection<File> collection) {
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mcr"));
		if (files != null) {
			Collections.addAll(collection, files);
		}
	}
}
