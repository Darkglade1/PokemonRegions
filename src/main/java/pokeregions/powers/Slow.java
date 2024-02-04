package pokeregions.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.atb;

public class Slow extends AbstractEasyPower {
    public static final String POWER_ID = PokemonRegions.makeID(Slow.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied;

    public Slow(AbstractCreature owner, int turnCount, boolean justApplied) {
        super(POWER_ID, NAME, PowerType.DEBUFF, false, owner, turnCount);
        this.amount2 = 0;
        this.justApplied = justApplied;
        this.isTwoAmount = true;
        this.loadRegion("slow");
        updateDescription();
        AbstractDungeon.onModifyPower();
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage * (1.0F + (float)this.amount2 * 0.1F) : damage;
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        amount2++;
        updateDescription();
        AbstractDungeon.onModifyPower();
    }

    @Override
    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            this.amount2 = 0;
            this.amount--;
            this.updateDescription();
            AbstractDungeon.onModifyPower();
            if (amount <= 0) {
                atb(new RemoveSpecificPowerAction(owner, owner, this));
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + (amount2 * 10) + DESCRIPTIONS[2];
    }
}
