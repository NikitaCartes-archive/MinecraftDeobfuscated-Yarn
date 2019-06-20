package net.minecraft;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_500 extends class_437 {
	private static final Logger field_3044 = LogManager.getLogger();
	private final class_644 field_3037 = new class_644();
	private final class_437 field_3049;
	protected class_4267 field_3043;
	private class_641 field_3040;
	private class_4185 field_3041;
	private class_4185 field_3050;
	private class_4185 field_3047;
	private String field_3042;
	private class_642 field_3051;
	private class_1134.class_1136 field_3046;
	private class_1134.class_1135 field_3045;
	private boolean field_3048;

	public class_500(class_437 arg) {
		super(new class_2588("multiplayer.title"));
		this.field_3049 = arg;
	}

	@Override
	protected void init() {
		super.init();
		this.minecraft.field_1774.method_1462(true);
		if (this.field_3048) {
			this.field_3043.updateSize(this.width, this.height, 32, this.height - 64);
		} else {
			this.field_3048 = true;
			this.field_3040 = new class_641(this.minecraft);
			this.field_3040.method_2981();
			this.field_3046 = new class_1134.class_1136();

			try {
				this.field_3045 = new class_1134.class_1135(this.field_3046);
				this.field_3045.start();
			} catch (Exception var2) {
				field_3044.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.field_3043 = new class_4267(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
			this.field_3043.method_20125(this.field_3040);
		}

		this.children.add(this.field_3043);
		this.field_3050 = this.addButton(
			new class_4185(this.width / 2 - 154, this.height - 52, 100, 20, class_1074.method_4662("selectServer.select"), arg -> this.method_2536())
		);
		this.addButton(new class_4185(this.width / 2 - 50, this.height - 52, 100, 20, class_1074.method_4662("selectServer.direct"), arg -> {
			this.field_3051 = new class_642(class_1074.method_4662("selectServer.defaultName"), "", false);
			this.minecraft.method_1507(new class_420(this::method_20380, this.field_3051));
		}));
		this.addButton(new class_4185(this.width / 2 + 4 + 50, this.height - 52, 100, 20, class_1074.method_4662("selectServer.add"), arg -> {
			this.field_3051 = new class_642(class_1074.method_4662("selectServer.defaultName"), "", false);
			this.minecraft.method_1507(new class_422(this::method_20379, this.field_3051));
		}));
		this.field_3041 = this.addButton(new class_4185(this.width / 2 - 154, this.height - 28, 70, 20, class_1074.method_4662("selectServer.edit"), arg -> {
			class_4267.class_504 lv = this.field_3043.getSelected();
			if (lv instanceof class_4267.class_4270) {
				class_642 lv2 = ((class_4267.class_4270)lv).method_20133();
				this.field_3051 = new class_642(lv2.field_3752, lv2.field_3761, false);
				this.field_3051.method_2996(lv2);
				this.minecraft.method_1507(new class_422(this::method_20378, this.field_3051));
			}
		}));
		this.field_3047 = this.addButton(new class_4185(this.width / 2 - 74, this.height - 28, 70, 20, class_1074.method_4662("selectServer.delete"), arg -> {
			class_4267.class_504 lv = this.field_3043.getSelected();
			if (lv instanceof class_4267.class_4270) {
				String string = ((class_4267.class_4270)lv).method_20133().field_3752;
				if (string != null) {
					class_2561 lv2 = new class_2588("selectServer.deleteQuestion");
					class_2561 lv3 = new class_2588("selectServer.deleteWarning", string);
					String string2 = class_1074.method_4662("selectServer.deleteButton");
					String string3 = class_1074.method_4662("gui.cancel");
					this.minecraft.method_1507(new class_410(this::method_20377, lv2, lv3, string2, string3));
				}
			}
		}));
		this.addButton(new class_4185(this.width / 2 + 4, this.height - 28, 70, 20, class_1074.method_4662("selectServer.refresh"), arg -> this.method_2534()));
		this.addButton(
			new class_4185(this.width / 2 + 4 + 76, this.height - 28, 75, 20, class_1074.method_4662("gui.cancel"), arg -> this.minecraft.method_1507(this.field_3049))
		);
		this.method_20121();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.field_3046.method_4823()) {
			List<class_1131> list = this.field_3046.method_4826();
			this.field_3046.method_4825();
			this.field_3043.method_20126(list);
		}

		this.field_3037.method_3000();
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
		if (this.field_3045 != null) {
			this.field_3045.interrupt();
			this.field_3045 = null;
		}

		this.field_3037.method_3004();
	}

	private void method_2534() {
		this.minecraft.method_1507(new class_500(this.field_3049));
	}

	private void method_20377(boolean bl) {
		class_4267.class_504 lv = this.field_3043.getSelected();
		if (bl && lv instanceof class_4267.class_4270) {
			this.field_3040.method_2983(((class_4267.class_4270)lv).method_20133());
			this.field_3040.method_2987();
			this.field_3043.method_20122(null);
			this.field_3043.method_20125(this.field_3040);
		}

		this.minecraft.method_1507(this);
	}

	private void method_20378(boolean bl) {
		class_4267.class_504 lv = this.field_3043.getSelected();
		if (bl && lv instanceof class_4267.class_4270) {
			class_642 lv2 = ((class_4267.class_4270)lv).method_20133();
			lv2.field_3752 = this.field_3051.field_3752;
			lv2.field_3761 = this.field_3051.field_3761;
			lv2.method_2996(this.field_3051);
			this.field_3040.method_2987();
			this.field_3043.method_20125(this.field_3040);
		}

		this.minecraft.method_1507(this);
	}

	private void method_20379(boolean bl) {
		if (bl) {
			this.field_3040.method_2988(this.field_3051);
			this.field_3040.method_2987();
			this.field_3043.method_20122(null);
			this.field_3043.method_20125(this.field_3040);
		}

		this.minecraft.method_1507(this);
	}

	private void method_20380(boolean bl) {
		if (bl) {
			this.method_2548(this.field_3051);
		} else {
			this.minecraft.method_1507(this);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 294) {
			this.method_2534();
			return true;
		} else if (this.field_3043.getSelected() == null || i != 257 && i != 335) {
			return false;
		} else {
			this.method_2536();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_3042 = null;
		this.renderBackground();
		this.field_3043.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
		if (this.field_3042 != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3042)), i, j);
		}
	}

	public void method_2536() {
		class_4267.class_504 lv = this.field_3043.getSelected();
		if (lv instanceof class_4267.class_4270) {
			this.method_2548(((class_4267.class_4270)lv).method_20133());
		} else if (lv instanceof class_4267.class_4269) {
			class_1131 lv2 = ((class_4267.class_4269)lv).method_20132();
			this.method_2548(new class_642(lv2.method_4813(), lv2.method_4812(), true));
		}
	}

	private void method_2548(class_642 arg) {
		this.minecraft.method_1507(new class_412(this, this.minecraft, arg));
	}

	public void method_2531(class_4267.class_504 arg) {
		this.field_3043.method_20122(arg);
		this.method_20121();
	}

	protected void method_20121() {
		this.field_3050.active = false;
		this.field_3041.active = false;
		this.field_3047.active = false;
		class_4267.class_504 lv = this.field_3043.getSelected();
		if (lv != null && !(lv instanceof class_4267.class_4268)) {
			this.field_3050.active = true;
			if (lv instanceof class_4267.class_4270) {
				this.field_3041.active = true;
				this.field_3047.active = true;
			}
		}
	}

	public class_644 method_2538() {
		return this.field_3037;
	}

	public void method_2528(String string) {
		this.field_3042 = string;
	}

	public class_641 method_2529() {
		return this.field_3040;
	}
}
