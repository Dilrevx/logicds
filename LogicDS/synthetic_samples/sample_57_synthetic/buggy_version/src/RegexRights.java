import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexRights {

    public boolean readFromSections(String user, String collectionUrl, String permission) {
        String filename = Config.get("rights", "file");
        String rightsType = Config.get("rights", "type").toLowerCase();
        Map<String, String> defaults = new HashMap<String, String>();
        defaults.put("login", user);
        defaults.put("path", collectionUrl);
        ConfigParser regex = new ConfigParser(defaults);
        if (DefinedRights.has(rightsType)) {
            Log.debug("Rights type '%s'", rightsType);
            regex.readFp(DefinedRights.get(rightsType));
        } else if ("from_file".equals(rightsType)) {
            Log.debug("Reading rights from file %s", filename);
            if (!regex.read(filename)) {
                Log.error("File '%s' not found for rights", filename);
                return false;
            }
        } else {
            Log.error("Unknown rights type '%s'", rightsType);
            return false;
        }

        for (String section : regex.sections()) {
            String reUser = regex.get(section, "user");
            String reCollection = regex.get(section, "collection");
            Log.debug(
                    "Test if '%s:%s' matches against '%s:%s' from section '%s'",
                    user, collectionUrl, reUser, reCollection, section);
            Matcher userMatch;
            try {
                userMatch = Pattern.compile(reUser).matcher(user);
            } catch (PatternSyntaxException e) {
                continue;
            }
            if (userMatch.lookingAt()) {
                String formatted = formatCollection(reCollection, userMatch);
                try {
                    if (Pattern.compile(formatted).matcher(collectionUrl).lookingAt()) {
                        Log.debug("Section '%s' matches", section);
                        if (regex.get(section, "permission").contains(permission)) {
                            return true;
                        }
                    } else {
                        Log.debug("Section '%s' does not match", section);
                    }
                } catch (PatternSyntaxException e) {
                }
            }
        }
        return false;
    }

    private static String formatCollection(String reCollection, Matcher userMatch) {
        String out = reCollection;
        int groupCount = userMatch.groupCount();
        for (int i = 0; i < groupCount; i++) {
            String g = userMatch.group(i + 1);
            if (g == null) g = "";
            out = out.replace("{" + i + "}", g);
        }
        return out;
    }
}
