package pokeregions.monsters.act2.allyPokemon;

import com.badlogic.gdx.graphics.Color;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.ThrowEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Wartortle extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Wartortle.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Wartortle(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Wartortle/Wartortle.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEFEND;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act2.Wartortle.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                //runAnim("Ranged");
                useFastAttackAnimation();
                float duration = 0.5f;
                atb(new VFXAction(ThrowEffect.throwEffect("WaterBlob.png", 1.0f, this.hb, target.hb, Color.BLUE.cpy(), duration, true), duration));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        Wiz.playAudio(ProAudio.LOUD_SPLASH, 2.0f);
                        this.isDone = true;
                    }
                });
                dmg(target, info, AbstractGameAction.AttackEffect.NONE);
                break;
            }
            case MOVE_2: {
                block(adp(), pokeregions.cards.pokemonAllyCards.act2.Wartortle.MOVE_2_BLOCK);
                break;
            }
        }
        postTurn();
    }

}