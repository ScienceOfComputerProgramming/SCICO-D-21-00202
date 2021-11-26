//================================================================================
//Peer Model Tool Chain
//Copyright (C) 2021 Eva Maria Kuehn
//--------------------------------------------------------------------------------
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as
//published by the Free Software Foundation, either version 3 of the
//License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
//================================================================================
// SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
// AUTHOR:    Eva Maria Kuehn
// CREATED:   January 2021
//================================================================================

package ui;

//================================================================================
// nice output formatting of messages
public class Out {
	//--------------------------------------------------------------------------------
	// the tab where all traces are aligned
	private static final int traceTabPosition = 28;
	//--------------------------------------------------------------------------------
	// banner line length
	private static final int bannerLen = 70;

	//================================================================================
	// NICE MESSAGE DISPLAY
	//================================================================================
	//--------------------------------------------------------------------------------
	// compute blank alignment towards given tab position for s
	private static String getCharAlignmentTowardsTabPosition(String s, char c, int tabPosition) {
		StringBuffer buf = new StringBuffer();
		int l = s.length();
		while(l < tabPosition) {
			buf.append(c);
			l++;
		}
		return new String(buf);
	}
	
	//================================================================================
	// ADD ALIGNMENT AT THE END OF A STRING
	//================================================================================
	//--------------------------------------------------------------------------------
	// default fu:
	// - add blank alignment towards given tab position to s at the end of s
	public static String addAlignment(String s, int tabPosition) {
		return s.concat(getCharAlignmentTowardsTabPosition(s, ' ', tabPosition));
	}
	//--------------------------------------------------------------------------------
	// add alignment towards g tab position to s at the end of s
	public static String addAlignment(String s) {
		return addAlignment(s, traceTabPosition);
	}
	
	//================================================================================
	// ADD ALIGNMENT BETWEEN 2 STRING
	//================================================================================
	//--------------------------------------------------------------------------------
	// return concatenated string consisting of s1 + char alignment to given tab position + s2 
	public static String addAlignmentInbetween(String s1, char c, int tabPosition, String s2) {
		return s1.concat(getCharAlignmentTowardsTabPosition(s1, c, tabPosition).concat(s2));
	}
	//--------------------------------------------------------------------------------
	// return concatenated string consisting of s1 + blank alignment to tracer tab position + s2 
	public static String addAlignmentInbetween(String s1, String s2) {
		return addAlignmentInbetween(s1, ' ', traceTabPosition, s2);
	}
	
	//================================================================================
	// BORDER(LINE)
	//================================================================================
	//--------------------------------------------------------------------------------
	// return a "borderline" without nl consisting of char 
	public static String border(char c) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < bannerLen; i++) {
			buf.append(c);
		}
		return new String(buf);
	}
	//--------------------------------------------------------------------------------
	// return a "borderline" with nl consisting of char 
	public static String borderline(char c) {
		return border(c) + "\n";
	}

	//================================================================================
	// BANNER
	//================================================================================
	//--------------------------------------------------------------------------------
	// return a nice banner based on the given char of string s 
	public static String banner(String s, char c) {
		StringBuffer buf = new StringBuffer();
		String borderline = borderline(c);
		buf.append(borderline);
		int k = 0;
		for(; k < traceTabPosition -1; k++)
			buf.append(c);
		String helpS = " " + s + " ";
		buf.append(helpS);
		k += helpS.length();
		for(; k < bannerLen; k++)
			buf.append(c);
		buf.append("\n");
		buf.append(borderline);
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
