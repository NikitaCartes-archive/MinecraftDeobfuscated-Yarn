package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ThreePartsLayoutWidget implements LayoutWidget {
	public static final int DEFAULT_HEADER_FOOTER_HEIGHT = 33;
	private static final int FOOTER_MARGIN_TOP = 30;
	private final SimplePositioningWidget header = new SimplePositioningWidget();
	private final SimplePositioningWidget footer = new SimplePositioningWidget();
	private final SimplePositioningWidget body = new SimplePositioningWidget();
	private final Screen screen;
	private int headerHeight;
	private int footerHeight;

	public ThreePartsLayoutWidget(Screen screen) {
		this(screen, 33);
	}

	public ThreePartsLayoutWidget(Screen screen, int headerFooterHeight) {
		this(screen, headerFooterHeight, headerFooterHeight);
	}

	public ThreePartsLayoutWidget(Screen screen, int headerHeight, int footerHeight) {
		this.screen = screen;
		this.headerHeight = headerHeight;
		this.footerHeight = footerHeight;
		this.header.getMainPositioner().relative(0.5F, 0.5F);
		this.footer.getMainPositioner().relative(0.5F, 0.5F);
	}

	@Override
	public void setX(int x) {
	}

	@Override
	public void setY(int y) {
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return this.screen.width;
	}

	@Override
	public int getHeight() {
		return this.screen.height;
	}

	public int getFooterHeight() {
		return this.footerHeight;
	}

	public void setFooterHeight(int footerHeight) {
		this.footerHeight = footerHeight;
	}

	public void setHeaderHeight(int headerHeight) {
		this.headerHeight = headerHeight;
	}

	public int getHeaderHeight() {
		return this.headerHeight;
	}

	public int getContentHeight() {
		return this.screen.height - this.getHeaderHeight() - this.getFooterHeight();
	}

	@Override
	public void forEachElement(Consumer<Widget> consumer) {
		this.header.forEachElement(consumer);
		this.body.forEachElement(consumer);
		this.footer.forEachElement(consumer);
	}

	@Override
	public void refreshPositions() {
		int i = this.getHeaderHeight();
		int j = this.getFooterHeight();
		this.header.setMinWidth(this.screen.width);
		this.header.setMinHeight(i);
		this.header.setPosition(0, 0);
		this.header.refreshPositions();
		this.footer.setMinWidth(this.screen.width);
		this.footer.setMinHeight(j);
		this.footer.refreshPositions();
		this.footer.setY(this.screen.height - j);
		this.body.setMinWidth(this.screen.width);
		this.body.refreshPositions();
		int k = i + 30;
		int l = this.screen.height - j - this.body.getHeight();
		this.body.setPosition(0, Math.min(k, l));
	}

	public <T extends Widget> T addHeader(T widget) {
		return this.header.add(widget);
	}

	public <T extends Widget> T addHeader(T widget, Consumer<Positioner> callback) {
		return this.header.add(widget, callback);
	}

	public void addHeader(Text text, TextRenderer textRenderer) {
		this.header.add(new TextWidget(text, textRenderer));
	}

	public <T extends Widget> T addFooter(T widget) {
		return this.footer.add(widget);
	}

	public <T extends Widget> T addFooter(T widget, Consumer<Positioner> callback) {
		return this.footer.add(widget, callback);
	}

	public <T extends Widget> T addBody(T widget) {
		return this.body.add(widget);
	}

	public <T extends Widget> T addBody(T widget, Consumer<Positioner> callback) {
		return this.body.add(widget, callback);
	}
}
