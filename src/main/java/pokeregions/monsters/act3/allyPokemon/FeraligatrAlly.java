package pokeregions.monsters.act3.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.actions.FeraligatrWallopAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Feraligatr;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.atb;
import static pokeregions.util.Wiz.dmg;

public class FeraligatrAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Feraligatr.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public FeraligatrAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Feraligatr/Feraligatr.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.ATTACK_DEFEND;
        addMove(MOVE_1, move1Intent, Feraligatr.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent, Feraligatr.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                atb(new VFXAction(new BiteEffect(target.hb.cX, target.hb.cY), 0.3F));
                dmg(target, info);
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                atb(new VFXAction(new BiteEffect(target.hb.cX, target.hb.cY), 0.3F));
                atb(new FeraligatrWallopAction(target, info));
                break;
            }
        }
        postTurn();
    }

}