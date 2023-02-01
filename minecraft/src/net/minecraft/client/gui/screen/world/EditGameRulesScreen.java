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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

@Environment(EnvType.CLIENT)
public class EditGameRulesScreen extends Screen {
	private final Consumer<Optional<GameRules>> ruleSaver;
	private EditGameRulesScreen.RuleListWidget ruleListWidget;
	private final Set<EditGameRulesScreen.AbstractRuleWidget> invalidRuleWidgets = Sets.<EditGameRulesScreen.AbstractRuleWidget>newHashSet();
	private ButtonWidget doneButton;
	@Nullable
	private List<OrderedText> field_24297;
	private final GameRules gameRules;

	public EditGameRulesScreen(GameRules gameRules, Consumer<Optional<GameRules>> ruleSaveConsumer) {
		super(Text.translatable("editGamerule.title"));
		this.gameRules = gameRules;
		this.ruleSaver = ruleSaveConsumer;
	}

	@Override
	protected void init() {
		this.ruleListWidget = new EditGameRulesScreen.RuleListWidget(this.gameRules);
		this.addSelectableChild(this.ruleListWidget);
		GridWidget.Adder adder = new GridWidget().setColumnSpacing(10).createAdder(2);
		this.doneButton = adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.ruleSaver.accept(Optional.of(this.gameRules))).build());
		adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.ruleSaver.accept(Optional.empty())).build());
		adder.getGridWidget().forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		adder.getGridWidget().setPosition(this.width / 2 - 155, this.height - 28);
		adder.getGridWidget().refreshPositions();
	}

	@Override
	public void close() {
		this.ruleSaver.accept(Optional.empty());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.field_24297 = null;
		this.ruleListWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private void updateDoneButton() {
		this.doneButton.active = this.invalidRuleWidgets.isEmpty();
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.drawName(matrices, y, x);
			this.toggleButton.setX(x + entryWidth - 45);
			this.toggleButton.setY(y);
			this.toggleButton.render(matrices, mouseX, mouseY, tickDelta);
		}
	}

	@Environment(EnvType.CLIENT)
	public class IntRuleWidget extends EditGameRulesScreen.NamedRuleWidget {
		private final TextFieldWidget valueWidget;

		public IntRuleWidget(Text name, List<OrderedText> description, String ruleName, GameRules.IntRule rule) {
			super(description, name);
			this.valueWidget = new TextFieldWidget(EditGameRulesScreen.this.client.textRenderer, 10, 5, 42, 20, name.copy().append("\n").append(ruleName).append("\n"));
			this.valueWidget.setText(Integer.toString(rule.get()));
			this.valueWidget.setChangedListener(value -> {
				if (rule.validate(value)) {
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.drawName(matrices, y, x);
			this.valueWidget.setX(x + entryWidth - 44);
			this.valueWidget.setY(y);
			this.valueWidget.render(matrices, mouseX, mouseY, tickDelta);
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

		protected void drawName(MatrixStack matrices, int x, int y) {
			if (this.name.size() == 1) {
				EditGameRulesScreen.this.client.textRenderer.draw(matrices, (OrderedText)this.name.get(0), (float)y, (float)(x + 5), 16777215);
			} else if (this.name.size() >= 2) {
				EditGameRulesScreen.this.client.textRenderer.draw(matrices, (OrderedText)this.name.get(0), (float)y, (float)x, 16777215);
				EditGameRulesScreen.this.client.textRenderer.draw(matrices, (OrderedText)this.name.get(1), (float)y, (float)(x + 10), 16777215);
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			DrawableHelper.drawCenteredText(matrices, EditGameRulesScreen.this.client.textRenderer, this.name, x + entryWidth / 2, y + 5, 16777215);
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
		public RuleListWidget(GameRules gameRules) {
			super(EditGameRulesScreen.this.client, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
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
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.render(matrices, mouseX, mouseY, delta);
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
