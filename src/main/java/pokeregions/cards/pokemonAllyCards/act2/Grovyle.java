package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.cards.Leaf;
import pokeregions.cards.pokemonAllyCards.act3.Sceptile;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.GrovyleAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Grovyle extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Grovyle.class.getSimpleName());
    public static final int MOVE_1_EFFECT = 1;
    public static final int MOVE_2_DAMAGE = 10;
    public static final int MOVE_2_ENERGY = 1;
    public static final int MOVE_2_DRAW = 1;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Grovyle() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_EFFECT + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DAMAGE + DESCRIPTIONS[5] + MOVE_2_DRAW + DESCRIPTIONS[6];
        this.cardsToPreview = getCard().makeStatEquivalentCopy();
        initializeDescriptionFromMoves();
    }

    public static AbstractCard getCard() {
        AbstractCard card = new Leaf();
        card.upgrade();
        return card;
    }

    @Override
    public AbstractAllyStarterPokemonCard getNextStage() {
        return new Sceptile();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new GrovyleAlly(x, y, this);
    }
}