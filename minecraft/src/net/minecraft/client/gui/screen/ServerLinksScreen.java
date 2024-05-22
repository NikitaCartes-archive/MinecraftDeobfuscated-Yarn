package net.minecraft.client.gui.screen;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ServerLinksScreen extends Screen {
	private static final int LIST_WIDTH = 310;
	private static final int ENTRY_HEIGHT = 25;
	private static final Text TITLE = Text.translatable("menu.server_links.title");
	private final Screen parent;
	@Nullable
	private ServerLinksScreen.LinksListWidget list;
	final ThreePartsLayoutWidget layoutWidget = new ThreePartsLayoutWidget(this);
	final ServerLinks serverLinks;

	public ServerLinksScreen(Screen parent, ServerLinks serverLinks) {
		super(TITLE);
		this.parent = parent;
		this.serverLinks = serverLinks;
	}

	@Override
	protected void init() {
		this.layoutWidget.addHeader(this.title, this.textRenderer);
		this.list = this.layoutWidget.addBody(new ServerLinksScreen.LinksListWidget(this.client, this.width, this));
		this.layoutWidget.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build());
		this.layoutWidget.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layoutWidget.refreshPositions();
		if (this.list != null) {
			this.list.position(this.width, this.layoutWidget);
		}
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Environment(EnvType.CLIENT)
	static class LinksListEntry extends ElementListWidget.Entry<ServerLinksScreen.LinksListEntry> {
		final ClickableWidget button;

		LinksListEntry(Screen screen, ServerLinks.Entry link) {
			this.button = ButtonWidget.builder(link.getText(), ConfirmLinkScreen.createOpenPressAction(screen, link.url(), false)).width(310).build();
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.button.setY(y);
			this.button.render(context, mouseX, mouseY, tickDelta);
		}

		@Override
		public List<? extends Element> children() {
			return List.of(this.button);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(this.button);
		}
	}

	@Environment(EnvType.CLIENT)
	static class LinksListWidget extends ElementListWidget<ServerLinksScreen.LinksListEntry> {
		public LinksListWidget(MinecraftClient client, int width, ServerLinksScreen screen) {
			super(client, width, screen.layoutWidget.getContentHeight(), screen.layoutWidget.getHeaderHeight(), 25);
			screen.serverLinks.entries().forEach(entry -> this.addEntry(new ServerLinksScreen.LinksListEntry(screen, entry)));
		}

		@Override
		public int getRowWidth() {
			return 310;
		}

		@Override
		public void position(int width, ThreePartsLayoutWidget layout) {
			super.position(width, layout);
			int i = width / 2 - 155;
			this.children().forEach(child -> child.button.setX(i));
		}
	}
}
