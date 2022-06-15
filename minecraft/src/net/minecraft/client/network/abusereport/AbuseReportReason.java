package net.minecraft.client.network.abusereport;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum AbuseReportReason {
	HATE_SPEECH("hate_speech"),
	TERRORISM_OR_VIOLENT_EXTREMISM("terrorism_or_violent_extremism"),
	CHILD_SEXUAL_EXPLOITATION_OR_ABUSE("child_sexual_exploitation_or_abuse"),
	IMMINENT_HARM("imminent_harm"),
	NON_CONSENSUAL_INTIMATE_IMAGERY("non_consensual_intimate_imagery"),
	HARASSMENT_OR_BULLYING("harassment_or_bullying"),
	PROFANITY("profanity"),
	DEFAMATION_IMPERSONATION_FALSE_INFORMATION("defamation_impersonation_false_information"),
	SELF_HARM_OR_SUICIDE("self_harm_or_suicide"),
	NUDITY_OR_PORNOGRAPHY("nudity_or_pornography"),
	EXTREME_VIOLENCE_OR_GORE("extreme_violence_or_gore"),
	ALCOHOL_TOBACCO_DRUGS("alcohol_tobacco_drugs");

	private final String id;
	private final Text text;
	private final Text description;

	private AbuseReportReason(String id) {
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
}
