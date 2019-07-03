package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.PendingInvite;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4401 extends RealmsScreen {
	private static final Logger field_19935 = LogManager.getLogger();
	private final RealmsScreen field_19936;
	private String field_19937;
	private boolean field_19938;
	private class_4401.class_4402 field_19939;
	private RealmsLabel field_19940;
	private int field_19941 = -1;
	private RealmsButton field_19942;
	private RealmsButton field_19943;

	public class_4401(RealmsScreen realmsScreen) {
		this.field_19936 = realmsScreen;
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.field_19939 = new class_4401.class_4402();
		(new Thread("Realms-pending-invitations-fetcher") {
				public void run() {
					class_4341 lv = class_4341.method_20989();

					try {
						List<PendingInvite> list = lv.method_21030().pendingInvites;
						List<class_4401.class_4403> list2 = (List<class_4401.class_4403>)list.stream()
							.map(pendingInvite -> class_4401.this.new class_4403(pendingInvite))
							.collect(Collectors.toList());
						Realms.execute((Runnable)(() -> class_4401.this.field_19939.replaceEntries(list2)));
					} catch (class_4355 var7) {
						class_4401.field_19935.error("Couldn't list invites");
					} finally {
						class_4401.this.field_19938 = true;
					}
				}
			})
			.start();
		this.buttonsAdd(
			this.field_19942 = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.accept")) {
				@Override
				public void onPress() {
					class_4401.this.method_21311(class_4401.this.field_19941);
					class_4401.this.field_19941 = -1;
					class_4401.this.method_21307();
				}
			}
		);
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, getLocalizedString("gui.done")) {
			@Override
			public void onPress() {
				Realms.setScreen(new class_4325(class_4401.this.field_19936));
			}
		});
		this.buttonsAdd(this.field_19943 = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.reject")) {
			@Override
			public void onPress() {
				class_4401.this.method_21308(class_4401.this.field_19941);
				class_4401.this.field_19941 = -1;
				class_4401.this.method_21307();
			}
		});
		this.field_19940 = new RealmsLabel(getLocalizedString("mco.invites.title"), this.width() / 2, 12, 16777215);
		this.addWidget(this.field_19940);
		this.addWidget(this.field_19939);
		this.narrateLabels();
		this.method_21307();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(new class_4325(this.field_19936));
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	private void method_21300(int i) {
		this.field_19939.method_21321(i);
	}

	private void method_21308(int i) {
		if (i < this.field_19939.getItemCount()) {
			(new Thread("Realms-reject-invitation") {
				public void run() {
					try {
						class_4341 lv = class_4341.method_20989();
						lv.method_21006(((class_4401.class_4403)class_4401.this.field_19939.children().get(i)).field_19953.invitationId);
						Realms.execute((Runnable)(() -> class_4401.this.method_21300(i)));
					} catch (class_4355 var2) {
						class_4401.field_19935.error("Couldn't reject invite");
					}
				}
			}).start();
		}
	}

	private void method_21311(int i) {
		if (i < this.field_19939.getItemCount()) {
			(new Thread("Realms-accept-invitation") {
				public void run() {
					try {
						class_4341 lv = class_4341.method_20989();
						lv.method_20999(((class_4401.class_4403)class_4401.this.field_19939.children().get(i)).field_19953.invitationId);
						Realms.execute((Runnable)(() -> class_4401.this.method_21300(i)));
					} catch (class_4355 var2) {
						class_4401.field_19935.error("Couldn't accept invite");
					}
				}
			}).start();
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_19937 = null;
		this.renderBackground();
		this.field_19939.render(i, j, f);
		this.field_19940.render(this);
		if (this.field_19937 != null) {
			this.method_21306(this.field_19937, i, j);
		}

		if (this.field_19939.getItemCount() == 0 && this.field_19938) {
			this.drawCenteredString(getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 16777215);
		}

		super.render(i, j, f);
	}

	protected void method_21306(String string, int i, int j) {
		if (string != null) {
			int k = i + 12;
			int l = j - 12;
			int m = this.fontWidth(string);
			this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(string, k, l, 16777215);
		}
	}

	private void method_21307() {
		this.field_19942.setVisible(this.method_21314(this.field_19941));
		this.field_19943.setVisible(this.method_21314(this.field_19941));
	}

	private boolean method_21314(int i) {
		return i != -1;
	}

	public static String method_21301(PendingInvite pendingInvite) {
		return class_4448.method_21567(System.currentTimeMillis() - pendingInvite.date.getTime());
	}

	@Environment(EnvType.CLIENT)
	class class_4402 extends RealmsObjectSelectionList<class_4401.class_4403> {
		public class_4402() {
			super(class_4401.this.width(), class_4401.this.height(), 32, class_4401.this.height() - 40, 36);
		}

		public void method_21321(int i) {
			this.remove(i);
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public int getRowWidth() {
			return 260;
		}

		@Override
		public boolean isFocused() {
			return class_4401.this.isFocused(this);
		}

		@Override
		public void renderBackground() {
			class_4401.this.renderBackground();
		}

		@Override
		public void selectItem(int i) {
			this.setSelected(i);
			if (i != -1) {
				List<class_4401.class_4403> list = class_4401.this.field_19939.children();
				PendingInvite pendingInvite = ((class_4401.class_4403)list.get(i)).field_19953;
				String string = RealmsScreen.getLocalizedString("narrator.select.list.position", i + 1, list.size());
				String string2 = Realms.joinNarrations(Arrays.asList(pendingInvite.worldName, pendingInvite.worldOwnerName, class_4401.method_21301(pendingInvite), string));
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string2));
			}

			this.method_21322(i);
		}

		public void method_21322(int i) {
			class_4401.this.field_19941 = i;
			class_4401.this.method_21307();
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4403 extends RealmListEntry {
		final PendingInvite field_19953;
		private final List<class_4371> field_19955;

		class_4403(PendingInvite pendingInvite) {
			this.field_19953 = pendingInvite;
			this.field_19955 = Arrays.asList(new class_4401.class_4403.class_4404(), new class_4401.class_4403.class_4405());
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.method_21324(this.field_19953, k, j, n, o);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			class_4371.method_21114(class_4401.this.field_19939, this, this.field_19955, i, d, e);
			return true;
		}

		private void method_21324(PendingInvite pendingInvite, int i, int j, int k, int l) {
			class_4401.this.drawString(pendingInvite.worldName, i + 38, j + 1, 16777215);
			class_4401.this.drawString(pendingInvite.worldOwnerName, i + 38, j + 12, 7105644);
			class_4401.this.drawString(class_4401.method_21301(pendingInvite), i + 38, j + 24, 7105644);
			class_4371.method_21113(this.field_19955, class_4401.this.field_19939, i, j, k, l);
			class_4446.method_21559(pendingInvite.worldOwnerUuid, () -> {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RealmsScreen.blit(i, j, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
				RealmsScreen.blit(i, j, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
			});
		}

		@Environment(EnvType.CLIENT)
		class class_4404 extends class_4371 {
			class_4404() {
				super(15, 15, 215, 5);
			}

			@Override
			protected void method_21112(int i, int j, boolean bl) {
				RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				RealmsScreen.blit(i, j, bl ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
				GlStateManager.popMatrix();
				if (bl) {
					class_4401.this.field_19937 = RealmsScreen.getLocalizedString("mco.invites.button.accept");
				}
			}

			@Override
			public void method_21110(int i) {
				class_4401.this.method_21311(i);
			}
		}

		@Environment(EnvType.CLIENT)
		class class_4405 extends class_4371 {
			class_4405() {
				super(15, 15, 235, 5);
			}

			@Override
			protected void method_21112(int i, int j, boolean bl) {
				RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				RealmsScreen.blit(i, j, bl ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
				GlStateManager.popMatrix();
				if (bl) {
					class_4401.this.field_19937 = RealmsScreen.getLocalizedString("mco.invites.button.reject");
				}
			}

			@Override
			public void method_21110(int i) {
				class_4401.this.method_21308(i);
			}
		}
	}
}
