package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.RhyhornAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Rhyhorn extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Rhyhorn.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 6;
    public static final int MOVE_1_BLOCK = 4;
    public static final int MOVE_2_BLOCK = 12;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 2;
    public static final int MAX_STAMINA = 5;

    public Rhyhorn() {
        super(ID, CardRarity.COMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + DESCRIPTIONS[4] + MOVE_1_BLOCK + DESCRIPTIONS[5];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_BLOCK + DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new RhyhornAlly(x, y, this);
    }
}