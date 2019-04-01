package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_525 extends class_437 {
	private final class_437 field_3187;
	private class_342 field_3188;
	private class_342 field_3184;
	private String field_3196;
	private String field_3201 = "survival";
	private String field_3185;
	private boolean field_3180 = true;
	private boolean field_3192;
	private boolean field_3179;
	private boolean field_3191;
	private boolean field_3178;
	private boolean field_3190;
	private boolean field_3202;
	private class_4185 field_3205;
	private class_4185 field_3186;
	private class_4185 field_3193;
	private class_4185 field_3203;
	private class_4185 field_3197;
	private class_4185 field_3189;
	private class_4185 field_3182;
	private class_4185 field_3198;
	private String field_3194;
	private String field_3199;
	private String field_3181;
	private String field_3195;
	private int field_3204;
	public class_2487 field_18979 = new class_2487();

	public class_525(class_437 arg) {
		super(new class_2588("selectWorld.create"));
		this.field_3187 = arg;
		this.field_3181 = "";
		this.field_3195 = class_1074.method_4662("selectWorld.newWorld");
	}

	@Override
	public void tick() {
		this.field_3188.method_1865();
		this.field_3184.method_1865();
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_3188 = new class_342(this.font, this.width / 2 - 100, 60, 200, 20);
		this.field_3188.method_1852(this.field_3195);
		this.field_3188.method_1863(string -> {
			this.field_3195 = string;
			this.field_3205.active = !this.field_3188.method_1882().isEmpty();
			this.method_2727();
		});
		this.children.add(this.field_3188);
		this.field_3186 = this.addButton(new class_4185(this.width / 2 - 75, 115, 150, 20, class_1074.method_4662("selectWorld.gameMode"), arg -> {
			if ("survival".equals(this.field_3201)) {
				if (!this.field_3179) {
					this.field_3192 = false;
				}

				this.field_3178 = false;
				this.field_3201 = "hardcore";
				this.field_3178 = true;
				this.field_3182.active = false;
				this.field_3197.active = false;
				this.method_2722();
			} else if ("hardcore".equals(this.field_3201)) {
				if (!this.field_3179) {
					this.field_3192 = true;
				}

				this.field_3178 = false;
				this.field_3201 = "creative";
				this.method_2722();
				this.field_3178 = false;
				this.field_3182.active = true;
				this.field_3197.active = true;
			} else {
				if (!this.field_3179) {
					this.field_3192 = false;
				}

				this.field_3201 = "survival";
				this.method_2722();
				this.field_3182.active = true;
				this.field_3197.active = true;
				this.field_3178 = false;
			}

			this.method_2722();
		}));
		this.field_3184 = new class_342(this.font, this.width / 2 - 100, 60, 200, 20);
		this.field_3184.method_1852(this.field_3181);
		this.field_3184.method_1863(string -> this.field_3181 = this.field_3184.method_1882());
		this.children.add(this.field_3184);
		this.field_3203 = this.addButton(new class_4185(this.width / 2 - 155, 100, 150, 20, class_1074.method_4662("selectWorld.mapFeatures"), arg -> {
			this.field_3180 = !this.field_3180;
			this.method_2722();
		}));
		this.field_3203.visible = false;
		this.field_3189 = this.addButton(new class_4185(this.width / 2 + 5, 100, 150, 20, class_1074.method_4662("selectWorld.mapType"), arg -> {
			this.field_3204++;
			if (this.field_3204 >= class_1942.field_9279.length) {
				this.field_3204 = 0;
			}

			while (!this.method_2723()) {
				this.field_3204++;
				if (this.field_3204 >= class_1942.field_9279.length) {
					this.field_3204 = 0;
				}
			}

			this.field_18979 = new class_2487();
			this.method_2722();
			this.method_2710(this.field_3202);
		}));
		this.field_3189.visible = false;
		this.field_3198 = this.addButton(new class_4185(this.width / 2 + 5, 120, 150, 20, class_1074.method_4662("selectWorld.customizeType"), arg -> {
			if (class_1942.field_9279[this.field_3204] == class_1942.field_9277) {
				this.minecraft.method_1507(new class_413(this, this.field_18979));
			}

			if (class_1942.field_9279[this.field_3204] == class_1942.field_9275) {
				this.minecraft.method_1507(new class_415(this, this.field_18979));
			}
		}));
		this.field_3198.visible = false;
		this.field_3182 = this.addButton(new class_4185(this.width / 2 - 155, 151, 150, 20, class_1074.method_4662("selectWorld.allowCommands"), arg -> {
			this.field_3179 = true;
			this.field_3192 = !this.field_3192;
			this.method_2722();
		}));
		this.field_3182.visible = false;
		this.field_3197 = this.addButton(new class_4185(this.width / 2 + 5, 151, 150, 20, class_1074.method_4662("selectWorld.bonusItems"), arg -> {
			this.field_3191 = !this.field_3191;
			this.method_2722();
		}));
		this.field_3197.visible = false;
		this.field_3193 = this.addButton(
			new class_4185(this.width / 2 - 75, 187, 150, 20, class_1074.method_4662("selectWorld.moreWorldOptions"), arg -> this.method_2721())
		);
		this.field_3205 = this.addButton(
			new class_4185(this.width / 2 - 155, this.height - 28, 150, 20, class_1074.method_4662("selectWorld.create"), arg -> this.method_2736())
		);
		this.addButton(
			new class_4185(this.width / 2 + 5, this.height - 28, 150, 20, class_1074.method_4662("gui.cancel"), arg -> this.minecraft.method_1507(this.field_3187))
		);
		this.method_2710(this.field_3202);
		this.method_20085(this.field_3188);
		this.method_2727();
		this.method_2722();
	}

	private void method_2727() {
		this.field_3196 = this.field_3188.method_1882().trim();
		if (this.field_3196.length() == 0) {
			this.field_3196 = "World";
		}

		try {
			this.field_3196 = class_4239.method_19773(this.minecraft.method_1586().method_19636(), this.field_3196, "");
		} catch (Exception var4) {
			this.field_3196 = "World";

			try {
				this.field_3196 = class_4239.method_19773(this.minecraft.method_1586().method_19636(), this.field_3196, "");
			} catch (Exception var3) {
				throw new RuntimeException("Could not create save folder", var3);
			}
		}
	}

	private void method_2722() {
		this.field_3186.setMessage(class_1074.method_4662("selectWorld.gameMode") + ": " + class_1074.method_4662("selectWorld.gameMode." + this.field_3201));
		this.field_3194 = class_1074.method_4662("selectWorld.gameMode." + this.field_3201 + ".line1");
		this.field_3199 = class_1074.method_4662("selectWorld.gameMode." + this.field_3201 + ".line2");
		this.field_3203.setMessage(class_1074.method_4662("selectWorld.mapFeatures") + ' ' + class_1074.method_4662(this.field_3180 ? "options.on" : "options.off"));
		this.field_3197
			.setMessage(
				class_1074.method_4662("selectWorld.bonusItems") + ' ' + class_1074.method_4662(this.field_3191 && !this.field_3178 ? "options.on" : "options.off")
			);
		this.field_3189
			.setMessage(class_1074.method_4662("selectWorld.mapType") + ' ' + class_1074.method_4662(class_1942.field_9279[this.field_3204].method_8640()));
		this.field_3182
			.setMessage(
				class_1074.method_4662("selectWorld.allowCommands") + ' ' + class_1074.method_4662(this.field_3192 && !this.field_3178 ? "options.on" : "options.off")
			);
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
	}

	private void method_2736() {
		this.minecraft.method_1507(null);
		if (!this.field_3190) {
			this.field_3190 = true;
			long l = new Random().nextLong();
			String string = this.field_3184.method_1882();
			if (!StringUtils.isEmpty(string)) {
				try {
					long m = Long.parseLong(string);
					if (m != 0L) {
						l = m;
					}
				} catch (NumberFormatException var6) {
					l = (long)string.hashCode();
				}
			}

			class_1940 lv = new class_1940(l, class_1934.method_8385(this.field_3201), this.field_3180, this.field_3178, class_1942.field_9279[this.field_3204]);
			lv.method_8579(Dynamic.convert(class_2509.field_11560, JsonOps.INSTANCE, this.field_18979));
			if (this.field_3191 && !this.field_3178) {
				lv.method_8575();
			}

			if (this.field_3192 && !this.field_3178) {
				lv.method_8578();
			}

			this.minecraft.method_1559(this.field_3196, this.field_3188.method_1882().trim(), lv);
		}
	}

	private boolean method_2723() {
		class_1942 lv = class_1942.field_9279[this.field_3204];
		if (lv == null || !lv.method_8642()) {
			return false;
		} else {
			return lv == class_1942.field_9266 ? hasShiftDown() : true;
		}
	}

	private void method_2721() {
		this.method_2710(!this.field_3202);
	}

	private void method_2710(boolean bl) {
		this.field_3202 = bl;
		if (class_1942.field_9279[this.field_3204] == class_1942.field_9266) {
			this.field_3186.visible = !this.field_3202;
			this.field_3186.active = false;
			if (this.field_3185 == null) {
				this.field_3185 = this.field_3201;
			}

			this.field_3201 = "spectator";
			this.field_3203.visible = false;
			this.field_3197.visible = false;
			this.field_3189.visible = this.field_3202;
			this.field_3182.visible = false;
			this.field_3198.visible = false;
		} else {
			this.field_3186.visible = !this.field_3202;
			this.field_3186.active = true;
			if (this.field_3185 != null) {
				this.field_3201 = this.field_3185;
				this.field_3185 = null;
			}

			this.field_3203.visible = this.field_3202 && class_1942.field_9279[this.field_3204] != class_1942.field_9278;
			this.field_3197.visible = this.field_3202;
			this.field_3189.visible = this.field_3202;
			this.field_3182.visible = this.field_3202;
			this.field_3198.visible = this.field_3202 && class_1942.field_9279[this.field_3204].method_8641();
		}

		this.method_2722();
		this.field_3184.method_1862(this.field_3202);
		this.field_3188.method_1862(!this.field_3202);
		if (this.field_3202) {
			this.field_3193.setMessage(class_1074.method_4662("gui.done"));
		} else {
			this.field_3193.setMessage(class_1074.method_4662("selectWorld.moreWorldOptions"));
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 20, -1);
		if (this.field_3202) {
			this.drawString(this.font, class_1074.method_4662("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.font, class_1074.method_4662("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
			if (this.field_3203.visible) {
				this.drawString(this.font, class_1074.method_4662("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.field_3182.visible) {
				this.drawString(this.font, class_1074.method_4662("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.field_3184.render(i, j, f);
			if (class_1942.field_9279[this.field_3204].method_8644()) {
				this.font
					.method_1712(
						class_1074.method_4662(class_1942.field_9279[this.field_3204].method_8630()),
						this.field_3189.x + 2,
						this.field_3189.y + 22,
						this.field_3189.getWidth(),
						10526880
					);
			}
		} else {
			this.drawString(this.font, class_1074.method_4662("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.font, class_1074.method_4662("selectWorld.resultFolder") + " " + this.field_3196, this.width / 2 - 100, 85, -6250336);
			this.field_3188.render(i, j, f);
			this.drawCenteredString(this.font, this.field_3194, this.width / 2, 137, -6250336);
			this.drawCenteredString(this.font, this.field_3199, this.width / 2, 149, -6250336);
		}

		super.render(i, j, f);
	}

	public void method_2737(class_31 arg) {
		this.field_3195 = arg.method_150();
		this.field_3181 = arg.method_184() + "";
		class_1942 lv = arg.method_153() == class_1942.field_9278 ? class_1942.field_9265 : arg.method_153();
		this.field_3204 = lv.method_8637();
		this.field_18979 = arg.method_169();
		this.field_3180 = arg.method_220();
		this.field_3192 = arg.method_194();
		if (arg.method_152()) {
			this.field_3201 = "hardcore";
		} else if (arg.method_210().method_8388()) {
			this.field_3201 = "survival";
		} else if (arg.method_210().method_8386()) {
			this.field_3201 = "creative";
		}
	}
}
