package net.minecraft.client.util;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

@Environment(EnvType.CLIENT)
public class TextCollector {
	private boolean needsFakeRoot = true;
	@Nullable
	private MutableText root;

	public void add(MutableText text) {
		if (this.root == null) {
			this.root = text;
		} else {
			if (this.needsFakeRoot) {
				this.root = new LiteralText("").append(this.root);
				this.needsFakeRoot = false;
			}

			this.root.append(text);
		}
	}

	@Nullable
	public MutableText getRawCombined() {
		return this.root;
	}

	public MutableText getCombined() {
		return (MutableText)(this.root != null ? this.root : new LiteralText(""));
	}
}
