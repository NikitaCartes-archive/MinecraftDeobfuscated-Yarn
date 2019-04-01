package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsBridge;

@Environment(EnvType.CLIENT)
public class class_433 extends class_437 {
	public class_433() {
		super(new class_2588("menu.game"));
	}

	@Override
	protected void init() {
		int i = -16;
		int j = 98;
		this.addButton(new class_4185(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, class_1074.method_4662("menu.returnToGame"), arg -> {
			this.minecraft.method_1507(null);
			this.minecraft.field_1729.method_1612();
		}));
		this.addButton(
			new class_4185(
				this.width / 2 - 102,
				this.height / 4 + 48 + -16,
				98,
				20,
				class_1074.method_4662("gui.advancements"),
				arg -> this.minecraft.method_1507(new class_457(this.minecraft.field_1724.field_3944.method_2869()))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 + 4,
				this.height / 4 + 48 + -16,
				98,
				20,
				class_1074.method_4662("gui.stats"),
				arg -> this.minecraft.method_1507(new class_447(this, this.minecraft.field_1724.method_3143()))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 - 102, this.height / 4 + 72 + -16, 204, 20, class_1074.method_4662("menu.sendFeedback"), arg -> this.minecraft.method_1507(new class_4293())
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 - 102,
				this.height / 4 + 96 + -16,
				98,
				20,
				class_1074.method_4662("menu.options"),
				arg -> this.minecraft.method_1507(new class_429(this, this.minecraft.field_1690))
			)
		);
		class_4185 lv = this.addButton(
			new class_4185(
				this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, class_1074.method_4662("menu.shareToLan"), arg -> this.minecraft.method_1507(new class_436(this))
			)
		);
		lv.active = this.minecraft.method_1496() && !this.minecraft.method_1576().method_3860();
		class_4185 lv2 = this.addButton(
			new class_4185(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, class_1074.method_4662("menu.returnToMenu"), arg -> {
				boolean bl = this.minecraft.method_1542();
				boolean bl2 = this.minecraft.method_1589();
				arg.active = false;
				this.minecraft.field_1687.method_8525();
				if (bl) {
					this.minecraft.method_18096(new class_424(new class_2588("menu.savingLevel")));
				} else {
					this.minecraft.method_18099();
				}

				if (bl) {
					this.minecraft.method_1507(new class_442());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new class_442());
				} else {
					this.minecraft.method_1507(new class_500(new class_442()));
				}
			})
		);
		if (!this.minecraft.method_1542()) {
			lv2.setMessage(class_1074.method_4662("menu.disconnect"));
		}
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 40, 16777215);
		super.render(i, j, f);
	}
}
