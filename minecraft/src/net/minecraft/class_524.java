package net.minecraft;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.FileUtils;

@Environment(EnvType.CLIENT)
public class class_524 extends class_437 {
	private class_4185 field_3168;
	private final BooleanConsumer field_3169;
	private class_342 field_3170;
	private final String field_3167;

	public class_524(BooleanConsumer booleanConsumer, String string) {
		super(new class_2588("selectWorld.edit.title"));
		this.field_3169 = booleanConsumer;
		this.field_3167 = string;
	}

	@Override
	public void tick() {
		this.field_3170.method_1865();
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		class_4185 lv = this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, class_1074.method_4662("selectWorld.edit.resetIcon"), arg -> {
				class_32 lvx = this.minecraft.method_1586();
				FileUtils.deleteQuietly(lvx.method_239(this.field_3167, "icon.png"));
				arg.active = false;
			})
		);
		this.addButton(new class_4185(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, class_1074.method_4662("selectWorld.edit.openFolder"), arg -> {
			class_32 lvx = this.minecraft.method_1586();
			class_156.method_668().method_672(lvx.method_239(this.field_3167, "icon.png").getParentFile());
		}));
		this.addButton(new class_4185(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, class_1074.method_4662("selectWorld.edit.backup"), arg -> {
			class_32 lvx = this.minecraft.method_1586();
			method_2701(lvx, this.field_3167);
			this.field_3169.accept(false);
		}));
		this.addButton(new class_4185(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, class_1074.method_4662("selectWorld.edit.backupFolder"), arg -> {
			class_32 lvx = this.minecraft.method_1586();
			Path path = lvx.method_236();

			try {
				Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
			} catch (IOException var5) {
				throw new RuntimeException(var5);
			}

			class_156.method_668().method_672(path.toFile());
		}));
		this.addButton(
			new class_4185(
				this.width / 2 - 100,
				this.height / 4 + 120 + 5,
				200,
				20,
				class_1074.method_4662("selectWorld.edit.optimize"),
				arg -> this.minecraft.method_1507(new class_405(this, (bl, bl2) -> {
						if (bl) {
							method_2701(this.minecraft.method_1586(), this.field_3167);
						}

						this.minecraft.method_1507(new class_527(this.field_3169, this.field_3167, this.minecraft.method_1586(), bl2));
					}, new class_2588("optimizeWorld.confirm.title"), new class_2588("optimizeWorld.confirm.description"), true))
			)
		);
		this.field_3168 = this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, class_1074.method_4662("selectWorld.edit.save"), arg -> this.method_2691())
		);
		this.addButton(
			new class_4185(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, class_1074.method_4662("gui.cancel"), arg -> this.field_3169.accept(false))
		);
		lv.active = this.minecraft.method_1586().method_239(this.field_3167, "icon.png").isFile();
		class_32 lv2 = this.minecraft.method_1586();
		class_31 lv3 = lv2.method_231(this.field_3167);
		String string = lv3 == null ? "" : lv3.method_150();
		this.field_3170 = new class_342(this.font, this.width / 2 - 100, 53, 200, 20, class_1074.method_4662("selectWorld.enterName"));
		this.field_3170.method_1852(string);
		this.field_3170.method_1863(stringx -> this.field_3168.active = !stringx.trim().isEmpty());
		this.children.add(this.field_3170);
		this.method_20085(this.field_3170);
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_3170.method_1882();
		this.init(arg, i, j);
		this.field_3170.method_1852(string);
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
	}

	private void method_2691() {
		class_32 lv = this.minecraft.method_1586();
		lv.method_241(this.field_3167, this.field_3170.method_1882().trim());
		this.field_3169.accept(true);
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
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 20, 16777215);
		this.drawString(this.font, class_1074.method_4662("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
		this.field_3170.render(i, j, f);
		super.render(i, j, f);
	}
}
