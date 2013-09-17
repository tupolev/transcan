package com.facelift;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FocusedPattern {
	private Integer captureGroup;
	private Pattern pattern;

	public Integer getCaptureGroup() {
		return captureGroup;
	}

	public void setCaptureGroup(Integer captureGroup) {
		this.captureGroup = captureGroup;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public FocusedPattern(Integer captureGroup, Pattern pattern) {
		this.captureGroup = captureGroup;
		this.pattern = pattern;
	}

	public List<String> matchAndCapture(String content) {
		ArrayList<String> captures = new ArrayList<String>();

		try{
			Matcher m = getPattern().matcher(content);
			while (m.find()) {
				if (getCaptureGroup() < (Integer)m.groupCount()) {
					String grp = m.group(getCaptureGroup());
					if (!captures.contains(grp)) {
						captures.add(grp);
					}
				}

			}

		} catch (Exception ex) {
			System.err.println("Warning: While scanning " + content + " for " + getPattern().toString());
		}
		return captures;
	}
}
