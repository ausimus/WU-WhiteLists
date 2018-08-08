package com.wurmonline.server.questions;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import com.wurmonline.shared.constants.ProtoConstants;
import org.ausimus.wurmunlimited.mods.wl.Initiator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RWL extends Question
{
    private Logger logger = Logger.getLogger(RWL.class.getName());
    public RWL(Creature aResponder, String aTitle, String aQuestion, long aTarget)
    {
        super(aResponder, aTitle, aQuestion, 1337, aTarget);
    }

    public void sendQuestion()
    {
        StringBuilder buf = new StringBuilder(this.getBmlHeader());
        int width = 256;
        int height = 256;
        try
        {
            String name = "";
            buf.append("harray{input{id='name'; maxchars='64'; text='").append(name).append("'}label{text='Name'}}");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        buf.append(createAnswerButton2());
        getResponder().getCommunicator().sendBml(width, height, true, true, buf.toString(), 200, 200, 200, title);
    }

    public void answer(Properties answers)
    {
        setAnswer(answers);
        execute(this);
    }

    private void execute(RWL question)
    {
        Creature responder = question.getResponder();
        String name = question.getAnswer().getProperty("name");

        try
        {
            // In
            FileInputStream in = new FileInputStream(Initiator.dir);
            Properties props = new Properties();
            props.load(in);
            in.close();
            // Out
            FileOutputStream out = new FileOutputStream(Initiator.dir);
            props.setProperty(name, String.valueOf(false));
            props.store(out, null);
            out.close();
        }
        catch (IOException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        // If there online kick them
        for (Player player : Players.getInstance().getPlayers())
        {
            if (Objects.equals(player.getName(), name))
            {
                Players.getInstance().sendGmMessage(
                        null, "WhiteLists:", "Player " + player.getName() +
                                " Disconnected, Not WhiteListed.", false);
                player.getCommunicator().sendShutDown(
                        "Disconnected : You are not on the whitelist.", true);
                player.setSecondsToLogout(5);
            }
        }
        responder.getCommunicator().sendNormalServerMessage("Player " + name + ", is no longer whitelisted. Kicking", ProtoConstants.M_FAIL);
    }
}