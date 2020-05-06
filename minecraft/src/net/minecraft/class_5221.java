package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface class_5221 {
	void error(Text text);

	default void error(String string) {
		this.error(new LiteralText(string));
	}
}
