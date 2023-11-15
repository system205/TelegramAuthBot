package com.system205.telegram.util;

public final class Utils {
    private Utils(){}

    /**
     * Escapes all special symbols that are used in Markdown V2 <br>
     * To not escape them the symbol should be followed with '/' in the text <br>
     *
     * <p>
     * Examples: <br>
     * <code> "*Hello*, World!" -> "\*Hello\*, World\!" </code> <br>
     * <code>"<b>/*</b>Hello<b>/*</b>, World!" -> "<b>*</b>Hello<b>*</b>, World\!" </code>
     * </p>
     *
     * So, you can write the text as usual and use '/' for MarkdownV2 cases
     * @param text The {@link SendMessage} text to be sent in MarkdownV2 ParseMode.
     * @return Properly escaped Markdown text
     * */
    public static String markdownEscapeCleaner(String text){
        return text.replaceAll("([_*\\[\\]()`~>#+\\-=|{}.!])", "\\\\$1")
            .replaceAll("/\\\\([_*\\[\\]()`~>#+\\-=|{}.!])", "$1");
    }
}
