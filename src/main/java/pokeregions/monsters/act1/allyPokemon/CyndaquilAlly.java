package pokeregions.monsters.act1.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Cyndaquil;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Burn;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class CyndaquilAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Cyndaquil.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public CyndaquilAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Cyndaquil/Cyndaquil.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent, Cyndaquil.MOVE_1_DAMAGE, Cyndaquil.MOVE_1_HITS);
        addMove(MOVE_2, move2Intent);
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
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                    } else {
                        dmg(target, info, AbstractGameAction.AttackEffect.SLASH_VERTICAL);
                    }
                }
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                applyToTarget(target, this, new Burn(target, Cyndaquil.MOVE_2_DEBUFF));
                makeInHand(new com.megacrit.cardcrawl.cards.status.Burn(), Cyndaquil.MOVE_2_STATUS);
                break;
            }
        }
        postTurn();
    }

}