package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Salamence extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Salamence.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Salamence(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Salamence/Salamence.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act3.Salamence.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                if (target.hasPower(VulnerablePower.POWER_ID)) {
                    info.output *= 2;
                }
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case MOVE_2: {
                applyToTarget(adp(), this, new StrengthPower(adp(), pokeregions.cards.pokemonAllyCards.act3.Salamence.MOVE_2_BUFF));
                applyToTarget(adp(), this, new VigorPower(adp(), pokeregions.cards.pokemonAllyCards.act3.Salamence.MOVE_2_VIGOR));
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers(AbstractCreature target) {
        int multiplier = -1;
        if (target.hasPower(VulnerablePower.POWER_ID)) {
            multiplier = 2;
        }
        applyPowers(target, multiplier);
    }

}