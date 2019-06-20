package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_519 extends class_437 {
	private final class_437 field_3156;
	private class_522 field_3157;
	private class_523 field_3154;
	private boolean field_3155;

	public class_519(class_437 arg) {
		super(new class_2588("resourcePack.title"));
		this.field_3156 = arg;
	}

	@Override
	protected void init() {
		this.addButton(
			new class_4185(
				this.width / 2 - 154,
				this.height - 48,
				150,
				20,
				class_1074.method_4662("resourcePack.openFolder"),
				arg -> class_156.method_668().method_672(this.minecraft.method_1479())
			)
		);
		this.addButton(new class_4185(this.width / 2 + 4, this.height - 48, 150, 20, class_1074.method_4662("gui.done"), arg -> {
			if (this.field_3155) {
				List<class_1075> listx = Lists.<class_1075>newArrayList();

				for (class_521.class_4271 lvx : this.field_3154.children()) {
					listx.add(lvx.method_20150());
				}

				Collections.reverse(listx);
				this.minecraft.method_1520().method_14447(listx);
				this.minecraft.field_1690.field_1887.clear();
				this.minecraft.field_1690.field_1846.clear();

				for (class_1075 lv2x : listx) {
					if (!lv2x.method_14465()) {
						this.minecraft.field_1690.field_1887.add(lv2x.method_14463());
						if (!lv2x.method_14460().method_14437()) {
							this.minecraft.field_1690.field_1846.add(lv2x.method_14463());
						}
					}
				}

				this.minecraft.field_1690.method_1640();
				this.minecraft.method_1507(this.field_3156);
				this.minecraft.method_1521();
			} else {
				this.minecraft.method_1507(this.field_3156);
			}
		}));
		class_522 lv = this.field_3157;
		class_523 lv2 = this.field_3154;
		this.field_3157 = new class_522(this.minecraft, 200, this.height);
		this.field_3157.setLeftPos(this.width / 2 - 4 - 200);
		if (lv != null) {
			this.field_3157.children().addAll(lv.children());
		}

		this.children.add(this.field_3157);
		this.field_3154 = new class_523(this.minecraft, 200, this.height);
		this.field_3154.setLeftPos(this.width / 2 + 4);
		if (lv2 != null) {
			this.field_3154.children().addAll(lv2.children());
		}

		this.children.add(this.field_3154);
		if (!this.field_3155) {
			this.field_3157.children().clear();
			this.field_3154.children().clear();
			class_3283<class_1075> lv3 = this.minecraft.method_1520();
			lv3.method_14445();
			List<class_1075> list = Lists.<class_1075>newArrayList(lv3.method_14441());
			list.removeAll(lv3.method_14444());

			for (class_1075 lv4 : list) {
				this.field_3157.method_2690(new class_521.class_4271(this.field_3157, this, lv4));
			}

			for (class_1075 lv4 : Lists.reverse(Lists.newArrayList(lv3.method_14444()))) {
				this.field_3154.method_2690(new class_521.class_4271(this.field_3154, this, lv4));
			}
		}
	}

	public void method_2674(class_521.class_4271 arg) {
		this.field_3157.children().remove(arg);
		arg.method_20145(this.field_3154);
		this.method_2660();
	}

	public void method_2663(class_521.class_4271 arg) {
		this.field_3154.children().remove(arg);
		this.field_3157.method_2690(arg);
		this.method_2660();
	}

	public boolean method_2669(class_521.class_4271 arg) {
		return this.field_3154.children().contains(arg);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.field_3157.render(i, j, f);
		this.field_3154.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.font, class_1074.method_4662("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.render(i, j, f);
	}

	public void method_2660() {
		this.field_3155 = true;
	}
}
