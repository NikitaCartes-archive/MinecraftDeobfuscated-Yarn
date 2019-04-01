package net.minecraft;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_497 extends class_437 {
	private final class_2633 field_2980;
	private class_2415 field_2996 = class_2415.field_11302;
	private class_2470 field_3003 = class_2470.field_11467;
	private class_2776 field_3004 = class_2776.field_12696;
	private boolean field_2985;
	private boolean field_2997;
	private boolean field_2983;
	private class_342 field_3005;
	private class_342 field_2982;
	private class_342 field_2999;
	private class_342 field_3010;
	private class_342 field_2988;
	private class_342 field_2998;
	private class_342 field_2978;
	private class_342 field_3000;
	private class_342 field_2992;
	private class_342 field_2986;
	private class_4185 field_3002;
	private class_4185 field_2994;
	private class_4185 field_2987;
	private class_4185 field_3006;
	private class_4185 field_2995;
	private class_4185 field_2981;
	private class_4185 field_3007;
	private class_4185 field_2993;
	private class_4185 field_2977;
	private class_4185 field_3009;
	private class_4185 field_2990;
	private class_4185 field_2979;
	private class_4185 field_3008;
	private class_4185 field_3001;
	private final DecimalFormat field_2991 = new DecimalFormat("0.0###");

	public class_497(class_2633 arg) {
		super(new class_2588(class_2246.field_10465.method_9539()));
		this.field_2980 = arg;
		this.field_2991.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
	}

	@Override
	public void tick() {
		this.field_3005.method_1865();
		this.field_2982.method_1865();
		this.field_2999.method_1865();
		this.field_3010.method_1865();
		this.field_2988.method_1865();
		this.field_2998.method_1865();
		this.field_2978.method_1865();
		this.field_3000.method_1865();
		this.field_2992.method_1865();
		this.field_2986.method_1865();
	}

	private void method_2515() {
		if (this.method_2516(class_2633.class_2634.field_12108)) {
			this.minecraft.method_1507(null);
		}
	}

	private void method_2514() {
		this.field_2980.method_11356(this.field_2996);
		this.field_2980.method_11385(this.field_3003);
		this.field_2980.method_11381(this.field_3004);
		this.field_2980.method_11352(this.field_2985);
		this.field_2980.method_11347(this.field_2997);
		this.field_2980.method_11360(this.field_2983);
		this.minecraft.method_1507(null);
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_3002 = this.addButton(new class_4185(this.width / 2 - 4 - 150, 210, 150, 20, class_1074.method_4662("gui.done"), arg -> this.method_2515()));
		this.field_2994 = this.addButton(new class_4185(this.width / 2 + 4, 210, 150, 20, class_1074.method_4662("gui.cancel"), arg -> this.method_2514()));
		this.field_2987 = this.addButton(new class_4185(this.width / 2 + 4 + 100, 185, 50, 20, class_1074.method_4662("structure_block.button.save"), arg -> {
			if (this.field_2980.method_11374() == class_2776.field_12695) {
				this.method_2516(class_2633.class_2634.field_12110);
				this.minecraft.method_1507(null);
			}
		}));
		this.field_3006 = this.addButton(new class_4185(this.width / 2 + 4 + 100, 185, 50, 20, class_1074.method_4662("structure_block.button.load"), arg -> {
			if (this.field_2980.method_11374() == class_2776.field_12697) {
				this.method_2516(class_2633.class_2634.field_12109);
				this.minecraft.method_1507(null);
			}
		}));
		this.field_2977 = this.addButton(new class_4185(this.width / 2 - 4 - 150, 185, 50, 20, "MODE", arg -> {
			this.field_2980.method_11380();
			this.method_2509();
		}));
		this.field_3009 = this.addButton(new class_4185(this.width / 2 + 4 + 100, 120, 50, 20, class_1074.method_4662("structure_block.button.detect_size"), arg -> {
			if (this.field_2980.method_11374() == class_2776.field_12695) {
				this.method_2516(class_2633.class_2634.field_12106);
				this.minecraft.method_1507(null);
			}
		}));
		this.field_2990 = this.addButton(new class_4185(this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES", arg -> {
			this.field_2980.method_11352(!this.field_2980.method_11367());
			this.method_2524();
		}));
		this.field_2979 = this.addButton(new class_4185(this.width / 2 - 20, 185, 40, 20, "MIRROR", arg -> {
			switch (this.field_2980.method_11345()) {
				case field_11302:
					this.field_2980.method_11356(class_2415.field_11300);
					break;
				case field_11300:
					this.field_2980.method_11356(class_2415.field_11301);
					break;
				case field_11301:
					this.field_2980.method_11356(class_2415.field_11302);
			}

			this.method_2508();
		}));
		this.field_3008 = this.addButton(new class_4185(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR", arg -> {
			this.field_2980.method_11347(!this.field_2980.method_11375());
			this.method_2513();
		}));
		this.field_3001 = this.addButton(new class_4185(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB", arg -> {
			this.field_2980.method_11360(!this.field_2980.method_11357());
			this.method_2511();
		}));
		this.field_2995 = this.addButton(new class_4185(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0", arg -> {
			this.field_2980.method_11385(class_2470.field_11467);
			this.method_2510();
		}));
		this.field_2981 = this.addButton(new class_4185(this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90", arg -> {
			this.field_2980.method_11385(class_2470.field_11463);
			this.method_2510();
		}));
		this.field_3007 = this.addButton(new class_4185(this.width / 2 + 1 + 20, 185, 40, 20, "180", arg -> {
			this.field_2980.method_11385(class_2470.field_11464);
			this.method_2510();
		}));
		this.field_2993 = this.addButton(new class_4185(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270", arg -> {
			this.field_2980.method_11385(class_2470.field_11465);
			this.method_2510();
		}));
		this.field_3005 = new class_342(this.font, this.width / 2 - 152, 40, 300, 20) {
			@Override
			public boolean charTyped(char c, int i) {
				return !class_497.this.isValidCharacterForName(this.method_1882(), c, this.method_1881()) ? false : super.charTyped(c, i);
			}
		};
		this.field_3005.method_1880(64);
		this.field_3005.method_1852(this.field_2980.method_11362());
		this.children.add(this.field_3005);
		class_2338 lv = this.field_2980.method_11359();
		this.field_2982 = new class_342(this.font, this.width / 2 - 152, 80, 80, 20);
		this.field_2982.method_1880(15);
		this.field_2982.method_1852(Integer.toString(lv.method_10263()));
		this.children.add(this.field_2982);
		this.field_2999 = new class_342(this.font, this.width / 2 - 72, 80, 80, 20);
		this.field_2999.method_1880(15);
		this.field_2999.method_1852(Integer.toString(lv.method_10264()));
		this.children.add(this.field_2999);
		this.field_3010 = new class_342(this.font, this.width / 2 + 8, 80, 80, 20);
		this.field_3010.method_1880(15);
		this.field_3010.method_1852(Integer.toString(lv.method_10260()));
		this.children.add(this.field_3010);
		class_2338 lv2 = this.field_2980.method_11349();
		this.field_2988 = new class_342(this.font, this.width / 2 - 152, 120, 80, 20);
		this.field_2988.method_1880(15);
		this.field_2988.method_1852(Integer.toString(lv2.method_10263()));
		this.children.add(this.field_2988);
		this.field_2998 = new class_342(this.font, this.width / 2 - 72, 120, 80, 20);
		this.field_2998.method_1880(15);
		this.field_2998.method_1852(Integer.toString(lv2.method_10264()));
		this.children.add(this.field_2998);
		this.field_2978 = new class_342(this.font, this.width / 2 + 8, 120, 80, 20);
		this.field_2978.method_1880(15);
		this.field_2978.method_1852(Integer.toString(lv2.method_10260()));
		this.children.add(this.field_2978);
		this.field_3000 = new class_342(this.font, this.width / 2 - 152, 120, 80, 20);
		this.field_3000.method_1880(15);
		this.field_3000.method_1852(this.field_2991.format((double)this.field_2980.method_11346()));
		this.children.add(this.field_3000);
		this.field_2992 = new class_342(this.font, this.width / 2 - 72, 120, 80, 20);
		this.field_2992.method_1880(31);
		this.field_2992.method_1852(Long.toString(this.field_2980.method_11371()));
		this.children.add(this.field_2992);
		this.field_2986 = new class_342(this.font, this.width / 2 - 152, 120, 240, 20);
		this.field_2986.method_1880(128);
		this.field_2986.method_1852(this.field_2980.method_11358());
		this.children.add(this.field_2986);
		this.field_2996 = this.field_2980.method_11345();
		this.method_2508();
		this.field_3003 = this.field_2980.method_11353();
		this.method_2510();
		this.field_3004 = this.field_2980.method_11374();
		this.method_2509();
		this.field_2985 = this.field_2980.method_11367();
		this.method_2524();
		this.field_2997 = this.field_2980.method_11375();
		this.method_2513();
		this.field_2983 = this.field_2980.method_11357();
		this.method_2511();
		this.method_20085(this.field_3005);
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_3005.method_1882();
		String string2 = this.field_2982.method_1882();
		String string3 = this.field_2999.method_1882();
		String string4 = this.field_3010.method_1882();
		String string5 = this.field_2988.method_1882();
		String string6 = this.field_2998.method_1882();
		String string7 = this.field_2978.method_1882();
		String string8 = this.field_3000.method_1882();
		String string9 = this.field_2992.method_1882();
		String string10 = this.field_2986.method_1882();
		this.init(arg, i, j);
		this.field_3005.method_1852(string);
		this.field_2982.method_1852(string2);
		this.field_2999.method_1852(string3);
		this.field_3010.method_1852(string4);
		this.field_2988.method_1852(string5);
		this.field_2998.method_1852(string6);
		this.field_2978.method_1852(string7);
		this.field_3000.method_1852(string8);
		this.field_2992.method_1852(string9);
		this.field_2986.method_1852(string10);
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
	}

	private void method_2524() {
		boolean bl = !this.field_2980.method_11367();
		if (bl) {
			this.field_2990.setMessage(class_1074.method_4662("options.on"));
		} else {
			this.field_2990.setMessage(class_1074.method_4662("options.off"));
		}
	}

	private void method_2513() {
		boolean bl = this.field_2980.method_11375();
		if (bl) {
			this.field_3008.setMessage(class_1074.method_4662("options.on"));
		} else {
			this.field_3008.setMessage(class_1074.method_4662("options.off"));
		}
	}

	private void method_2511() {
		boolean bl = this.field_2980.method_11357();
		if (bl) {
			this.field_3001.setMessage(class_1074.method_4662("options.on"));
		} else {
			this.field_3001.setMessage(class_1074.method_4662("options.off"));
		}
	}

	private void method_2508() {
		class_2415 lv = this.field_2980.method_11345();
		switch (lv) {
			case field_11302:
				this.field_2979.setMessage("|");
				break;
			case field_11300:
				this.field_2979.setMessage("< >");
				break;
			case field_11301:
				this.field_2979.setMessage("^ v");
		}
	}

	private void method_2510() {
		this.field_2995.active = true;
		this.field_2981.active = true;
		this.field_3007.active = true;
		this.field_2993.active = true;
		switch (this.field_2980.method_11353()) {
			case field_11467:
				this.field_2995.active = false;
				break;
			case field_11464:
				this.field_3007.active = false;
				break;
			case field_11465:
				this.field_2993.active = false;
				break;
			case field_11463:
				this.field_2981.active = false;
		}
	}

	private void method_2509() {
		this.field_3005.method_1862(false);
		this.field_2982.method_1862(false);
		this.field_2999.method_1862(false);
		this.field_3010.method_1862(false);
		this.field_2988.method_1862(false);
		this.field_2998.method_1862(false);
		this.field_2978.method_1862(false);
		this.field_3000.method_1862(false);
		this.field_2992.method_1862(false);
		this.field_2986.method_1862(false);
		this.field_2987.visible = false;
		this.field_3006.visible = false;
		this.field_3009.visible = false;
		this.field_2990.visible = false;
		this.field_2979.visible = false;
		this.field_2995.visible = false;
		this.field_2981.visible = false;
		this.field_3007.visible = false;
		this.field_2993.visible = false;
		this.field_3008.visible = false;
		this.field_3001.visible = false;
		switch (this.field_2980.method_11374()) {
			case field_12695:
				this.field_3005.method_1862(true);
				this.field_2982.method_1862(true);
				this.field_2999.method_1862(true);
				this.field_3010.method_1862(true);
				this.field_2988.method_1862(true);
				this.field_2998.method_1862(true);
				this.field_2978.method_1862(true);
				this.field_2987.visible = true;
				this.field_3009.visible = true;
				this.field_2990.visible = true;
				this.field_3008.visible = true;
				break;
			case field_12697:
				this.field_3005.method_1862(true);
				this.field_2982.method_1862(true);
				this.field_2999.method_1862(true);
				this.field_3010.method_1862(true);
				this.field_3000.method_1862(true);
				this.field_2992.method_1862(true);
				this.field_3006.visible = true;
				this.field_2990.visible = true;
				this.field_2979.visible = true;
				this.field_2995.visible = true;
				this.field_2981.visible = true;
				this.field_3007.visible = true;
				this.field_2993.visible = true;
				this.field_3001.visible = true;
				this.method_2510();
				break;
			case field_12699:
				this.field_3005.method_1862(true);
				break;
			case field_12696:
				this.field_2986.method_1862(true);
		}

		this.field_2977.setMessage(class_1074.method_4662("structure_block.mode." + this.field_2980.method_11374().method_15434()));
	}

	private boolean method_2516(class_2633.class_2634 arg) {
		class_2338 lv = new class_2338(
			this.method_2517(this.field_2982.method_1882()), this.method_2517(this.field_2999.method_1882()), this.method_2517(this.field_3010.method_1882())
		);
		class_2338 lv2 = new class_2338(
			this.method_2517(this.field_2988.method_1882()), this.method_2517(this.field_2998.method_1882()), this.method_2517(this.field_2978.method_1882())
		);
		float f = this.method_2500(this.field_3000.method_1882());
		long l = this.method_2504(this.field_2992.method_1882());
		this.minecraft
			.method_1562()
			.method_2883(
				new class_2875(
					this.field_2980.method_11016(),
					arg,
					this.field_2980.method_11374(),
					this.field_3005.method_1882(),
					lv,
					lv2,
					this.field_2980.method_11345(),
					this.field_2980.method_11353(),
					this.field_2986.method_1882(),
					this.field_2980.method_11367(),
					this.field_2980.method_11375(),
					this.field_2980.method_11357(),
					f,
					l
				)
			);
		return true;
	}

	private long method_2504(String string) {
		try {
			return Long.valueOf(string);
		} catch (NumberFormatException var3) {
			return 0L;
		}
	}

	private float method_2500(String string) {
		try {
			return Float.valueOf(string);
		} catch (NumberFormatException var3) {
			return 1.0F;
		}
	}

	private int method_2517(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException var3) {
			return 0;
		}
	}

	@Override
	public void onClose() {
		this.method_2514();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.method_2515();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		class_2776 lv = this.field_2980.method_11374();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 10, 16777215);
		if (lv != class_2776.field_12696) {
			this.drawString(this.font, class_1074.method_4662("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
			this.field_3005.render(i, j, f);
		}

		if (lv == class_2776.field_12697 || lv == class_2776.field_12695) {
			this.drawString(this.font, class_1074.method_4662("structure_block.position"), this.width / 2 - 153, 70, 10526880);
			this.field_2982.render(i, j, f);
			this.field_2999.render(i, j, f);
			this.field_3010.render(i, j, f);
			String string = class_1074.method_4662("structure_block.include_entities");
			int k = this.font.method_1727(string);
			this.drawString(this.font, string, this.width / 2 + 154 - k, 150, 10526880);
		}

		if (lv == class_2776.field_12695) {
			this.drawString(this.font, class_1074.method_4662("structure_block.size"), this.width / 2 - 153, 110, 10526880);
			this.field_2988.render(i, j, f);
			this.field_2998.render(i, j, f);
			this.field_2978.render(i, j, f);
			String string = class_1074.method_4662("structure_block.detect_size");
			int k = this.font.method_1727(string);
			this.drawString(this.font, string, this.width / 2 + 154 - k, 110, 10526880);
			String string2 = class_1074.method_4662("structure_block.show_air");
			int l = this.font.method_1727(string2);
			this.drawString(this.font, string2, this.width / 2 + 154 - l, 70, 10526880);
		}

		if (lv == class_2776.field_12697) {
			this.drawString(this.font, class_1074.method_4662("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
			this.field_3000.render(i, j, f);
			this.field_2992.render(i, j, f);
			String string = class_1074.method_4662("structure_block.show_boundingbox");
			int k = this.font.method_1727(string);
			this.drawString(this.font, string, this.width / 2 + 154 - k, 70, 10526880);
		}

		if (lv == class_2776.field_12696) {
			this.drawString(this.font, class_1074.method_4662("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
			this.field_2986.render(i, j, f);
		}

		String string = "structure_block.mode_info." + lv.method_15434();
		this.drawString(this.font, class_1074.method_4662(string), this.width / 2 - 153, 174, 10526880);
		super.render(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
