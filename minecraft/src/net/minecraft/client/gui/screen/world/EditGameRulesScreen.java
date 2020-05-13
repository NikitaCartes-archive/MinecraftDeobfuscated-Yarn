package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

@Environment(EnvType.CLIENT)
public class EditGameRulesScreen extends Screen {
	private final Consumer<Optional<GameRules>> ruleSaver;
	private EditGameRulesScreen.RuleListWidget ruleListWidget;
	private final Set<EditGameRulesScreen.AbstractRuleWidget> invalidRuleWidgets = Sets.<EditGameRulesScreen.AbstractRuleWidget>newHashSet();
	private ButtonWidget doneButton;
	@Nullable
	private List<Text> tooltip;
	private final GameRules gameRules;

	public EditGameRulesScreen(GameRules gameRules, Consumer<Optional<GameRules>> ruleSaveConsumer) {
		super(new TranslatableText("editGamerule.title"));
		this.gameRules = gameRules;
		this.ruleSaver = ruleSaveConsumer;
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		super.init();
		this.ruleListWidget = new EditGameRulesScreen.RuleListWidget(this.gameRules);
		this.children.add(this.ruleListWidget);
		this.addButton(
			new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.ruleSaver.accept(Optional.empty()))
		);
		this.doneButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, ScreenTexts.DONE, buttonWidget -> this.ruleSaver.accept(Optional.of(this.gameRules)))
		);
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public void onClose() {
		this.ruleSaver.accept(Optional.empty());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltip = null;
		this.ruleListWidget.render(matrices, mouseX, mouseY, delta);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		if (this.tooltip != null) {
			this.renderTooltip(matrices, this.tooltip, mouseX, mouseY);
		}
	}

	private void setTooltipDescription(@Nullable List<Text> description) {
		this.tooltip = description;
	}

	private void updateDoneButton() {
		this.doneButton.active = this.invalidRuleWidgets.isEmpty();
	}

	private void markInvalid(EditGameRulesScreen.AbstractRuleWidget ruleWidget) {
		this.invalidRuleWidgets.add(ruleWidget);
		this.updateDoneButton();
	}

	private void markValid(EditGameRulesScreen.AbstractRuleWidget ruleWidget) {
		this.invalidRuleWidgets.remove(ruleWidget);
		this.updateDoneButton();
	}

	@Environment(EnvType.CLIENT)
	public abstract class AbstractRuleWidget extends ElementListWidget.Entry<EditGameRulesScreen.AbstractRuleWidget> {
		@Nullable
		private final List<Text> description;

		public AbstractRuleWidget(@Nullable List<Text> description) {
			this.description = description;
		}
	}

	@Environment(EnvType.CLIENT)
	public class BooleanRuleWidget extends EditGameRulesScreen.AbstractRuleWidget {
		private final ButtonWidget toggleButton;
		private final List<? extends Element> children;

		public BooleanRuleWidget(Text name, List<Text> description, String ruleName, GameRules.BooleanRule rule) {
			super(description);
			this.toggleButton = new ButtonWidget(10, 5, 220, 20, this.createBooleanRuleText(name, rule.get()), buttonWidget -> {
				boolean bl = !rule.get();
				rule.set(bl, null);
				buttonWidget.setMessage(this.createBooleanRuleText(name, bl));
			}) {
				@Override
				protected MutableText getNarrationMessage() {
					return this.getMessage().shallowCopy().append("\n").append(ruleName);
				}
			};
			this.children = ImmutableList.of(this.toggleButton);
		}

		private Text createBooleanRuleText(Text text, boolean value) {
			return text.shallowCopy().append(": ").append(ScreenTexts.getToggleText(value));
		}

		@Override
		public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
			this.toggleButton.x = width;
			this.toggleButton.y = y;
			this.toggleButton.render(matrices, mouseY, i, tickDelta);
		}

		@Override
		public List<? extends Element> children() {
			return this.children;
		}
	}

	@Environment(EnvType.CLIENT)
	public class IntRuleWidget extends EditGameRulesScreen.AbstractRuleWidget {
		private final Text name;
		private final TextFieldWidget valueWidget;
		private final List<? extends Element> children;

		public IntRuleWidget(Text name, List<Text> description, String ruleName, GameRules.IntRule rule) {
			super(description);
			this.name = name;
			this.valueWidget = new TextFieldWidget(
				EditGameRulesScreen.this.client.textRenderer, 10, 5, 42, 20, name.shallowCopy().append("\n").append(ruleName).append("\n")
			);
			this.valueWidget.setText(Integer.toString(rule.get()));
			this.valueWidget.setChangedListener(string -> {
				if (rule.validate(string)) {
					this.valueWidget.setEditableColor(14737632);
					EditGameRulesScreen.this.markValid(this);
				} else {
					this.valueWidget.setEditableColor(16711680);
					EditGameRulesScreen.this.markInvalid(this);
				}
			});
			this.children = ImmutableList.of(this.valueWidget);
		}

		@Override
		public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
			EditGameRulesScreen.this.client.textRenderer.draw(matrices, this.name, (float)width, (float)(y + 5), 16777215);
			this.valueWidget.x = width + height - 44;
			this.valueWidget.y = y;
			this.valueWidget.render(matrices, mouseY, i, tickDelta);
		}

		@Override
		public List<? extends Element> children() {
			return this.children;
		}
	}

	@Environment(EnvType.CLIENT)
	public class RuleCategoryWidget extends EditGameRulesScreen.AbstractRuleWidget {
		private final Text name;

		public RuleCategoryWidget(Text text) {
			super(null);
			this.name = text;
		}

		@Override
		public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
			EditGameRulesScreen.this.drawCenteredText(matrices, EditGameRulesScreen.this.client.textRenderer, this.name, width + height / 2, y + 5, 16777215);
		}

		@Override
		public List<? extends Element> children() {
			return ImmutableList.of();
		}
	}

	@Environment(EnvType.CLIENT)
	public class RuleListWidget extends ElementListWidget<EditGameRulesScreen.AbstractRuleWidget> {
		public RuleListWidget(GameRules gameRules) {
			super(EditGameRulesScreen.this.client, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
			final Map<GameRules.RuleCategory, Map<GameRules.RuleKey<?>, EditGameRulesScreen.AbstractRuleWidget>> map = Maps.<GameRules.RuleCategory, Map<GameRules.RuleKey<?>, EditGameRulesScreen.AbstractRuleWidget>>newHashMap();
			GameRules.forEachType(new GameRules.RuleTypeConsumer() {
				@Override
				public void acceptBoolean(GameRules.RuleKey<GameRules.BooleanRule> key, GameRules.RuleType<GameRules.BooleanRule> type) {
					this.createRuleWidget(key, (text, list, string, booleanRule) -> EditGameRulesScreen.this.new BooleanRuleWidget(text, list, string, booleanRule));
				}

				@Override
				public void acceptInt(GameRules.RuleKey<GameRules.IntRule> key, GameRules.RuleType<GameRules.IntRule> type) {
					this.createRuleWidget(key, (text, list, string, intRule) -> EditGameRulesScreen.this.new IntRuleWidget(text, list, string, intRule));
				}

				private <T extends GameRules.Rule<T>> void createRuleWidget(GameRules.RuleKey<T> key, EditGameRulesScreen.RuleWidgetFactory<T> widgetFactory) {
					Text text = new TranslatableText(key.getTranslationKey());
					Text text2 = new LiteralText(key.getName()).formatted(Formatting.YELLOW);
					T rule = gameRules.get(key);
					String string = rule.serialize();
					Text text3 = new TranslatableText("editGamerule.default", new LiteralText(string)).formatted(Formatting.GRAY);
					String string2 = key.getTranslationKey() + ".description";
					List<Text> list;
					String string3;
					if (I18n.hasTranslation(string2)) {
						Builder<Text> builder = ImmutableList.<Text>builder().add(text2);
						Text text4 = new TranslatableText(string2);
						EditGameRulesScreen.this.textRenderer.wrapLines(text4, 150).forEach(builder::add);
						list = builder.add(text3).build();
						string3 = text4.getString() + "\n" + text3.getString();
					} else {
						list = ImmutableList.of(text2, text3);
						string3 = text3.getString();
					}

					((Map)map.computeIfAbsent(key.getCategory(), ruleCategory -> Maps.newHashMap())).put(key, widgetFactory.create(text, list, string3, rule));
				}
			});
			map.entrySet()
				.stream()
				.sorted(java.util.Map.Entry.comparingByKey())
				.forEach(
					entry -> {
						this.addEntry(
							EditGameRulesScreen.this.new RuleCategoryWidget(
								new TranslatableText(((GameRules.RuleCategory)entry.getKey()).getCategory()).formatted(new Formatting[]{Formatting.BOLD, Formatting.YELLOW})
							)
						);
						((Map)entry.getValue())
							.entrySet()
							.stream()
							.sorted(java.util.Map.Entry.comparingByKey(Comparator.comparing(GameRules.RuleKey::getName)))
							.forEach(entryx -> this.addEntry((EntryListWidget.Entry)entryx.getValue()));
					}
				);
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.render(matrices, mouseX, mouseY, delta);
			if (this.isMouseOver((double)mouseX, (double)mouseY)) {
				EditGameRulesScreen.AbstractRuleWidget abstractRuleWidget = this.getEntryAtPosition((double)mouseX, (double)mouseY);
				if (abstractRuleWidget != null) {
					EditGameRulesScreen.this.setTooltipDescription(abstractRuleWidget.description);
				}
			}
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface RuleWidgetFactory<T extends GameRules.Rule<T>> {
		EditGameRulesScreen.AbstractRuleWidget create(Text name, List<Text> description, String ruleName, T rule);
	}
}