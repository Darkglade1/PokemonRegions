package code.cards;

import code.monsters.AbstractPokemonMonster;
import code.relics.AbstractEasyRelic;
import code.relics.PokeballBelt;
import code.vfx.FlexibleGiantTextEffect;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;
import static code.util.Wiz.atb;

public class Pokeball extends AbstractEasyCard {
    public final static String ID = makeID(Pokeball.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Pokeball() {
        super(ID, NAME, 1, AbstractCard.CardType.SKILL, CardRarity.SPECIAL, AbstractCard.CardTarget.ENEMY, CardColor.COLORLESS);
        selfRetain = true;
        purgeOnUse = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractRelic pokeBallBelt = adp().getRelic(PokeballBelt.ID);
        if (pokeBallBelt != null) {
            pokeBallBelt.counter--;
            if (pokeBallBelt instanceof AbstractEasyRelic) {
                ((AbstractEasyRelic) pokeBallBelt).fixDescription();
            }
        }
        int roll = AbstractDungeon.miscRng.random(1, 100);
        int chance = calculateCaptureChance(m);
        if (roll <= chance && m instanceof AbstractPokemonMonster) {
            m.currentBlock = 0;
            atb(new SuicideAction(m));
            AbstractCard pokemonCard = ((AbstractPokemonMonster) m).getAssociatedPokemonCard();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(pokemonCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            atb(new VFXAction(new FlexibleGiantTextEffect(m.hb.cX, m.hb.cY, cardStrings.EXTENDED_DESCRIPTION[4]), 0.5f));
        } else {
            atb(new VFXAction(new FlexibleGiantTextEffect(m.hb.cX, m.hb.cY, cardStrings.EXTENDED_DESCRIPTION[5]), 0.5f));
        }
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) {
            return false;
        }

        int numPokeballs;
        AbstractRelic pokeBallBelt = adp().getRelic(PokeballBelt.ID);
        if (pokeBallBelt == null) {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[3];
            return false;
        }
        numPokeballs = pokeBallBelt.counter;
        if (numPokeballs < 1) {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[3];
            return false;
        } else if (!(m instanceof AbstractPokemonMonster)) {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[2];
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        //changes description when player is hovering monster to show capture chance
        if (mo != null) {
            int captureChance = calculateCaptureChance(mo);
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0] + captureChance + cardStrings.EXTENDED_DESCRIPTION[1];
            this.initializeDescription();
        }
    }

    private int calculateCaptureChance(AbstractMonster mo) {
        float ZERO_CHANCE_THRESHOLD;
        float FIFTY_CHANCE_THRESHOLD;
        float ONE_HUNDRED_CHANCE_THRESHOLD;

        switch (mo.type) {
            case ELITE:
                ZERO_CHANCE_THRESHOLD = 0.3f;
                FIFTY_CHANCE_THRESHOLD = 0.2f;
                ONE_HUNDRED_CHANCE_THRESHOLD = 0.1f;
                break;
            case BOSS:
                ZERO_CHANCE_THRESHOLD = 0.1f;
                FIFTY_CHANCE_THRESHOLD = 0.07f;
                ONE_HUNDRED_CHANCE_THRESHOLD = 0.05f;
                break;
            default:
                ZERO_CHANCE_THRESHOLD = 0.5f;
                FIFTY_CHANCE_THRESHOLD = 0.3f;
                ONE_HUNDRED_CHANCE_THRESHOLD = 0.2f;
                break;

        }
        float captureChance = 0.0f;
        float monsterThreshold = ((float)mo.currentHealth / mo.maxHealth);
        if (monsterThreshold > ZERO_CHANCE_THRESHOLD) {
            captureChance = 0.0f;
        } else if (monsterThreshold <= ZERO_CHANCE_THRESHOLD && monsterThreshold >= FIFTY_CHANCE_THRESHOLD) {
            float thresholdLength = ZERO_CHANCE_THRESHOLD - FIFTY_CHANCE_THRESHOLD;
            float progressToThreshold = 1 - (monsterThreshold - FIFTY_CHANCE_THRESHOLD) / thresholdLength;
            captureChance = 0.5f * progressToThreshold;
        } else if (monsterThreshold < FIFTY_CHANCE_THRESHOLD && monsterThreshold > ONE_HUNDRED_CHANCE_THRESHOLD) {
            float thresholdLength = FIFTY_CHANCE_THRESHOLD - ONE_HUNDRED_CHANCE_THRESHOLD;
            float progressToThreshold = 1 - (monsterThreshold - ONE_HUNDRED_CHANCE_THRESHOLD) / thresholdLength;
            captureChance = progressToThreshold;
        } else if (monsterThreshold <= ONE_HUNDRED_CHANCE_THRESHOLD) {
            captureChance = 1.0f;
        }
        return (int)(captureChance * 100);
    }

    @Override
    public void update() {
        super.update();
        //changes description back when player is not hovering monster
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

}