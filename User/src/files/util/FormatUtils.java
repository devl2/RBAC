package util;

import java.util.List;

public class FormatUtils {
    public static String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return "";

        int[] colWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != null) {
                    colWidths[i] = Math.max(colWidths[i], row[i].length());
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append(buildBorder(colWidths)).append("\n");

        sb.append("|");
        for (int i = 0; i < headers.length; i++) {
            sb.append(" ").append(padRight(headers[i], colWidths[i])).append(" |");
        }
        sb.append("\n");

        sb.append(buildBorder(colWidths)).append("\n");

        for (String[] row : rows) {
            sb.append("|");
            for (int i = 0; i < headers.length; i++) {
                String cell = i < row.length ? row[i] : "";
                sb.append(" ").append(padRight(cell, colWidths[i])).append(" |");
            }
            sb.append("\n");
        }
        sb.append(buildBorder(colWidths));

        return sb.toString();
    }

    private static String buildBorder(int[] colWidths) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int w : colWidths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }

    public static String formatBox(String text) {
        int length = text.length();
        StringBuilder sb = new StringBuilder();
        sb.append("+").append("-".repeat(length + 2)).append("+\n");
        sb.append(text).append("\n");
        sb.append("+").append("-".repeat(length + 2)).append("+");
        return sb.toString();
    }

    public static String formatHeader(String text) {
        return "\n" + text.toUpperCase() + "\n";
    }

    public static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        if (maxLength <= 3) return "...";
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }

    public static String padLeft(String text, int length) {
        return String.format("%" + length + "s", text);
    }
}