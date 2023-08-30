package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.time.Duration;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.realms.RepeatedNarrator;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.RealmsLoadingWidget;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final RepeatedNarrator NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
	private LongRunningTask task;
	private final Screen parent;
	private volatile Text title = ScreenTexts.EMPTY;
	private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();
	@Nullable
	private RealmsLoadingWidget loading;

	public RealmsLongRunningMcoTaskScreen(Screen parent, LongRunningTask task) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
		this.task = task;
		this.setTitle(task.getTitle());
		Thread thread = new Thread(task, "Realms-long-running-task");
		thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
		thread.start();
	}

	@Override
	public void tick() {
		super.tick();
		NARRATOR.narrate(this.client.getNarratorManager(), this.loading.getMessage());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.onCancel();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void init() {
		this.layout.getMainPositioner().alignHorizontalCenter();
		this.loading = new RealmsLoadingWidget(this.textRenderer, this.title);
		this.layout.add(this.loading, positioner -> positioner.marginBottom(30));
		this.layout.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCancel()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
	}

	protected void onCancel() {
		this.task.abortTask();
		this.client.setScreen(this.parent);
	}

	public void setTitle(Text title) {
		if (this.loading != null) {
			this.loading.setMessage(title);
		}

		this.title = title;
	}
}
