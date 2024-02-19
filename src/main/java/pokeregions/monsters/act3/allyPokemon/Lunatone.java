package pokeregions.monsters.act3.allyPokemon;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Lunatone extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Lunatone.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Lunatone(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 110.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lunatone/Lunatone.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.MAGIC;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                AbstractPower str = adp().getPower(StrengthPower.POWER_ID);
                AbstractPower dex = adp().getPower(DexterityPower.POWER_ID);
                int strAmt = 0;
                int dexAmt = 0;
                if (str != null) {
                    strAmt = str.amount;
                }
                if (dex != null) {
                    dexAmt = dex.amount;
                }
                atb(new RemoveSpecificPowerAction(adp(), this, StrengthPower.POWER_ID));
                atb(new RemoveSpecificPowerAction(adp(), this, DexterityPower.POWER_ID));
                if (dexAmt != 0) {
                    applyToTarget(adp(), this, new StrengthPower(adp(), dexAmt));
                }
                if (strAmt != 0) {
                    applyToTarget(adp(), this, new DexterityPower(adp(), strAmt));
                }
                applyToTarget(adp(), this, new StrengthPower(adp(), pokeregions.cards.pokemonAllyCards.act3.Lunatone.MOVE_1_EFFECT));
                applyToTarget(adp(), this, new DexterityPower(adp(), pokeregions.cards.pokemonAllyCards.act3.Lunatone.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                atb(new AddTemporaryHPAction(adp(), this, pokeregions.cards.pokemonAllyCards.act3.Lunatone.MOVE_2_EFFECT));
                break;
            }
        }
        postTurn();
    }

}