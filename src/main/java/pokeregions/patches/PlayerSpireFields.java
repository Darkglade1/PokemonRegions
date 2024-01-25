package pokeregions.patches;

import basemod.abstracts.CustomSavable;
import pokeregions.monsters.AbstractPokemonAlly;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import static pokeregions.util.Wiz.adp;

@SpirePatch(
        clz=AbstractPlayer.class,
        method=SpirePatch.CLASS
)
public class PlayerSpireFields implements CustomSavable<Integer>
{
    public static SpireField<CardGroup> pokemonTeam = new SpireField<CardGroup>(() -> new CardGroup(CardGroup.CardGroupType.UNSPECIFIED));
    public static SpireField<AbstractPokemonAlly> activePokemon = new SpireField<>(() -> null);
    public static SpireField<String> mostRecentlyUsedPokemonCardID = new SpireField<>(() -> null);
    public static SpireField<Integer> totalPokemonCaught = new SpireField<>(() -> 0);

    @Override
    public Integer onSave() {
        return totalPokemonCaught.get(adp());
    }

    @Override
    public void onLoad(Integer integer) {
        totalPokemonCaught.set(adp(), integer);
    }
}