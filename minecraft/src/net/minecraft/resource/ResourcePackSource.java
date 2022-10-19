package net.minecraft.resource;

import java.util.function.UnaryOperator;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface ResourcePackSource {
	UnaryOperator<Text> NONE_SOURCE_TEXT_SUPPLIER = UnaryOperator.identity();
	ResourcePackSource NONE = create(NONE_SOURCE_TEXT_SUPPLIER, true);
	ResourcePackSource BUILTIN = create(getSourceTextSupplier("pack.source.builtin"), true);
	ResourcePackSource FEATURE = create(getSourceTextSupplier("pack.source.feature"), false);
	ResourcePackSource WORLD = create(getSourceTextSupplier("pack.source.world"), true);
	ResourcePackSource SERVER = create(getSourceTextSupplier("pack.source.server"), true);

	Text decorate(Text packName);

	boolean canBeEnabledLater();

	static ResourcePackSource create(UnaryOperator<Text> sourceTextSupplier, boolean canBeEnabledLater) {
		return new ResourcePackSource() {
			@Override
			public Text decorate(Text packName) {
				return (Text)sourceTextSupplier.apply(packName);
			}

			@Override
			public boolean canBeEnabledLater() {
				return canBeEnabledLater;
			}
		};
	}

	private static UnaryOperator<Text> getSourceTextSupplier(String translationKey) {
		Text text = Text.translatable(translationKey);
		return name -> Text.translatable("pack.nameAndSource", name, text).formatted(Formatting.GRAY);
	}
}
