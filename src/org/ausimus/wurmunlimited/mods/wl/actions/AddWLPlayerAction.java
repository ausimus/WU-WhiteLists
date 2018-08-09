package org.ausimus.wurmunlimited.mods.wl.actions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.questions.AWL;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.zones.Zones;
import java.util.Collections;
import java.util.List;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class AddWLPlayerAction implements WurmServerMod, ItemTypes, MiscConstants, ModAction, BehaviourProvider, ActionPerformer
{
    private static short actionID;
    private static ActionEntry actionEntry;

    public AddWLPlayerAction()
    {
        actionID = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionID, "Add Player to WhiteList", "", new int[0]);
        ModActions.registerAction(actionEntry);
    }

    public BehaviourProvider getBehaviourProvider()
    {
        return this;
    }

    public ActionPerformer getActionPerformer()
    {
        return this;
    }

    public short getActionId()
    {
        return actionID;
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target)
    {
        if (source.isWand() && target.isWand() && performer.getPower() > POWER_NONE)
        {
            return Collections.singletonList(actionEntry);
        }
        else
        {
            return null;
        }
    }

    public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter)
    {
        if (source.isWand() && target.isWand() && performer.getPower() > POWER_NONE)
        {
            AWL q = new AWL(performer, "Whitelist Player", "", target.getWurmId());
            q.sendQuestion();
        }
        return true;
    }
}