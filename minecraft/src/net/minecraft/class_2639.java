package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2639 implements class_2596<class_2602> {
	private int field_12122;
	private Suggestions field_12121;

	public class_2639() {
	}

	public class_2639(int i, Suggestions suggestions) {
		this.field_12122 = i;
		this.field_12121 = suggestions;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12122 = arg.method_10816();
		int i = arg.method_10816();
		int j = arg.method_10816();
		StringRange stringRange = StringRange.between(i, i + j);
		int k = arg.method_10816();
		List<Suggestion> list = Lists.<Suggestion>newArrayListWithCapacity(k);

		for (int l = 0; l < k; l++) {
			String string = arg.method_10800(32767);
			class_2561 lv = arg.readBoolean() ? arg.method_10808() : null;
			list.add(new Suggestion(stringRange, string, lv));
		}

		this.field_12121 = new Suggestions(stringRange, list);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12122);
		arg.method_10804(this.field_12121.getRange().getStart());
		arg.method_10804(this.field_12121.getRange().getLength());
		arg.method_10804(this.field_12121.getList().size());

		for (Suggestion suggestion : this.field_12121.getList()) {
			arg.method_10814(suggestion.getText());
			arg.writeBoolean(suggestion.getTooltip() != null);
			if (suggestion.getTooltip() != null) {
				arg.method_10805(class_2564.method_10883(suggestion.getTooltip()));
			}
		}
	}

	public void method_11398(class_2602 arg) {
		arg.method_11081(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11399() {
		return this.field_12122;
	}

	@Environment(EnvType.CLIENT)
	public Suggestions method_11397() {
		return this.field_12121;
	}
}
