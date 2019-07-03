package net.minecraft;

import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplate;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4434 {
	private static final Logger field_20211 = LogManager.getLogger();

	private static void method_21553(int i) {
		try {
			Thread.sleep((long)(i * 1000));
		} catch (InterruptedException var2) {
			field_20211.error("", (Throwable)var2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4435 extends class_4358 {
		private final RealmsServer field_20212;
		private final class_4388 field_20213;

		public class_4435(RealmsServer realmsServer, class_4388 arg) {
			this.field_20212 = realmsServer;
			this.field_20213 = arg;
		}

		public void run() {
			this.method_21069(RealmsScreen.getLocalizedString("mco.configure.world.closing"));
			class_4341 lv = class_4341.method_20989();

			for (int i = 0; i < 25; i++) {
				if (this.method_21065()) {
					return;
				}

				try {
					boolean bl = lv.method_21022(this.field_20212.id);
					if (bl) {
						this.field_20213.method_21198();
						this.field_20212.state = RealmsServer.class_4320.CLOSED;
						Realms.setScreen(this.field_20213);
						break;
					}
				} catch (class_4356 var4) {
					if (this.method_21065()) {
						return;
					}

					class_4434.method_21553(var4.field_19608);
				} catch (Exception var5) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Failed to close server", (Throwable)var5);
					this.method_21067("Failed to close the server");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4436 extends class_4358 {
		private final long field_20214;
		private final int field_20215;
		private final RealmsScreen field_20216;
		private final String field_20217;

		public class_4436(long l, int i, String string, RealmsScreen realmsScreen) {
			this.field_20214 = l;
			this.field_20215 = i;
			this.field_20216 = realmsScreen;
			this.field_20217 = string;
		}

		public void run() {
			this.method_21069(RealmsScreen.getLocalizedString("mco.download.preparing"));
			class_4341 lv = class_4341.method_20989();
			int i = 0;

			while (i < 25) {
				try {
					if (this.method_21065()) {
						return;
					}

					WorldDownload worldDownload = lv.method_21003(this.field_20214, this.field_20215);
					class_4434.method_21553(1);
					if (this.method_21065()) {
						return;
					}

					Realms.setScreen(new class_4392(this.field_20216, worldDownload, this.field_20217));
					return;
				} catch (class_4356 var4) {
					if (this.method_21065()) {
						return;
					}

					class_4434.method_21553(var4.field_19608);
					i++;
				} catch (class_4355 var5) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Couldn't download world data");
					Realms.setScreen(new class_4394(var5, this.field_20216));
					return;
				} catch (Exception var6) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Couldn't download world data", (Throwable)var6);
					this.method_21067(var6.getLocalizedMessage());
					return;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4437 extends class_4358 {
		private final RealmsServer field_20218;
		private final RealmsScreen field_20219;
		private final boolean field_20220;
		private final RealmsScreen field_20221;

		public class_4437(RealmsServer realmsServer, RealmsScreen realmsScreen, RealmsScreen realmsScreen2, boolean bl) {
			this.field_20218 = realmsServer;
			this.field_20219 = realmsScreen;
			this.field_20220 = bl;
			this.field_20221 = realmsScreen2;
		}

		public void run() {
			this.method_21069(RealmsScreen.getLocalizedString("mco.configure.world.opening"));
			class_4341 lv = class_4341.method_20989();

			for (int i = 0; i < 25; i++) {
				if (this.method_21065()) {
					return;
				}

				try {
					boolean bl = lv.method_21019(this.field_20218.id);
					if (bl) {
						if (this.field_20219 instanceof class_4388) {
							((class_4388)this.field_20219).method_21198();
						}

						this.field_20218.state = RealmsServer.class_4320.OPEN;
						if (this.field_20220) {
							((class_4325)this.field_20221).method_20853(this.field_20218, this.field_20219);
						} else {
							Realms.setScreen(this.field_20219);
						}
						break;
					}
				} catch (class_4356 var4) {
					if (this.method_21065()) {
						return;
					}

					class_4434.method_21553(var4.field_19608);
				} catch (Exception var5) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Failed to open server", (Throwable)var5);
					this.method_21067("Failed to open the server");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4438 extends class_4358 {
		private final RealmsConnect field_20222;
		private final RealmsServerAddress field_20223;

		public class_4438(RealmsScreen realmsScreen, RealmsServerAddress realmsServerAddress) {
			this.field_20223 = realmsServerAddress;
			this.field_20222 = new RealmsConnect(realmsScreen);
		}

		public void run() {
			this.method_21069(RealmsScreen.getLocalizedString("mco.connect.connecting"));
			net.minecraft.realms.RealmsServerAddress realmsServerAddress = net.minecraft.realms.RealmsServerAddress.parseString(this.field_20223.address);
			this.field_20222.connect(realmsServerAddress.getHost(), realmsServerAddress.getPort());
		}

		@Override
		public void method_21071() {
			this.field_20222.abort();
			Realms.clearResourcePack();
		}

		@Override
		public void method_21068() {
			this.field_20222.tick();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4439 extends class_4358 {
		private final RealmsServer field_20224;
		private final RealmsScreen field_20225;
		private final class_4325 field_20226;
		private final ReentrantLock field_20227;

		public class_4439(class_4325 arg, RealmsScreen realmsScreen, RealmsServer realmsServer, ReentrantLock reentrantLock) {
			this.field_20225 = realmsScreen;
			this.field_20226 = arg;
			this.field_20224 = realmsServer;
			this.field_20227 = reentrantLock;
		}

		public void run() {
			this.method_21069(RealmsScreen.getLocalizedString("mco.connect.connecting"));
			class_4341 lv = class_4341.method_20989();
			boolean bl = false;
			boolean bl2 = false;
			int i = 5;
			RealmsServerAddress realmsServerAddress = null;
			boolean bl3 = false;
			boolean bl4 = false;

			for (int j = 0; j < 40 && !this.method_21065(); j++) {
				try {
					realmsServerAddress = lv.method_21009(this.field_20224.id);
					bl = true;
				} catch (class_4356 var10) {
					i = var10.field_19608;
				} catch (class_4355 var11) {
					if (var11.field_19606 == 6002) {
						bl3 = true;
					} else if (var11.field_19606 == 6006) {
						bl4 = true;
					} else {
						bl2 = true;
						this.method_21067(var11.toString());
						class_4434.field_20211.error("Couldn't connect to world", (Throwable)var11);
					}
					break;
				} catch (IOException var12) {
					class_4434.field_20211.error("Couldn't parse response connecting to world", (Throwable)var12);
				} catch (Exception var13) {
					bl2 = true;
					class_4434.field_20211.error("Couldn't connect to world", (Throwable)var13);
					this.method_21067(var13.getLocalizedMessage());
					break;
				}

				if (bl) {
					break;
				}

				this.method_21554(i);
			}

			if (bl3) {
				Realms.setScreen(new class_4426(this.field_20225, this.field_20226, this.field_20224));
			} else if (bl4) {
				if (this.field_20224.ownerUUID.equals(Realms.getUUID())) {
					class_4384 lv4 = new class_4384(this.field_20225, this.field_20226, this.field_20224.id);
					if (this.field_20224.worldType.equals(RealmsServer.class_4321.MINIGAME)) {
						lv4.method_21185(RealmsScreen.getLocalizedString("mco.brokenworld.minigame.title"));
					}

					Realms.setScreen(lv4);
				} else {
					Realms.setScreen(
						new class_4394(
							RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.title"), RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.error"), this.field_20225
						)
					);
				}
			} else if (!this.method_21065() && !bl2) {
				if (bl) {
					if (realmsServerAddress.resourcePackUrl != null && realmsServerAddress.resourcePackHash != null) {
						String string = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line1");
						String string2 = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line2");
						Realms.setScreen(
							new class_4396(new class_4414(this.field_20225, realmsServerAddress, this.field_20227), class_4396.class_4397.INFO, string, string2, true, 100)
						);
					} else {
						class_4398 lv5 = new class_4398(this.field_20225, new class_4434.class_4438(this.field_20225, realmsServerAddress));
						lv5.method_21288();
						Realms.setScreen(lv5);
					}
				} else {
					this.method_21067(RealmsScreen.getLocalizedString("mco.errorMessage.connectionFailure"));
				}
			}
		}

		private void method_21554(int i) {
			try {
				Thread.sleep((long)(i * 1000));
			} catch (InterruptedException var3) {
				class_4434.field_20211.warn(var3.getLocalizedMessage());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4440 extends class_4358 {
		private final String field_20228;
		private final WorldTemplate field_20229;
		private final int field_20230;
		private final boolean field_20231;
		private final long field_20232;
		private final RealmsScreen field_20233;
		private int field_20234 = -1;
		private String field_20235 = RealmsScreen.getLocalizedString("mco.reset.world.resetting.screen.title");

		public class_4440(long l, RealmsScreen realmsScreen, WorldTemplate worldTemplate) {
			this.field_20228 = null;
			this.field_20229 = worldTemplate;
			this.field_20230 = -1;
			this.field_20231 = true;
			this.field_20232 = l;
			this.field_20233 = realmsScreen;
		}

		public class_4440(long l, RealmsScreen realmsScreen, String string, int i, boolean bl) {
			this.field_20228 = string;
			this.field_20229 = null;
			this.field_20230 = i;
			this.field_20231 = bl;
			this.field_20232 = l;
			this.field_20233 = realmsScreen;
		}

		public void method_21555(int i) {
			this.field_20234 = i;
		}

		public void method_21556(String string) {
			this.field_20235 = string;
		}

		public void run() {
			class_4341 lv = class_4341.method_20989();
			this.method_21069(this.field_20235);
			int i = 0;

			while (i < 25) {
				try {
					if (this.method_21065()) {
						return;
					}

					if (this.field_20229 != null) {
						lv.method_21023(this.field_20232, this.field_20229.id);
					} else {
						lv.method_20995(this.field_20232, this.field_20228, this.field_20230, this.field_20231);
					}

					if (this.method_21065()) {
						return;
					}

					if (this.field_20234 == -1) {
						Realms.setScreen(this.field_20233);
					} else {
						this.field_20233.confirmResult(true, this.field_20234);
					}

					return;
				} catch (class_4356 var4) {
					if (this.method_21065()) {
						return;
					}

					class_4434.method_21553(var4.field_19608);
					i++;
				} catch (Exception var5) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Couldn't reset world");
					this.method_21067(var5.toString());
					return;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4441 extends class_4358 {
		private final Backup field_20236;
		private final long field_20237;
		private final class_4388 field_20238;

		public class_4441(Backup backup, long l, class_4388 arg) {
			this.field_20236 = backup;
			this.field_20237 = l;
			this.field_20238 = arg;
		}

		public void run() {
			this.method_21069(RealmsScreen.getLocalizedString("mco.backup.restoring"));
			class_4341 lv = class_4341.method_20989();
			int i = 0;

			while (i < 25) {
				try {
					if (this.method_21065()) {
						return;
					}

					lv.method_21010(this.field_20237, this.field_20236.backupId);
					class_4434.method_21553(1);
					if (this.method_21065()) {
						return;
					}

					Realms.setScreen(this.field_20238.method_21219());
					return;
				} catch (class_4356 var4) {
					if (this.method_21065()) {
						return;
					}

					class_4434.method_21553(var4.field_19608);
					i++;
				} catch (class_4355 var5) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Couldn't restore backup", (Throwable)var5);
					Realms.setScreen(new class_4394(var5, this.field_20238));
					return;
				} catch (Exception var6) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Couldn't restore backup", (Throwable)var6);
					this.method_21067(var6.getLocalizedMessage());
					return;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4442 extends class_4358 {
		private final long field_20239;
		private final WorldTemplate field_20240;
		private final class_4388 field_20241;

		public class_4442(long l, WorldTemplate worldTemplate, class_4388 arg) {
			this.field_20239 = l;
			this.field_20240 = worldTemplate;
			this.field_20241 = arg;
		}

		public void run() {
			class_4341 lv = class_4341.method_20989();
			String string = RealmsScreen.getLocalizedString("mco.minigame.world.starting.screen.title");
			this.method_21069(string);

			for (int i = 0; i < 25; i++) {
				try {
					if (this.method_21065()) {
						return;
					}

					if (lv.method_21014(this.field_20239, this.field_20240.id)) {
						Realms.setScreen(this.field_20241);
						break;
					}
				} catch (class_4356 var5) {
					if (this.method_21065()) {
						return;
					}

					class_4434.method_21553(var5.field_19608);
				} catch (Exception var6) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Couldn't start mini game!");
					this.method_21067(var6.toString());
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4443 extends class_4358 {
		private final long field_20242;
		private final int field_20243;
		private final RealmsConfirmResultListener field_20244;
		private final int field_20245;

		public class_4443(long l, int i, RealmsConfirmResultListener realmsConfirmResultListener, int j) {
			this.field_20242 = l;
			this.field_20243 = i;
			this.field_20244 = realmsConfirmResultListener;
			this.field_20245 = j;
		}

		public void run() {
			class_4341 lv = class_4341.method_20989();
			String string = RealmsScreen.getLocalizedString("mco.minigame.world.slot.screen.title");
			this.method_21069(string);

			for (int i = 0; i < 25; i++) {
				try {
					if (this.method_21065()) {
						return;
					}

					if (lv.method_20992(this.field_20242, this.field_20243)) {
						this.field_20244.confirmResult(true, this.field_20245);
						break;
					}
				} catch (class_4356 var5) {
					if (this.method_21065()) {
						return;
					}

					class_4434.method_21553(var5.field_19608);
				} catch (Exception var6) {
					if (this.method_21065()) {
						return;
					}

					class_4434.field_20211.error("Couldn't switch world!");
					this.method_21067(var6.toString());
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4444 extends class_4358 {
		private final String field_20246;
		private final String field_20247;
		private final class_4325 field_20248;

		public class_4444(String string, String string2, class_4325 arg) {
			this.field_20246 = string;
			this.field_20247 = string2;
			this.field_20248 = arg;
		}

		public void run() {
			String string = RealmsScreen.getLocalizedString("mco.create.world.wait");
			this.method_21069(string);
			class_4341 lv = class_4341.method_20989();

			try {
				RealmsServer realmsServer = lv.method_21000(this.field_20246, this.field_20247);
				if (realmsServer != null) {
					this.field_20248.method_20869(true);
					this.field_20248.method_20905();
					class_4410 lv2 = new class_4410(
						this.field_20248,
						realmsServer,
						this.field_20248.method_20902(),
						RealmsScreen.getLocalizedString("mco.selectServer.create"),
						RealmsScreen.getLocalizedString("mco.create.world.subtitle"),
						10526880,
						RealmsScreen.getLocalizedString("mco.create.world.skip")
					);
					lv2.method_21376(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
					Realms.setScreen(lv2);
				} else {
					this.method_21067(RealmsScreen.getLocalizedString("mco.trial.unavailable"));
				}
			} catch (class_4355 var5) {
				class_4434.field_20211.error("Couldn't create trial");
				this.method_21067(var5.toString());
			} catch (UnsupportedEncodingException var6) {
				class_4434.field_20211.error("Couldn't create trial");
				this.method_21067(var6.getLocalizedMessage());
			} catch (IOException var7) {
				class_4434.field_20211.error("Could not parse response creating trial");
				this.method_21067(var7.getLocalizedMessage());
			} catch (Exception var8) {
				class_4434.field_20211.error("Could not create trial");
				this.method_21067(var8.getLocalizedMessage());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4445 extends class_4358 {
		private final String field_20249;
		private final String field_20250;
		private final long field_20251;
		private final RealmsScreen field_20252;

		public class_4445(long l, String string, String string2, RealmsScreen realmsScreen) {
			this.field_20251 = l;
			this.field_20249 = string;
			this.field_20250 = string2;
			this.field_20252 = realmsScreen;
		}

		public void run() {
			String string = RealmsScreen.getLocalizedString("mco.create.world.wait");
			this.method_21069(string);
			class_4341 lv = class_4341.method_20989();

			try {
				lv.method_20996(this.field_20251, this.field_20249, this.field_20250);
				Realms.setScreen(this.field_20252);
			} catch (class_4355 var4) {
				class_4434.field_20211.error("Couldn't create world");
				this.method_21067(var4.toString());
			} catch (UnsupportedEncodingException var5) {
				class_4434.field_20211.error("Couldn't create world");
				this.method_21067(var5.getLocalizedMessage());
			} catch (IOException var6) {
				class_4434.field_20211.error("Could not parse response creating world");
				this.method_21067(var6.getLocalizedMessage());
			} catch (Exception var7) {
				class_4434.field_20211.error("Could not create world");
				this.method_21067(var7.getLocalizedMessage());
			}
		}
	}
}
