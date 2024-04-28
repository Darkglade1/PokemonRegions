package pokeregions.powers;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnLoseTempHpPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.*;

public class ClosedOffHeart extends AbstractUnremovablePower implements OnLoseTempHpPower {
    public static final String POWER_ID = PokemonRegions.makeID(ClosedOffHeart.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int count = 0;
    private final int TEMP_HP_TURNS;
    private final int TEMP_HP;

    public ClosedOffHeart(AbstractCreature owner, int amount, int tempHPTurns, int tempHP) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.TEMP_HP_TURNS = tempHPTurns;
        this.TEMP_HP = tempHP;
        this.priority = 99;
        updateDescription();
        this.loadRegion("corruption");
    }

    @Override
    public void onInitialApplication() {
        atb(new AddTemporaryHPAction(owner, owner, TEMP_HP));
        applyToTarget(adp(), owner, new InvisibleClosedOffHeart(adp(), amount));
    }

    @Override
    public void atEndOfRound() {
        count++;
        if (count >= TEMP_HP_TURNS) {
            flash();
            if (!adp().hasPower(InvisibleClosedOffHeart.POWER_ID)) {
                applyToTarget(adp(), owner, new InvisibleClosedOffHeart(adp(), amount));
            }
            atb(new AddTemporaryHPAction(owner, owner, TEMP_HP));
            count = 0;
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + TEMP_HP_TURNS + DESCRIPTIONS[2] + TEMP_HP + DESCRIPTIONS[3];
    }

    @Override
    public int onLoseTempHp(DamageInfo damageInfo, int i) {
        if (TempHPField.tempHp.get(owner) - i <= 0) {
            makePowerRemovable(adp(), InvisibleClosedOffHeart.POWER_ID);
            atb(new RemoveSpecificPowerAction(adp(), adp(), InvisibleClosedOffHeart.POWER_ID));
        }
        return i;
    }
}
