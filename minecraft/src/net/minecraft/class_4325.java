package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4325 extends RealmsScreen {
	private static final Logger field_19475 = LogManager.getLogger();
	private static boolean field_19476;
	private final RateLimiter field_19477;
	private boolean field_19478;
	private static final String[] field_19479 = new String[]{
		"realms:textures/gui/realms/images/sand_castle.png",
		"realms:textures/gui/realms/images/factory_floor.png",
		"realms:textures/gui/realms/images/escher_tunnel.png",
		"realms:textures/gui/realms/images/tree_houses.png",
		"realms:textures/gui/realms/images/balloon_trip.png",
		"realms:textures/gui/realms/images/halloween_woods.png",
		"realms:textures/gui/realms/images/flower_mountain.png",
		"realms:textures/gui/realms/images/dornenstein_estate.png",
		"realms:textures/gui/realms/images/desert.png",
		"realms:textures/gui/realms/images/gray.png",
		"realms:textures/gui/realms/images/imperium.png",
		"realms:textures/gui/realms/images/ludo.png",
		"realms:textures/gui/realms/images/makersspleef.png",
		"realms:textures/gui/realms/images/negentropy.png",
		"realms:textures/gui/realms/images/pumpkin_party.png",
		"realms:textures/gui/realms/images/sparrenhout.png",
		"realms:textures/gui/realms/images/spindlewood.png"
	};
	private static final class_4360 field_19480 = new class_4360();
	private static int field_19481 = -1;
	private final RealmsScreen field_19482;
	private volatile class_4325.class_4329 field_19483;
	private long field_19484 = -1L;
	private RealmsButton field_19485;
	private RealmsButton field_19486;
	private RealmsButton field_19487;
	private RealmsButton field_19488;
	private RealmsButton field_19489;
	private String field_19490;
	private List<RealmsServer> field_19491 = Lists.<RealmsServer>newArrayList();
	private volatile int field_19492;
	private int field_19493;
	private static volatile boolean field_19494;
	private static volatile boolean field_19495;
	private static volatile boolean field_19496;
	private boolean field_19497;
	private boolean field_19498;
	private boolean field_19499;
	private volatile boolean field_19500;
	private volatile boolean field_19456;
	private volatile boolean field_19457;
	private volatile boolean field_19458;
	private volatile String field_19459;
	private int field_19460;
	private int field_19461;
	private boolean field_19462;
	private static RealmsScreen field_19463;
	private static boolean field_19464;
	private List<class_4324> field_19465;
	private int field_19466;
	private ReentrantLock field_19467 = new ReentrantLock();
	private boolean field_19468;
	private class_4325.class_4332 field_19469;
	private class_4325.class_4328 field_19470;
	private class_4325.class_4327 field_19471;
	private RealmsButton field_19472;
	private RealmsButton field_19473;
	private RealmsButton field_19474;

	public class_4325(RealmsScreen realmsScreen) {
		this.field_19482 = realmsScreen;
		this.field_19477 = RateLimiter.create(0.016666668F);
	}

	public boolean method_20842() {
		if (this.method_20918() && this.field_19497) {
			if (this.field_19500 && !this.field_19456) {
				return true;
			} else {
				for (RealmsServer realmsServer : this.field_19491) {
					if (realmsServer.ownerUUID.equals(Realms.getUUID())) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	public boolean method_20870() {
		if (!this.method_20918() || !this.field_19497) {
			return false;
		} else if (this.field_19498) {
			return true;
		} else {
			return this.field_19500 && !this.field_19456 && this.field_19491.isEmpty() ? true : this.field_19491.isEmpty();
		}
	}

	@Override
	public void init() {
		this.field_19465 = Lists.<class_4324>newArrayList(
			new class_4324(new char[]{'3', '2', '1', '4', '5', '6'}, () -> field_19476 = !field_19476), new class_4324(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
				if (class_4341.field_19576.equals(class_4341.class_4343.STAGE)) {
					this.method_20941();
				} else {
					this.method_20938();
				}
			}), new class_4324(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
				if (class_4341.field_19576.equals(class_4341.class_4343.LOCAL)) {
					this.method_20941();
				} else {
					this.method_20940();
				}
			})
		);
		if (field_19463 != null) {
			Realms.setScreen(field_19463);
		} else {
			this.field_19467 = new ReentrantLock();
			if (field_19496 && !this.method_20918()) {
				this.method_20936();
			}

			this.method_20932();
			this.method_20934();
			if (!this.field_19478) {
				Realms.setConnectedToRealms(false);
			}

			this.setKeyboardHandlerSendRepeatsToGui(true);
			if (this.method_20918()) {
				field_19480.method_21090();
			}

			this.field_19457 = false;
			this.method_20890();
		}
	}

	private boolean method_20918() {
		return field_19495 && field_19494;
	}

	public void method_20882() {
		this.buttonsAdd(
			this.field_19488 = new RealmsButton(1, this.width() / 2 - 190, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.configure")) {
				@Override
				public void onPress() {
					class_4325.this.method_20903(class_4325.this.method_20851(class_4325.this.field_19484));
				}
			}
		);
		this.buttonsAdd(this.field_19485 = new RealmsButton(3, this.width() / 2 - 93, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.play")) {
			@Override
			public void onPress() {
				class_4325.this.method_20926();
			}
		});
		this.buttonsAdd(this.field_19486 = new RealmsButton(2, this.width() / 2 + 4, this.height() - 32, 90, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				if (!class_4325.this.field_19499) {
					Realms.setScreen(class_4325.this.field_19482);
				}
			}
		});
		this.buttonsAdd(
			this.field_19487 = new RealmsButton(0, this.width() / 2 + 100, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.expiredRenew")) {
				@Override
				public void onPress() {
					class_4325.this.method_20928();
				}
			}
		);
		this.buttonsAdd(this.field_19489 = new RealmsButton(7, this.width() / 2 - 202, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.leave")) {
			@Override
			public void onPress() {
				class_4325.this.method_20906(class_4325.this.method_20851(class_4325.this.field_19484));
			}
		});
		this.buttonsAdd(this.field_19470 = new class_4325.class_4328());
		this.buttonsAdd(this.field_19471 = new class_4325.class_4327());
		this.buttonsAdd(this.field_19469 = new class_4325.class_4332());
		this.buttonsAdd(this.field_19474 = new class_4325.class_4326());
		this.buttonsAdd(
			this.field_19472 = new RealmsButton(6, this.width() / 2 + 52, this.method_20836() + 137 - 20, 98, 20, getLocalizedString("mco.selectServer.trial")) {
				@Override
				public void onPress() {
					class_4325.this.method_20930();
				}
			}
		);
		this.buttonsAdd(
			this.field_19473 = new RealmsButton(5, this.width() / 2 + 52, this.method_20836() + 160 - 20, 98, 20, getLocalizedString("mco.selectServer.buy")) {
				@Override
				public void onPress() {
					class_4448.method_21570("https://minecraft.net/realms");
				}
			}
		);
		RealmsServer realmsServer = this.method_20851(this.field_19484);
		this.method_20852(realmsServer);
	}

	private void method_20852(RealmsServer realmsServer) {
		this.field_19485.active(this.method_20874(realmsServer) && !this.method_20870());
		this.field_19487.setVisible(this.method_20884(realmsServer));
		this.field_19488.setVisible(this.method_20892(realmsServer));
		this.field_19489.setVisible(this.method_20899(realmsServer));
		boolean bl = this.method_20870() && this.field_19500 && !this.field_19456;
		this.field_19472.setVisible(bl);
		this.field_19472.active(bl);
		this.field_19473.setVisible(this.method_20870());
		this.field_19474.setVisible(this.method_20870() && this.field_19498);
		this.field_19487.active(!this.method_20870());
		this.field_19488.active(!this.method_20870());
		this.field_19489.active(!this.method_20870());
		this.field_19471.active(true);
		this.field_19470.active(true);
		this.field_19486.active(true);
		this.field_19469.active(!this.method_20870());
	}

	private boolean method_20920() {
		return (!this.method_20870() || this.field_19498) && this.method_20918() && this.field_19497;
	}

	private boolean method_20874(RealmsServer realmsServer) {
		return realmsServer != null && !realmsServer.expired && realmsServer.state == RealmsServer.class_4320.OPEN;
	}

	private boolean method_20884(RealmsServer realmsServer) {
		return realmsServer != null && realmsServer.expired && this.method_20909(realmsServer);
	}

	private boolean method_20892(RealmsServer realmsServer) {
		return realmsServer != null && this.method_20909(realmsServer);
	}

	private boolean method_20899(RealmsServer realmsServer) {
		return realmsServer != null && !this.method_20909(realmsServer);
	}

	public void method_20890() {
		if (this.method_20918() && this.field_19497) {
			this.method_20882();
		}

		this.field_19483 = new class_4325.class_4329();
		if (field_19481 != -1) {
			this.field_19483.scroll(field_19481);
		}

		this.addWidget(this.field_19483);
		this.focusOn(this.field_19483);
	}

	@Override
	public void tick() {
		this.tickButtons();
		this.field_19499 = false;
		this.field_19493++;
		this.field_19466--;
		if (this.field_19466 < 0) {
			this.field_19466 = 0;
		}

		if (this.method_20918()) {
			field_19480.method_21083();
			if (field_19480.method_21075(class_4360.class_4364.SERVER_LIST)) {
				List<RealmsServer> list = field_19480.method_21091();
				this.field_19483.clear();
				boolean bl = !this.field_19497;
				if (bl) {
					this.field_19497 = true;
				}

				if (list != null) {
					boolean bl2 = false;

					for (RealmsServer realmsServer : list) {
						if (this.method_20912(realmsServer)) {
							bl2 = true;
						}
					}

					this.field_19491 = list;
					if (this.method_20842()) {
						this.field_19483.addEntry(new class_4325.class_4331());
					}

					for (RealmsServer realmsServerx : this.field_19491) {
						this.field_19483.addEntry(new class_4325.class_4330(realmsServerx));
					}

					if (!field_19464 && bl2) {
						field_19464 = true;
						this.method_20922();
					}
				}

				if (bl) {
					this.method_20882();
				}
			}

			if (field_19480.method_21075(class_4360.class_4364.PENDING_INVITE)) {
				this.field_19492 = field_19480.method_21092();
				if (this.field_19492 > 0 && this.field_19477.tryAcquire(1)) {
					Realms.narrateNow(getLocalizedString("mco.configure.world.invite.narration", new Object[]{this.field_19492}));
				}
			}

			if (field_19480.method_21075(class_4360.class_4364.TRIAL_AVAILABLE) && !this.field_19456) {
				boolean bl3 = field_19480.method_21093();
				if (bl3 != this.field_19500 && this.method_20870()) {
					this.field_19500 = bl3;
					this.field_19457 = false;
				} else {
					this.field_19500 = bl3;
				}
			}

			if (field_19480.method_21075(class_4360.class_4364.LIVE_STATS)) {
				RealmsServerPlayerLists realmsServerPlayerLists = field_19480.method_21094();

				for (RealmsServerPlayerList realmsServerPlayerList : realmsServerPlayerLists.servers) {
					for (RealmsServer realmsServerx : this.field_19491) {
						if (realmsServerx.id == realmsServerPlayerList.serverId) {
							realmsServerx.updateServerPing(realmsServerPlayerList);
							break;
						}
					}
				}
			}

			if (field_19480.method_21075(class_4360.class_4364.UNREAD_NEWS)) {
				this.field_19458 = field_19480.method_21095();
				this.field_19459 = field_19480.method_21096();
			}

			field_19480.method_21088();
			if (this.method_20870()) {
				this.field_19461++;
			}

			if (this.field_19469 != null) {
				this.field_19469.setVisible(this.method_20920());
			}
		}
	}

	private void method_20866(String string) {
		Realms.setClipboard(string);
		class_4448.method_21570(string);
	}

	private void method_20922() {
		(new Thread() {
			public void run() {
				List<RegionPingResult> list = class_4339.method_20981();
				class_4341 lv = class_4341.method_20989();
				PingResult pingResult = new PingResult();
				pingResult.pingResults = list;
				pingResult.worldIds = class_4325.this.method_20924();

				try {
					lv.method_20997(pingResult);
				} catch (Throwable var5) {
					class_4325.field_19475.warn("Could not send ping result to Realms: ", var5);
				}
			}
		}).start();
	}

	private List<Long> method_20924() {
		List<Long> list = new ArrayList();

		for (RealmsServer realmsServer : this.field_19491) {
			if (this.method_20912(realmsServer)) {
				list.add(realmsServer.id);
			}
		}

		return list;
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
		this.method_20942();
	}

	public void method_20869(boolean bl) {
		this.field_19456 = bl;
	}

	private void method_20926() {
		RealmsServer realmsServer = this.method_20851(this.field_19484);
		if (realmsServer != null) {
			this.method_20853(realmsServer, this);
		}
	}

	private void method_20928() {
		RealmsServer realmsServer = this.method_20851(this.field_19484);
		if (realmsServer != null) {
			String string = "https://account.mojang.com/buy/realms?sid="
				+ realmsServer.remoteSubscriptionId
				+ "&pid="
				+ Realms.getUUID()
				+ "&ref="
				+ (realmsServer.expiredTrial ? "expiredTrial" : "expiredRealm");
			this.method_20866(string);
		}
	}

	private void method_20930() {
		if (this.field_19500 && !this.field_19456) {
			Realms.setScreen(new class_4391(this));
		}
	}

	private void method_20932() {
		if (!field_19496) {
			field_19496 = true;
			(new Thread("MCO Compatability Checker #1") {
					public void run() {
						class_4341 lv = class_4341.method_20989();

						try {
							class_4341.class_4342 lv2 = lv.method_21027();
							if (lv2.equals(class_4341.class_4342.OUTDATED)) {
								class_4325.field_19463 = new class_4387(class_4325.this.field_19482, true);
								Realms.setScreen(class_4325.field_19463);
							} else if (lv2.equals(class_4341.class_4342.OTHER)) {
								class_4325.field_19463 = new class_4387(class_4325.this.field_19482, false);
								Realms.setScreen(class_4325.field_19463);
							} else {
								class_4325.this.method_20936();
							}
						} catch (class_4355 var3) {
							class_4325.field_19496 = false;
							class_4325.field_19475.error("Couldn't connect to realms: ", var3.toString());
							if (var3.field_19604 == 401) {
								class_4325.field_19463 = new class_4394(
									RealmsScreen.getLocalizedString("mco.error.invalid.session.title"),
									RealmsScreen.getLocalizedString("mco.error.invalid.session.message"),
									class_4325.this.field_19482
								);
								Realms.setScreen(class_4325.field_19463);
							} else {
								Realms.setScreen(new class_4394(var3, class_4325.this.field_19482));
							}
						} catch (IOException var4) {
							class_4325.field_19496 = false;
							class_4325.field_19475.error("Couldn't connect to realms: ", var4.getMessage());
							Realms.setScreen(new class_4394(var4.getMessage(), class_4325.this.field_19482));
						}
					}
				})
				.start();
		}
	}

	private void method_20934() {
	}

	private void method_20936() {
		(new Thread("MCO Compatability Checker #1") {
			public void run() {
				class_4341 lv = class_4341.method_20989();

				try {
					Boolean boolean_ = lv.method_21021();
					if (boolean_) {
						class_4325.field_19475.info("Realms is available for this user");
						class_4325.field_19494 = true;
					} else {
						class_4325.field_19475.info("Realms is not available for this user");
						class_4325.field_19494 = false;
						Realms.setScreen(new class_4400(class_4325.this.field_19482));
					}

					class_4325.field_19495 = true;
				} catch (class_4355 var3) {
					class_4325.field_19475.error("Couldn't connect to realms: ", var3.toString());
					Realms.setScreen(new class_4394(var3, class_4325.this.field_19482));
				} catch (IOException var4) {
					class_4325.field_19475.error("Couldn't connect to realms: ", var4.getMessage());
					Realms.setScreen(new class_4394(var4.getMessage(), class_4325.this.field_19482));
				}
			}
		}).start();
	}

	private void method_20938() {
		if (!class_4341.field_19576.equals(class_4341.class_4343.STAGE)) {
			(new Thread("MCO Stage Availability Checker #1") {
				public void run() {
					class_4341 lv = class_4341.method_20989();

					try {
						Boolean boolean_ = lv.method_21024();
						if (boolean_) {
							class_4341.method_21001();
							class_4325.field_19475.info("Switched to stage");
							class_4325.field_19480.method_21090();
						}
					} catch (class_4355 var3) {
						class_4325.field_19475.error("Couldn't connect to Realms: " + var3);
					} catch (IOException var4) {
						class_4325.field_19475.error("Couldn't parse response connecting to Realms: " + var4.getMessage());
					}
				}
			}).start();
		}
	}

	private void method_20940() {
		if (!class_4341.field_19576.equals(class_4341.class_4343.LOCAL)) {
			(new Thread("MCO Local Availability Checker #1") {
				public void run() {
					class_4341 lv = class_4341.method_20989();

					try {
						Boolean boolean_ = lv.method_21024();
						if (boolean_) {
							class_4341.method_21012();
							class_4325.field_19475.info("Switched to local");
							class_4325.field_19480.method_21090();
						}
					} catch (class_4355 var3) {
						class_4325.field_19475.error("Couldn't connect to Realms: " + var3);
					} catch (IOException var4) {
						class_4325.field_19475.error("Couldn't parse response connecting to Realms: " + var4.getMessage());
					}
				}
			}).start();
		}
	}

	private void method_20941() {
		class_4341.method_21008();
		field_19480.method_21090();
	}

	private void method_20942() {
		field_19480.method_21097();
	}

	private void method_20903(RealmsServer realmsServer) {
		if (Realms.getUUID().equals(realmsServer.ownerUUID) || field_19476) {
			this.method_20943();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.execute(() -> minecraftClient.openScreen(new class_4388(this, realmsServer.id).getProxy()));
		}
	}

	private void method_20906(RealmsServer realmsServer) {
		if (!Realms.getUUID().equals(realmsServer.ownerUUID)) {
			this.method_20943();
			String string = getLocalizedString("mco.configure.world.leave.question.line1");
			String string2 = getLocalizedString("mco.configure.world.leave.question.line2");
			Realms.setScreen(new class_4396(this, class_4396.class_4397.INFO, string, string2, true, 4));
		}
	}

	private void method_20943() {
		field_19481 = this.field_19483.getScroll();
	}

	private RealmsServer method_20851(long l) {
		for (RealmsServer realmsServer : this.field_19491) {
			if (realmsServer.id == l) {
				return realmsServer;
			}
		}

		return null;
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 4) {
			if (bl) {
				(new Thread("Realms-leave-server") {
					public void run() {
						try {
							RealmsServer realmsServer = class_4325.this.method_20851(class_4325.this.field_19484);
							if (realmsServer != null) {
								class_4341 lv = class_4341.method_20989();
								lv.method_21013(realmsServer.id);
								class_4325.field_19480.method_21074(realmsServer);
								class_4325.this.field_19491.remove(realmsServer);
								class_4325.this.field_19484 = -1L;
								class_4325.this.field_19485.active(false);
							}
						} catch (class_4355 var3) {
							class_4325.field_19475.error("Couldn't configure world");
							Realms.setScreen(new class_4394(var3, class_4325.this));
						}
					}
				}).start();
			}

			Realms.setScreen(this);
		}
	}

	public void method_20897() {
		this.field_19484 = -1L;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		switch (i) {
			case 256:
				this.field_19465.forEach(class_4324::method_20832);
				this.method_20834();
				return true;
			default:
				return super.keyPressed(i, j, k);
		}
	}

	private void method_20834() {
		if (this.method_20870() && this.field_19498) {
			this.field_19498 = false;
		} else {
			Realms.setScreen(this.field_19482);
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		this.field_19465.forEach(arg -> arg.method_20833(c));
		return true;
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_19468 = false;
		this.field_19490 = null;
		this.renderBackground();
		this.field_19483.render(i, j, f);
		this.method_20845(this.width() / 2 - 50, 7);
		if (class_4341.field_19576.equals(class_4341.class_4343.STAGE)) {
			this.method_20838();
		}

		if (class_4341.field_19576.equals(class_4341.class_4343.LOCAL)) {
			this.method_20837();
		}

		if (this.method_20870()) {
			this.method_20872(i, j);
		} else {
			if (this.field_19457) {
				this.method_20852(null);
				if (!this.hasWidget(this.field_19483)) {
					this.addWidget(this.field_19483);
				}

				RealmsServer realmsServer = this.method_20851(this.field_19484);
				this.field_19485.active(this.method_20874(realmsServer));
			}

			this.field_19457 = false;
		}

		super.render(i, j, f);
		if (this.field_19490 != null) {
			this.method_20867(this.field_19490, i, j);
		}

		if (this.field_19500 && !this.field_19456 && this.method_20870()) {
			RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			int k = 8;
			int l = 8;
			int m = 0;
			if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
				m = 8;
			}

			RealmsScreen.blit(
				this.field_19472.x() + this.field_19472.getWidth() - 8 - 4, this.field_19472.y() + this.field_19472.getHeight() / 2 - 4, 0.0F, (float)m, 8, 8, 8, 16
			);
			GlStateManager.popMatrix();
		}
	}

	private void method_20845(int i, int j) {
		RealmsScreen.bind("realms:textures/gui/title/realms.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.scalef(0.5F, 0.5F, 0.5F);
		RealmsScreen.blit(i * 2, j * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.method_20844(d, e) && this.field_19498) {
			this.field_19498 = false;
			this.field_19499 = true;
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	private boolean method_20844(double d, double e) {
		int i = this.method_20835();
		int j = this.method_20836();
		return d < (double)(i - 5) || d > (double)(i + 315) || e < (double)(j - 5) || e > (double)(j + 171);
	}

	private void method_20872(int i, int j) {
		int k = this.method_20835();
		int l = this.method_20836();
		String string = getLocalizedString("mco.selectServer.popup");
		List<String> list = this.fontSplit(string, 100);
		if (!this.field_19457) {
			this.field_19460 = 0;
			this.field_19461 = 0;
			this.field_19462 = true;
			this.method_20852(null);
			if (this.hasWidget(this.field_19483)) {
				this.removeWidget(this.field_19483);
			}

			Realms.narrateNow(string);
		}

		if (this.field_19497) {
			this.field_19457 = true;
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.7F);
		GlStateManager.enableBlend();
		RealmsScreen.bind("realms:textures/gui/realms/darken.png");
		GlStateManager.pushMatrix();
		int m = 0;
		int n = 32;
		RealmsScreen.blit(0, 32, 0.0F, 0.0F, this.width(), this.height() - 40 - 32, 310, 166);
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RealmsScreen.bind("realms:textures/gui/realms/popup.png");
		GlStateManager.pushMatrix();
		RealmsScreen.blit(k, l, 0.0F, 0.0F, 310, 166, 310, 166);
		GlStateManager.popMatrix();
		RealmsScreen.bind(field_19479[this.field_19460]);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(k + 7, l + 7, 0.0F, 0.0F, 195, 152, 195, 152);
		GlStateManager.popMatrix();
		if (this.field_19461 % 95 < 5) {
			if (!this.field_19462) {
				if (this.field_19460 == field_19479.length - 1) {
					this.field_19460 = 0;
				} else {
					this.field_19460++;
				}

				this.field_19462 = true;
			}
		} else {
			this.field_19462 = false;
		}

		int o = 0;

		for (String string2 : list) {
			int var10002 = this.width() / 2 + 52;
			o++;
			this.drawString(string2, var10002, l + 10 * o - 3, 5000268, false);
		}
	}

	private int method_20835() {
		return (this.width() - 310) / 2;
	}

	private int method_20836() {
		return this.height() / 2 - 80;
	}

	private void method_20849(int i, int j, int k, int l, boolean bl, boolean bl2) {
		int m = this.field_19492;
		boolean bl3 = this.method_20871((double)i, (double)j);
		boolean bl4 = bl2 && bl;
		if (bl4) {
			float f = 0.25F + (1.0F + RealmsMth.sin((float)this.field_19493 * 0.5F)) * 0.25F;
			int n = 0xFF000000 | (int)(f * 64.0F) << 16 | (int)(f * 64.0F) << 8 | (int)(f * 64.0F) << 0;
			this.fillGradient(k - 2, l - 2, k + 18, l + 18, n, n);
			n = 0xFF000000 | (int)(f * 255.0F) << 16 | (int)(f * 255.0F) << 8 | (int)(f * 255.0F) << 0;
			this.fillGradient(k - 2, l - 2, k + 18, l - 1, n, n);
			this.fillGradient(k - 2, l - 2, k - 1, l + 18, n, n);
			this.fillGradient(k + 17, l - 2, k + 18, l + 18, n, n);
			this.fillGradient(k - 2, l + 17, k + 18, l + 18, n, n);
		}

		RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		boolean bl5 = bl2 && bl;
		RealmsScreen.blit(k, l - 6, bl5 ? 16.0F : 0.0F, 0.0F, 15, 25, 31, 25);
		GlStateManager.popMatrix();
		boolean bl6 = bl2 && m != 0;
		if (bl6) {
			int o = (Math.min(m, 6) - 1) * 8;
			int p = (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.field_19493) * 0.57F), RealmsMth.cos((float)this.field_19493 * 0.35F))) * -6.0F);
			RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			RealmsScreen.blit(k + 4, l + 4 + p, (float)o, bl3 ? 8.0F : 0.0F, 8, 8, 48, 16);
			GlStateManager.popMatrix();
		}

		int o = i + 12;
		boolean bl7 = bl2 && bl3;
		if (bl7) {
			String string = getLocalizedString(m == 0 ? "mco.invites.nopending" : "mco.invites.pending");
			int q = this.fontWidth(string);
			this.fillGradient(o - 3, j - 3, o + q + 3, j + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(string, o, j, -1);
		}
	}

	private boolean method_20871(double d, double e) {
		int i = this.width() / 2 + 50;
		int j = this.width() / 2 + 66;
		int k = 11;
		int l = 23;
		if (this.field_19492 != 0) {
			i -= 3;
			j += 3;
			k -= 5;
			l += 5;
		}

		return (double)i <= d && d <= (double)j && (double)k <= e && e <= (double)l;
	}

	public void method_20853(RealmsServer realmsServer, RealmsScreen realmsScreen) {
		if (realmsServer != null) {
			try {
				if (!this.field_19467.tryLock(1L, TimeUnit.SECONDS)) {
					return;
				}

				if (this.field_19467.getHoldCount() > 1) {
					return;
				}
			} catch (InterruptedException var4) {
				return;
			}

			this.field_19478 = true;
			this.method_20875(realmsServer, realmsScreen);
		}
	}

	private void method_20875(RealmsServer realmsServer, RealmsScreen realmsScreen) {
		class_4398 lv = new class_4398(realmsScreen, new class_4434.class_4439(this, realmsScreen, realmsServer, this.field_19467));
		lv.method_21288();
		Realms.setScreen(lv);
	}

	private boolean method_20909(RealmsServer realmsServer) {
		return realmsServer.ownerUUID != null && realmsServer.ownerUUID.equals(Realms.getUUID());
	}

	private boolean method_20912(RealmsServer realmsServer) {
		return realmsServer.ownerUUID != null && realmsServer.ownerUUID.equals(Realms.getUUID()) && !realmsServer.expired;
	}

	private void method_20846(int i, int j, int k, int l) {
		RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 10, 28);
		GlStateManager.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height() - 40 && l > 32 && !this.method_20870()) {
			this.field_19490 = getLocalizedString("mco.selectServer.expired");
		}
	}

	private void method_20847(int i, int j, int k, int l, int m) {
		RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		if (this.field_19493 % 20 < 10) {
			RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 20, 28);
		} else {
			RealmsScreen.blit(i, j, 10.0F, 0.0F, 10, 28, 20, 28);
		}

		GlStateManager.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height() - 40 && l > 32 && !this.method_20870()) {
			if (m <= 0) {
				this.field_19490 = getLocalizedString("mco.selectServer.expires.soon");
			} else if (m == 1) {
				this.field_19490 = getLocalizedString("mco.selectServer.expires.day");
			} else {
				this.field_19490 = getLocalizedString("mco.selectServer.expires.days", new Object[]{m});
			}
		}
	}

	private void method_20873(int i, int j, int k, int l) {
		RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 10, 28);
		GlStateManager.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height() - 40 && l > 32 && !this.method_20870()) {
			this.field_19490 = getLocalizedString("mco.selectServer.open");
		}
	}

	private void method_20883(int i, int j, int k, int l) {
		RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 10, 28);
		GlStateManager.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height() - 40 && l > 32 && !this.method_20870()) {
			this.field_19490 = getLocalizedString("mco.selectServer.closed");
		}
	}

	private void method_20891(int i, int j, int k, int l) {
		boolean bl = false;
		if (k >= i && k <= i + 28 && l >= j && l <= j + 28 && l < this.height() - 40 && l > 32 && !this.method_20870()) {
			bl = true;
		}

		RealmsScreen.bind("realms:textures/gui/realms/leave_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(i, j, bl ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
		GlStateManager.popMatrix();
		if (bl) {
			this.field_19490 = getLocalizedString("mco.selectServer.leave");
		}
	}

	private void method_20898(int i, int j, int k, int l) {
		boolean bl = false;
		if (k >= i && k <= i + 28 && l >= j && l <= j + 28 && l < this.height() - 40 && l > 32 && !this.method_20870()) {
			bl = true;
		}

		RealmsScreen.bind("realms:textures/gui/realms/configure_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(i, j, bl ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
		GlStateManager.popMatrix();
		if (bl) {
			this.field_19490 = getLocalizedString("mco.selectServer.configure");
		}
	}

	protected void method_20867(String string, int i, int j) {
		if (string != null) {
			int k = 0;
			int l = 0;

			for (String string2 : string.split("\n")) {
				int m = this.fontWidth(string2);
				if (m > l) {
					l = m;
				}
			}

			int n = i - l - 5;
			int o = j;
			if (n < 0) {
				n = i + 12;
			}

			for (String string3 : string.split("\n")) {
				this.fillGradient(n - 3, o - (k == 0 ? 3 : 0) + k, n + l + 3, o + 8 + 3 + k, -1073741824, -1073741824);
				this.fontDrawShadow(string3, n, o + k, 16777215);
				k += 10;
			}
		}
	}

	private void method_20848(int i, int j, int k, int l, boolean bl) {
		boolean bl2 = false;
		if (i >= k && i <= k + 20 && j >= l && j <= l + 20) {
			bl2 = true;
		}

		RealmsScreen.bind("realms:textures/gui/realms/questionmark.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(k, l, bl ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
		GlStateManager.popMatrix();
		if (bl2) {
			this.field_19490 = getLocalizedString("mco.selectServer.info");
		}
	}

	private void method_20850(int i, int j, boolean bl, int k, int l, boolean bl2, boolean bl3) {
		boolean bl4 = false;
		if (i >= k && i <= k + 20 && j >= l && j <= l + 20) {
			bl4 = true;
		}

		RealmsScreen.bind("realms:textures/gui/realms/news_icon.png");
		if (bl3) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
		}

		GlStateManager.pushMatrix();
		boolean bl5 = bl3 && bl2;
		RealmsScreen.blit(k, l, bl5 ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
		GlStateManager.popMatrix();
		if (bl4 && bl3) {
			this.field_19490 = getLocalizedString("mco.news");
		}

		if (bl && bl3) {
			int m = bl4
				? 0
				: (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.field_19493) * 0.57F), RealmsMth.cos((float)this.field_19493 * 0.35F))) * -6.0F);
			RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			RealmsScreen.blit(k + 10, l + 2 + m, 40.0F, 0.0F, 8, 8, 48, 16);
			GlStateManager.popMatrix();
		}
	}

	private void method_20837() {
		String string = "LOCAL!";
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
		GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scalef(1.5F, 1.5F, 1.5F);
		this.drawString("LOCAL!", 0, 0, 8388479);
		GlStateManager.popMatrix();
	}

	private void method_20838() {
		String string = "STAGE!";
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
		GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scalef(1.5F, 1.5F, 1.5F);
		this.drawString("STAGE!", 0, 0, -256);
		GlStateManager.popMatrix();
	}

	public class_4325 method_20902() {
		return new class_4325(this.field_19482);
	}

	public void method_20905() {
		if (this.method_20870() && this.field_19498) {
			this.field_19498 = false;
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4326 extends RealmsButton {
		public class_4326() {
			super(11, class_4325.this.method_20835() + 4, class_4325.this.method_20836() + 4, 12, 12, RealmsScreen.getLocalizedString("mco.selectServer.close"));
		}

		@Override
		public void tick() {
			super.tick();
		}

		@Override
		public void render(int i, int j, float f) {
			super.render(i, j, f);
		}

		@Override
		public void renderButton(int i, int j, float f) {
			RealmsScreen.bind("realms:textures/gui/realms/cross_icon.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			RealmsScreen.blit(this.x(), this.y(), 0.0F, this.getProxy().isHovered() ? 12.0F : 0.0F, 12, 12, 12, 24);
			GlStateManager.popMatrix();
			if (this.getProxy().isMouseOver((double)i, (double)j)) {
				class_4325.this.field_19490 = this.getProxy().getMessage();
			}
		}

		@Override
		public void onPress() {
			class_4325.this.method_20834();
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4327 extends RealmsButton {
		public class_4327() {
			super(9, class_4325.this.width() - 62, 6, 20, 20, "");
		}

		@Override
		public void tick() {
			this.setMessage(Realms.getLocalizedString("mco.news"));
		}

		@Override
		public void render(int i, int j, float f) {
			super.render(i, j, f);
		}

		@Override
		public void onPress() {
			if (class_4325.this.field_19459 != null) {
				class_4448.method_21570(class_4325.this.field_19459);
				if (class_4325.this.field_19458) {
					class_4432.class_4433 lv = class_4432.method_21549();
					lv.field_20210 = false;
					class_4325.this.field_19458 = false;
					class_4432.method_21550(lv);
				}
			}
		}

		@Override
		public void renderButton(int i, int j, float f) {
			class_4325.this.method_20850(i, j, class_4325.this.field_19458, this.x(), this.y(), this.getProxy().isHovered(), this.active());
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4328 extends RealmsButton {
		public class_4328() {
			super(8, class_4325.this.width() / 2 + 47, 6, 22, 22, "");
		}

		@Override
		public void tick() {
			this.setMessage(Realms.getLocalizedString(class_4325.this.field_19492 == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
		}

		@Override
		public void render(int i, int j, float f) {
			super.render(i, j, f);
		}

		@Override
		public void onPress() {
			class_4401 lv = new class_4401(class_4325.this.field_19482);
			Realms.setScreen(lv);
		}

		@Override
		public void renderButton(int i, int j, float f) {
			class_4325.this.method_20849(i, j, this.x(), this.y(), this.getProxy().isHovered(), this.active());
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4329 extends RealmsObjectSelectionList {
		public class_4329() {
			super(class_4325.this.width(), class_4325.this.height(), 32, class_4325.this.height() - 40, 36);
		}

		@Override
		public boolean isFocused() {
			return class_4325.this.isFocused(this);
		}

		@Override
		public boolean keyPressed(int i, int j, int k) {
			if (i != 257 && i != 32 && i != 335) {
				return false;
			} else {
				RealmListEntry realmListEntry = this.getSelected();
				return realmListEntry == null ? super.keyPressed(i, j, k) : realmListEntry.mouseClicked(0.0, 0.0, 0);
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0 && d < (double)this.getScrollbarPosition() && e >= (double)this.y0() && e <= (double)this.y1()) {
				int j = class_4325.this.field_19483.getRowLeft();
				int k = this.getScrollbarPosition();
				int l = (int)Math.floor(e - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
				int m = l / this.itemHeight();
				if (d >= (double)j && d <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
					this.itemClicked(l, m, d, e, this.width());
					class_4325.this.field_19466 = class_4325.this.field_19466 + 7;
					this.selectItem(m);
				}

				return true;
			} else {
				return super.mouseClicked(d, e, i);
			}
		}

		@Override
		public void selectItem(int i) {
			this.setSelected(i);
			if (i != -1) {
				RealmsServer realmsServer;
				if (class_4325.this.method_20842()) {
					if (i == 0) {
						Realms.narrateNow(RealmsScreen.getLocalizedString("mco.trial.message.line1"), RealmsScreen.getLocalizedString("mco.trial.message.line2"));
						realmsServer = null;
					} else {
						realmsServer = (RealmsServer)class_4325.this.field_19491.get(i - 1);
					}
				} else {
					realmsServer = (RealmsServer)class_4325.this.field_19491.get(i);
				}

				class_4325.this.method_20852(realmsServer);
				if (realmsServer == null) {
					class_4325.this.field_19484 = -1L;
				} else if (realmsServer.state == RealmsServer.class_4320.UNINITIALIZED) {
					Realms.narrateNow(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized") + RealmsScreen.getLocalizedString("mco.gui.button"));
					class_4325.this.field_19484 = -1L;
				} else {
					class_4325.this.field_19484 = realmsServer.id;
					if (class_4325.this.field_19466 >= 10 && class_4325.this.field_19485.active()) {
						class_4325.this.method_20853(class_4325.this.method_20851(class_4325.this.field_19484), class_4325.this);
					}

					Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", realmsServer.name));
				}
			}
		}

		@Override
		public void itemClicked(int i, int j, double d, double e, int k) {
			if (class_4325.this.method_20842()) {
				if (j == 0) {
					class_4325.this.field_19498 = true;
					return;
				}

				j--;
			}

			if (j < class_4325.this.field_19491.size()) {
				RealmsServer realmsServer = (RealmsServer)class_4325.this.field_19491.get(j);
				if (realmsServer != null) {
					if (realmsServer.state == RealmsServer.class_4320.UNINITIALIZED) {
						class_4325.this.field_19484 = -1L;
						Realms.setScreen(new class_4390(realmsServer, class_4325.this));
					} else {
						class_4325.this.field_19484 = realmsServer.id;
					}

					if (class_4325.this.field_19490 != null && class_4325.this.field_19490.equals(RealmsScreen.getLocalizedString("mco.selectServer.configure"))) {
						class_4325.this.field_19484 = realmsServer.id;
						class_4325.this.method_20903(realmsServer);
					} else if (class_4325.this.field_19490 != null && class_4325.this.field_19490.equals(RealmsScreen.getLocalizedString("mco.selectServer.leave"))) {
						class_4325.this.field_19484 = realmsServer.id;
						class_4325.this.method_20906(realmsServer);
					} else if (class_4325.this.method_20909(realmsServer) && realmsServer.expired && class_4325.this.field_19468) {
						class_4325.this.method_20928();
					}
				}
			}
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public int getRowWidth() {
			return 300;
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4330 extends RealmListEntry {
		final RealmsServer field_19518;

		public class_4330(RealmsServer realmsServer) {
			this.field_19518 = realmsServer;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.method_20945(this.field_19518, k, j, n, o);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (this.field_19518.state == RealmsServer.class_4320.UNINITIALIZED) {
				class_4325.this.field_19484 = -1L;
				Realms.setScreen(new class_4390(this.field_19518, class_4325.this));
			} else {
				class_4325.this.field_19484 = this.field_19518.id;
			}

			return true;
		}

		private void method_20945(RealmsServer realmsServer, int i, int j, int k, int l) {
			this.method_20946(realmsServer, i + 36, j, k, l);
		}

		private void method_20946(RealmsServer realmsServer, int i, int j, int k, int l) {
			if (realmsServer.state == RealmsServer.class_4320.UNINITIALIZED) {
				RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableAlphaTest();
				GlStateManager.pushMatrix();
				RealmsScreen.blit(i + 10, j + 6, 0.0F, 0.0F, 40, 20, 40, 20);
				GlStateManager.popMatrix();
				float f = 0.5F + (1.0F + RealmsMth.sin((float)class_4325.this.field_19493 * 0.25F)) * 0.25F;
				int m = 0xFF000000 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
				class_4325.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), i + 10 + 40 + 75, j + 12, m);
			} else {
				int n = 225;
				int m = 2;
				if (realmsServer.expired) {
					class_4325.this.method_20846(i + 225 - 14, j + 2, k, l);
				} else if (realmsServer.state == RealmsServer.class_4320.CLOSED) {
					class_4325.this.method_20883(i + 225 - 14, j + 2, k, l);
				} else if (class_4325.this.method_20909(realmsServer) && realmsServer.daysLeft < 7) {
					class_4325.this.method_20847(i + 225 - 14, j + 2, k, l, realmsServer.daysLeft);
				} else if (realmsServer.state == RealmsServer.class_4320.OPEN) {
					class_4325.this.method_20873(i + 225 - 14, j + 2, k, l);
				}

				if (!class_4325.this.method_20909(realmsServer) && !class_4325.field_19476) {
					class_4325.this.method_20891(i + 225, j + 2, k, l);
				} else {
					class_4325.this.method_20898(i + 225, j + 2, k, l);
				}

				if (!"0".equals(realmsServer.serverPing.nrOfPlayers)) {
					String string = class_4357.GRAY + "" + realmsServer.serverPing.nrOfPlayers;
					class_4325.this.drawString(string, i + 207 - class_4325.this.fontWidth(string), j + 3, 8421504);
					if (k >= i + 207 - class_4325.this.fontWidth(string)
						&& k <= i + 207
						&& l >= j + 1
						&& l <= j + 10
						&& l < class_4325.this.height() - 40
						&& l > 32
						&& !class_4325.this.method_20870()) {
						class_4325.this.field_19490 = realmsServer.serverPing.playerList;
					}
				}

				if (class_4325.this.method_20909(realmsServer) && realmsServer.expired) {
					boolean bl = false;
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.enableBlend();
					RealmsScreen.bind("minecraft:textures/gui/widgets.png");
					GlStateManager.pushMatrix();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					String string2 = RealmsScreen.getLocalizedString("mco.selectServer.expiredList");
					String string3 = RealmsScreen.getLocalizedString("mco.selectServer.expiredRenew");
					if (realmsServer.expiredTrial) {
						string2 = RealmsScreen.getLocalizedString("mco.selectServer.expiredTrial");
						string3 = RealmsScreen.getLocalizedString("mco.selectServer.expiredSubscribe");
					}

					int o = class_4325.this.fontWidth(string3) + 17;
					int p = 16;
					int q = i + class_4325.this.fontWidth(string2) + 8;
					int r = j + 13;
					if (k >= q && k < q + o && l > r && l <= r + 16 & l < class_4325.this.height() - 40 && l > 32 && !class_4325.this.method_20870()) {
						bl = true;
						class_4325.this.field_19468 = true;
					}

					int s = bl ? 2 : 1;
					RealmsScreen.blit(q, r, 0.0F, (float)(46 + s * 20), o / 2, 8, 256, 256);
					RealmsScreen.blit(q + o / 2, r, (float)(200 - o / 2), (float)(46 + s * 20), o / 2, 8, 256, 256);
					RealmsScreen.blit(q, r + 8, 0.0F, (float)(46 + s * 20 + 12), o / 2, 8, 256, 256);
					RealmsScreen.blit(q + o / 2, r + 8, (float)(200 - o / 2), (float)(46 + s * 20 + 12), o / 2, 8, 256, 256);
					GlStateManager.popMatrix();
					GlStateManager.disableBlend();
					int t = j + 11 + 5;
					int u = bl ? 16777120 : 16777215;
					class_4325.this.drawString(string2, i + 2, t + 1, 15553363);
					class_4325.this.drawCenteredString(string3, q + o / 2, t + 1, u);
				} else {
					if (realmsServer.worldType.equals(RealmsServer.class_4321.MINIGAME)) {
						int v = 13413468;
						String string2x = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
						int w = class_4325.this.fontWidth(string2x);
						class_4325.this.drawString(string2x, i + 2, j + 12, 13413468);
						class_4325.this.drawString(realmsServer.getMinigameName(), i + 2 + w, j + 12, 7105644);
					} else {
						class_4325.this.drawString(realmsServer.getDescription(), i + 2, j + 12, 7105644);
					}

					if (!class_4325.this.method_20909(realmsServer)) {
						class_4325.this.drawString(realmsServer.owner, i + 2, j + 12 + 11, 5000268);
					}
				}

				class_4325.this.drawString(realmsServer.getName(), i + 2, j + 1, 16777215);
				class_4446.method_21559(realmsServer.ownerUUID, () -> {
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					RealmsScreen.blit(i - 36, j, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
					RealmsScreen.blit(i - 36, j, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
				});
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4331 extends RealmListEntry {
		public class_4331() {
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.method_20947(i, k, j, n, o);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			class_4325.this.field_19498 = true;
			return true;
		}

		private void method_20947(int i, int j, int k, int l, int m) {
			int n = k + 8;
			int o = 0;
			String string = RealmsScreen.getLocalizedString("mco.trial.message.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.trial.message.line2");
			boolean bl = false;
			if (j <= l && l <= class_4325.this.field_19483.getScroll() && k <= m && m <= k + 32) {
				bl = true;
			}

			int p = 8388479;
			if (bl && !class_4325.this.method_20870()) {
				p = 6077788;
			}

			for (String string2 : string.split("\\\\n")) {
				class_4325.this.drawCenteredString(string2, class_4325.this.width() / 2, n + o, p);
				o += 10;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4332 extends RealmsButton {
		public class_4332() {
			super(10, class_4325.this.width() - 37, 6, 20, 20, RealmsScreen.getLocalizedString("mco.selectServer.info"));
		}

		@Override
		public void tick() {
			super.tick();
		}

		@Override
		public void render(int i, int j, float f) {
			super.render(i, j, f);
		}

		@Override
		public void renderButton(int i, int j, float f) {
			class_4325.this.method_20848(i, j, this.x(), this.y(), this.getProxy().isHovered());
		}

		@Override
		public void onPress() {
			class_4325.this.field_19498 = !class_4325.this.field_19498;
		}
	}
}
