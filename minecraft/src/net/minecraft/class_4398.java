package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4398 extends RealmsScreen {
	private static final Logger field_19909 = LogManager.getLogger();
	private final int field_19910 = 666;
	private final int field_19911 = 667;
	private final RealmsScreen field_19912;
	private final class_4358 field_19913;
	private volatile String field_19914 = "";
	private volatile boolean field_19915;
	private volatile String field_19916;
	private volatile boolean field_19917;
	private int field_19918;
	private final class_4358 field_19919;
	private final int field_19920 = 212;
	public static final String[] field_19908 = new String[]{
		"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃",
		"_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
		"_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅",
		"_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆",
		"_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇",
		"_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █",
		"_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇",
		"_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆",
		"_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅",
		"_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
		"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃",
		"▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _",
		"▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _",
		"▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
		"▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _",
		"█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _",
		"▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _",
		"▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
		"▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _",
		"▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"
	};

	public class_4398(RealmsScreen realmsScreen, class_4358 arg) {
		this.field_19912 = realmsScreen;
		this.field_19919 = arg;
		arg.method_21066(this);
		this.field_19913 = arg;
	}

	public void method_21288() {
		Thread thread = new Thread(this.field_19913, "Realms-long-running-task");
		thread.setUncaughtExceptionHandler(new class_4353(field_19909));
		thread.start();
	}

	@Override
	public void tick() {
		super.tick();
		Realms.narrateRepeatedly(this.field_19914);
		this.field_19918++;
		this.field_19919.method_21068();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.method_21293();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void init() {
		this.field_19919.method_21070();
		this.buttonsAdd(new RealmsButton(666, this.width() / 2 - 106, class_4359.method_21072(12), 212, 20, getLocalizedString("gui.cancel")) {
			@Override
			public void onPress() {
				class_4398.this.method_21293();
			}
		});
	}

	private void method_21293() {
		this.field_19917 = true;
		this.field_19919.method_21071();
		Realms.setScreen(this.field_19912);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.field_19914, this.width() / 2, class_4359.method_21072(3), 16777215);
		if (!this.field_19915) {
			this.drawCenteredString(field_19908[this.field_19918 % field_19908.length], this.width() / 2, class_4359.method_21072(8), 8421504);
		}

		if (this.field_19915) {
			this.drawCenteredString(this.field_19916, this.width() / 2, class_4359.method_21072(8), 16711680);
		}

		super.render(i, j, f);
	}

	public void method_21290(String string) {
		this.field_19915 = true;
		this.field_19916 = string;
		Realms.narrateNow(string);
		this.buttonsClear();
		this.buttonsAdd(new RealmsButton(667, this.width() / 2 - 106, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				class_4398.this.method_21293();
			}
		});
	}

	public void method_21292(String string) {
		this.field_19914 = string;
	}

	public boolean method_21291() {
		return this.field_19917;
	}
}
