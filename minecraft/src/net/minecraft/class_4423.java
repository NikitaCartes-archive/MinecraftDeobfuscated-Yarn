package net.minecraft;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSliderButton;

@Environment(EnvType.CLIENT)
public class class_4423 extends RealmsScreen {
	private RealmsEditBox field_20113;
	protected final class_4388 field_20109;
	private int field_20114;
	private int field_20115;
	private int field_20116;
	private final RealmsWorldOptions field_20117;
	private final RealmsServer.class_4321 field_20118;
	private final int field_20119;
	private int field_20120;
	private int field_20121;
	private Boolean field_20122;
	private Boolean field_20123;
	private Boolean field_20124;
	private Boolean field_20125;
	private Integer field_20126;
	private Boolean field_20127;
	private Boolean field_20128;
	private RealmsButton field_20129;
	private RealmsButton field_20130;
	private RealmsButton field_20131;
	private RealmsButton field_20132;
	private RealmsSliderButton field_20133;
	private RealmsButton field_20134;
	private RealmsButton field_20106;
	String[] field_20110;
	String[] field_20111;
	String[][] field_20112;
	private RealmsLabel field_20107;
	private RealmsLabel field_20108 = null;

	public class_4423(class_4388 arg, RealmsWorldOptions realmsWorldOptions, RealmsServer.class_4321 arg2, int i) {
		this.field_20109 = arg;
		this.field_20117 = realmsWorldOptions;
		this.field_20118 = arg2;
		this.field_20119 = i;
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public void tick() {
		this.field_20113.tick();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		switch (i) {
			case 256:
				Realms.setScreen(this.field_20109);
				return true;
			default:
				return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void init() {
		this.field_20115 = 170;
		this.field_20114 = this.width() / 2 - this.field_20115 * 2 / 2;
		this.field_20116 = this.width() / 2 + 10;
		this.method_21457();
		this.field_20120 = this.field_20117.difficulty;
		this.field_20121 = this.field_20117.gameMode;
		if (this.field_20118.equals(RealmsServer.class_4321.NORMAL)) {
			this.field_20122 = this.field_20117.pvp;
			this.field_20126 = this.field_20117.spawnProtection;
			this.field_20128 = this.field_20117.forceGameMode;
			this.field_20124 = this.field_20117.spawnAnimals;
			this.field_20125 = this.field_20117.spawnMonsters;
			this.field_20123 = this.field_20117.spawnNPCs;
			this.field_20127 = this.field_20117.commandBlocks;
		} else {
			String string;
			if (this.field_20118.equals(RealmsServer.class_4321.ADVENTUREMAP)) {
				string = getLocalizedString("mco.configure.world.edit.subscreen.adventuremap");
			} else if (this.field_20118.equals(RealmsServer.class_4321.INSPIRATION)) {
				string = getLocalizedString("mco.configure.world.edit.subscreen.inspiration");
			} else {
				string = getLocalizedString("mco.configure.world.edit.subscreen.experience");
			}

			this.field_20108 = new RealmsLabel(string, this.width() / 2, 26, 16711680);
			this.field_20122 = true;
			this.field_20126 = 0;
			this.field_20128 = false;
			this.field_20124 = true;
			this.field_20125 = true;
			this.field_20123 = true;
			this.field_20127 = true;
		}

		this.field_20113 = this.newEditBox(
			11, this.field_20114 + 2, class_4359.method_21072(1), this.field_20115 - 4, 20, getLocalizedString("mco.configure.world.edit.slot.name")
		);
		this.field_20113.setMaxLength(10);
		this.field_20113.setValue(this.field_20117.getSlotName(this.field_20119));
		this.focusOn(this.field_20113);
		this.buttonsAdd(this.field_20129 = new RealmsButton(4, this.field_20116, class_4359.method_21072(1), this.field_20115, 20, this.method_21469()) {
			@Override
			public void onPress() {
				class_4423.this.field_20122 = !class_4423.this.field_20122;
				this.setMessage(class_4423.this.method_21469());
			}
		});
		this.buttonsAdd(new RealmsButton(3, this.field_20114, class_4359.method_21072(3), this.field_20115, 20, this.method_21466()) {
			@Override
			public void onPress() {
				class_4423.this.field_20121 = (class_4423.this.field_20121 + 1) % class_4423.this.field_20111.length;
				this.setMessage(class_4423.this.method_21466());
			}
		});
		this.buttonsAdd(this.field_20130 = new RealmsButton(5, this.field_20116, class_4359.method_21072(3), this.field_20115, 20, this.method_21472()) {
			@Override
			public void onPress() {
				class_4423.this.field_20124 = !class_4423.this.field_20124;
				this.setMessage(class_4423.this.method_21472());
			}
		});
		this.buttonsAdd(new RealmsButton(2, this.field_20114, class_4359.method_21072(5), this.field_20115, 20, this.method_21462()) {
			@Override
			public void onPress() {
				class_4423.this.field_20120 = (class_4423.this.field_20120 + 1) % class_4423.this.field_20110.length;
				this.setMessage(class_4423.this.method_21462());
				if (class_4423.this.field_20118.equals(RealmsServer.class_4321.NORMAL)) {
					class_4423.this.field_20131.active(class_4423.this.field_20120 != 0);
					class_4423.this.field_20131.setMessage(class_4423.this.method_21475());
				}
			}
		});
		this.buttonsAdd(this.field_20131 = new RealmsButton(6, this.field_20116, class_4359.method_21072(5), this.field_20115, 20, this.method_21475()) {
			@Override
			public void onPress() {
				class_4423.this.field_20125 = !class_4423.this.field_20125;
				this.setMessage(class_4423.this.method_21475());
			}
		});
		this.buttonsAdd(
			this.field_20133 = new class_4423.class_4424(8, this.field_20114, class_4359.method_21072(7), this.field_20115, this.field_20126, 0.0F, 16.0F)
		);
		this.buttonsAdd(this.field_20132 = new RealmsButton(7, this.field_20116, class_4359.method_21072(7), this.field_20115, 20, this.method_21478()) {
			@Override
			public void onPress() {
				class_4423.this.field_20123 = !class_4423.this.field_20123;
				this.setMessage(class_4423.this.method_21478());
			}
		});
		this.buttonsAdd(this.field_20106 = new RealmsButton(10, this.field_20114, class_4359.method_21072(9), this.field_20115, 20, this.method_21482()) {
			@Override
			public void onPress() {
				class_4423.this.field_20128 = !class_4423.this.field_20128;
				this.setMessage(class_4423.this.method_21482());
			}
		});
		this.buttonsAdd(this.field_20134 = new RealmsButton(9, this.field_20116, class_4359.method_21072(9), this.field_20115, 20, this.method_21480()) {
			@Override
			public void onPress() {
				class_4423.this.field_20127 = !class_4423.this.field_20127;
				this.setMessage(class_4423.this.method_21480());
			}
		});
		if (!this.field_20118.equals(RealmsServer.class_4321.NORMAL)) {
			this.field_20129.active(false);
			this.field_20130.active(false);
			this.field_20132.active(false);
			this.field_20131.active(false);
			this.field_20133.active(false);
			this.field_20134.active(false);
			this.field_20133.active(false);
			this.field_20106.active(false);
		}

		if (this.field_20120 == 0) {
			this.field_20131.active(false);
		}

		this.buttonsAdd(
			new RealmsButton(1, this.field_20114, class_4359.method_21072(13), this.field_20115, 20, getLocalizedString("mco.configure.world.buttons.done")) {
				@Override
				public void onPress() {
					class_4423.this.method_21486();
				}
			}
		);
		this.buttonsAdd(new RealmsButton(0, this.field_20116, class_4359.method_21072(13), this.field_20115, 20, getLocalizedString("gui.cancel")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4423.this.field_20109);
			}
		});
		this.addWidget(this.field_20113);
		this.addWidget(this.field_20107 = new RealmsLabel(getLocalizedString("mco.configure.world.buttons.options"), this.width() / 2, 17, 16777215));
		if (this.field_20108 != null) {
			this.addWidget(this.field_20108);
		}

		this.narrateLabels();
	}

	private void method_21457() {
		this.field_20110 = new String[]{
			getLocalizedString("options.difficulty.peaceful"),
			getLocalizedString("options.difficulty.easy"),
			getLocalizedString("options.difficulty.normal"),
			getLocalizedString("options.difficulty.hard")
		};
		this.field_20111 = new String[]{
			getLocalizedString("selectWorld.gameMode.survival"),
			getLocalizedString("selectWorld.gameMode.creative"),
			getLocalizedString("selectWorld.gameMode.adventure")
		};
		this.field_20112 = new String[][]{
			{getLocalizedString("selectWorld.gameMode.survival.line1"), getLocalizedString("selectWorld.gameMode.survival.line2")},
			{getLocalizedString("selectWorld.gameMode.creative.line1"), getLocalizedString("selectWorld.gameMode.creative.line2")},
			{getLocalizedString("selectWorld.gameMode.adventure.line1"), getLocalizedString("selectWorld.gameMode.adventure.line2")}
		};
	}

	private String method_21462() {
		String string = getLocalizedString("options.difficulty");
		return string + ": " + this.field_20110[this.field_20120];
	}

	private String method_21466() {
		String string = getLocalizedString("selectWorld.gameMode");
		return string + ": " + this.field_20111[this.field_20121];
	}

	private String method_21469() {
		return getLocalizedString("mco.configure.world.pvp") + ": " + getLocalizedString(this.field_20122 ? "mco.configure.world.on" : "mco.configure.world.off");
	}

	private String method_21472() {
		return getLocalizedString("mco.configure.world.spawnAnimals")
			+ ": "
			+ getLocalizedString(this.field_20124 ? "mco.configure.world.on" : "mco.configure.world.off");
	}

	private String method_21475() {
		return this.field_20120 == 0
			? getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString("mco.configure.world.off")
			: getLocalizedString("mco.configure.world.spawnMonsters")
				+ ": "
				+ getLocalizedString(this.field_20125 ? "mco.configure.world.on" : "mco.configure.world.off");
	}

	private String method_21478() {
		return getLocalizedString("mco.configure.world.spawnNPCs")
			+ ": "
			+ getLocalizedString(this.field_20123 ? "mco.configure.world.on" : "mco.configure.world.off");
	}

	private String method_21480() {
		return getLocalizedString("mco.configure.world.commandBlocks")
			+ ": "
			+ getLocalizedString(this.field_20127 ? "mco.configure.world.on" : "mco.configure.world.off");
	}

	private String method_21482() {
		return getLocalizedString("mco.configure.world.forceGameMode")
			+ ": "
			+ getLocalizedString(this.field_20128 ? "mco.configure.world.on" : "mco.configure.world.off");
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		String string = getLocalizedString("mco.configure.world.edit.slot.name");
		this.drawString(string, this.field_20114 + this.field_20115 / 2 - this.fontWidth(string) / 2, class_4359.method_21072(0) - 5, 16777215);
		this.field_20107.render(this);
		if (this.field_20108 != null) {
			this.field_20108.render(this);
		}

		this.field_20113.render(i, j, f);
		super.render(i, j, f);
	}

	private String method_21484() {
		return this.field_20113.getValue().equals(this.field_20117.getDefaultSlotName(this.field_20119)) ? "" : this.field_20113.getValue();
	}

	private void method_21486() {
		if (!this.field_20118.equals(RealmsServer.class_4321.ADVENTUREMAP)
			&& !this.field_20118.equals(RealmsServer.class_4321.EXPERIENCE)
			&& !this.field_20118.equals(RealmsServer.class_4321.INSPIRATION)) {
			this.field_20109
				.method_21208(
					new RealmsWorldOptions(
						this.field_20122,
						this.field_20124,
						this.field_20125,
						this.field_20123,
						this.field_20126,
						this.field_20127,
						this.field_20120,
						this.field_20121,
						this.field_20128,
						this.method_21484()
					)
				);
		} else {
			this.field_20109
				.method_21208(
					new RealmsWorldOptions(
						this.field_20117.pvp,
						this.field_20117.spawnAnimals,
						this.field_20117.spawnMonsters,
						this.field_20117.spawnNPCs,
						this.field_20117.spawnProtection,
						this.field_20117.commandBlocks,
						this.field_20120,
						this.field_20121,
						this.field_20117.forceGameMode,
						this.method_21484()
					)
				);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4424 extends RealmsSliderButton {
		public class_4424(int i, int j, int k, int l, int m, float f, float g) {
			super(i, j, k, l, m, (double)f, (double)g);
		}

		@Override
		public void applyValue() {
			if (class_4423.this.field_20133.active()) {
				class_4423.this.field_20126 = (int)this.toValue(this.getValue());
			}
		}

		@Override
		public String getMessage() {
			return RealmsScreen.getLocalizedString("mco.configure.world.spawnProtection")
				+ ": "
				+ (class_4423.this.field_20126 == 0 ? RealmsScreen.getLocalizedString("mco.configure.world.off") : class_4423.this.field_20126);
		}
	}
}
