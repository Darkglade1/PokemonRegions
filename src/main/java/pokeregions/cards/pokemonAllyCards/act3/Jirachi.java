package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Wish;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.JirachiAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Jirachi extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Jirachi.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 30;
    public static final int MOVE_1_DELAY = 2;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 2;
    public static final int MAX_STAMINA = 6;

    public Jirachi() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DELAY + DESCRIPTIONS[3] + MOVE_1_DAMAGE + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5];
        move2isLimited = true;
        initializeDescriptionFromMoves();
        AbstractCard card = new Wish();
        card.upgrade();
        this.cardsToPreview = card;
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new JirachiAlly(x, y, this);
    }
}