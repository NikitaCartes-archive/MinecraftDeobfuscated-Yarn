package net.minecraft.client.report;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum AbuseReportReason {
	HATE_SPEECH(5, "hate_speech"),
	TERRORISM_OR_VIOLENT_EXTREMISM(16, "terrorism_or_violent_extremism"),
	CHILD_SEXUAL_EXPLOITATION_OR_ABUSE(17, "child_sexual_exploitation_or_abuse"),
	IMMINENT_HARM(18, "imminent_harm"),
	NON_CONSENSUAL_INTIMATE_IMAGERY(19, "non_consensual_intimate_imagery"),
	HARASSMENT_OR_BULLYING(21, "harassment_or_bullying"),
	DEFAMATION_IMPERSONATION_FALSE_INFORMATION(27, "defamation_impersonation_false_information"),
	SELF_HARM_OR_SUICIDE(31, "self_harm_or_suicide"),
	ALCOHOL_TOBACCO_DRUGS(39, "alcohol_tobacco_drugs");

	private final int banReasonId;
	private final String id;
	private final Text text;
	private final Text description;

	private AbuseReportReason(int banReasonId, String id) {
		this.banReasonId = banReasonId;
		this.id = id.toUpperCase(Locale.ROOT);
		String string2 = "gui.abuseReport.reason." + id;
		this.text = Text.translatable(string2);
		this.description = Text.translatable(string2 + ".description");
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
