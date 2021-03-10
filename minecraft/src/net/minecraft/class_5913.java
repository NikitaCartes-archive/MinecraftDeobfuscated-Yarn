package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.FileNameUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

@Environment(EnvType.CLIENT)
public abstract class class_5913 {
	private static final Pattern field_29200 = Pattern.compile(
		"(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(#(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))"
	);
	private static final Pattern field_29201 = Pattern.compile(
		"(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(#(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(\\d+))\\b"
	);

	public List<String> method_34229(String string) {
		class_5913.class_5914 lv = new class_5913.class_5914();
		List<String> list = this.method_34232(string, lv, "");
		list.set(0, this.method_34230((String)list.get(0), lv.field_29202));
		return list;
	}

	private List<String> method_34232(String string, class_5913.class_5914 arg, String string2) {
		int i = arg.field_29203;
		int j = 0;
		String string3 = "";
		List<String> list = Lists.<String>newArrayList();
		Matcher matcher = field_29200.matcher(string);

		while (matcher.find()) {
			String string4 = matcher.group(2);
			boolean bl = string4 != null;
			if (!bl) {
				string4 = matcher.group(3);
			}

			if (string4 != null) {
				String string5 = string.substring(j, matcher.start(1));
				String string6 = string2 + string4;
				String string7 = this.method_34233(bl, string6);
				if (!Strings.isEmpty(string7)) {
					arg.field_29203 = arg.field_29203 + 1;
					int k = arg.field_29203;
					List<String> list2 = this.method_34232(string7, arg, bl ? FileNameUtil.method_34675(string6) : "");
					list2.set(0, String.format("#line %d %d\n%s", 0, k, this.method_34231((String)list2.get(0), arg)));
					if (!StringUtils.isBlank(string5)) {
						list.add(string5);
					}

					list.addAll(list2);
				} else {
					String string8 = bl ? String.format("/*#moj_import \"%s\"*/", string4) : String.format("/*#moj_import <%s>*/", string4);
					list.add(string3 + string5 + string8);
				}

				int k = ChatUtil.method_34238(string.substring(0, matcher.end(1)));
				string3 = String.format("#line %d %d", k, i);
				j = matcher.end(1);
			}
		}

		String string4x = string.substring(j);
		if (!StringUtils.isBlank(string4x)) {
			list.add(string3 + string4x);
		}

		return list;
	}

	private String method_34231(String string, class_5913.class_5914 arg) {
		Matcher matcher = field_29201.matcher(string);
		if (matcher.find()) {
			arg.field_29202 = Math.max(arg.field_29202, Integer.parseInt(matcher.group(2)));
			return string.substring(0, matcher.start(1)) + "/*" + string.substring(matcher.start(1), matcher.end(1)) + "*/" + string.substring(matcher.end(1));
		} else {
			return string;
		}
	}

	private String method_34230(String string, int i) {
		Matcher matcher = field_29201.matcher(string);
		return matcher.find() ? string.substring(0, matcher.start(2)) + Math.max(i, Integer.parseInt(matcher.group(2))) + string.substring(matcher.end(2)) : string;
	}

	@Nullable
	public abstract String method_34233(boolean bl, String string);

	@Environment(EnvType.CLIENT)
	static final class class_5914 {
		private int field_29202;
		private int field_29203;

		private class_5914() {
		}
	}
}
