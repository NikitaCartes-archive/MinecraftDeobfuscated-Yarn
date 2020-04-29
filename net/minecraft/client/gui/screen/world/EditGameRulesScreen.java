/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
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
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EditGameRulesScreen
extends Screen {
    private final Consumer<Optional<GameRules>> ruleSaver;
    private RuleListWidget ruleListWidget;
    private final Set<AbstractRuleWidget> invalidRuleWidgets = Sets.newHashSet();
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
        this.ruleListWidget = new RuleListWidget(this.gameRules);
        this.children.add(this.ruleListWidget);
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.ruleSaver.accept(Optional.empty())));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, ScreenTexts.DONE, buttonWidget -> this.ruleSaver.accept(Optional.of(this.gameRules))));
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
        this.drawStringWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
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

    private void markInvalid(AbstractRuleWidget ruleWidget) {
        this.invalidRuleWidgets.add(ruleWidget);
        this.updateDoneButton();
    }

    private void markValid(AbstractRuleWidget ruleWidget) {
        this.invalidRuleWidgets.remove(ruleWidget);
        this.updateDoneButton();
    }

    @Environment(value=EnvType.CLIENT)
    public class RuleListWidget
    extends ElementListWidget<AbstractRuleWidget> {
        public RuleListWidget(final GameRules gameRules) {
            super(EditGameRulesScreen.this.client, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
            final HashMap map = Maps.newHashMap();
            GameRules.forEachType(new GameRules.RuleTypeConsumer(){

                @Override
                public void acceptBoolean(GameRules.RuleKey<GameRules.BooleanRule> key, GameRules.RuleType<GameRules.BooleanRule> type) {
                    this.createRuleWidget(key, (text, list, string, booleanRule) -> new BooleanRuleWidget(text, list, string, (GameRules.BooleanRule)booleanRule));
                }

                @Override
                public void acceptInt(GameRules.RuleKey<GameRules.IntRule> key, GameRules.RuleType<GameRules.IntRule> type) {
                    this.createRuleWidget(key, (text, list, string, intRule) -> new IntRuleWidget(text, list, string, (GameRules.IntRule)intRule));
                }

                private <T extends GameRules.Rule<T>> void createRuleWidget(GameRules.RuleKey<T> key, RuleWidgetFactory<T> widgetFactory) {
                    String string3;
                    ImmutableCollection list;
                    TranslatableText text = new TranslatableText(key.getTranslationKey());
                    MutableText text2 = new LiteralText(key.getName()).formatted(Formatting.YELLOW);
                    T rule = gameRules.get(key);
                    String string = ((GameRules.Rule)rule).serialize();
                    MutableText text3 = new TranslatableText("editGamerule.default", new LiteralText(string)).formatted(Formatting.GRAY);
                    String string2 = key.getTranslationKey() + ".description";
                    if (I18n.hasTranslation(string2)) {
                        ImmutableCollection.ArrayBasedBuilder builder = ImmutableList.builder().add(text2);
                        TranslatableText text4 = new TranslatableText(string2);
                        EditGameRulesScreen.this.textRenderer.wrapLines(text4, 150).forEach(((ImmutableList.Builder)builder)::add);
                        list = ((ImmutableList.Builder)((ImmutableList.Builder)builder).add(text3)).build();
                        string3 = text4.getString() + "\n" + text3.getString();
                    } else {
                        list = ImmutableList.of(text2, text3);
                        string3 = text3.getString();
                    }
                    map.computeIfAbsent(key.getCategory(), ruleCategory -> Maps.newHashMap()).put(key, widgetFactory.create(text, (List<Text>)((Object)list), string3, rule));
                }
            });
            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry2 -> {
                this.addEntry(new RuleCategoryWidget(new TranslatableText(((GameRules.RuleCategory)((Object)((Object)entry2.getKey()))).getCategory()).formatted(Formatting.BOLD, Formatting.YELLOW)));
                ((Map)entry2.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.RuleKey::getName))).forEach(entry -> this.addEntry((EntryListWidget.Entry)entry.getValue()));
            });
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            AbstractRuleWidget abstractRuleWidget;
            super.render(matrices, mouseX, mouseY, delta);
            if (this.isMouseOver(mouseX, mouseY) && (abstractRuleWidget = (AbstractRuleWidget)this.getEntryAtPosition(mouseX, mouseY)) != null) {
                EditGameRulesScreen.this.setTooltipDescription(abstractRuleWidget.description);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class IntRuleWidget
    extends AbstractRuleWidget {
        private final Text name;
        private final TextFieldWidget valueWidget;
        private final List<? extends Element> children;

        public IntRuleWidget(Text name, List<Text> description, String ruleName, GameRules.IntRule rule) {
            super(description);
            this.name = name;
            this.valueWidget = new TextFieldWidget(((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, 10, 5, 42, 20, name.shallowCopy().append("\n").append(ruleName).append("\n"));
            this.valueWidget.setText(Integer.toString(rule.get()));
            this.valueWidget.setChangedListener(string -> {
                if (rule.validate((String)string)) {
                    this.valueWidget.setEditableColor(0xE0E0E0);
                    EditGameRulesScreen.this.markValid(this);
                } else {
                    this.valueWidget.setEditableColor(0xFF0000);
                    EditGameRulesScreen.this.markInvalid(this);
                }
            });
            this.children = ImmutableList.of(this.valueWidget);
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
            ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer.draw(matrices, this.name, (float)width, (float)(y + 5), 0xFFFFFF);
            this.valueWidget.x = width + height - 44;
            this.valueWidget.y = y;
            this.valueWidget.render(matrices, mouseY, i, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface RuleWidgetFactory<T extends GameRules.Rule<T>> {
        public AbstractRuleWidget create(Text var1, List<Text> var2, String var3, T var4);
    }

    @Environment(value=EnvType.CLIENT)
    public class BooleanRuleWidget
    extends AbstractRuleWidget {
        private final ButtonWidget toggleButton;
        private final List<? extends Element> children;

        public BooleanRuleWidget(Text name, List<Text> description, final String ruleName, GameRules.BooleanRule rule) {
            super(description);
            this.toggleButton = new ButtonWidget(10, 5, 220, 20, this.createBooleanRuleText(name, rule.get()), buttonWidget -> {
                boolean bl = !rule.get();
                rule.set(bl, null);
                buttonWidget.setMessage(this.createBooleanRuleText(name, bl));
            }){

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

    @Environment(value=EnvType.CLIENT)
    public class RuleCategoryWidget
    extends AbstractRuleWidget {
        private final Text name;

        public RuleCategoryWidget(Text text) {
            super(null);
            this.name = text;
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
            EditGameRulesScreen.this.drawStringWithShadow(matrices, ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, this.name, width + height / 2, y + 5, 0xFFFFFF);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public abstract class AbstractRuleWidget
    extends ElementListWidget.Entry<AbstractRuleWidget> {
        @Nullable
        private final List<Text> description;

        public AbstractRuleWidget(List<Text> description) {
            this.description = description;
        }
    }
}

