package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

@Environment(EnvType.CLIENT)
public class EditGameRulesScreen extends Screen {
	private static final Text TITLE = Text.translatable("editGamerule.title");
	private static final int field_49559 = 8;
	final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final Consumer<Optional<GameRules>> ruleSaver;
	private final Set<EditGameRulesScreen.AbstractRuleWidget> invalidRuleWidgets = Sets.<EditGameRulesScreen.AbstractRuleWidget>newHashSet();
	@Nullable
	private ButtonWidget doneButton;
	private final GameRules gameRules;

	public EditGameRulesScreen(GameRules gameRules, Consumer<Optional<GameRules>> ruleSaveConsumer) {
		super(TITLE);
		this.gameRules = gameRules;
		this.ruleSaver = ruleSaveConsumer;
	}

	@Override
	protected void init() {
		this.layout.addHeader(TITLE, this.textRenderer);
		this.layout.addBody(new EditGameRulesScreen.RuleListWidget(this.gameRules));
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		this.doneButton = directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.ruleSaver.accept(Optional.of(this.gameRules))).build());
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, buttonWidget -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	@Override
	public void close() {
		this.ruleSaver.accept(Optional.empty());
	}

	private void updateDoneButton() {
		if (this.doneButton != null) {
			this.doneButton.active = this.invalidRuleWidgets.isEmpty();
		}
	}

	void markInvalid(EditGameRulesScreen.AbstractRuleWidget ruleWidget) {
		this.invalidRuleWidgets.add(ruleWidget);
		this.updateDoneButton();
	}

	void markValid(EditGameRulesScreen.AbstractRuleWidget ruleWidget) {
		this.invalidRuleWidgets.remove(ruleWidget);
		this.updateDoneButton();
	}

	@Environment(EnvType.CLIENT)
	public abstract static class AbstractRuleWidget extends ElementListWidget.Entry<EditGameRulesScreen.AbstractRuleWidget> {
		@Nullable
		final List<OrderedText> description;

		public AbstractRuleWidget(@Nullable List<OrderedText> description) {
			this.description = description;
		}
	}

	@Environment(EnvType.CLIENT)
	public class BooleanRuleWidget extends EditGameRulesScreen.NamedRuleWidget {
		private final CyclingButtonWidget<Boolean> toggleButton;

		public BooleanRuleWidget(Text name, List<OrderedText> description, String ruleName, GameRules.BooleanRule rule) {
			super(description, name);
			this.toggleButton = CyclingButtonWidget.onOffBuilder(rule.get())
				.omitKeyText()
				.narration(button -> button.getGenericNarrationMessage().append("\n").append(ruleName))
				.build(10, 5, 44, 20, name, (button, value) -> rule.set(value, null));
			this.children.add(this.toggleButton);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.drawName(context, y, x);
			this.toggleButton.setX(x + entryWidth - 45);
			this.toggleButton.setY(y);
			this.toggleButton.render(context, mouseX, mouseY, tickDelta);
		}
	}

	@Environment(EnvType.CLIENT)
	public class IntRuleWidget extends EditGameRulesScreen.NamedRuleWidget {
		private final TextFieldWidget valueWidget;

		public IntRuleWidget(Text name, List<OrderedText> description, String ruleName, GameRules.IntRule rule) {
			super(description, name);
			this.valueWidget = new TextFieldWidget(EditGameRulesScreen.this.client.textRenderer, 10, 5, 44, 20, name.copy().append("\n").append(ruleName).append("\n"));
			this.valueWidget.setText(Integer.toString(rule.get()));
			this.valueWidget.setChangedListener(value -> {
				if (rule.validateAndSet(value)) {
					this.valueWidget.setEditableColor(14737632);
					EditGameRulesScreen.this.markValid(this);
				} else {
					this.valueWidget.setEditableColor(16711680);
					EditGameRulesScreen.this.markInvalid(this);
				}
			});
			this.children.add(this.valueWidget);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.drawName(context, y, x);
			this.valueWidget.setX(x + entryWidth - 45);
			this.valueWidget.setY(y);
			this.valueWidget.render(context, mouseX, mouseY, tickDelta);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract class NamedRuleWidget extends EditGameRulesScreen.AbstractRuleWidget {
		private final List<OrderedText> name;
		protected final List<ClickableWidget> children = Lists.<ClickableWidget>newArrayList();

		public NamedRuleWidget(@Nullable List<OrderedText> description, Text name) {
			super(description);
			this.name = EditGameRulesScreen.this.client.textRenderer.wrapLines(name, 175);
		}

		@Override
		public List<? extends Element> children() {
			return this.children;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.children;
		}

		protected void drawName(DrawContext context, int x, int y) {
			if (this.name.size() == 1) {
				context.drawText(EditGameRulesScreen.this.client.textRenderer, (OrderedText)this.name.get(0), y, x + 5, 16777215, false);
			} else if (this.name.size() >= 2) {
				context.drawText(EditGameRulesScreen.this.client.textRenderer, (OrderedText)this.name.get(0), y, x, 16777215, false);
				context.drawText(EditGameRulesScreen.this.client.textRenderer, (OrderedText)this.name.get(1), y, x + 10, 16777215, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public class RuleCategoryWidget extends EditGameRulesScreen.AbstractRuleWidget {
		final Text name;

		public RuleCategoryWidget(Text text) {
			super(null);
			this.name = text;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			context.drawCenteredTextWithShadow(EditGameRulesScreen.this.client.textRenderer, this.name, x + entryWidth / 2, y + 5, 16777215);
		}

		@Override
		public List<? extends Element> children() {
			return ImmutableList.of();
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(new Selectable() {
				@Override
				public Selectable.SelectionType getType() {
					return Selectable.SelectionType.HOVERED;
				}

				@Override
				public void appendNarrations(NarrationMessageBuilder builder) {
					builder.put(NarrationPart.TITLE, RuleCategoryWidget.this.name);
				}
			});
		}
	}

	@Environment(EnvType.CLIENT)
	public class RuleListWidget extends ElementListWidget<EditGameRulesScreen.AbstractRuleWidget> {
		private static final int field_49561 = 24;

		public RuleListWidget(GameRules gameRules) {
			super(
				MinecraftClient.getInstance(),
				EditGameRulesScreen.this.width,
				EditGameRulesScreen.this.layout.getContentHeight(),
				EditGameRulesScreen.this.layout.getHeaderHeight(),
				24
			);
			final Map<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> map = Maps.<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>>newHashMap();
			GameRules.accept(new GameRules.Visitor() {
				@Override
				public void visitBoolean(GameRules.Key<GameRules.BooleanRule> key, GameRules.Type<GameRules.BooleanRule> type) {
					this.createRuleWidget(key, (name, description, ruleName, rule) -> EditGameRulesScreen.this.new BooleanRuleWidget(name, description, ruleName, rule));
				}

				@Override
				public void visitInt(GameRules.Key<GameRules.IntRule> key, GameRules.Type<GameRules.IntRule> type) {
					this.createRuleWidget(key, (name, description, ruleName, rule) -> EditGameRulesScreen.this.new IntRuleWidget(name, description, ruleName, rule));
				}

				private <T extends GameRules.Rule<T>> void createRuleWidget(GameRules.Key<T> key, EditGameRulesScreen.RuleWidgetFactory<T> widgetFactory) {
					Text text = Text.translatable(key.getTranslationKey());
					Text text2 = Text.literal(key.getName()).formatted(Formatting.YELLOW);
					T rule = gameRules.get(key);
					String string = rule.serialize();
					Text text3 = Text.translatable("editGamerule.default", Text.literal(string)).formatted(Formatting.GRAY);
					String string2 = key.getTranslationKey() + ".description";
					List<OrderedText> list;
					String string3;
					if (I18n.hasTranslation(string2)) {
						Builder<OrderedText> builder = ImmutableList.<OrderedText>builder().add(text2.asOrderedText());
						Text text4 = Text.translatable(string2);
						EditGameRulesScreen.this.textRenderer.wrapLines(text4, 150).forEach(builder::add);
						list = builder.add(text3.asOrderedText()).build();
						string3 = text4.getString() + "\n" + text3.getString();
					} else {
						list = ImmutableList.of(text2.asOrderedText(), text3.asOrderedText());
						string3 = text3.getString();
					}

					((Map)map.computeIfAbsent(key.getCategory(), category -> Maps.newHashMap())).put(key, widgetFactory.create(text, list, string3, rule));
				}
			});
			map.entrySet()
				.stream()
				.sorted(java.util.Map.Entry.comparingByKey())
				.forEach(
					entry -> {
						this.addEntry(
							EditGameRulesScreen.this.new RuleCategoryWidget(
								Text.translatable(((GameRules.Category)entry.getKey()).getCategory()).formatted(Formatting.BOLD, Formatting.YELLOW)
							)
						);
						((Map)entry.getValue())
							.entrySet()
							.stream()
							.sorted(java.util.Map.Entry.comparingByKey(Comparator.comparing(GameRules.Key::getName)))
							.forEach(e -> this.addEntry((EditGameRulesScreen.AbstractRuleWidget)e.getValue()));
					}
				);
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			super.renderWidget(context, mouseX, mouseY, delta);
			EditGameRulesScreen.AbstractRuleWidget abstractRuleWidget = this.getHoveredEntry();
			if (abstractRuleWidget != null && abstractRuleWidget.description != null) {
				EditGameRulesScreen.this.setTooltip(abstractRuleWidget.description);
			}
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface RuleWidgetFactory<T extends GameRules.Rule<T>> {
		EditGameRulesScreen.AbstractRuleWidget create(Text name, List<OrderedText> description, String ruleName, T rule);
	}
}
