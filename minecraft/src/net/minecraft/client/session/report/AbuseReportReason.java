package net.minecraft.client.session.report;

import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum AbuseReportReason {
	I_WANT_TO_REPORT_THEM("i_want_to_report_them"),
	HATE_SPEECH("hate_speech"),
	HARASSMENT_OR_BULLYING("harassment_or_bullying"),
	SELF_HARM_OR_SUICIDE("self_harm_or_suicide"),
	IMMINENT_HARM("imminent_harm"),
	DEFAMATION_IMPERSONATION_FALSE_INFORMATION("defamation_impersonation_false_information"),
	ALCOHOL_TOBACCO_DRUGS("alcohol_tobacco_drugs"),
	CHILD_SEXUAL_EXPLOITATION_OR_ABUSE("child_sexual_exploitation_or_abuse"),
	TERRORISM_OR_VIOLENT_EXTREMISM("terrorism_or_violent_extremism"),
	NON_CONSENSUAL_INTIMATE_IMAGERY("non_consensual_intimate_imagery"),
	SEXUALLY_INAPPROPRIATE("sexually_inappropriate");

	private final String id;
	private final Text text;
	private final Text description;

	private AbuseReportReason(final String id) {
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

	public static List<AbuseReportReason> method_62164(AbuseReportType abuseReportType) {
		return switch (abuseReportType) {
			case CHAT -> List.of(SEXUALLY_INAPPROPRIATE);
			case SKIN -> List.of(IMMINENT_HARM, DEFAMATION_IMPERSONATION_FALSE_INFORMATION);
			default -> List.of();
		};
	}
}
