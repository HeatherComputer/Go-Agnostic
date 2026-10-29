package computer.heather.goagnostic.api.util.chat;

public class ChatSymbols {
    

    public static enum Formatting {
        //Shuffles each character repeatedly so it's impossible to read
        OBFUSCATED("§k"),
        //Emboldens the text
        BOLD("§l"),
        //Draws a line through the centre of the text vertically
        STRIKETHROUGH("§m"),
        //Draws a line underneath the text
        UNDERLINE("§n"),
        //Italicises the text  
        ITALIC("§o"),
        //Removes all formatting codes
        RESET("§r");

        private final String code;

        Formatting(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code;
        }

    }

    //Fuck ignoring the U.
    public static enum Colour {

        BLACK("§0"),
        DARK_BLUE("§1"),
        DARK_GREEN("§2"),
        DARK_AQUA("§3"),
        DARK_RED("§4"),
        DARK_PURPLE("§5"),
        GOLD("§6"),
        GRAY("§7"),
        DARK_GRAY("§8"),
        BLUE("§9"),
        GREEN("§a"),
        AQUA("§b"),
        RED("§c"),
        LIGHT_PURPLE("§d"),
        YELLOW("§e"),
        WHITE("§f");

        private final String code;

        Colour(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    
}
