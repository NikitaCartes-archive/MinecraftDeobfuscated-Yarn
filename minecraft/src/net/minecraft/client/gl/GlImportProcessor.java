package net.minecraft.client.gl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PathUtil;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;

/**
 * Handles the flattening of "moj_" import strings in the loaded GLSL shader file.
 * Instances of an import are replaced by the contents of the referenced file
 * prefixed by a comment describing the line position and original file location
 * of the import.
 */
@Environment(EnvType.CLIENT)
public abstract class GlImportProcessor {
	private static final String MULTI_LINE_COMMENT_PATTERN = "/\\*(?:[^*]|\\*+[^*/])*\\*+/";
	private static final String SINGLE_LINE_COMMENT_PATTERN = "//[^\\v]*";
	private static final Pattern MOJ_IMPORT_PATTERN = Pattern.compile(
		"(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))"
	);
	private static final Pattern IMPORT_VERSION_PATTERN = Pattern.compile(
		"(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(\\d+))\\b"
	);
	private static final Pattern TRAILING_WHITESPACE_PATTERN = Pattern.compile("(?:^|\\v)(?:\\s|/\\*(?:[^*]|\\*+[^*/])*\\*+/|(//[^\\v]*))*\\z");

	/**
	 * Reads the source code supplied into a list of lines suitable for uploading to
	 * the GL Shader cache.
	 * 
	 * <p>Imports are processed as per the description of this class.
	 */
	public List<String> readSource(String source) {
		GlImportProcessor.Context context = new GlImportProcessor.Context();
		List<String> list = this.parseImports(source, context, "");
		list.set(0, this.readImport((String)list.get(0), context.column));
		return list;
	}

	private List<String> parseImports(String source, GlImportProcessor.Context context, String path) {
		int i = context.line;
		int j = 0;
		String string = "";
		List<String> list = Lists.<String>newArrayList();
		Matcher matcher = MOJ_IMPORT_PATTERN.matcher(source);

		while (matcher.find()) {
			if (!hasBogusString(source, matcher, j)) {
				String string2 = matcher.group(2);
				boolean bl = string2 != null;
				if (!bl) {
					string2 = matcher.group(3);
				}

				if (string2 != null) {
					String string3 = source.substring(j, matcher.start(1));
					String string4 = path + string2;
					String string5 = this.loadImport(bl, string4);
					if (!Strings.isNullOrEmpty(string5)) {
						if (!StringHelper.endsWithLineBreak(string5)) {
							string5 = string5 + System.lineSeparator();
						}

						context.line++;
						int k = context.line;
						List<String> list2 = this.parseImports(string5, context, bl ? PathUtil.getPosixFullPath(string4) : "");
						list2.set(0, String.format(Locale.ROOT, "#line %d %d\n%s", 0, k, this.extractVersion((String)list2.get(0), context)));
						if (!Util.isBlank(string3)) {
							list.add(string3);
						}

						list.addAll(list2);
					} else {
						String string6 = bl ? String.format(Locale.ROOT, "/*#moj_import \"%s\"*/", string2) : String.format(Locale.ROOT, "/*#moj_import <%s>*/", string2);
						list.add(string + string3 + string6);
					}

					int k = StringHelper.countLines(source.substring(0, matcher.end(1)));
					string = String.format(Locale.ROOT, "#line %d %d", k, i);
					j = matcher.end(1);
				}
			}
		}

		String string2x = source.substring(j);
		if (!Util.isBlank(string2x)) {
			list.add(string + string2x);
		}

		return list;
	}

	/**
	 * Converts a line known to contain an import into a fully-qualified
	 * version of itself for insertion as a comment.
	 */
	private String extractVersion(String line, GlImportProcessor.Context context) {
		Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
		if (matcher.find() && isLineValid(line, matcher)) {
			context.column = Math.max(context.column, Integer.parseInt(matcher.group(2)));
			return line.substring(0, matcher.start(1)) + "/*" + line.substring(matcher.start(1), matcher.end(1)) + "*/" + line.substring(matcher.end(1));
		} else {
			return line;
		}
	}

	private String readImport(String line, int start) {
		Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
		return matcher.find() && isLineValid(line, matcher)
			? line.substring(0, matcher.start(2)) + Math.max(start, Integer.parseInt(matcher.group(2))) + line.substring(matcher.end(2))
			: line;
	}

	private static boolean isLineValid(String line, Matcher matcher) {
		return !hasBogusString(line, matcher, 0);
	}

	private static boolean hasBogusString(String string, Matcher matcher, int matchEnd) {
		int i = matcher.start() - matchEnd;
		if (i == 0) {
			return false;
		} else {
			Matcher matcher2 = TRAILING_WHITESPACE_PATTERN.matcher(string.substring(matchEnd, matcher.start()));
			if (!matcher2.find()) {
				return true;
			} else {
				int j = matcher2.end(1);
				return j == matcher.start();
			}
		}
	}

	/**
	 * Called to load an import reference's source code.
	 */
	@Nullable
	public abstract String loadImport(boolean inline, String name);

	/**
	 * A context for the parser to keep track of its current line and caret position in the file.
	 */
	@Environment(EnvType.CLIENT)
	static final class Context {
		int column;
		int line;
	}
}
