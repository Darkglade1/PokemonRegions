package code.cards;

import code.monsters.AbstractPokemonMonster;
import code.relics.AbstractEasyRelic;
import code.relics.PokeballBelt;
import code.util.PokemonReward;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;
import static code.util.Wiz.atb;

public class Pokeball extends AbstractEasyCard {
    public final static String ID = makeID(Pokeball.class.getSimpleName());

    public Pokeball() {
        super(ID, 1, AbstractCard.CardType.SKILL, CardRarity.SPECIAL, AbstractCard.CardTarget.ENEMY, CardColor.COLORLESS);
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
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(pokemonCard.cardID));
            AbstractDungeon.effectList.add(new BlockedWordEffect(m, m.hb.cX, m.hb.cY, cardStrings.EXTENDED_DESCRIPTION[4]));
        } else {
            AbstractDungeon.effectList.add(new BlockedWordEffect(m, m.hb.cX, m.hb.cY, cardStrings.EXTENDED_DESCRIPTION[5]));
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
            captureChance = Interpolation.linear.apply(0, 50, progressToThreshold);
        } else if (monsterThreshold < FIFTY_CHANCE_THRESHOLD && monsterThreshold > ONE_HUNDRED_CHANCE_THRESHOLD) {
            float thresholdLength = FIFTY_CHANCE_THRESHOLD - ONE_HUNDRED_CHANCE_THRESHOLD;
            float progressToThreshold = 1 - (monsterThreshold - ONE_HUNDRED_CHANCE_THRESHOLD) / thresholdLength;
            captureChance = Interpolation.linear.apply(50, 100, progressToThreshold);
        } else if (monsterThreshold <= ONE_HUNDRED_CHANCE_THRESHOLD) {
            captureChance = 100.0f;
        }
        return (int)(captureChance);
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