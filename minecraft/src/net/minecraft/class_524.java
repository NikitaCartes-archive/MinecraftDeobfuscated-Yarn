package net.minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.FileUtils;

@Environment(EnvType.CLIENT)
public class class_524 extends class_437 {
	private class_339 field_3168;
	private final class_411 field_3169;
	private class_342 field_3170;
	private final String field_3167;

	public class_524(class_411 arg, String string) {
		this.field_3169 = arg;
		this.field_3167 = string;
	}

	@Override
	public void method_2225() {
		this.field_3170.method_1865();
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		class_339 lv = this.method_2219(
			new class_339(3, this.field_2561 / 2 - 100, this.field_2559 / 4 + 24 + 5, class_1074.method_4662("selectWorld.edit.resetIcon")) {
				@Override
				public void method_1826(double d, double e) {
					class_32 lv = class_524.this.field_2563.method_1586();
					FileUtils.deleteQuietly(lv.method_239(class_524.this.field_3167, "icon.png"));
					this.field_2078 = false;
				}
			}
		);
		this.method_2219(new class_339(4, this.field_2561 / 2 - 100, this.field_2559 / 4 + 48 + 5, class_1074.method_4662("selectWorld.edit.openFolder")) {
			@Override
			public void method_1826(double d, double e) {
				class_32 lv = class_524.this.field_2563.method_1586();
				class_156.method_668().method_672(lv.method_239(class_524.this.field_3167, "icon.png").getParentFile());
			}
		});
		this.method_2219(new class_339(5, this.field_2561 / 2 - 100, this.field_2559 / 4 + 72 + 5, class_1074.method_4662("selectWorld.edit.backup")) {
			@Override
			public void method_1826(double d, double e) {
				class_32 lv = class_524.this.field_2563.method_1586();
				class_524.method_2701(lv, class_524.this.field_3167);
				class_524.this.field_3169.confirmResult(false, 0);
			}
		});
		this.method_2219(new class_339(6, this.field_2561 / 2 - 100, this.field_2559 / 4 + 96 + 5, class_1074.method_4662("selectWorld.edit.backupFolder")) {
			@Override
			public void method_1826(double d, double e) {
				class_32 lv = class_524.this.field_2563.method_1586();
				Path path = lv.method_236();

				try {
					Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
				} catch (IOException var8) {
					throw new RuntimeException(var8);
				}

				class_156.method_668().method_672(path.toFile());
			}
		});
		this.method_2219(new class_339(7, this.field_2561 / 2 - 100, this.field_2559 / 4 + 120 + 5, class_1074.method_4662("selectWorld.edit.optimize")) {
			@Override
			public void method_1826(double d, double e) {
				class_524.this.field_2563.method_1507(new class_405(class_524.this, bl -> {
					if (bl) {
						class_524.method_2701(class_524.this.field_2563.method_1586(), class_524.this.field_3167);
					}

					class_524.this.field_2563.method_1507(new class_527(class_524.this.field_3169, class_524.this.field_3167, class_524.this.field_2563.method_1586()));
				}, class_1074.method_4662("optimizeWorld.confirm.title"), class_1074.method_4662("optimizeWorld.confirm.description")));
			}
		});
		this.field_3168 = this.method_2219(
			new class_339(0, this.field_2561 / 2 - 100, this.field_2559 / 4 + 144 + 5, 98, 20, class_1074.method_4662("selectWorld.edit.save")) {
				@Override
				public void method_1826(double d, double e) {
					class_524.this.method_2691();
				}
			}
		);
		this.method_2219(new class_339(1, this.field_2561 / 2 + 2, this.field_2559 / 4 + 144 + 5, 98, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_524.this.field_3169.confirmResult(false, 0);
			}
		});
		lv.field_2078 = this.field_2563.method_1586().method_239(this.field_3167, "icon.png").isFile();
		class_32 lv2 = this.field_2563.method_1586();
		class_31 lv3 = lv2.method_238(this.field_3167);
		String string = lv3 == null ? "" : lv3.method_150();
		this.field_3170 = new class_342(2, this.field_2554, this.field_2561 / 2 - 100, 53, 200, 20);
		this.field_3170.method_1876(true);
		this.field_3170.method_1852(string);
		this.field_2557.add(this.field_3170);
	}

	@Override
	public void method_2228(class_310 arg, int i, int j) {
		String string = this.field_3170.method_1882();
		this.method_2233(arg, i, j);
		this.field_3170.method_1852(string);
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
	}

	private void method_2691() {
		class_32 lv = this.field_2563.method_1586();
		lv.method_241(this.field_3167, this.field_3170.method_1882().trim());
		this.field_3169.confirmResult(true, 0);
	}

	public static void method_2701(class_32 arg, String string) {
		class_374 lv = class_310.method_1551().method_1566();
		long l = 0L;
		IOException iOException = null;

		try {
			l = arg.method_237(string);
		} catch (IOException var8) {
			iOException = var8;
		}

		class_2561 lv2;
		class_2561 lv3;
		if (iOException != null) {
			lv2 = new class_2588("selectWorld.edit.backupFailed");
			lv3 = new class_2585(iOException.getMessage());
		} else {
			lv2 = new class_2588("selectWorld.edit.backupCreated", string);
			lv3 = new class_2588("selectWorld.edit.backupSize", class_3532.method_15384((double)l / 1048576.0));
		}

		lv.method_1999(new class_370(class_370.class_371.field_2220, lv2, lv3));
	}

	@Override
	public boolean method_16806(char c, int i) {
		if (this.field_3170.method_16806(c, i)) {
			this.field_3168.field_2078 = !this.field_3170.method_1882().trim().isEmpty();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (this.field_3170.method_16805(i, j, k)) {
			this.field_3168.field_2078 = !this.field_3170.method_1882().trim().isEmpty();
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.method_2691();
			return true;
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("selectWorld.edit.title"), this.field_2561 / 2, 20, 16777215);
		this.method_1780(this.field_2554, class_1074.method_4662("selectWorld.enterName"), this.field_2561 / 2 - 100, 40, 10526880);
		this.field_3170.method_1857(i, j, f);
		super.method_2214(i, j, f);
	}
}
