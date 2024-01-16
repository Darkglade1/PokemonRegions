package pokeregions.monsters.act1.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.applyToTarget;
import static pokeregions.util.Wiz.dmg;

public class Caterpie extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Caterpie.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Caterpie(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 110.0f, 80.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Caterpie/Caterpie.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
        move2Intent = Intent.ATTACK_DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, pokeregions.cards.pokemonAllyCards.Caterpie.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(target, this, new StrengthPower(target, -pokeregions.cards.pokemonAllyCards.Caterpie.MOVE_1_EFFECT));
                if (!target.hasPower(ArtifactPower.POWER_ID)) {
                    applyToTarget(target, this, new GainStrengthPower(target, pokeregions.cards.pokemonAllyCards.Caterpie.MOVE_1_EFFECT));
                }
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.POISON);
                applyToTarget(target, this, new WeakPower(target, pokeregions.cards.pokemonAllyCards.Caterpie.MOVE_2_WEAK, false));
                break;
            }
        }
        postTurn();
    }

}