package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.PiloswineAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Piloswine extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Piloswine.class.getSimpleName());
    public static final int MOVE_1_BLOCK = 15;
    public static final int MOVE_2_BLOCK = 5;
    public static final int MOVE_2_EFFECT = 1;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Piloswine() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_BLOCK + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_BLOCK + DESCRIPTIONS[5] + MOVE_2_EFFECT + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new PiloswineAlly(x, y, this);
    }
}