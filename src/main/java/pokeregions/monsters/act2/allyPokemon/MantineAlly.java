package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Mantine;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.applyToTarget;

public class MantineAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Mantine.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public MantineAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mantine/Mantine.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new ArtifactPower(adp(), Mantine.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new WeakPower(mo, Mantine.MOVE_2_EFFECT, false));
                }
                break;
            }
        }
        postTurn();
    }
}