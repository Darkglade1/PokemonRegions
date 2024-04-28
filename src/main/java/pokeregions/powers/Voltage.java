package pokeregions.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class Voltage extends AbstractUnremovablePower  {
    public static final String POWER_ID = PokemonRegions.makeID(Voltage.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Voltage(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.DEBUFF, false, owner, amount);
        this.loadRegion("mastery");
    }

    @Override
    public void onInitialApplication() {
        this.flash();
        atb(new DamageAction(adp(), new DamageInfo(adp(), amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        this.flash();
        atb(new DamageAction(adp(), new DamageInfo(adp(), amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
    }

    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        atb(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
