package com.wurmonline.server.questions;
import com.wurmonline.server.creatures.Creature;
import org.ausimus.wurmunlimited.mods.wl.Initiator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AWL extends Question
{
    private Logger logger = Logger.getLogger(AWL.class.getName());
    public AWL(Creature aResponder, String aTitle, String aQuestion, long aTarget)
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
        parse(this);
    }

    private void parse(AWL question)
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
            props.setProperty(name, String.valueOf(true));
            props.store(out, null);
            out.close();
        }
        catch (IOException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        responder.getCommunicator().sendNormalServerMessage("Player " + name + ", is now whitelisted.");
    }
}