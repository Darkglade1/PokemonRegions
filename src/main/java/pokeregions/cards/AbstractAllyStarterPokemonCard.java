package pokeregions.cards;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pokeregions.cards.pokemonAllyCards.act1.*;
import pokeregions.cards.pokemonAllyCards.act2.*;
import pokeregions.cards.pokemonAllyCards.act3.*;
import pokeregions.util.Tags;

import java.util.ArrayList;
import java.util.Collections;


public abstract class AbstractAllyStarterPokemonCard extends AbstractAllyPokemonCard {

    public AbstractAllyStarterPokemonCard(final String cardID, final CardRarity rarity) {
        super(cardID,  rarity);
        tags.add(Tags.STARTER_POKEMON);
    }

    public AbstractAllyStarterPokemonCard getNextStage() {
        return null;
    }

    public static ArrayList<String> getTier1StarterIDsGrass() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Bulbasaur.ID);
        list.add(Chikorita.ID);
        list.add(Treecko.ID);
        return list;
    }

    public static ArrayList<String> getTier2StarterIDsGrass() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Ivysaur.ID);
        list.add(Bayleef.ID);
        list.add(Grovyle.ID);
        return list;
    }

    public static ArrayList<String> getTier3StarterIDsGrass() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Venusaur.ID);
        list.add(Meganium.ID);
        list.add(Sceptile.ID);
        return list;
    }

    public static ArrayList<String> getTier1StarterIDsWater() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Squirtle.ID);
        list.add(Totodile.ID);
        return list;
    }

    public static ArrayList<String> getTier2StarterIDsWater() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Wartortle.ID);
        list.add(Croconaw.ID);
        return list;
    }

    public static ArrayList<String> getTier3StarterIDsWater() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Blastoise.ID);
        list.add(Feraligatr.ID);
        return list;
    }

    public static ArrayList<String> getTier1StarterIDsFire() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Charmander.ID);
        list.add(Cyndaquil.ID);
        list.add(Torchic.ID);
        return list;
    }

    public static ArrayList<String> getTier2StarterIDsFire() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Charmeleon.ID);
        list.add(Quilava.ID);
        list.add(Combusken.ID);
        return list;
    }

    public static ArrayList<String> getTier3StarterIDsFire() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Charizard.ID);
        list.add(Typhlosion.ID);
        list.add(Blaziken.ID);
        return list;
    }

    public static ArrayList<String> selectStarters(ArrayList<String> grassStarters, ArrayList<String> waterStarters, ArrayList<String> fireStarters) {
        Collections.shuffle(grassStarters, AbstractDungeon.cardRandomRng.random);
        Collections.shuffle(waterStarters, AbstractDungeon.cardRandomRng.random);
        Collections.shuffle(fireStarters, AbstractDungeon.cardRandomRng.random);
        ArrayList<String> starters = new ArrayList<>();
        starters.add(grassStarters.get(0));
        starters.add(waterStarters.get(0));
        starters.add(fireStarters.get(0));
        return starters;
    }
}
