package pokeregions.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.*;

public class FutureSight extends AbstractUnremovablePower implements NonStackablePower {
    public static final String POWER_ID = PokemonRegions.makeID(FutureSight.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int turns;

    public FutureSight(AbstractCreature owner, int amount, int turns) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.turns = turns;
        this.loadRegion("mantra");
        updateDescription();
    }

    @Override
    public void duringTurn() {
        if (turns == 1) {
            this.flash();
            atb(new DamageAction(adp(), new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
            makePowerRemovable(this);
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            turns--;
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        if (turns == 1) {
            this.description = DESCRIPTIONS[0] + DESCRIPTIONS[1] + DESCRIPTIONS[3] + amount + DESCRIPTIONS[4];
        } else if (turns == 2) {
            this.description = DESCRIPTIONS[0] + DESCRIPTIONS[2] + DESCRIPTIONS[3] + amount + DESCRIPTIONS[4];
        }
    }
}
