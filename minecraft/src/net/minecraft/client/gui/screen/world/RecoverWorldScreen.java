package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Urls;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RecoverWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_46863 = 25;
	private static final Text TITLE_TEXT = Text.translatable("recover_world.title").formatted(Formatting.BOLD);
	private static final Text BUG_TRACKER_TEXT = Text.translatable("recover_world.bug_tracker");
	private static final Text RESTORE_TEXT = Text.translatable("recover_world.restore");
	private static final Text NO_FALLBACK_TEXT = Text.translatable("recover_world.no_fallback");
	private static final Text DONE_TITLE_TEXT = Text.translatable("recover_world.done.title");
	private static final Text DONE_SUCCESS_TEXT = Text.translatable("recover_world.done.success");
	private static final Text DONE_FAILED_TEXT = Text.translatable("recover_world.done.failed");
	private static final Text ISSUE_NONE_TEXT = Text.translatable("recover_world.issue.none").formatted(Formatting.GREEN);
	private static final Text MISSING_FILE_TEXT = Text.translatable("recover_world.issue.missing_file").formatted(Formatting.RED);
	private final BooleanConsumer callback;
	private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(8);
	private final Text message;
	private final MultilineTextWidget messageWidget;
	private final MultilineTextWidget exceptionWidget;
	private final LevelStorage.Session session;

	public RecoverWorldScreen(MinecraftClient client, BooleanConsumer callback, LevelStorage.Session session) {
		super(TITLE_TEXT);
		this.callback = callback;
		this.message = Text.translatable("recover_world.message", Text.literal(session.getDirectoryName()).formatted(Formatting.GRAY));
		this.messageWidget = new MultilineTextWidget(this.message, client.textRenderer);
		this.session = session;
		Exception exception = this.getLoadingException(session, false);
		Exception exception2 = this.getLoadingException(session, true);
		Text text = Text.empty().append(this.toText(session, false, exception)).append("\n").append(this.toText(session, true, exception2));
		this.exceptionWidget = new MultilineTextWidget(text, client.textRenderer);
		boolean bl = exception != null && exception2 == null;
		this.layout.getMainPositioner().alignHorizontalCenter();
		this.layout.add(new TextWidget(this.title, client.textRenderer));
		this.layout.add(this.messageWidget.setCentered(true));
		this.layout.add(this.exceptionWidget);
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(5);
		directionalLayoutWidget.add(ButtonWidget.builder(BUG_TRACKER_TEXT, ConfirmLinkScreen.opening(this, Urls.SNAPSHOT_BUGS)).size(120, 20).build());
		directionalLayoutWidget.add(
				ButtonWidget.builder(RESTORE_TEXT, buttonWidget -> this.tryRestore(client)).size(120, 20).tooltip(bl ? null : Tooltip.of(NO_FALLBACK_TEXT)).build()
			)
			.active = bl;
		this.layout.add(directionalLayoutWidget);
		this.layout.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).size(120, 20).build());
		this.layout.forEachChild(this::addDrawableChild);
	}

	private void tryRestore(MinecraftClient client) {
		Exception exception = this.getLoadingException(this.session, false);
		Exception exception2 = this.getLoadingException(this.session, true);
		if (exception != null && exception2 == null) {
			client.setScreenAndRender(new MessageScreen(Text.translatable("recover_world.restoring")));
			EditWorldScreen.backupLevel(this.session);
			if (this.session.tryRestoreBackup()) {
				client.setScreen(new ConfirmScreen(this.callback, DONE_TITLE_TEXT, DONE_SUCCESS_TEXT, ScreenTexts.CONTINUE, ScreenTexts.BACK));
			} else {
				client.setScreen(new NoticeScreen(() -> this.callback.accept(false), DONE_TITLE_TEXT, DONE_FAILED_TEXT));
			}
		} else {
			LOGGER.error(
				"Failed to recover world, files not as expected. level.dat: {}, level.dat_old: {}",
				exception != null ? exception.getMessage() : "no issues",
				exception2 != null ? exception2.getMessage() : "no issues"
			);
			client.setScreen(new NoticeScreen(() -> this.callback.accept(false), DONE_TITLE_TEXT, DONE_FAILED_TEXT));
		}
	}

	private Text toText(LevelStorage.Session session, boolean old, @Nullable Exception exception) {
		if (old && exception instanceof FileNotFoundException) {
			return Text.empty();
		} else {
			MutableText mutableText = Text.empty();
			Instant instant = session.getLastModifiedTime(old);
			MutableText mutableText2 = instant != null
				? Text.literal(WorldListWidget.DATE_FORMAT.format(instant))
				: Text.translatable("recover_world.state_entry.unknown");
			mutableText.append(Text.translatable("recover_world.state_entry", mutableText2.formatted(Formatting.GRAY)));
			if (exception == null) {
				mutableText.append(ISSUE_NONE_TEXT);
			} else if (exception instanceof FileNotFoundException) {
				mutableText.append(MISSING_FILE_TEXT);
			} else if (exception instanceof NbtCrashException) {
				mutableText.append(Text.literal(exception.getCause().toString()).formatted(Formatting.RED));
			} else {
				mutableText.append(Text.literal(exception.toString()).formatted(Formatting.RED));
			}

			return mutableText;
		}
	}

	@Nullable
	private Exception getLoadingException(LevelStorage.Session session, boolean old) {
		try {
			if (!old) {
				session.getLevelSummary(session.readLevelProperties());
			} else {
				session.getLevelSummary(session.readOldLevelProperties());
			}

			return null;
		} catch (NbtException | NbtCrashException | IOException var4) {
			return var4;
		}
	}

	@Override
	protected void init() {
		super.init();
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.exceptionWidget.setMaxWidth(this.width - 50);
		this.messageWidget.setMaxWidth(this.width - 50);
		this.layout.refreshPositions();
		SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), this.message);
	}

	@Override
	public void close() {
		this.callback.accept(false);
	}
}
