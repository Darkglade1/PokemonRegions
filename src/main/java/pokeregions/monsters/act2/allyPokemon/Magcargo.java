package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Burn;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Magcargo extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Magcargo.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Magcargo(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Magcargo/Magcargo.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEFEND;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act2.Magcargo.MOVE_1_DAMAGE, pokeregions.cards.pokemonAllyCards.act2.Magcargo.MOVE_1_HITS);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                int bonus = 1;
                if (!target.hasPower(Burn.POWER_ID)) {
                    bonus = 0;
                }
                info.output = info.output * bonus;
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                }
                break;
            }
            case MOVE_2: {
                block(adp(), pokeregions.cards.pokemonAllyCards.act2.Slugma.MOVE_2_BLOCK);
                makeInHand(new com.megacrit.cardcrawl.cards.status.Burn(), pokeregions.cards.pokemonAllyCards.act2.Magcargo.MOVE_2_STATUS);
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers(AbstractCreature target) {
        int multiplier = 1;
        if (!target.hasPower(Burn.POWER_ID)) {
            multiplier = 0;
        }
        applyPowers(target, multiplier);
    }

}