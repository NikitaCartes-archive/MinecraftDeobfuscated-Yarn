package net.minecraft.util;

import com.mojang.util.UndashedUuid;
import java.net.URI;
import java.util.UUID;

public class Urls {
	public static final URI GDPR = URI.create("https://aka.ms/MinecraftGDPR");
	public static final URI EULA = URI.create("https://aka.ms/MinecraftEULA");
	public static final URI PRIVACY_STATEMENT = URI.create("http://go.microsoft.com/fwlink/?LinkId=521839");
	public static final URI JAVA_ATTRIBUTION = URI.create("https://aka.ms/MinecraftJavaAttribution");
	public static final URI JAVA_LICENSES = URI.create("https://aka.ms/MinecraftJavaLicenses");
	public static final URI BUY_JAVA = URI.create("https://aka.ms/BuyMinecraftJava");
	public static final URI JAVA_ACCOUNT_SETTINGS = URI.create("https://aka.ms/JavaAccountSettings");
	public static final URI SNAPSHOT_FEEDBACK = URI.create("https://aka.ms/snapshotfeedback?ref=game");
	public static final URI JAVA_FEEDBACK = URI.create("https://aka.ms/javafeedback?ref=game");
	public static final URI SNAPSHOT_BUGS = URI.create("https://aka.ms/snapshotbugs?ref=game");
	public static final URI MINECRAFT_SUPPORT = URI.create("https://aka.ms/Minecraft-Support");
	public static final URI JAVA_ACCESSIBILITY = URI.create("https://aka.ms/MinecraftJavaAccessibility");
	public static final URI ABOUT_JAVA_REPORTING = URI.create("https://aka.ms/aboutjavareporting");
	public static final URI JAVA_MODERATION = URI.create("https://aka.ms/mcjavamoderation");
	public static final URI JAVA_BLOCKING = URI.create("https://aka.ms/javablocking");
	public static final URI MINECRAFT_SYMLINKS = URI.create("https://aka.ms/MinecraftSymLinks");
	public static final URI JAVA_REALMS_TRIAL = URI.create("https://aka.ms/startjavarealmstrial");
	public static final URI BUY_JAVA_REALMS = URI.create("https://aka.ms/BuyJavaRealms");
	public static final URI REALMS_TERMS = URI.create("https://aka.ms/MinecraftRealmsTerms");
	public static final URI REALMS_CONTENT_CREATOR = URI.create("https://aka.ms/MinecraftRealmsContentCreator");

	public static String getExtendJavaRealmsUrl(String subscriptionId, UUID uuid, boolean trial) {
		return getExtendJavaRealmsUrl(subscriptionId, uuid) + "&ref=" + (trial ? "expiredTrial" : "expiredRealm");
	}

	public static String getExtendJavaRealmsUrl(String subscriptionId, UUID uuid) {
		return "https://aka.ms/ExtendJavaRealms?subscriptionId=" + subscriptionId + "&profileId=" + UndashedUuid.toString(uuid);
	}
}
