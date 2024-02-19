package pokeregions.powers;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.PokemonRegions;
import pokeregions.util.Wiz;

import static pokeregions.util.Wiz.applyToTarget;
import static pokeregions.util.Wiz.atb;

public class Emerald extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(Emerald.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Emerald(AbstractCreature owner, int hpLoss, int buff) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, buff);
        this.amount2 = hpLoss;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        AbstractMonster lowestHP = Wiz.getEnemies().get(0);
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo.currentHealth < lowestHP.currentHealth) {
                lowestHP = mo;
            }
        }
        this.flash();
        applyToTarget(lowestHP, lowestHP, new StrengthPower(lowestHP, amount));
        atb(new LoseHPAction(lowestHP, lowestHP, amount2));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + amount2 + DESCRIPTIONS[2];
    }
}
