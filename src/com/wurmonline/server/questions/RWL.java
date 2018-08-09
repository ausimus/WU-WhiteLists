package com.wurmonline.server.questions;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import com.wurmonline.shared.constants.ProtoConstants;
import org.ausimus.wurmunlimited.mods.wl.Initiator;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RWL extends Question
{
    private Logger logger = Logger.getLogger(RWL.class.getName());
    private ArrayList<String> wlPlayers = new ArrayList<>();

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
            BufferedReader br = new BufferedReader(new FileReader(Initiator.dir));
            String cl;
            while ((cl = br.readLine()) != null)
            {
                wlPlayers.add(cl);
            }
            if (Initiator.showWhiteList)
            {
                Object[] theArray = wlPlayers.toArray();
                buf.append("text{text='Currently WhiteListed Players.'}");
                for (int x = 0; x < theArray.length; ++x)
                {
                    if (!wlPlayers.get(x).startsWith("#")) /*No deed to un-whitelist the timestamp.*/
                    {
                        String theString = wlPlayers.get(x);
                        buf.append("text{text='").append(
                                theString.substring(/*Remove the =true*/0, theString.length() - 5)).append("'}");
                    }
                }
            }
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
        if (name.equals(""))
        {
            responder.getCommunicator().sendNormalServerMessage(
                    "You did not select a name.", ProtoConstants.M_FAIL);
            return;
        }
        try
        {
            // In
            FileInputStream in = new FileInputStream(Initiator.dir);
            Properties props = new Properties();
            props.load(in);
            in.close();
            // Out
            FileOutputStream out = new FileOutputStream(Initiator.dir);
            props.remove(name);
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
            if (player.getName().equals(name))
            {
                player.getCommunicator().sendShutDown("Disconnected : You are not on the whitelist.", true);
                player.setSecondsToLogout(5);
            }
        }
        responder.getCommunicator().sendNormalServerMessage(
                "Player " + name + ", is no longer whitelisted. If they are online, " +
                        "they are being kicked.", ProtoConstants.M_FAIL);
        RWL q = new RWL(responder, "Remove WhiteListed Player", "", NOID);
        q.sendQuestion();
    }
}