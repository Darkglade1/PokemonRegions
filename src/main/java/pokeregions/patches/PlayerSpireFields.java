package pokeregions.patches;

import pokeregions.monsters.AbstractPokemonAlly;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(
        clz=AbstractPlayer.class,
        method=SpirePatch.CLASS
)
public class PlayerSpireFields
{
    public static SpireField<CardGroup> pokemonTeam = new SpireField<CardGroup>(() -> new CardGroup(CardGroup.CardGroupType.UNSPECIFIED));
    public static SpireField<AbstractPokemonAlly> activePokemon = new SpireField<>(() -> null);
    public static SpireField<String> mostRecentlyUsedPokemonCardID = new SpireField<>(() -> null);
}