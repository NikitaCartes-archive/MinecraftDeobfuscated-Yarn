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
	protected static final int field_33056 = 20;
	protected static final int field_33057 = 7;
	protected static final long MAX_FILE_SIZE = 5368709120L;
	public static final int WHITE = 16777215;
	public static final int field_33060 = 10526880;
	protected static final int field_33061 = 5000268;
	protected static final int field_33062 = 7105644;
	protected static final int field_33063 = 8388479;
	protected static final int field_33064 = 6077788;
	protected static final int RED = 16711680;
	protected static final int field_33036 = 15553363;
	protected static final int field_33037 = -1073741824;
	protected static final int field_33038 = 13413468;
	protected static final int field_33039 = -256;
	protected static final int field_33040 = 3368635;
	protected static final int field_33041 = 7107012;
	protected static final int field_33042 = 8226750;
	protected static final int field_33043 = 16777120;
	protected static final String ADVENTURE_MAPS_IN_1_9_URL = "https://www.minecraft.net/realms/adventure-maps-in-1-9";
	protected static final int field_33045 = 8;
	protected static final int field_33046 = 8;
	protected static final int field_33047 = 8;
	protected static final int field_33048 = 8;
	protected static final int field_33049 = 40;
	protected static final int field_33050 = 8;
	protected static final int field_33051 = 8;
	protected static final int field_33052 = 8;
	protected static final int field_33053 = 64;
	protected static final int field_33054 = 64;
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
