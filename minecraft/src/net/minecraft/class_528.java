package net.minecraft;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_528 extends class_4280<class_528.class_4272> {
	private static final Logger field_3238 = LogManager.getLogger();
	private static final DateFormat field_19132 = new SimpleDateFormat();
	private static final class_2960 field_19133 = new class_2960("textures/misc/unknown_server.png");
	private static final class_2960 field_19134 = new class_2960("textures/gui/world_selection.png");
	private final class_526 field_3237;
	@Nullable
	private List<class_34> field_3239;

	public class_528(class_526 arg, class_310 arg2, int i, int j, int k, int l, int m, Supplier<String> supplier, @Nullable class_528 arg3) {
		super(arg2, i, j, k, l, m);
		this.field_3237 = arg;
		if (arg3 != null) {
			this.field_3239 = arg3.field_3239;
		}

		this.method_2750(supplier, false);
	}

	public void method_2750(Supplier<String> supplier, boolean bl) {
		this.clearEntries();
		class_32 lv = this.minecraft.method_1586();
		if (this.field_3239 == null || bl) {
			try {
				this.field_3239 = lv.method_235();
			} catch (class_33 var7) {
				field_3238.error("Couldn't load level list", (Throwable)var7);
				this.minecraft.method_1507(new class_421(new class_2588("selectWorld.unable_to_load"), var7.getMessage()));
				return;
			}

			Collections.sort(this.field_3239);
		}

		String string = ((String)supplier.get()).toLowerCase(Locale.ROOT);

		for (class_34 lv3 : this.field_3239) {
			if (lv3.method_252().toLowerCase(Locale.ROOT).contains(string) || lv3.method_248().toLowerCase(Locale.ROOT).contains(string)) {
				this.addEntry(new class_528.class_4272(this, lv3, this.minecraft.method_1586()));
			}
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	@Override
	protected boolean isFocused() {
		return this.field_3237.getFocused() == this;
	}

	public void method_20157(@Nullable class_528.class_4272 arg) {
		super.setSelected(arg);
		if (arg != null) {
			class_34 lv = arg.field_19138;
			class_333.field_2054
				.method_19788(
					new class_2588(
							"narrator.select",
							new class_2588(
								"narrator.select.world",
								lv.method_252(),
								new Date(lv.method_249()),
								lv.method_257() ? class_1074.method_4662("gameMode.hardcore") : class_1074.method_4662("gameMode." + lv.method_247().method_8381()),
								lv.method_259() ? class_1074.method_4662("selectWorld.cheats") : "",
								lv.method_258()
							)
						)
						.getString()
				);
		}
	}

	@Override
	protected void moveSelection(int i) {
		super.moveSelection(i);
		this.field_3237.method_19940(true);
	}

	public Optional<class_528.class_4272> method_20159() {
		return Optional.ofNullable(this.getSelected());
	}

	public class_526 method_2752() {
		return this.field_3237;
	}

	@Environment(EnvType.CLIENT)
	public final class class_4272 extends class_4280.class_4281<class_528.class_4272> implements AutoCloseable {
		private final class_310 field_19136;
		private final class_526 field_19137;
		private final class_34 field_19138;
		private final class_2960 field_19139;
		private File field_19140;
		@Nullable
		private final class_1043 field_19141;
		private long field_19142;

		public class_4272(class_528 arg2, class_34 arg3, class_32 arg4) {
			this.field_19137 = arg2.method_2752();
			this.field_19138 = arg3;
			this.field_19136 = class_310.method_1551();
			this.field_19139 = new class_2960("worlds/" + Hashing.sha1().hashUnencodedChars(arg3.method_248()) + "/icon");
			this.field_19140 = arg4.method_239(arg3.method_248(), "icon.png");
			if (!this.field_19140.isFile()) {
				this.field_19140 = null;
			}

			this.field_19141 = this.method_20175();
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			String string = this.field_19138.method_252();
			String string2 = this.field_19138.method_248() + " (" + class_528.field_19132.format(new Date(this.field_19138.method_249())) + ")";
			if (StringUtils.isEmpty(string)) {
				string = class_1074.method_4662("selectWorld.world") + " " + (i + 1);
			}

			String string3 = "";
			if (this.field_19138.method_255()) {
				string3 = class_1074.method_4662("selectWorld.conversion") + " " + string3;
			} else {
				string3 = class_1074.method_4662("gameMode." + this.field_19138.method_247().method_8381());
				if (this.field_19138.method_257()) {
					string3 = class_124.field_1079 + class_1074.method_4662("gameMode.hardcore") + class_124.field_1070;
				}

				if (this.field_19138.method_259()) {
					string3 = string3 + ", " + class_1074.method_4662("selectWorld.cheats");
				}

				String string4 = this.field_19138.method_258().method_10863();
				if (this.field_19138.method_256()) {
					if (this.field_19138.method_260()) {
						string3 = string3 + ", " + class_1074.method_4662("selectWorld.version") + " " + class_124.field_1061 + string4 + class_124.field_1070;
					} else {
						string3 = string3 + ", " + class_1074.method_4662("selectWorld.version") + " " + class_124.field_1056 + string4 + class_124.field_1070;
					}
				} else {
					string3 = string3 + ", " + class_1074.method_4662("selectWorld.version") + " " + string4;
				}
			}

			this.field_19136.field_1772.method_1729(string, (float)(k + 32 + 3), (float)(j + 1), 16777215);
			this.field_19136.field_1772.method_1729(string2, (float)(k + 32 + 3), (float)(j + 9 + 3), 8421504);
			this.field_19136.field_1772.method_1729(string3, (float)(k + 32 + 3), (float)(j + 9 + 9 + 3), 8421504);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_19136.method_1531().method_4618(this.field_19141 != null ? this.field_19139 : class_528.field_19133);
			GlStateManager.enableBlend();
			class_332.blit(k, j, 0.0F, 0.0F, 32, 32, 32, 32);
			GlStateManager.disableBlend();
			if (this.field_19136.field_1690.field_1854 || bl) {
				this.field_19136.method_1531().method_4618(class_528.field_19134);
				class_332.fill(k, j, k + 32, j + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int p = n - k;
				int q = p < 32 ? 32 : 0;
				if (this.field_19138.method_256()) {
					class_332.blit(k, j, 32.0F, (float)q, 32, 32, 256, 256);
					if (this.field_19138.method_253()) {
						class_332.blit(k, j, 96.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							class_2561 lv = new class_2588("selectWorld.tooltip.unsupported", this.field_19138.method_258()).method_10854(class_124.field_1061);
							this.field_19137.method_2739(this.field_19136.field_1772.method_1722(lv.method_10863(), 175));
						}
					} else if (this.field_19138.method_260()) {
						class_332.blit(k, j, 96.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							this.field_19137
								.method_2739(
									class_124.field_1061
										+ class_1074.method_4662("selectWorld.tooltip.fromNewerVersion1")
										+ "\n"
										+ class_124.field_1061
										+ class_1074.method_4662("selectWorld.tooltip.fromNewerVersion2")
								);
						}
					} else if (!class_155.method_16673().isStable()) {
						class_332.blit(k, j, 64.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							this.field_19137
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
					class_332.blit(k, j, 0.0F, (float)q, 32, 32, 256, 256);
				}
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			class_528.this.method_20157(this);
			this.field_19137.method_19940(class_528.this.method_20159().isPresent());
			if (d - (double)class_528.this.getRowLeft() <= 32.0) {
				this.method_20164();
				return true;
			} else if (class_156.method_658() - this.field_19142 < 250L) {
				this.method_20164();
				return true;
			} else {
				this.field_19142 = class_156.method_658();
				return false;
			}
		}

		public void method_20164() {
			if (this.field_19138.method_254() || this.field_19138.method_253()) {
				class_2561 lv = new class_2588("selectWorld.backupQuestion");
				class_2561 lv2 = new class_2588("selectWorld.backupWarning", this.field_19138.method_258().method_10863(), class_155.method_16673().getName());
				if (this.field_19138.method_253()) {
					lv = new class_2588("selectWorld.backupQuestion.customized");
					lv2 = new class_2588("selectWorld.backupWarning.customized");
				}

				this.field_19136.method_1507(new class_405(this.field_19137, (bl, bl2) -> {
					if (bl) {
						String string = this.field_19138.method_248();
						class_524.method_2701(this.field_19136.method_1586(), string);
					}

					this.method_20174();
				}, lv, lv2, false));
			} else if (this.field_19138.method_260()) {
				this.field_19136
					.method_1507(
						new class_410(
							bl -> {
								if (bl) {
									try {
										this.method_20174();
									} catch (Exception var3) {
										class_528.field_3238.error("Failure to open 'future world'", (Throwable)var3);
										this.field_19136
											.method_1507(
												new class_403(
													() -> this.field_19136.method_1507(this.field_19137),
													new class_2588("selectWorld.futureworld.error.title"),
													new class_2588("selectWorld.futureworld.error.text")
												)
											);
									}
								} else {
									this.field_19136.method_1507(this.field_19137);
								}
							},
							new class_2588("selectWorld.versionQuestion"),
							new class_2588("selectWorld.versionWarning", this.field_19138.method_258().method_10863()),
							class_1074.method_4662("selectWorld.versionJoinButton"),
							class_1074.method_4662("gui.cancel")
						)
					);
			} else {
				this.method_20174();
			}
		}

		public void method_20169() {
			this.field_19136
				.method_1507(
					new class_410(
						bl -> {
							if (bl) {
								this.field_19136.method_1507(new class_435());
								class_32 lv = this.field_19136.method_1586();
								lv.method_233(this.field_19138.method_248());
								class_528.this.method_2750(() -> this.field_19137.field_3220.method_1882(), true);
							}

							this.field_19136.method_1507(this.field_19137);
						},
						new class_2588("selectWorld.deleteQuestion"),
						new class_2588("selectWorld.deleteWarning", this.field_19138.method_252()),
						class_1074.method_4662("selectWorld.deleteButton"),
						class_1074.method_4662("gui.cancel")
					)
				);
		}

		public void method_20171() {
			this.field_19136.method_1507(new class_524(bl -> {
				if (bl) {
					class_528.this.method_2750(() -> this.field_19137.field_3220.method_1882(), true);
				}

				this.field_19136.method_1507(this.field_19137);
			}, this.field_19138.method_248()));
		}

		public void method_20173() {
			try {
				this.field_19136.method_1507(new class_435());
				class_525 lv = new class_525(this.field_19137);
				class_29 lv2 = this.field_19136.method_1586().method_242(this.field_19138.method_248(), null);
				class_31 lv3 = lv2.method_133();
				if (lv3 != null) {
					lv.method_2737(lv3);
					if (this.field_19138.method_253()) {
						this.field_19136
							.method_1507(
								new class_410(
									bl -> this.field_19136.method_1507((class_437)(bl ? lv : this.field_19137)),
									new class_2588("selectWorld.recreate.customized.title"),
									new class_2588("selectWorld.recreate.customized.text"),
									class_1074.method_4662("gui.proceed"),
									class_1074.method_4662("gui.cancel")
								)
							);
					} else {
						this.field_19136.method_1507(lv);
					}
				}
			} catch (Exception var4) {
				class_528.field_3238.error("Unable to recreate world", (Throwable)var4);
				this.field_19136
					.method_1507(
						new class_403(
							() -> this.field_19136.method_1507(this.field_19137),
							new class_2588("selectWorld.recreate.error.title"),
							new class_2588("selectWorld.recreate.error.text")
						)
					);
			}
		}

		private void method_20174() {
			this.field_19136.method_1483().method_4873(class_1109.method_4758(class_3417.field_15015, 1.0F));
			if (this.field_19136.method_1586().method_230(this.field_19138.method_248())) {
				this.field_19136.method_1559(this.field_19138.method_248(), this.field_19138.method_252(), null);
			}
		}

		@Nullable
		private class_1043 method_20175() {
			boolean bl = this.field_19140 != null && this.field_19140.isFile();
			if (bl) {
				try {
					InputStream inputStream = new FileInputStream(this.field_19140);
					Throwable var3 = null;

					class_1043 var6;
					try {
						class_1011 lv = class_1011.method_4309(inputStream);
						Validate.validState(lv.method_4307() == 64, "Must be 64 pixels wide");
						Validate.validState(lv.method_4323() == 64, "Must be 64 pixels high");
						class_1043 lv2 = new class_1043(lv);
						this.field_19136.method_1531().method_4616(this.field_19139, lv2);
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
					class_528.field_3238.error("Invalid icon for world {}", this.field_19138.method_248(), var18);
					this.field_19140 = null;
					return null;
				}
			} else {
				this.field_19136.method_1531().method_4615(this.field_19139);
				return null;
			}
		}

		public void close() {
			if (this.field_19141 != null) {
				this.field_19141.close();
			}
		}
	}
}
