package pokeregions.monsters.act1.allyPokemon;

import com.badlogic.gdx.graphics.Color;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Cloyster;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.vfx.ColoredThrowDaggerEffect;
import pokeregions.vfx.WaitEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class CloysterAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Cloyster.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public CloysterAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Cloyster/Cloyster.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEFEND;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Cloyster.MOVE_2_DAMAGE, Cloyster.MOVE_2_HITS);
        defaultMove = MOVE_2;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                block(adp(), Cloyster.MOVE_1_BLOCK);
                break;
            }
            case MOVE_2: {
                runAnim("Spear");
                atb(new VFXAction(new WaitEffect(), 0.2f));
                for (int i = 0; i < multiplier; i++) {
                    atb(new VFXAction(new ColoredThrowDaggerEffect(target.hb.cX, target.hb.cY, Color.CYAN.cpy())));
                    dmg(target, info, AbstractGameAction.AttackEffect.NONE);
                }
                break;
            }
        }
        postTurn();
    }

}