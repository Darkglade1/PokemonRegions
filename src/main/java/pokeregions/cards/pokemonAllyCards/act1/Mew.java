package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.MewAlly;

import java.util.HashMap;
import java.util.UUID;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.monsters.AbstractPokemonAlly.MOVE_1;
import static pokeregions.monsters.AbstractPokemonAlly.MOVE_2;

@NoPools
public class Mew extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Mew.class.getSimpleName());
    public static final int MAX_STAMINA = 6;

    public HashMap<UUID, Boolean> usedLimitedMoves1 = new HashMap<>();
    public HashMap<UUID, Boolean> usedLimitedMoves2 = new HashMap<>();
    public AbstractAllyPokemonCard originalCard;
    public Mew() {
        super(ID, CardRarity.RARE);
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.overrideWithDescription = true;
        initializeDescriptionFromMoves();
    }

    public void setUsedLimitedMoves(AbstractAllyPokemonCard originalCard) {
        this.originalCard = originalCard;
        if (usedLimitedMoves1.containsKey(this.originalCard.uuid) && usedLimitedMoves1.get(this.originalCard.uuid)) {
            hasUsedMove1 = true;
        } else {
            hasUsedMove1 = false;
        }
        if (usedLimitedMoves2.containsKey(this.originalCard.uuid) && usedLimitedMoves2.get(this.originalCard.uuid)) {
            hasUsedMove2 = true;
        } else {
            hasUsedMove2 = false;
        }
    }

    public void updateUsedLimitedMoves(int move) {
        if (originalCard.move1isLimited && move == MOVE_1) {
            usedLimitedMoves1.put(originalCard.uuid, true);
        }
        if (originalCard.move2isLimited && move == MOVE_2) {
            usedLimitedMoves2.put(originalCard.uuid, true);
        }
    }

    public void resetLimitedTracker() {
        usedLimitedMoves1.clear();
        usedLimitedMoves2.clear();
        originalCard = null;
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new MewAlly(x, y, this);
    }
}