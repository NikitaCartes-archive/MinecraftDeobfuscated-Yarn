package net.minecraft;

public class class_8216 {
	public static final String field_43117 = "https://aka.ms/MinecraftGDPR";
	public static final String field_43118 = "https://aka.ms/MinecraftEULA";
	public static final String field_43119 = "https://aka.ms/MinecraftJavaAttribution";
	public static final String field_43120 = "https://aka.ms/MinecraftJavaLicenses";
	public static final String field_43121 = "https://aka.ms/BuyMinecraftJava";
	public static final String field_43122 = "https://aka.ms/JavaAccountSettings";
	public static final String field_43123 = "https://aka.ms/snapshotfeedback?ref=game";
	public static final String field_43124 = "https://aka.ms/javafeedback?ref=game";
	public static final String field_43125 = "https://aka.ms/snapshotbugs?ref=game";
	public static final String field_43126 = "https://aka.ms/MinecraftJavaAccessibility";
	public static final String field_43127 = "https://aka.ms/aboutjavareporting";
	public static final String field_43128 = "https://aka.ms/mcjavamoderation";
	public static final String field_43129 = "https://aka.ms/javablocking";
	public static final String field_43130 = "https://aka.ms/startjavarealmstrial";
	public static final String field_43131 = "https://aka.ms/BuyJavaRealms";
	public static final String field_43132 = "https://aka.ms/MinecraftRealmsTerms";
	public static final String field_43133 = "https://aka.ms/MinecraftRealmsContentCreator";
	public static final String field_43134 = "https://aka.ms/UpdateMojangAccount";

	public static String method_49720(String string, String string2, boolean bl) {
		return method_49719(string, string2) + "&ref=" + (bl ? "expiredTrial" : "expiredRealm");
	}

	public static String method_49719(String string, String string2) {
		return "https://aka.ms/ExtendJavaRealms?subscriptionId=" + string + "&profileId=" + string2;
	}
}
