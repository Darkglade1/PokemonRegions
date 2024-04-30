package pokeregions.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.PokemonRegions;
import pokeregions.powers.AbstractLambdaPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.*;

public class SicEm extends AbstractEasyCard {

    public static final String ID = PokemonRegions.makeID(SicEm.class.getSimpleName());
    private static final int BONUS_DAMAGE = 50;

    public static final String POWER_ID = makeID("SicEm");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SicEm() {
        super(ID, 0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        exhaust = true;
        setInnate(false, true);
        setMagic(1);
        this.rawDescription = cardStrings.DESCRIPTION + BONUS_DAMAGE + cardStrings.EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(m, adp(), new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.DEBUFF, false, m, BONUS_DAMAGE) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        atb(new DrawCardAction(magicNumber));
    }
}
