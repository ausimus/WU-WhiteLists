package org.ausimus.wurmunlimited.mods.wl;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.Player;
import org.ausimus.wurmunlimited.mods.wl.actions.AddWLPlayerAction;
import org.ausimus.wurmunlimited.mods.wl.actions.RemoveWLPlayerAction;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initiator implements WurmServerMod, PlayerLoginListener, Initable, PreInitable, ServerStartedListener, Configurable
{
    private Logger logger = Logger.getLogger(Initiator.class.getName());
    public static String dir;
    public static boolean showWhiteList;

    @Override
    public void onPlayerLogin(Player player)
    {
        // There is no need to WhiteList Higher power players.
        if (player.getPower() < MiscConstants.POWER_HERO)
        {
            Players.getInstance().sendGmMessage(null, "WhiteLists:", "Player " + player.getName() + " Connecting.", false);
            try
            {
                // Call the WhiteList file
                Properties propPlayers = new Properties();
                InputStream inputPlayers = new FileInputStream(dir);
                propPlayers.load(inputPlayers);
                // Bool value of name, if name is non existent (new conn) value is always false.
                Boolean isWhiteListed = Boolean.parseBoolean(propPlayers.getProperty(player.getName()));
                if (!isWhiteListed)
                {
                    if (player.hasLink())
                    {
                        Players.getInstance().sendGmMessage(
                                null, "WhiteLists:", "Player " + player.getName() +
                                        " Disconnected, Not WhiteListed.", false);
                        player.getCommunicator().sendShutDown(
                                "Disconnected : You are not on the whitelist.", true);
                        player.setSecondsToLogout(5);
                    }
                    else
                    {
                        Players.getInstance().logoutPlayer(player);
                    }
                }
            }
            catch (Exception ex)
            {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void init()
    {
        try
        {
            // Create WhiteList file if it does not already exist,
            // directory should always exist as its the folder the jar is contained..
            File file = new File(dir);
            if (!file.exists())
            {
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
        }
        catch (IOException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void preInit()
    {
        ModActions.init();
    }

    @Override
    public void onServerStarted()
    {
        // Register the actions.
        ModActions.registerAction(new AddWLPlayerAction());
        ModActions.registerAction(new RemoveWLPlayerAction());
    }

    @Override
    public void configure(Properties properties)
    {
        showWhiteList = Boolean.parseBoolean(properties.getProperty("showWhiteList", Boolean.toString(showWhiteList)));
        dir = properties.getProperty("whiteListDirectory", dir);
    }
}