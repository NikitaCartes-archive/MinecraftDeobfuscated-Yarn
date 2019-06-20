package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_405 extends class_437 {
	private final class_437 field_2360;
	protected final class_405.class_406 field_18971;
	private final class_2561 field_2364;
	private final boolean field_19232;
	private final List<String> field_2365 = Lists.<String>newArrayList();
	private final String field_19233;
	private final String field_2361;
	private final String field_2359;
	private final String field_2362;
	private class_4286 field_19234;

	public class_405(class_437 arg, class_405.class_406 arg2, class_2561 arg3, class_2561 arg4, boolean bl) {
		super(arg3);
		this.field_2360 = arg;
		this.field_18971 = arg2;
		this.field_2364 = arg4;
		this.field_19232 = bl;
		this.field_19233 = class_1074.method_4662("selectWorld.backupEraseCache");
		this.field_2361 = class_1074.method_4662("selectWorld.backupJoinConfirmButton");
		this.field_2359 = class_1074.method_4662("selectWorld.backupJoinSkipButton");
		this.field_2362 = class_1074.method_4662("gui.cancel");
	}

	@Override
	protected void init() {
		super.init();
		this.field_2365.clear();
		this.field_2365.addAll(this.font.method_1728(this.field_2364.method_10863(), this.width - 50));
		int i = (this.field_2365.size() + 1) * 9;
		this.addButton(
			new class_4185(this.width / 2 - 155, 100 + i, 150, 20, this.field_2361, arg -> this.field_18971.proceed(true, this.field_19234.method_20372()))
		);
		this.addButton(
			new class_4185(this.width / 2 - 155 + 160, 100 + i, 150, 20, this.field_2359, arg -> this.field_18971.proceed(false, this.field_19234.method_20372()))
		);
		this.addButton(new class_4185(this.width / 2 - 155 + 80, 124 + i, 150, 20, this.field_2362, arg -> this.minecraft.method_1507(this.field_2360)));
		this.field_19234 = new class_4286(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.field_19233, false);
		if (this.field_19232) {
			this.addButton(this.field_19234);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 50, 16777215);
		int k = 70;

		for (String string : this.field_2365) {
			this.drawCenteredString(this.font, string, this.width / 2, k, 16777215);
			k += 9;
		}

		super.render(i, j, f);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.minecraft.method_1507(this.field_2360);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_406 {
		void proceed(boolean bl, boolean bl2);
	}
}
