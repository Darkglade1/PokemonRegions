package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.optionCards.BecomeAlmighty;
import com.megacrit.cardcrawl.cards.optionCards.FameAndFortune;
import com.megacrit.cardcrawl.cards.optionCards.LiveForever;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.CustomIntent.IntentEnums;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Courage;
import pokeregions.powers.DoomDesire;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Jirachi extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Jirachi.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Jirachi(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 120.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Jirachi/Jirachi.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = IntentEnums.MASS_ATTACK;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act3.Jirachi.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new DoomDesire(adp(), info.output, pokeregions.cards.pokemonAllyCards.act3.Jirachi.MOVE_1_DELAY));
                Wiz.makePowerRemovable(this, Courage.POWER_ID);
                atb(new RemoveSpecificPowerAction(this, this, Courage.POWER_ID));
                break;
            }
            case MOVE_2: {
                ArrayList<AbstractCard> wishes = new ArrayList<>();
                wishes.add(new BecomeAlmighty());
                wishes.add(new FameAndFortune());
                wishes.add(new LiveForever());
                atb(new ChooseOneAction(wishes));
                break;
            }
        }
        postTurn();
    }

}