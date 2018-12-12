package net.minecraft.client.gui.menu.settings;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class SnooperSettingsGui extends Gui {
	private final Gui parent;
	private final GameOptions settings;
	private final List<String> field_2569 = Lists.<String>newArrayList();
	private final List<String> field_2567 = Lists.<String>newArrayList();
	private String field_2571;
	private String[] field_2572;
	private SnooperSettingsGui.class_439 field_2573;
	private ButtonWidget field_2568;

	public SnooperSettingsGui(Gui gui, GameOptions gameOptions) {
		this.parent = gui;
		this.settings = gameOptions;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.field_2573;
	}

	@Override
	protected void onInitialized() {
		this.field_2571 = I18n.translate("options.snooper.title");
		String string = I18n.translate("options.snooper.desc");
		List<String> list = Lists.<String>newArrayList();

		for (String string2 : this.fontRenderer.wrapStringToWidthAsList(string, this.width - 30)) {
			list.add(string2);
		}

		this.field_2572 = (String[])list.toArray(new String[list.size()]);
		this.field_2569.clear();
		this.field_2567.clear();
		ButtonWidget buttonWidget = new ButtonWidget(1, this.width / 2 - 152, this.height - 30, 150, 20, this.settings.getTranslatedName(GameOptions.Option.SNOOPER)) {
			@Override
			public void onPressed(double d, double e) {
				SnooperSettingsGui.this.settings.updateOption(GameOptions.Option.SNOOPER, 1);
				SnooperSettingsGui.this.field_2568.text = SnooperSettingsGui.this.settings.getTranslatedName(GameOptions.Option.SNOOPER);
			}
		};
		buttonWidget.enabled = false;
		this.field_2568 = this.addButton(buttonWidget);
		this.addButton(new ButtonWidget(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				SnooperSettingsGui.this.settings.write();
				SnooperSettingsGui.this.settings.write();
				SnooperSettingsGui.this.client.openGui(SnooperSettingsGui.this.parent);
			}
		});
		boolean bl = this.client.getServer() != null && this.client.getServer().getSnooper() != null;

		for (Entry<String, String> entry : new TreeMap(this.client.getSnooper().getEntryListClient()).entrySet()) {
			this.field_2569.add((bl ? "C " : "") + (String)entry.getKey());
			this.field_2567.add(this.fontRenderer.method_1714((String)entry.getValue(), this.width - 220));
		}

		if (bl) {
			for (Entry<String, String> entry : new TreeMap(this.client.getServer().getSnooper().getEntryListClient()).entrySet()) {
				this.field_2569.add("S " + (String)entry.getKey());
				this.field_2567.add(this.fontRenderer.method_1714((String)entry.getValue(), this.width - 220));
			}
		}

		this.field_2573 = new SnooperSettingsGui.class_439();
		this.listeners.add(this.field_2573);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.field_2573.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.field_2571, this.width / 2, 8, 16777215);
		int k = 22;

		for (String string : this.field_2572) {
			this.drawStringCentered(this.fontRenderer, string, this.width / 2, k, 8421504);
			k += this.fontRenderer.fontHeight;
		}

		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_439 extends AbstractListWidget {
		public class_439() {
			super(
				SnooperSettingsGui.this.client,
				SnooperSettingsGui.this.width,
				SnooperSettingsGui.this.height,
				80,
				SnooperSettingsGui.this.height - 40,
				SnooperSettingsGui.this.fontRenderer.fontHeight + 1
			);
		}

		@Override
		protected int getEntryCount() {
			return SnooperSettingsGui.this.field_2569.size();
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return false;
		}

		@Override
		protected void drawBackground() {
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			SnooperSettingsGui.this.fontRenderer.draw((String)SnooperSettingsGui.this.field_2569.get(i), 10.0F, (float)k, 16777215);
			SnooperSettingsGui.this.fontRenderer.draw((String)SnooperSettingsGui.this.field_2567.get(i), 230.0F, (float)k, 16777215);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 10;
		}
	}
}
