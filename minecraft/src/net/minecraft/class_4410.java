package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4410 extends class_4415<WorldTemplate> {
	private static final Logger field_19999 = LogManager.getLogger();
	private final RealmsScreen field_20000;
	private final RealmsServer field_20001;
	private final RealmsScreen field_20002;
	private RealmsLabel field_20003;
	private RealmsLabel field_20004;
	private String field_20005 = getLocalizedString("mco.reset.world.title");
	private String field_20006 = getLocalizedString("mco.reset.world.warning");
	private String field_20007 = getLocalizedString("gui.cancel");
	private int field_20008 = 16711680;
	private final int field_20009 = 0;
	private final int field_20010 = 100;
	private WorldTemplatePaginatedList field_20011 = null;
	private WorldTemplatePaginatedList field_20012 = null;
	private WorldTemplatePaginatedList field_20013 = null;
	private WorldTemplatePaginatedList field_20014 = null;
	public int field_19998 = -1;
	private class_4410.class_4412 field_20015 = class_4410.class_4412.NONE;
	private class_4410.class_4413 field_20016 = null;
	private WorldTemplate field_20017 = null;
	private String field_20018 = null;
	private int field_20019 = -1;

	public class_4410(RealmsScreen realmsScreen, RealmsServer realmsServer, RealmsScreen realmsScreen2) {
		this.field_20000 = realmsScreen;
		this.field_20001 = realmsServer;
		this.field_20002 = realmsScreen2;
	}

	public class_4410(RealmsScreen realmsScreen, RealmsServer realmsServer, RealmsScreen realmsScreen2, String string, String string2, int i, String string3) {
		this(realmsScreen, realmsServer, realmsScreen2);
		this.field_20005 = string;
		this.field_20006 = string2;
		this.field_20008 = i;
		this.field_20007 = string3;
	}

	public void method_21369(int i) {
		this.field_20019 = i;
	}

	public void method_21379(int i) {
		this.field_19998 = i;
	}

	public void method_21376(String string) {
		this.field_20018 = string;
	}

	@Override
	public void init() {
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 40, class_4359.method_21072(14) - 10, 80, 20, this.field_20007) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4410.this.field_20000);
			}
		});
		(new Thread("Realms-reset-world-fetcher") {
			public void run() {
				class_4341 lv = class_4341.method_20989();

				try {
					WorldTemplatePaginatedList worldTemplatePaginatedList = lv.method_20990(1, 10, RealmsServer.class_4321.NORMAL);
					WorldTemplatePaginatedList worldTemplatePaginatedList2 = lv.method_20990(1, 10, RealmsServer.class_4321.ADVENTUREMAP);
					WorldTemplatePaginatedList worldTemplatePaginatedList3 = lv.method_20990(1, 10, RealmsServer.class_4321.EXPERIENCE);
					WorldTemplatePaginatedList worldTemplatePaginatedList4 = lv.method_20990(1, 10, RealmsServer.class_4321.INSPIRATION);
					Realms.execute((Runnable)(() -> {
						class_4410.this.field_20011 = worldTemplatePaginatedList;
						class_4410.this.field_20012 = worldTemplatePaginatedList2;
						class_4410.this.field_20013 = worldTemplatePaginatedList3;
						class_4410.this.field_20014 = worldTemplatePaginatedList4;
					}));
				} catch (class_4355 var6) {
					class_4410.field_19999.error("Couldn't fetch templates in reset world", (Throwable)var6);
				}
			}
		}).start();
		this.addWidget(this.field_20003 = new RealmsLabel(this.field_20005, this.width() / 2, 7, 16777215));
		this.addWidget(this.field_20004 = new RealmsLabel(this.field_20006, this.width() / 2, 22, this.field_20008));
		this.buttonsAdd(
			new class_4410.class_4411(
				this.method_21384(1),
				class_4359.method_21072(0) + 10,
				getLocalizedString("mco.reset.world.generate"),
				-1L,
				"realms:textures/gui/realms/new_world.png",
				class_4410.class_4412.GENERATE
			) {
				@Override
				public void onPress() {
					Realms.setScreen(new class_4409(class_4410.this, class_4410.this.field_20005));
				}
			}
		);
		this.buttonsAdd(
			new class_4410.class_4411(
				this.method_21384(2),
				class_4359.method_21072(0) + 10,
				getLocalizedString("mco.reset.world.upload"),
				-1L,
				"realms:textures/gui/realms/upload.png",
				class_4410.class_4412.UPLOAD
			) {
				@Override
				public void onPress() {
					int var10003 = class_4410.this.field_19998 != -1 ? class_4410.this.field_19998 : class_4410.this.field_20001.activeSlot;
					Realms.setScreen(new class_4416(class_4410.this.field_20001.id, var10003, class_4410.this));
				}
			}
		);
		this.buttonsAdd(
			new class_4410.class_4411(
				this.method_21384(3),
				class_4359.method_21072(0) + 10,
				getLocalizedString("mco.reset.world.template"),
				-1L,
				"realms:textures/gui/realms/survival_spawn.png",
				class_4410.class_4412.SURVIVAL_SPAWN
			) {
				@Override
				public void onPress() {
					class_4419 lv = new class_4419(class_4410.this, RealmsServer.class_4321.NORMAL, class_4410.this.field_20011);
					lv.method_21423(RealmsScreen.getLocalizedString("mco.reset.world.template"));
					Realms.setScreen(lv);
				}
			}
		);
		this.buttonsAdd(
			new class_4410.class_4411(
				this.method_21384(1),
				class_4359.method_21072(6) + 20,
				getLocalizedString("mco.reset.world.adventure"),
				-1L,
				"realms:textures/gui/realms/adventure.png",
				class_4410.class_4412.ADVENTURE
			) {
				@Override
				public void onPress() {
					class_4419 lv = new class_4419(class_4410.this, RealmsServer.class_4321.ADVENTUREMAP, class_4410.this.field_20012);
					lv.method_21423(RealmsScreen.getLocalizedString("mco.reset.world.adventure"));
					Realms.setScreen(lv);
				}
			}
		);
		this.buttonsAdd(
			new class_4410.class_4411(
				this.method_21384(2),
				class_4359.method_21072(6) + 20,
				getLocalizedString("mco.reset.world.experience"),
				-1L,
				"realms:textures/gui/realms/experience.png",
				class_4410.class_4412.EXPERIENCE
			) {
				@Override
				public void onPress() {
					class_4419 lv = new class_4419(class_4410.this, RealmsServer.class_4321.EXPERIENCE, class_4410.this.field_20013);
					lv.method_21423(RealmsScreen.getLocalizedString("mco.reset.world.experience"));
					Realms.setScreen(lv);
				}
			}
		);
		this.buttonsAdd(
			new class_4410.class_4411(
				this.method_21384(3),
				class_4359.method_21072(6) + 20,
				getLocalizedString("mco.reset.world.inspiration"),
				-1L,
				"realms:textures/gui/realms/inspiration.png",
				class_4410.class_4412.INSPIRATION
			) {
				@Override
				public void onPress() {
					class_4419 lv = new class_4419(class_4410.this, RealmsServer.class_4321.INSPIRATION, class_4410.this.field_20014);
					lv.method_21423(RealmsScreen.getLocalizedString("mco.reset.world.inspiration"));
					Realms.setScreen(lv);
				}
			}
		);
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_20000);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return super.mouseClicked(d, e, i);
	}

	private int method_21384(int i) {
		return this.width() / 2 - 130 + (i - 1) * 100;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_20003.render(this);
		this.field_20004.render(this);
		super.render(i, j, f);
	}

	private void method_21370(int i, int j, String string, long l, String string2, class_4410.class_4412 arg, boolean bl, boolean bl2) {
		if (l == -1L) {
			bind(string2);
		} else {
			class_4446.method_21560(String.valueOf(l), string2);
		}

		if (bl) {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		RealmsScreen.blit(i + 2, j + 14, 0.0F, 0.0F, 56, 56, 56, 56);
		bind("realms:textures/gui/realms/slot_frame.png");
		if (bl) {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		RealmsScreen.blit(i, j + 12, 0.0F, 0.0F, 60, 60, 60, 60);
		this.drawCenteredString(string, i + 30, j, bl ? 10526880 : 16777215);
	}

	void method_21371(WorldTemplate worldTemplate) {
		if (worldTemplate != null) {
			if (this.field_19998 == -1) {
				this.method_21380(worldTemplate);
			} else {
				switch (worldTemplate.type) {
					case WORLD_TEMPLATE:
						this.field_20015 = class_4410.class_4412.SURVIVAL_SPAWN;
						break;
					case ADVENTUREMAP:
						this.field_20015 = class_4410.class_4412.ADVENTURE;
						break;
					case EXPERIENCE:
						this.field_20015 = class_4410.class_4412.EXPERIENCE;
						break;
					case INSPIRATION:
						this.field_20015 = class_4410.class_4412.INSPIRATION;
				}

				this.field_20017 = worldTemplate;
				this.method_21378();
			}
		}
	}

	private void method_21378() {
		this.method_21377(this);
	}

	public void method_21377(RealmsScreen realmsScreen) {
		class_4434.class_4443 lv = new class_4434.class_4443(this.field_20001.id, this.field_19998, realmsScreen, 100);
		class_4398 lv2 = new class_4398(this.field_20000, lv);
		lv2.method_21288();
		Realms.setScreen(lv2);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 100 && bl) {
			switch (this.field_20015) {
				case ADVENTURE:
				case SURVIVAL_SPAWN:
				case EXPERIENCE:
				case INSPIRATION:
					if (this.field_20017 != null) {
						this.method_21380(this.field_20017);
					}
					break;
				case GENERATE:
					if (this.field_20016 != null) {
						this.method_21381(this.field_20016);
					}
					break;
				default:
					return;
			}
		} else {
			if (bl) {
				Realms.setScreen(this.field_20002);
				if (this.field_20019 != -1) {
					this.field_20002.confirmResult(true, this.field_20019);
				}
			}
		}
	}

	public void method_21380(WorldTemplate worldTemplate) {
		class_4434.class_4440 lv = new class_4434.class_4440(this.field_20001.id, this.field_20002, worldTemplate);
		if (this.field_20018 != null) {
			lv.method_21556(this.field_20018);
		}

		if (this.field_20019 != -1) {
			lv.method_21555(this.field_20019);
		}

		class_4398 lv2 = new class_4398(this.field_20000, lv);
		lv2.method_21288();
		Realms.setScreen(lv2);
	}

	public void method_21372(class_4410.class_4413 arg) {
		if (this.field_19998 == -1) {
			this.method_21381(arg);
		} else {
			this.field_20015 = class_4410.class_4412.GENERATE;
			this.field_20016 = arg;
			this.method_21378();
		}
	}

	private void method_21381(class_4410.class_4413 arg) {
		class_4434.class_4440 lv = new class_4434.class_4440(this.field_20001.id, this.field_20002, arg.field_20042, arg.field_20043, arg.field_20044);
		if (this.field_20018 != null) {
			lv.method_21556(this.field_20018);
		}

		if (this.field_20019 != -1) {
			lv.method_21555(this.field_20019);
		}

		class_4398 lv2 = new class_4398(this.field_20000, lv);
		lv2.method_21288();
		Realms.setScreen(lv2);
	}

	@Environment(EnvType.CLIENT)
	abstract class class_4411 extends RealmsButton {
		private final long field_20030;
		private final String field_20032;
		private final class_4410.class_4412 field_20033;

		public class_4411(int i, int j, String string, long l, String string2, class_4410.class_4412 arg2) {
			super(100 + arg2.ordinal(), i, j, 60, 72, string);
			this.field_20030 = l;
			this.field_20032 = string2;
			this.field_20033 = arg2;
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
			class_4410.this.method_21370(
				this.x(),
				this.y(),
				this.getProxy().getMessage(),
				this.field_20030,
				this.field_20032,
				this.field_20033,
				this.getProxy().isHovered(),
				this.getProxy().isMouseOver((double)i, (double)j)
			);
		}
	}

	@Environment(EnvType.CLIENT)
	static enum class_4412 {
		NONE,
		GENERATE,
		UPLOAD,
		ADVENTURE,
		SURVIVAL_SPAWN,
		EXPERIENCE,
		INSPIRATION;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4413 {
		String field_20042;
		int field_20043;
		boolean field_20044;

		public class_4413(String string, int i, boolean bl) {
			this.field_20042 = string;
			this.field_20043 = i;
			this.field_20044 = bl;
		}
	}
}
