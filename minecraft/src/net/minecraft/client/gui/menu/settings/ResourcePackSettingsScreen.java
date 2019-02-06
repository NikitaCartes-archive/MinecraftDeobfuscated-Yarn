package net.minecraft.client.gui.menu.settings;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_520;
import net.minecraft.class_522;
import net.minecraft.class_523;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ResourcePackSettingsScreen extends Screen {
	private final Screen parent;
	private class_522 field_3157;
	private class_523 field_3154;
	private boolean field_3155;

	public ResourcePackSettingsScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new OptionButtonWidget(2, this.width / 2 - 154, this.height - 48, I18n.translate("resourcePack.openFolder")) {
			@Override
			public void onPressed(double d, double e) {
				SystemUtil.getOperatingSystem().open(ResourcePackSettingsScreen.this.client.getResourcePackDir());
			}
		});
		this.addButton(new OptionButtonWidget(1, this.width / 2 + 4, this.height - 48, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				if (ResourcePackSettingsScreen.this.field_3155) {
					List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList();

					for (class_520 lv : ResourcePackSettingsScreen.this.field_3154.getEntries()) {
						list.add(lv.method_2681());
					}

					Collections.reverse(list);
					ResourcePackSettingsScreen.this.client.method_1520().setEnabled(list);
					ResourcePackSettingsScreen.this.client.options.resourcePacks.clear();
					ResourcePackSettingsScreen.this.client.options.incompatibleResourcePacks.clear();

					for (ClientResourcePackContainer clientResourcePackContainer : list) {
						if (!clientResourcePackContainer.sortsTillEnd()) {
							ResourcePackSettingsScreen.this.client.options.resourcePacks.add(clientResourcePackContainer.getName());
							if (!clientResourcePackContainer.getCompatibility().isCompatible()) {
								ResourcePackSettingsScreen.this.client.options.incompatibleResourcePacks.add(clientResourcePackContainer.getName());
							}
						}
					}

					ResourcePackSettingsScreen.this.client.options.write();
					ResourcePackSettingsScreen.this.client.reloadResources();
				}

				ResourcePackSettingsScreen.this.client.openScreen(ResourcePackSettingsScreen.this.parent);
			}
		});
		class_522 lv = this.field_3157;
		class_523 lv2 = this.field_3154;
		this.field_3157 = new class_522(this.client, 200, this.height);
		this.field_3157.setX(this.width / 2 - 4 - 200);
		if (lv != null) {
			this.field_3157.getEntries().addAll(lv.getEntries());
		}

		this.listeners.add(this.field_3157);
		this.field_3154 = new class_523(this.client, 200, this.height);
		this.field_3154.setX(this.width / 2 + 4);
		if (lv2 != null) {
			this.field_3154.getEntries().addAll(lv2.getEntries());
		}

		this.listeners.add(this.field_3154);
		if (!this.field_3155) {
			this.field_3157.getEntries().clear();
			this.field_3154.getEntries().clear();
			ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager = this.client.method_1520();
			resourcePackContainerManager.callCreators();
			List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList(resourcePackContainerManager.getAlphabeticallyOrderedContainers());
			list.removeAll(resourcePackContainerManager.getEnabledContainers());

			for (ClientResourcePackContainer clientResourcePackContainer : list) {
				this.field_3157.method_2690(new class_520(this, clientResourcePackContainer));
			}

			for (ClientResourcePackContainer clientResourcePackContainer : Lists.reverse(Lists.newArrayList(resourcePackContainerManager.getEnabledContainers()))) {
				this.field_3154.method_2690(new class_520(this, clientResourcePackContainer));
			}
		}
	}

	@Override
	public void mouseMoved(double d, double e) {
		if (this.field_3157.isSelected(d, e)) {
			this.setFocused(this.field_3157);
		} else if (this.field_3154.isSelected(d, e)) {
			this.setFocused(this.field_3154);
		}
	}

	public void method_2674(class_520 arg) {
		this.field_3157.getEntries().remove(arg);
		arg.method_2686(this.field_3154);
		this.method_2660();
	}

	public void method_2663(class_520 arg) {
		this.field_3154.getEntries().remove(arg);
		this.field_3157.method_2690(arg);
		this.method_2660();
	}

	public boolean method_2669(class_520 arg) {
		return this.field_3154.getEntries().contains(arg);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.field_3157.draw(i, j, f);
		this.field_3154.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, I18n.translate("resourcePack.title"), this.width / 2, 16, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.draw(i, j, f);
	}

	public void method_2660() {
		this.field_3155 = true;
	}
}
