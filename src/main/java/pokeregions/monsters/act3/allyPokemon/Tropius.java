package pokeregions.monsters.act3.allyPokemon;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.vfx.ColoredMindblastEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Tropius extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Tropius.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Tropius(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tropius/Tropius.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, Intent.ATTACK_DEBUFF, pokeregions.cards.pokemonAllyCards.act3.Tropius.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new EnergizedPower(adp(),  pokeregions.cards.pokemonAllyCards.act3.Tropius.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                atb(new SFXAction("ATTACK_HEAVY"));
                atb(new VFXAction(this, new ColoredMindblastEffect(this.hb.cX, this.hb.cY, false, Color.GREEN.cpy()), 0.1F));
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, pokeregions.cards.pokemonAllyCards.act3.Tropius.MOVE_2_DEBUFF, false));
                break;
            }
        }
        postTurn();
    }

}