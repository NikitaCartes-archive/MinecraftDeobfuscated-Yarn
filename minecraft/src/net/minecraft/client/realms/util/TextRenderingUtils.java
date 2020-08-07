package net.minecraft.client.realms.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextRenderingUtils {
	@VisibleForTesting
	protected static List<String> lineBreak(String text) {
		return Arrays.asList(text.split("\\n"));
	}

	public static List<TextRenderingUtils.Line> decompose(String text, TextRenderingUtils.LineSegment... links) {
		return decompose(text, Arrays.asList(links));
	}

	private static List<TextRenderingUtils.Line> decompose(String text, List<TextRenderingUtils.LineSegment> links) {
		List<String> list = lineBreak(text);
		return insertLinks(list, links);
	}

	private static List<TextRenderingUtils.Line> insertLinks(List<String> lines, List<TextRenderingUtils.LineSegment> links) {
		int i = 0;
		List<TextRenderingUtils.Line> list = Lists.<TextRenderingUtils.Line>newArrayList();

		for (String string : lines) {
			List<TextRenderingUtils.LineSegment> list2 = Lists.<TextRenderingUtils.LineSegment>newArrayList();

			for (String string2 : split(string, "%link")) {
				if ("%link".equals(string2)) {
					list2.add(links.get(i++));
				} else {
					list2.add(TextRenderingUtils.LineSegment.text(string2));
				}
			}

			list.add(new TextRenderingUtils.Line(list2));
		}

		return list;
	}

	public static List<String> split(String line, String delimiter) {
		if (delimiter.isEmpty()) {
			throw new IllegalArgumentException("Delimiter cannot be the empty string");
		} else {
			List<String> list = Lists.<String>newArrayList();
			int i = 0;

			int j;
			while ((j = line.indexOf(delimiter, i)) != -1) {
				if (j > i) {
					list.add(line.substring(i, j));
				}

				list.add(delimiter);
				i = j + delimiter.length();
			}

			if (i < line.length()) {
				list.add(line.substring(i));
			}

			return list;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Line {
		public final List<TextRenderingUtils.LineSegment> segments;

		Line(List<TextRenderingUtils.LineSegment> segments) {
			this.segments = segments;
		}

		public String toString() {
			return "Line{segments=" + this.segments + '}';
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				TextRenderingUtils.Line line = (TextRenderingUtils.Line)o;
				return Objects.equals(this.segments, line.segments);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.segments});
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LineSegment {
		private final String fullText;
		private final String linkTitle;
		private final String linkUrl;

		private LineSegment(String fullText) {
			this.fullText = fullText;
			this.linkTitle = null;
			this.linkUrl = null;
		}

		private LineSegment(String fullText, String linkTitle, String linkUrl) {
			this.fullText = fullText;
			this.linkTitle = linkTitle;
			this.linkUrl = linkUrl;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				TextRenderingUtils.LineSegment lineSegment = (TextRenderingUtils.LineSegment)o;
				return Objects.equals(this.fullText, lineSegment.fullText)
					&& Objects.equals(this.linkTitle, lineSegment.linkTitle)
					&& Objects.equals(this.linkUrl, lineSegment.linkUrl);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.fullText, this.linkTitle, this.linkUrl});
		}

		public String toString() {
			return "Segment{fullText='" + this.fullText + '\'' + ", linkTitle='" + this.linkTitle + '\'' + ", linkUrl='" + this.linkUrl + '\'' + '}';
		}

		public String renderedText() {
			return this.isLink() ? this.linkTitle : this.fullText;
		}

		public boolean isLink() {
			return this.linkTitle != null;
		}

		public String getLinkUrl() {
			if (!this.isLink()) {
				throw new IllegalStateException("Not a link: " + this);
			} else {
				return this.linkUrl;
			}
		}

		public static TextRenderingUtils.LineSegment link(String linkTitle, String linkUrl) {
			return new TextRenderingUtils.LineSegment(null, linkTitle, linkUrl);
		}

		@VisibleForTesting
		protected static TextRenderingUtils.LineSegment text(String fullText) {
			return new TextRenderingUtils.LineSegment(fullText);
		}
	}
}
