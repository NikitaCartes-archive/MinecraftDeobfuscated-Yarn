package net.minecraft;

import java.net.IDN;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_422 extends class_437 {
	private class_4185 field_2472;
	private final class_437 field_2470;
	private final class_642 field_2469;
	private class_342 field_2474;
	private class_342 field_2471;
	private class_4185 field_2473;
	private final Predicate<String> field_2475 = string -> {
		if (class_3544.method_15438(string)) {
			return true;
		} else {
			String[] strings = string.split(":");
			if (strings.length == 0) {
				return true;
			} else {
				try {
					String string2 = IDN.toASCII(strings[0]);
					return true;
				} catch (IllegalArgumentException var3) {
					return false;
				}
			}
		}
	};

	public class_422(class_437 arg, class_642 arg2) {
		super(new class_2588("addServer.title"));
		this.field_2470 = arg;
		this.field_2469 = arg2;
	}

	@Override
	public void tick() {
		this.field_2471.method_1865();
		this.field_2474.method_1865();
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_2471 = new class_342(this.font, this.width / 2 - 100, 66, 200, 20);
		this.field_2471.method_1876(true);
		this.field_2471.method_1852(this.field_2469.field_3752);
		this.field_2471.method_1863(this::method_2171);
		this.children.add(this.field_2471);
		this.field_2474 = new class_342(this.font, this.width / 2 - 100, 106, 200, 20);
		this.field_2474.method_1880(128);
		this.field_2474.method_1852(this.field_2469.field_3761);
		this.field_2474.method_1890(this.field_2475);
		this.field_2474.method_1863(this::method_2171);
		this.children.add(this.field_2474);
		this.field_2473 = this.addButton(
			new class_4185(
				this.width / 2 - 100,
				this.height / 4 + 72,
				200,
				20,
				class_1074.method_4662("addServer.resourcePack") + ": " + this.field_2469.method_2990().method_2997().method_10863(),
				arg -> {
					this.field_2469.method_2995(class_642.class_643.values()[(this.field_2469.method_2990().ordinal() + 1) % class_642.class_643.values().length]);
					this.field_2473.setMessage(class_1074.method_4662("addServer.resourcePack") + ": " + this.field_2469.method_2990().method_2997().method_10863());
				}
			)
		);
		this.field_2472 = this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, class_1074.method_4662("addServer.add"), arg -> this.method_2172())
		);
		this.addButton(
			new class_4185(
				this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, class_1074.method_4662("gui.cancel"), arg -> this.field_2470.confirmResult(false, 0)
			)
		);
		this.onClose();
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_2474.method_1882();
		String string2 = this.field_2471.method_1882();
		this.init(arg, i, j);
		this.field_2474.method_1852(string);
		this.field_2471.method_1852(string2);
	}

	private void method_2171(String string) {
		this.onClose();
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
	}

	private void method_2172() {
		this.field_2469.field_3752 = this.field_2471.method_1882();
		this.field_2469.field_3761 = this.field_2474.method_1882();
		this.field_2470.confirmResult(true, 0);
	}

	@Override
	public void onClose() {
		this.field_2472.active = !this.field_2474.method_1882().isEmpty()
			&& this.field_2474.method_1882().split(":").length > 0
			&& !this.field_2471.method_1882().isEmpty();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 17, 16777215);
		this.drawString(this.font, class_1074.method_4662("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
		this.drawString(this.font, class_1074.method_4662("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
		this.field_2471.render(i, j, f);
		this.field_2474.render(i, j, f);
		super.render(i, j, f);
	}
}
