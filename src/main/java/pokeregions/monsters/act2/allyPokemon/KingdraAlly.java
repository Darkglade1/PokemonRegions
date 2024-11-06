package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.CustomIntent.IntentEnums;
import pokeregions.PokemonRegions;
import pokeregions.actions.AllyDamageAllEnemiesAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Kingdra;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Concentrate;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class KingdraAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Kingdra.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public KingdraAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 130.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Kingdra/Kingdra.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = IntentEnums.MASS_ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Kingdra.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new Concentrate(adp(), Kingdra.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.POISON));
                break;
            }
        }
        postTurn();
    }
}