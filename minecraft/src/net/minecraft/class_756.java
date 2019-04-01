package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_756 {
	private static final class_2627[] field_3981 = (class_2627[])Arrays.stream(class_1767.values())
		.sorted(Comparator.comparingInt(class_1767::method_7789))
		.map(class_2627::new)
		.toArray(class_2627[]::new);
	private static final class_2627 field_3984 = new class_2627(null);
	public static final class_756 field_3986 = new class_756();
	private final class_2595 field_3976 = new class_2595();
	private final class_2595 field_3978 = new class_2646();
	private final class_2611 field_3977 = new class_2611();
	private final class_2573 field_3983 = new class_2573();
	private final class_2587 field_3982 = new class_2587();
	private final class_2631 field_3987 = new class_2631();
	private final class_2597 field_3979 = new class_2597();
	private final class_600 field_3980 = new class_600();
	private final class_613 field_3985 = new class_613();

	public void method_3166(class_1799 arg) {
		class_1792 lv = arg.method_7909();
		if (lv instanceof class_1746) {
			this.field_3983.method_10913(arg, ((class_1746)lv).method_7706());
			class_824.field_4346.method_3552(this.field_3983);
		} else if (lv instanceof class_1747 && ((class_1747)lv).method_7711() instanceof class_2244) {
			this.field_3982.method_11019(((class_2244)((class_1747)lv).method_7711()).method_9487());
			class_824.field_4346.method_3552(this.field_3982);
		} else if (lv == class_1802.field_8255) {
			if (arg.method_7941("BlockEntityTag") != null) {
				this.field_3983.method_10913(arg, class_1819.method_8013(arg));
				class_310.method_1551()
					.method_1531()
					.method_4618(class_770.field_4155.method_3331(this.field_3983.method_10915(), this.field_3983.method_10911(), this.field_3983.method_10909()));
			} else {
				class_310.method_1551().method_1531().method_4618(class_770.field_4152);
			}

			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			this.field_3980.method_2828();
			if (arg.method_7958()) {
				this.method_3164(this.field_3980::method_2828);
			}

			GlStateManager.popMatrix();
		} else if (lv instanceof class_1747 && ((class_1747)lv).method_7711() instanceof class_2190) {
			GameProfile gameProfile = null;
			if (arg.method_7985()) {
				class_2487 lv2 = arg.method_7969();
				if (lv2.method_10573("SkullOwner", 10)) {
					gameProfile = class_2512.method_10683(lv2.method_10562("SkullOwner"));
				} else if (lv2.method_10573("SkullOwner", 8) && !StringUtils.isBlank(lv2.method_10558("SkullOwner"))) {
					GameProfile var6 = new GameProfile(null, lv2.method_10558("SkullOwner"));
					gameProfile = class_2631.method_11335(var6);
					lv2.method_10551("SkullOwner");
					lv2.method_10566("SkullOwner", class_2512.method_10684(new class_2487(), gameProfile));
				}
			}

			if (class_836.field_4392 != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				class_836.field_4392.method_3581(0.0F, 0.0F, 0.0F, null, 180.0F, ((class_2190)((class_1747)lv).method_7711()).method_9327(), gameProfile, -1, 0.0F);
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
			}
		} else if (lv == class_1802.field_8547) {
			class_310.method_1551().method_1531().method_4618(class_613.field_3592);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			this.field_3985.method_2835();
			if (arg.method_7958()) {
				this.method_3164(this.field_3985::method_2835);
			}

			GlStateManager.popMatrix();
		} else if (lv instanceof class_1747 && ((class_1747)lv).method_7711() == class_2246.field_10502) {
			class_824.field_4346.method_3552(this.field_3979);
		} else if (lv == class_2246.field_10443.method_8389()) {
			class_824.field_4346.method_3552(this.field_3977);
		} else if (lv == class_2246.field_10380.method_8389()) {
			class_824.field_4346.method_3552(this.field_3978);
		} else if (class_2248.method_9503(lv) instanceof class_2480) {
			class_1767 lv3 = class_2480.method_10527(lv);
			if (lv3 == null) {
				class_824.field_4346.method_3552(field_3984);
			} else {
				class_824.field_4346.method_3552(field_3981[lv3.method_7789()]);
			}
		} else {
			class_824.field_4346.method_3552(this.field_3976);
		}
	}

	private void method_3164(Runnable runnable) {
		GlStateManager.color3f(0.5019608F, 0.2509804F, 0.8F);
		class_310.method_1551().method_1531().method_4618(class_918.field_4731);
		class_918.method_4011(class_310.method_1551().method_1531(), runnable, 1);
	}
}
