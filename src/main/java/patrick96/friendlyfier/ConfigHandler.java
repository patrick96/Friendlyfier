package patrick96.friendlyfier;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public class ConfigHandler {

    public static Property defuseCreeper, friendlyLimit, useWhitelist, blacklist, invulnerable, successMessage, dimensionalSuccessMessage, radiusSuccessMessage;

    public static void init(File file) {
        Configuration config = new Configuration(file, true);
        config.load();

        defuseCreeper = config.get(CATEGORY_GENERAL, "defuseCreeper", true);
        defuseCreeper.comment = "Should a creeper be defused when made friendly (default: true)";

        friendlyLimit = config.get(CATEGORY_GENERAL, "friendlyLimit", 0);
        // TODO add per dimension depending on tracking method used
        friendlyLimit.comment = "Set a limit to how many mobs a player can make friendly. A value of zero means no limit (default: 0)";

        blacklist = config.get(CATEGORY_GENERAL, "blacklist", new String[] {"WitherBoss", "EnderDragon", "Ghast"});
        blacklist.comment = "A list of Mobs that should not be friendlyfied. The names to put here can be found on the Minecraft Wiki as 'Savegame ID'. "
                + "(default: \"WitherBoss\", \"EnderDragon\", \"Ghast\")";

        useWhitelist = config.get(CATEGORY_GENERAL, "useWhitelist", false);
        useWhitelist.comment = "Set to true if the blacklist should be used as a whitelist (default: false)";

        successMessage = config.get(CATEGORY_GENERAL, "successMessage", "@o (@n) has been friendlyfied by @p!");
        successMessage.comment = "Format of the message shown in chat after a mob has been friendlyfied. The message can contain the following variables:"
        + "\n@n Name of the mob in the language minecraft is set to."
        + "\n@t Custom nametag of the mob. If the mob has not been renamed this will be empty."
        + "\n@o Nametag or name. Will yield the nametag if set and the normal name if not."
        + "\n@i The ingame name of the mob, also known as 'Savegame ID'"
        + "\n@p The name of the player that friendlyfied the mob"
        + "\n@h Health of the mob at the time of friendlyfication in half hearts"
        + "\n@f Health of the mob at the time of friendlyfication in full hearts"
        + "\n(default: @o (@n) has been friendlyfied by @p!)";

        dimensionalSuccessMessage = config.get(CATEGORY_GENERAL, "dimensionalSuccessMessage", false);
        dimensionalSuccessMessage.comment = "Should the chat message be sent to all players in the same dimension (default: false)";

        radiusSuccessMessage = config.get(CATEGORY_GENERAL, "radiusSuccessMessage", 0);
        radiusSuccessMessage.comment = "If set higher than 0 the chat message will be sent to all players in a radius of that many meters. If dimensionalSuccessMessage is set to true, this will be ignored (default: 0)";

        invulnerable = config.get(CATEGORY_GENERAL, "invulnerable", true);
        invulnerable.comment = "If set to true friendlyfied mobs will become invulnerable. "
                + "If set to false this will cause friendly Zombie Pigmen to be aggrovated when you hit them."
                + "Friendly Pigmen will still aggrovate if you attack their friends and with this set to true you cannot kill them, you can only run. (default: true)";

        config.save();
    }

}
