package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public class class_417 extends class_437 {
	private static final class_2960 field_2447 = new class_2960("textures/gui/demo_background.png");

	public class_417() {
		super(new class_2588("demo.help.title"));
	}

	@Override
	protected void init() {
		int i = -16;
		this.addButton(new class_4185(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, class_1074.method_4662("demo.help.buy"), arg -> {
			try {
				Path path = Files.createTempDirectory("25th");
				Path path2 = path.resolve("ORDER.TXT");
				OutputStream outputStream = Files.newOutputStream(path2);
				Throwable var4 = null;

				try {
					InputStream inputStream = class_417.class.getClassLoader().getResourceAsStream("ORDER.FRM");
					Throwable var6 = null;

					try {
						IOUtils.copy(inputStream, outputStream);
					} catch (Throwable var31) {
						var6 = var31;
						throw var31;
					} finally {
						if (inputStream != null) {
							if (var6 != null) {
								try {
									inputStream.close();
								} catch (Throwable var30) {
									var6.addSuppressed(var30);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (Throwable var33) {
					var4 = var33;
					throw var33;
				} finally {
					if (outputStream != null) {
						if (var4 != null) {
							try {
								outputStream.close();
							} catch (Throwable var29) {
								var4.addSuppressed(var29);
							}
						} else {
							outputStream.close();
						}
					}
				}

				class_156.method_668().method_673(path2.toUri());
			} catch (IOException var35) {
				var35.printStackTrace();
			}
		}));
		this.addButton(new class_4185(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, class_1074.method_4662("demo.help.later"), arg -> {
			this.minecraft.method_1507(null);
			this.minecraft.field_1729.method_1612();
		}));
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2447);
		int i = (this.width - 248) / 2;
		int j = (this.height - 166) / 2;
		this.blit(i, j, 0, 0, 248, 166);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		int k = (this.width - 248) / 2 + 10;
		int l = (this.height - 166) / 2 + 8;
		this.font.method_1729(this.title.method_10863(), (float)k, (float)l, 2039583);
		l += 12;
		class_315 lv = this.minecraft.field_1690;
		this.font.method_1729(class_1074.method_4662("demo.help.nag1"), (float)k, (float)l, 5197647);
		this.font.method_1729(class_1074.method_4662("demo.help.nag2"), (float)k, (float)(l + 12), 5197647);
		this.font.method_1729(class_1074.method_4662("demo.help.nag3"), (float)k, (float)(l + 24), 5197647);
		super.render(i, j, f);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
