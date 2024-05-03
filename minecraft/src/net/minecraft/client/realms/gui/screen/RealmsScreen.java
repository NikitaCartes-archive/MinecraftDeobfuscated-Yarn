package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class RealmsScreen extends Screen {
	protected static final int field_33055 = 17;
	protected static final int field_33057 = 7;
	protected static final long MAX_FILE_SIZE = 5368709120L;
	protected static final int field_33061 = 5000268;
	protected static final int field_33062 = 7105644;
	protected static final int field_33063 = 8388479;
	protected static final int field_33040 = 3368635;
	protected static final int field_33041 = 7107012;
	protected static final int field_39676 = 32;
	private final List<RealmsLabel> labels = Lists.<RealmsLabel>newArrayList();

	public RealmsScreen(Text text) {
		super(text);
	}

	/**
	 * Moved from RealmsConstants in 20w10a
	 */
	protected static int row(int index) {
		return 40 + index * 13;
	}

	protected RealmsLabel addLabel(RealmsLabel label) {
		this.labels.add(label);
		return this.addDrawable(label);
	}

	public Text narrateLabels() {
		return ScreenTexts.joinLines((Collection<? extends Text>)this.labels.stream().map(RealmsLabel::getText).collect(Collectors.toList()));
	}
}
