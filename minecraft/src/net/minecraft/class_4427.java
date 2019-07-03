package net.minecraft;

import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.UploadInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4427 extends RealmsScreen {
	private static final Logger field_20174 = LogManager.getLogger();
	private final class_4410 field_20175;
	private final RealmsLevelSummary field_20176;
	private final long field_20177;
	private final int field_20178;
	private final class_4351 field_20179;
	private final RateLimiter field_20180;
	private volatile String field_20181;
	private volatile String field_20182;
	private volatile String field_20183;
	private volatile boolean field_20184;
	private volatile boolean field_20185;
	private volatile boolean field_20186 = true;
	private volatile boolean field_20187;
	private RealmsButton field_20188;
	private RealmsButton field_20189;
	private int field_20190;
	private static final String[] field_20191 = new String[]{"", ".", ". .", ". . ."};
	private int field_20192;
	private Long field_20193;
	private Long field_20194;
	private long field_20195;
	private static final ReentrantLock field_20196 = new ReentrantLock();

	public class_4427(long l, int i, class_4410 arg, RealmsLevelSummary realmsLevelSummary) {
		this.field_20177 = l;
		this.field_20178 = i;
		this.field_20175 = arg;
		this.field_20176 = realmsLevelSummary;
		this.field_20179 = new class_4351();
		this.field_20180 = RateLimiter.create(0.1F);
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.field_20188 = new RealmsButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				class_4427.this.method_21525();
			}
		};
		this.buttonsAdd(this.field_20189 = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")) {
			@Override
			public void onPress() {
				class_4427.this.method_21528();
			}
		});
		if (!this.field_20187) {
			if (this.field_20175.field_19998 == -1) {
				this.method_21536();
			} else {
				this.field_20175.method_21377(this);
			}
		}
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (bl && !this.field_20187) {
			this.field_20187 = true;
			Realms.setScreen(this);
			this.method_21536();
		}
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	private void method_21525() {
		this.field_20175.confirmResult(true, 0);
	}

	private void method_21528() {
		this.field_20184 = true;
		Realms.setScreen(this.field_20175);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			if (this.field_20186) {
				this.method_21528();
			} else {
				this.method_21525();
			}

			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		if (!this.field_20185 && this.field_20179.field_19601 != 0L && this.field_20179.field_19601 == this.field_20179.field_19602) {
			this.field_20182 = getLocalizedString("mco.upload.verifying");
			this.field_20189.active(false);
		}

		this.drawCenteredString(this.field_20182, this.width() / 2, 50, 16777215);
		if (this.field_20186) {
			this.method_21530();
		}

		if (this.field_20179.field_19601 != 0L && !this.field_20184) {
			this.method_21532();
			this.method_21534();
		}

		if (this.field_20181 != null) {
			String[] strings = this.field_20181.split("\\\\n");

			for (int k = 0; k < strings.length; k++) {
				this.drawCenteredString(strings[k], this.width() / 2, 110 + 12 * k, 16711680);
			}
		}

		super.render(i, j, f);
	}

	private void method_21530() {
		int i = this.fontWidth(this.field_20182);
		if (this.field_20190 % 10 == 0) {
			this.field_20192++;
		}

		this.drawString(field_20191[this.field_20192 % field_20191.length], this.width() / 2 + i / 2 + 5, 50, 16777215);
	}

	private void method_21532() {
		double d = this.field_20179.field_19601.doubleValue() / this.field_20179.field_19602.doubleValue() * 100.0;
		if (d > 100.0) {
			d = 100.0;
		}

		this.field_20183 = String.format(Locale.ROOT, "%.1f", d);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableTexture();
		double e = (double)(this.width() / 2 - 100);
		double f = 0.5;
		Tezzelator tezzelator = Tezzelator.instance;
		tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
		tezzelator.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
		tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
		tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
		tezzelator.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
		tezzelator.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
		tezzelator.vertex(e + 200.0 * d / 100.0, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
		tezzelator.vertex(e + 200.0 * d / 100.0, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
		tezzelator.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
		tezzelator.end();
		GlStateManager.enableTexture();
		this.drawCenteredString(this.field_20183 + " %", this.width() / 2, 84, 16777215);
	}

	private void method_21534() {
		if (this.field_20190 % 20 == 0) {
			if (this.field_20193 != null) {
				long l = System.currentTimeMillis() - this.field_20194;
				if (l == 0L) {
					l = 1L;
				}

				this.field_20195 = 1000L * (this.field_20179.field_19601 - this.field_20193) / l;
				this.method_21526(this.field_20195);
			}

			this.field_20193 = this.field_20179.field_19601;
			this.field_20194 = System.currentTimeMillis();
		} else {
			this.method_21526(this.field_20195);
		}
	}

	private void method_21526(long l) {
		if (l > 0L) {
			int i = this.fontWidth(this.field_20183);
			String string = "(" + method_21509(l) + ")";
			this.drawString(string, this.width() / 2 + i / 2 + 15, 84, 16777215);
		}
	}

	public static String method_21509(long l) {
		int i = 1024;
		if (l < 1024L) {
			return l + " B";
		} else {
			int j = (int)(Math.log((double)l) / Math.log(1024.0));
			String string = "KMGTPE".charAt(j - 1) + "";
			return String.format(Locale.ROOT, "%.1f %sB/s", (double)l / Math.pow(1024.0, (double)j), string);
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.field_20190++;
		if (this.field_20182 != null && this.field_20180.tryAcquire(1)) {
			ArrayList<String> arrayList = new ArrayList();
			arrayList.add(this.field_20182);
			if (this.field_20183 != null) {
				arrayList.add(this.field_20183 + "%");
			}

			if (this.field_20181 != null) {
				arrayList.add(this.field_20181);
			}

			Realms.narrateNow(String.join(System.lineSeparator(), arrayList));
		}
	}

	public static class_4427.class_4428 method_21518(long l) {
		if (l < 1024L) {
			return class_4427.class_4428.B;
		} else {
			int i = (int)(Math.log((double)l) / Math.log(1024.0));
			String string = "KMGTPE".charAt(i - 1) + "";

			try {
				return class_4427.class_4428.valueOf(string + "B");
			} catch (Exception var5) {
				return class_4427.class_4428.GB;
			}
		}
	}

	public static double method_21510(long l, class_4427.class_4428 arg) {
		return arg.equals(class_4427.class_4428.B) ? (double)l : (double)l / Math.pow(1024.0, (double)arg.ordinal());
	}

	public static String method_21519(long l, class_4427.class_4428 arg) {
		return String.format("%." + (arg.equals(class_4427.class_4428.GB) ? "1" : "0") + "f %s", method_21510(l, arg), arg.name());
	}

	private void method_21536() {
		this.field_20187 = true;
		(new Thread() {
				public void run() {
					File file = null;
					class_4341 lv = class_4341.method_20989();
					long l = class_4427.this.field_20177;

					try {
						if (class_4427.field_20196.tryLock(1L, TimeUnit.SECONDS)) {
							class_4427.this.field_20182 = RealmsScreen.getLocalizedString("mco.upload.preparing");
							UploadInfo uploadInfo = null;

							for (int i = 0; i < 20; i++) {
								try {
									if (class_4427.this.field_20184) {
										class_4427.this.method_21538();
										return;
									}

									uploadInfo = lv.method_21026(l, class_4453.method_21585(l));
									break;
								} catch (class_4356 var20) {
									Thread.sleep((long)(var20.field_19608 * 1000));
								}
							}

							if (uploadInfo == null) {
								class_4427.this.field_20182 = RealmsScreen.getLocalizedString("mco.upload.close.failure");
							} else {
								class_4453.method_21586(l, uploadInfo.getToken());
								if (!uploadInfo.isWorldClosed()) {
									class_4427.this.field_20182 = RealmsScreen.getLocalizedString("mco.upload.close.failure");
								} else if (class_4427.this.field_20184) {
									class_4427.this.method_21538();
								} else {
									File file2 = new File(Realms.getGameDirectoryPath(), "saves");
									file = class_4427.this.method_21524(new File(file2, class_4427.this.field_20176.getLevelId()));
									if (class_4427.this.field_20184) {
										class_4427.this.method_21538();
									} else if (class_4427.this.method_21515(file)) {
										class_4427.this.field_20182 = RealmsScreen.getLocalizedString("mco.upload.uploading", class_4427.this.field_20176.getLevelName());
										class_4337 lv6 = new class_4337(
											file,
											class_4427.this.field_20177,
											class_4427.this.field_20178,
											uploadInfo,
											Realms.getSessionId(),
											Realms.getName(),
											Realms.getMinecraftVersionString(),
											class_4427.this.field_20179
										);
										lv6.method_20973(arg -> {
											if (arg.field_20205 >= 200 && arg.field_20205 < 300) {
												class_4427.this.field_20185 = true;
												class_4427.this.field_20182 = RealmsScreen.getLocalizedString("mco.upload.done");
												class_4427.this.field_20188.setMessage(RealmsScreen.getLocalizedString("gui.done"));
												class_4453.method_21587(l);
											} else if (arg.field_20205 == 400 && arg.field_20206 != null) {
												class_4427.this.field_20181 = RealmsScreen.getLocalizedString("mco.upload.failed", arg.field_20206);
											} else {
												class_4427.this.field_20181 = RealmsScreen.getLocalizedString("mco.upload.failed", arg.field_20205);
											}
										});

										while (!lv6.method_20978()) {
											if (class_4427.this.field_20184) {
												lv6.method_20970();
												class_4427.this.method_21538();
												return;
											}

											try {
												Thread.sleep(500L);
											} catch (InterruptedException var19) {
												class_4427.field_20174.error("Failed to check Realms file upload status");
											}
										}
									} else {
										long m = file.length();
										class_4427.class_4428 lv3 = class_4427.method_21518(m);
										class_4427.class_4428 lv4 = class_4427.method_21518(5368709120L);
										if (class_4427.method_21519(m, lv3).equals(class_4427.method_21519(5368709120L, lv4)) && lv3 != class_4427.class_4428.B) {
											class_4427.class_4428 lv5 = class_4427.class_4428.values()[lv3.ordinal() - 1];
											class_4427.this.field_20181 = RealmsScreen.getLocalizedString("mco.upload.size.failure.line1", class_4427.this.field_20176.getLevelName())
												+ "\\n"
												+ RealmsScreen.getLocalizedString("mco.upload.size.failure.line2", class_4427.method_21519(m, lv5), class_4427.method_21519(5368709120L, lv5));
										} else {
											class_4427.this.field_20181 = RealmsScreen.getLocalizedString("mco.upload.size.failure.line1", class_4427.this.field_20176.getLevelName())
												+ "\\n"
												+ RealmsScreen.getLocalizedString("mco.upload.size.failure.line2", class_4427.method_21519(m, lv3), class_4427.method_21519(5368709120L, lv4));
										}
									}
								}
							}
						}
					} catch (IOException var21) {
						class_4427.this.field_20181 = RealmsScreen.getLocalizedString("mco.upload.failed", var21.getMessage());
					} catch (class_4355 var22) {
						class_4427.this.field_20181 = RealmsScreen.getLocalizedString("mco.upload.failed", var22.toString());
					} catch (InterruptedException var23) {
						class_4427.field_20174.error("Could not acquire upload lock");
					} finally {
						class_4427.this.field_20185 = true;
						if (class_4427.field_20196.isHeldByCurrentThread()) {
							class_4427.field_20196.unlock();
							class_4427.this.field_20186 = false;
							class_4427.this.childrenClear();
							class_4427.this.buttonsAdd(class_4427.this.field_20188);
							if (file != null) {
								class_4427.field_20174.debug("Deleting file " + file.getAbsolutePath());
								file.delete();
							}
						} else {
							return;
						}
					}
				}
			})
			.start();
	}

	private void method_21538() {
		this.field_20182 = getLocalizedString("mco.upload.cancelled");
		field_20174.debug("Upload was cancelled");
	}

	private boolean method_21515(File file) {
		return file.length() < 5368709120L;
	}

	private File method_21524(File file) throws IOException {
		TarArchiveOutputStream tarArchiveOutputStream = null;

		File var4;
		try {
			File file2 = File.createTempFile("realms-upload-file", ".tar.gz");
			tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file2)));
			tarArchiveOutputStream.setLongFileMode(3);
			this.method_21516(tarArchiveOutputStream, file.getAbsolutePath(), "world", true);
			tarArchiveOutputStream.finish();
			var4 = file2;
		} finally {
			if (tarArchiveOutputStream != null) {
				tarArchiveOutputStream.close();
			}
		}

		return var4;
	}

	private void method_21516(TarArchiveOutputStream tarArchiveOutputStream, String string, String string2, boolean bl) throws IOException {
		if (!this.field_20184) {
			File file = new File(string);
			String string3 = bl ? string2 : string2 + file.getName();
			TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, string3);
			tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
			if (file.isFile()) {
				IOUtils.copy(new FileInputStream(file), tarArchiveOutputStream);
				tarArchiveOutputStream.closeArchiveEntry();
			} else {
				tarArchiveOutputStream.closeArchiveEntry();
				File[] files = file.listFiles();
				if (files != null) {
					for (File file2 : files) {
						this.method_21516(tarArchiveOutputStream, file2.getAbsolutePath(), string3 + "/", false);
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static enum class_4428 {
		B,
		KB,
		MB,
		GB;
	}
}
