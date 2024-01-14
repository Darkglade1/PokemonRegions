package pokeregions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RemoveMonsterAction extends AbstractGameAction {
    AbstractMonster mo;

    public RemoveMonsterAction(AbstractMonster mo) {
        this.actionType = ActionType.SPECIAL;
        this.duration = Settings.ACTION_DUR_FAST;
        this.mo = mo;
    }

    public void update() {
        AbstractDungeon.getCurrRoom().monsters.monsters.remove(mo);
        this.isDone = true;
    }
}


