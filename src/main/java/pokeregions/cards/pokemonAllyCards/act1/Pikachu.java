package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.PikachuAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Pikachu extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Pikachu.class.getSimpleName());
    public static final int MOVE_2_DAMAGE = 6;
    public static final int MOVE_2_DEBUFF = 1;

    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Pikachu() {
        super(ID, CardRarity.COMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2];
        this.move2Description = DESCRIPTIONS[3] + MOVE_2_DAMAGE + DESCRIPTIONS[4] + MOVE_2_DEBUFF + DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new PikachuAlly(x, y, this);
    }
}