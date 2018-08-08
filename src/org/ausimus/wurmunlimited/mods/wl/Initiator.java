package org.ausimus.wurmunlimited.mods.wl;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initiator implements WurmServerMod, PlayerLoginListener, Initable, PreInitable, ServerStartedListener
{
    private Logger logger = Logger.getLogger(Initiator.class.getName());
    public static String dir = "mods/WhiteList/whiteList.txt";
    @Override
    public void onPlayerLogin(Player player)
    {
        if (player.getPower() < MiscConstants.POWER_HERO)
        {
            Players.getInstance().sendGmMessage(null, "WhiteLists:", "Player " + player.getName() + " Connecting.", false);
            try
            {
                Properties propPlayers = new Properties();
                InputStream inputPlayers = new FileInputStream(dir);
                propPlayers.load(inputPlayers);
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
    public void onPlayerLogout(Player player)
    {

    }

    @Override
    public void init()
    {
        try
        {
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
        ModActions.registerAction(new AddWLPlayerAction());
        ModActions.registerAction(new RemoveWLPlayerAction());
    }
}
