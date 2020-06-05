package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

@Environment(EnvType.CLIENT)
public class TextCollector {
	private final List<StringRenderable> field_25260 = Lists.<StringRenderable>newArrayList();

	public void add(StringRenderable stringRenderable) {
		this.field_25260.add(stringRenderable);
	}

	@Nullable
	public StringRenderable getRawCombined() {
		if (this.field_25260.isEmpty()) {
			return null;
		} else {
			return this.field_25260.size() == 1 ? (StringRenderable)this.field_25260.get(0) : StringRenderable.concat(this.field_25260);
		}
	}

	public StringRenderable getCombined() {
		StringRenderable stringRenderable = this.getRawCombined();
		return stringRenderable != null ? stringRenderable : StringRenderable.EMPTY;
	}
}
