package net.minecraft.client.report;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum AbuseReportReason {
	FALSE_REPORTING(2, "false_reporting", false),
	HATE_SPEECH(5, "hate_speech", true),
	TERRORISM_OR_VIOLENT_EXTREMISM(16, "terrorism_or_violent_extremism", true),
	CHILD_SEXUAL_EXPLOITATION_OR_ABUSE(17, "child_sexual_exploitation_or_abuse", true),
	IMMINENT_HARM(18, "imminent_harm", true),
	NON_CONSENSUAL_INTIMATE_IMAGERY(19, "non_consensual_intimate_imagery", true),
	HARASSMENT_OR_BULLYING(21, "harassment_or_bullying", true),
	DEFAMATION_IMPERSONATION_FALSE_INFORMATION(27, "defamation_impersonation_false_information", true),
	SELF_HARM_OR_SUICIDE(31, "self_harm_or_suicide", true),
	ALCOHOL_TOBACCO_DRUGS(39, "alcohol_tobacco_drugs", true);

	private final int banReasonId;
	private final String id;
	private final boolean field_39907;
	private final Text text;
	private final Text description;

	private AbuseReportReason(int banReasonId, String string2, boolean bl) {
		this.banReasonId = banReasonId;
		this.id = string2.toUpperCase(Locale.ROOT);
		this.field_39907 = bl;
		String string3 = "gui.abuseReport.reason." + string2;
		this.text = Text.translatable(string3);
		this.description = Text.translatable(string3 + ".description");
	}

	public String getId() {
		return this.id;
	}

	public Text getText() {
		return this.text;
	}

	public Text getDescription() {
		return this.description;
	}

	public boolean method_45032() {
		return this.field_39907;
	}

	@Nullable
	public static Text getText(int banReasonId) {
		for (AbuseReportReason abuseReportReason : values()) {
			if (abuseReportReason.banReasonId == banReasonId) {
				return abuseReportReason.text;
			}
		}

		return null;
	}
}
