package pokeregions.powers;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class BinaryBody extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(BinaryBody.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BinaryBody(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, 0);
        this.loadRegion("ai");
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner == adp() && info.type == DamageInfo.DamageType.NORMAL) {
            this.flash();
            atb(new RollMoveAction((AbstractMonster)this.owner));
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
