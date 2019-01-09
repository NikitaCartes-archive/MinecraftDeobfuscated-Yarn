package net.minecraft;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public final class class_529 extends class_350.class_351<class_529> implements AutoCloseable {
	private static final Logger field_3249 = LogManager.getLogger();
	private static final DateFormat field_3242 = new SimpleDateFormat();
	private static final class_2960 field_3243 = new class_2960("textures/misc/unknown_server.png");
	private static final class_2960 field_3240 = new class_2960("textures/gui/world_selection.png");
	private final class_310 field_3250;
	private final class_526 field_3251;
	private final class_34 field_3246;
	private final class_2960 field_3247;
	private final class_528 field_3241;
	private File field_3245;
	@Nullable
	private final class_1043 field_3244;
	private long field_3248;

	public class_529(class_528 arg, class_34 arg2, class_32 arg3) {
		this.field_3241 = arg;
		this.field_3251 = arg.method_2752();
		this.field_3246 = arg2;
		this.field_3250 = class_310.method_1551();
		this.field_3247 = new class_2960("worlds/" + Hashing.sha1().hashUnencodedChars(arg2.method_248()) + "/icon");
		this.field_3245 = arg3.method_239(arg2.method_248(), "icon.png");
		if (!this.field_3245.isFile()) {
			this.field_3245 = null;
		}

		this.field_3244 = this.method_2758();
	}

	@Override
	public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1906();
		int n = this.method_1907();
		String string = this.field_3246.method_252();
		String string2 = this.field_3246.method_248() + " (" + field_3242.format(new Date(this.field_3246.method_249())) + ")";
		String string3 = "";
		if (StringUtils.isEmpty(string)) {
			string = class_1074.method_4662("selectWorld.world") + " " + (this.method_1908() + 1);
		}

		if (this.field_3246.method_255()) {
			string3 = class_1074.method_4662("selectWorld.conversion") + " " + string3;
		} else {
			string3 = class_1074.method_4662("gameMode." + this.field_3246.method_247().method_8381());
			if (this.field_3246.method_257()) {
				string3 = class_124.field_1079 + class_1074.method_4662("gameMode.hardcore") + class_124.field_1070;
			}

			if (this.field_3246.method_259()) {
				string3 = string3 + ", " + class_1074.method_4662("selectWorld.cheats");
			}

			String string4 = this.field_3246.method_258().method_10863();
			if (this.field_3246.method_256()) {
				if (this.field_3246.method_260()) {
					string3 = string3 + ", " + class_1074.method_4662("selectWorld.version") + " " + class_124.field_1061 + string4 + class_124.field_1070;
				} else {
					string3 = string3 + ", " + class_1074.method_4662("selectWorld.version") + " " + class_124.field_1056 + string4 + class_124.field_1070;
				}
			} else {
				string3 = string3 + ", " + class_1074.method_4662("selectWorld.version") + " " + string4;
			}
		}

		this.field_3250.field_1772.method_1729(string, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		this.field_3250.field_1772.method_1729(string2, (float)(n + 32 + 3), (float)(m + 9 + 3), 8421504);
		this.field_3250.field_1772.method_1729(string3, (float)(n + 32 + 3), (float)(m + 9 + 9 + 3), 8421504);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_3250.method_1531().method_4618(this.field_3244 != null ? this.field_3247 : field_3243);
		GlStateManager.enableBlend();
		class_332.method_1781(n, m, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
		if (this.field_3250.field_1690.field_1854 || bl) {
			this.field_3250.method_1531().method_4618(field_3240);
			class_332.method_1785(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int o = k - n;
			int p = o < 32 ? 32 : 0;
			if (this.field_3246.method_256()) {
				class_332.method_1781(n, m, 32.0F, (float)p, 32, 32, 256.0F, 256.0F);
				if (this.field_3246.method_253()) {
					class_332.method_1781(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						class_2561 lv = new class_2588("selectWorld.tooltip.unsupported", this.field_3246.method_258()).method_10854(class_124.field_1061);
						this.field_3251.method_2739(this.field_3250.field_1772.method_1722(lv.method_10863(), 175));
					}
				} else if (this.field_3246.method_260()) {
					class_332.method_1781(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						this.field_3251
							.method_2739(
								class_124.field_1061
									+ class_1074.method_4662("selectWorld.tooltip.fromNewerVersion1")
									+ "\n"
									+ class_124.field_1061
									+ class_1074.method_4662("selectWorld.tooltip.fromNewerVersion2")
							);
					}
				} else if (!class_155.method_16673().isStable()) {
					class_332.method_1781(n, m, 64.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						this.field_3251
							.method_2739(
								class_124.field_1065
									+ class_1074.method_4662("selectWorld.tooltip.snapshot1")
									+ "\n"
									+ class_124.field_1065
									+ class_1074.method_4662("selectWorld.tooltip.snapshot2")
							);
					}
				}
			} else {
				class_332.method_1781(n, m, 0.0F, (float)p, 32, 32, 256.0F, 256.0F);
			}
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		this.field_3241.method_2751(this.method_1908());
		if (d - (double)this.method_1907() <= 32.0) {
			this.method_2768();
			return true;
		} else if (class_156.method_658() - this.field_3248 < 250L) {
			this.method_2768();
			return true;
		} else {
			this.field_3248 = class_156.method_658();
			return false;
		}
	}

	public void method_2768() {
		if (this.field_3246.method_254() || this.field_3246.method_253()) {
			String string = class_1074.method_4662("selectWorld.backupQuestion");
			String string2 = class_1074.method_4662("selectWorld.backupWarning", this.field_3246.method_258().method_10863(), class_155.method_16673().getName());
			if (this.field_3246.method_253()) {
				string = class_1074.method_4662("selectWorld.backupQuestion.customized");
				string2 = class_1074.method_4662("selectWorld.backupWarning.customized");
			}

			this.field_3250.method_1507(new class_405(this.field_3251, bl -> {
				if (bl) {
					String stringx = this.field_3246.method_248();
					class_524.method_2701(this.field_3250.method_1586(), stringx);
				}

				this.method_2767();
			}, string, string2));
		} else if (this.field_3246.method_260()) {
			this.field_3250
				.method_1507(
					new class_410(
						(bl, i) -> {
							if (bl) {
								try {
									this.method_2767();
								} catch (Exception var4) {
									field_3249.error("Failure to open 'future world'", (Throwable)var4);
									this.field_3250
										.method_1507(
											new class_403(
												() -> this.field_3250.method_1507(this.field_3251),
												new class_2588("selectWorld.futureworld.error.title"),
												new class_2588("selectWorld.futureworld.error.text")
											)
										);
								}
							} else {
								this.field_3250.method_1507(this.field_3251);
							}
						},
						class_1074.method_4662("selectWorld.versionQuestion"),
						class_1074.method_4662("selectWorld.versionWarning", this.field_3246.method_258().method_10863()),
						class_1074.method_4662("selectWorld.versionJoinButton"),
						class_1074.method_4662("gui.cancel"),
						0
					)
				);
		} else {
			this.method_2767();
		}
	}

	public void method_2755() {
		this.field_3250
			.method_1507(
				new class_410(
					(bl, i) -> {
						if (bl) {
							this.field_3250.method_1507(new class_435());
							class_32 lv = this.field_3250.method_1586();
							lv.method_245();
							lv.method_233(this.field_3246.method_248());
							this.field_3241.method_2750(() -> this.field_3251.field_3220.method_1882(), true);
						}

						this.field_3250.method_1507(this.field_3251);
					},
					class_1074.method_4662("selectWorld.deleteQuestion"),
					class_1074.method_4662("selectWorld.deleteWarning", this.field_3246.method_252()),
					class_1074.method_4662("selectWorld.deleteButton"),
					class_1074.method_4662("gui.cancel"),
					0
				)
			);
	}

	public void method_2756() {
		this.field_3250.method_1507(new class_524((bl, i) -> {
			if (bl) {
				this.field_3241.method_2750(() -> this.field_3251.field_3220.method_1882(), true);
			}

			this.field_3250.method_1507(this.field_3251);
		}, this.field_3246.method_248()));
	}

	public void method_2757() {
		try {
			this.field_3250.method_1507(new class_435());
			class_525 lv = new class_525(this.field_3251);
			class_30 lv2 = this.field_3250.method_1586().method_242(this.field_3246.method_248(), null);
			class_31 lv3 = lv2.method_133();
			class_2867.method_12438();
			if (lv3 != null) {
				lv.method_2737(lv3);
				if (this.field_3246.method_253()) {
					this.field_3250
						.method_1507(
							new class_410(
								(bl, i) -> {
									if (bl) {
										this.field_3250.method_1507(lv);
									} else {
										this.field_3250.method_1507(this.field_3251);
									}
								},
								class_1074.method_4662("selectWorld.recreate.customized.title"),
								class_1074.method_4662("selectWorld.recreate.customized.text"),
								class_1074.method_4662("gui.proceed"),
								class_1074.method_4662("gui.cancel"),
								0
							)
						);
				} else {
					this.field_3250.method_1507(lv);
				}
			}
		} catch (Exception var4) {
			field_3249.error("Unable to recreate world", (Throwable)var4);
			this.field_3250
				.method_1507(
					new class_403(
						() -> this.field_3250.method_1507(this.field_3251), new class_2588("selectWorld.recreate.error.title"), new class_2588("selectWorld.recreate.error.text")
					)
				);
		}
	}

	private void method_2767() {
		this.field_3250.method_1483().method_4873(class_1109.method_4758(class_3417.field_15015, 1.0F));
		if (this.field_3250.method_1586().method_230(this.field_3246.method_248())) {
			this.field_3250.method_1559(this.field_3246.method_248(), this.field_3246.method_252(), null);
		}
	}

	@Nullable
	private class_1043 method_2758() {
		boolean bl = this.field_3245 != null && this.field_3245.isFile();
		if (bl) {
			try {
				InputStream inputStream = new FileInputStream(this.field_3245);
				Throwable var3 = null;

				class_1043 var6;
				try {
					class_1011 lv = class_1011.method_4309(inputStream);
					Validate.validState(lv.method_4307() == 64, "Must be 64 pixels wide");
					Validate.validState(lv.method_4323() == 64, "Must be 64 pixels high");
					class_1043 lv2 = new class_1043(lv);
					this.field_3250.method_1531().method_4616(this.field_3247, lv2);
					var6 = lv2;
				} catch (Throwable var16) {
					var3 = var16;
					throw var16;
				} finally {
					if (inputStream != null) {
						if (var3 != null) {
							try {
								inputStream.close();
							} catch (Throwable var15) {
								var3.addSuppressed(var15);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var6;
			} catch (Throwable var18) {
				field_3249.error("Invalid icon for world {}", this.field_3246.method_248(), var18);
				this.field_3245 = null;
				return null;
			}
		} else {
			this.field_3250.method_1531().method_4615(this.field_3247);
			return null;
		}
	}

	public void close() {
		if (this.field_3244 != null) {
			this.field_3244.close();
		}
	}

	@Override
	public void method_1904(float f) {
	}
}
