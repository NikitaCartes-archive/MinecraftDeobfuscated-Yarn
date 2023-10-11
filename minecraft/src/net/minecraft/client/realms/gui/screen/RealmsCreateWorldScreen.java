package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.ResettingNormalWorldTask;
import net.minecraft.client.realms.task.ResettingWorldTemplateTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsCreateWorldScreen extends RealmsScreen {
	static final Logger LOGGER = LogUtils.getLogger();
	static final Identifier SLOT_FRAME_TEXTURE = new Identifier("widget/slot_frame");
	private static final Text CREATE_REALM_TITLE = Text.translatable("mco.selectServer.create");
	private static final Text CREATE_REALM_SUBTITLE = Text.translatable("mco.selectServer.create.subtitle");
	private static final Text CREATE_WORLD_TITLE = Text.translatable("mco.configure.world.switch.slot");
	private static final Text CREATE_WORLD_SUBTITLE = Text.translatable("mco.configure.world.switch.slot.subtitle");
	private static final Text RESET_WORLD_TITLE = Text.translatable("mco.reset.world.title");
	private static final Text RESET_WORLD_SUBTITLE = Text.translatable("mco.reset.world.warning");
	public static final Text CREATING_TEXT = Text.translatable("mco.create.world.reset.title");
	private static final Text RESETTING_TEXT = Text.translatable("mco.reset.world.resetting.screen.title");
	private static final Text TEMPLATE_TEXT = Text.translatable("mco.reset.world.template");
	private static final Text ADVENTURE_TEXT = Text.translatable("mco.reset.world.adventure");
	private static final Text EXPERIENCE_TEXT = Text.translatable("mco.reset.world.experience");
	private static final Text INSPIRATION_TEXT = Text.translatable("mco.reset.world.inspiration");
	private final Screen parent;
	private final RealmsServer serverData;
	private final Text subtitle;
	private final int subtitleColor;
	private final Text taskTitle;
	private static final Identifier UPLOAD_TEXTURE = new Identifier("textures/gui/realms/upload.png");
	private static final Identifier ADVENTURE_TEXTURE = new Identifier("textures/gui/realms/adventure.png");
	private static final Identifier SURVIVAL_SPAWN_TEXTURE = new Identifier("textures/gui/realms/survival_spawn.png");
	private static final Identifier NEW_WORLD_TEXTURE = new Identifier("textures/gui/realms/new_world.png");
	private static final Identifier EXPERIENCE_TEXTURE = new Identifier("textures/gui/realms/experience.png");
	private static final Identifier INSPIRATION_TEXTURE = new Identifier("textures/gui/realms/inspiration.png");
	WorldTemplatePaginatedList normalWorldTemplates;
	WorldTemplatePaginatedList adventureWorldTemplates;
	WorldTemplatePaginatedList experienceWorldTemplates;
	WorldTemplatePaginatedList inspirationWorldTemplates;
	public final int slot;
	@Nullable
	private final WorldCreationTask creationTask;
	private final Runnable callback;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	private RealmsCreateWorldScreen(
		Screen parent, RealmsServer serverData, int slot, Text title, Text subtitle, int subtitleColor, Text taskTitle, Runnable callback
	) {
		this(parent, serverData, slot, title, subtitle, subtitleColor, taskTitle, null, callback);
	}

	public RealmsCreateWorldScreen(
		Screen parent,
		RealmsServer serverData,
		int slot,
		Text title,
		Text subtitle,
		int subtitleColor,
		Text taskTitle,
		@Nullable WorldCreationTask creationTask,
		Runnable callback
	) {
		super(title);
		this.parent = parent;
		this.serverData = serverData;
		this.slot = slot;
		this.subtitle = subtitle;
		this.subtitleColor = subtitleColor;
		this.taskTitle = taskTitle;
		this.creationTask = creationTask;
		this.callback = callback;
	}

	public static RealmsCreateWorldScreen newRealm(Screen parent, RealmsServer serverData, WorldCreationTask creationTask, Runnable callback) {
		return new RealmsCreateWorldScreen(
			parent, serverData, serverData.activeSlot, CREATE_REALM_TITLE, CREATE_REALM_SUBTITLE, -6250336, CREATING_TEXT, creationTask, callback
		);
	}

	public static RealmsCreateWorldScreen newWorld(Screen parent, int slot, RealmsServer serverData, Runnable callback) {
		return new RealmsCreateWorldScreen(parent, serverData, slot, CREATE_WORLD_TITLE, CREATE_WORLD_SUBTITLE, -6250336, CREATING_TEXT, callback);
	}

	public static RealmsCreateWorldScreen resetWorld(Screen parent, RealmsServer serverData, Runnable callback) {
		return new RealmsCreateWorldScreen(parent, serverData, serverData.activeSlot, RESET_WORLD_TITLE, RESET_WORLD_SUBTITLE, -65536, RESETTING_TEXT, callback);
	}

	@Override
	public void init() {
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical();
		directionalLayoutWidget.add(new TextWidget(this.title, this.textRenderer), Positioner::alignHorizontalCenter);
		directionalLayoutWidget.add(EmptyWidget.ofHeight(3));
		directionalLayoutWidget.add(new TextWidget(this.subtitle, this.textRenderer).setTextColor(this.subtitleColor), Positioner::alignHorizontalCenter);
		this.layout.addHeader(directionalLayoutWidget);
		(new Thread("Realms-reset-world-fetcher") {
			public void run() {
				RealmsClient realmsClient = RealmsClient.create();

				try {
					WorldTemplatePaginatedList worldTemplatePaginatedList = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
					WorldTemplatePaginatedList worldTemplatePaginatedList2 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
					WorldTemplatePaginatedList worldTemplatePaginatedList3 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
					WorldTemplatePaginatedList worldTemplatePaginatedList4 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
					RealmsCreateWorldScreen.this.client.execute(() -> {
						RealmsCreateWorldScreen.this.normalWorldTemplates = worldTemplatePaginatedList;
						RealmsCreateWorldScreen.this.adventureWorldTemplates = worldTemplatePaginatedList2;
						RealmsCreateWorldScreen.this.experienceWorldTemplates = worldTemplatePaginatedList3;
						RealmsCreateWorldScreen.this.inspirationWorldTemplates = worldTemplatePaginatedList4;
					});
				} catch (RealmsServiceException var6) {
					RealmsCreateWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)var6);
				}
			}
		}).start();
		this.addDrawableChild(
			new RealmsCreateWorldScreen.FrameButton(
				this.frame(1),
				row(0) + 10,
				RealmsResetNormalWorldScreen.TITLE,
				NEW_WORLD_TEXTURE,
				button -> this.client.setScreen(new RealmsResetNormalWorldScreen(this::onResetNormalWorld, this.title))
			)
		);
		this.addDrawableChild(
			new RealmsCreateWorldScreen.FrameButton(
				this.frame(2),
				row(0) + 10,
				RealmsSelectFileToUploadScreen.TITLE,
				UPLOAD_TEXTURE,
				button -> this.client.setScreen(new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot, this))
			)
		);
		this.addDrawableChild(
			new RealmsCreateWorldScreen.FrameButton(
				this.frame(3),
				row(0) + 10,
				TEMPLATE_TEXT,
				SURVIVAL_SPAWN_TEXTURE,
				button -> this.client
						.setScreen(new RealmsSelectWorldTemplateScreen(TEMPLATE_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.NORMAL, this.normalWorldTemplates))
			)
		);
		this.addDrawableChild(
			new RealmsCreateWorldScreen.FrameButton(
				this.frame(1),
				row(6) + 20,
				ADVENTURE_TEXT,
				ADVENTURE_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectWorldTemplateScreen(ADVENTURE_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.ADVENTUREMAP, this.adventureWorldTemplates)
						)
			)
		);
		this.addDrawableChild(
			new RealmsCreateWorldScreen.FrameButton(
				this.frame(2),
				row(6) + 20,
				EXPERIENCE_TEXT,
				EXPERIENCE_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectWorldTemplateScreen(EXPERIENCE_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.EXPERIENCE, this.experienceWorldTemplates)
						)
			)
		);
		this.addDrawableChild(
			new RealmsCreateWorldScreen.FrameButton(
				this.frame(3),
				row(6) + 20,
				INSPIRATION_TEXT,
				INSPIRATION_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectWorldTemplateScreen(INSPIRATION_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.INSPIRATION, this.inspirationWorldTemplates)
						)
			)
		);
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.layout.refreshPositions();
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(this.getTitle(), this.subtitle);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	private int frame(int i) {
		return this.width / 2 - 130 + (i - 1) * 100;
	}

	private void onSelectWorldTemplate(@Nullable WorldTemplate template) {
		this.client.setScreen(this);
		if (template != null) {
			this.runTasks(new ResettingWorldTemplateTask(template, this.serverData.id, this.taskTitle, this.callback));
		}
	}

	private void onResetNormalWorld(@Nullable ResetWorldInfo info) {
		this.client.setScreen(this);
		if (info != null) {
			this.runTasks(new ResettingNormalWorldTask(info, this.serverData.id, this.taskTitle, this.callback));
		}
	}

	private void runTasks(LongRunningTask task) {
		List<LongRunningTask> list = new ArrayList();
		if (this.creationTask != null) {
			list.add(this.creationTask);
		}

		if (this.slot != this.serverData.activeSlot) {
			list.add(new SwitchSlotTask(this.serverData.id, this.slot, () -> {
			}));
		}

		list.add(task);
		this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, (LongRunningTask[])list.toArray(new LongRunningTask[0])));
	}

	public void switchSlot(Runnable callback) {
		this.client
			.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.serverData.id, this.slot, () -> this.client.execute(callback))));
	}

	@Environment(EnvType.CLIENT)
	class FrameButton extends ButtonWidget {
		private static final int field_46128 = 60;
		private static final int field_46129 = 72;
		private static final int field_46130 = 56;
		private final Identifier image;

		FrameButton(int x, int y, Text message, Identifier image, ButtonWidget.PressAction onPress) {
			super(x, y, 60, 72, message, onPress, DEFAULT_NARRATION_SUPPLIER);
			this.image = image;
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			boolean bl = this.isSelected();
			if (bl) {
				context.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
			}

			int i = this.getX();
			int j = this.getY();
			context.drawTexture(this.image, i + 2, j + 14, 0.0F, 0.0F, 56, 56, 56, 56);
			context.drawGuiTexture(RealmsCreateWorldScreen.SLOT_FRAME_TEXTURE, i, j + 12, 60, 60);
			context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int k = bl ? -6250336 : -1;
			context.drawCenteredTextWithShadow(RealmsCreateWorldScreen.this.textRenderer, this.getMessage(), i + 30, j, k);
		}
	}
}
