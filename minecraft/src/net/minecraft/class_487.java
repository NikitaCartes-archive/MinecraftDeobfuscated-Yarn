package net.minecraft;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_487 {
	private static final class_487 field_2917 = new class_487();
	private final Random field_2918 = new Random();
	private final String[] field_2916 = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale phnglui mglwnafh cthulhu rlyeh wgahnagl fhtagnbaguette"
		.split(" ");

	private class_487() {
	}

	public static class_487 method_2481() {
		return field_2917;
	}

	public String method_2479(class_327 arg, int i) {
		int j = this.field_2918.nextInt(2) + 3;
		String string = "";

		for (int k = 0; k < j; k++) {
			if (k > 0) {
				string = string + " ";
			}

			string = string + this.field_2916[this.field_2918.nextInt(this.field_2916.length)];
		}

		List<String> list = arg.method_1728(string, i);
		return StringUtils.join(list.size() >= 2 ? list.subList(0, 2) : list, " ");
	}

	public void method_2480(long l) {
		this.field_2918.setSeed(l);
	}
}
