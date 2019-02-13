package net.minecraft.client.gui.menu.settings;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class SnooperSettingsScreen extends Screen {
	private final Screen parent;
	private final GameOptions settings;
	private final List<String> field_2569 = Lists.<String>newArrayList();
	private final List<String> field_2567 = Lists.<String>newArrayList();
	private String title;
	private String[] description;
	private SnooperSettingsScreen.SnooperInfoList snooperInfoList;
	private ButtonWidget allowSnooperButton;

	public SnooperSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.snooperInfoList;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.snooper.title");
		String string = I18n.translate("options.snooper.desc");
		List<String> list = Lists.<String>newArrayList();

		for (String string2 : this.fontRenderer.wrapStringToWidthAsList(string, this.width - 30)) {
			list.add(string2);
		}

		this.description = (String[])list.toArray(new String[list.size()]);
		this.field_2569.clear();
		this.field_2567.clear();
		ButtonWidget buttonWidget = new ButtonWidget(1, this.width / 2 - 152, this.height - 30, 150, 20, this.settings.getTranslatedName(GameOptions.Option.SNOOPER)) {
			@Override
			public void onPressed(double d, double e) {
				SnooperSettingsScreen.this.settings.setInteger(GameOptions.Option.SNOOPER, 1);
				SnooperSettingsScreen.this.allowSnooperButton.setText(SnooperSettingsScreen.this.settings.getTranslatedName(GameOptions.Option.SNOOPER));
			}
		};
		buttonWidget.enabled = false;
		this.allowSnooperButton = this.addButton(buttonWidget);
		this.addButton(new ButtonWidget(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				SnooperSettingsScreen.this.settings.write();
				SnooperSettingsScreen.this.settings.write();
				SnooperSettingsScreen.this.client.openScreen(SnooperSettingsScreen.this.parent);
			}
		});
		boolean bl = this.client.getServer() != null && this.client.getServer().getSnooper() != null;

		for (Entry<String, String> entry : new TreeMap(this.client.getSnooper().getEntryListClient()).entrySet()) {
			this.field_2569.add((bl ? "C " : "") + (String)entry.getKey());
			this.field_2567.add(this.fontRenderer.trimToWidth((String)entry.getValue(), this.width - 220));
		}

		if (bl) {
			for (Entry<String, String> entry : new TreeMap(this.client.getServer().getSnooper().getEntryListClient()).entrySet()) {
				this.field_2569.add("S " + (String)entry.getKey());
				this.field_2567.add(this.fontRenderer.trimToWidth((String)entry.getValue(), this.width - 220));
			}
		}

		this.snooperInfoList = new SnooperSettingsScreen.SnooperInfoList();
		this.listeners.add(this.snooperInfoList);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.snooperInfoList.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
		int k = 22;

		for (String string : this.description) {
			this.drawStringCentered(this.fontRenderer, string, this.width / 2, k, 8421504);
			k += 9;
		}

		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class SnooperInfoList extends AbstractListWidget {
		public SnooperInfoList() {
			super(
				SnooperSettingsScreen.this.client, SnooperSettingsScreen.this.width, SnooperSettingsScreen.this.height, 80, SnooperSettingsScreen.this.height - 40, 9 + 1
			);
		}

		@Override
		protected int getEntryCount() {
			return SnooperSettingsScreen.this.field_2569.size();
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
			SnooperSettingsScreen.this.fontRenderer.draw((String)SnooperSettingsScreen.this.field_2569.get(i), 10.0F, (float)k, 16777215);
			SnooperSettingsScreen.this.fontRenderer.draw((String)SnooperSettingsScreen.this.field_2567.get(i), 230.0F, (float)k, 16777215);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 10;
		}
	}
}
