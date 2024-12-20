package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.QuagsireAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Quagsire extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Quagsire.class.getSimpleName());
    public static final int MOVE_2_DAMAGE = 5;
    public static final int MOVE_2_EFFECT = 1;
    public static final int MOVE_1_STAMINA_COST = 3;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Quagsire() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2];
        this.move2Description = DESCRIPTIONS[3] + MOVE_2_DAMAGE + DESCRIPTIONS[4] + MOVE_2_EFFECT + DESCRIPTIONS[5];
        move1isLimited = true;
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new QuagsireAlly(x, y, this);
    }
}