package pokeregions.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;
import pokeregions.monsters.act4.GiratinaEnemy;

public class LostInDistortion extends AbstractUnremovablePower  {
    public static final String POWER_ID = PokemonRegions.makeID(LostInDistortion.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    GiratinaEnemy giratina;

    public LostInDistortion(GiratinaEnemy owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.giratina = owner;
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount > this.amount) {
            damageAmount = this.amount;
        }

        this.amount -= damageAmount;
        if (this.amount < 0) {
            this.amount = 0;
        }

        this.updateDescription();
        return damageAmount;
    }

    @Override
    public void atEndOfRound() {
        if (this.amount == 0) {
            giratina.enterDistortionWorld();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
