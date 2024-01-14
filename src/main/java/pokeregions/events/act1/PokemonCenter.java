package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import pokeregions.PokemonRegions;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.ui.PokemonTeamButton;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class PokemonCenter extends PhasedEvent {
    public static final String ID = makeID("PokemonCenter");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractPotion potion;
    private int gold;

    public PokemonCenter() {
        super(ID, title, PokemonRegions.makeEventPath("PokemonCenter.png"));
        potion = adp().getRandomPotion();
        if (potion != null) {
            if (potion.rarity == AbstractPotion.PotionRarity.RARE) {
                gold = 200;
            } else if (potion.rarity == AbstractPotion.PotionRarity.UNCOMMON) {
                gold = 150;
            } else {
                gold = 100;
            }
        }
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(new TextPhase.OptionInfo(hasPotion() ? (OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + potion.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + gold + OPTIONS[5], "g")) : OPTIONS[8]).enabledCondition(this::hasPotion), (i)->{
                    adp().removePotion(potion);
                    AbstractDungeon.effectList.add(new RainingGoldEffect(gold));
                    adp().gainGold(gold);
                    transitionKey("Sell");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[6], "g"), (i)->{
                    PokemonTeamButton.teamWideHeal(1.0f);
                    AbstractDungeon.topPanel.panelHealEffect();
                    transitionKey("Heal");
                }));

        registerPhase("Sell", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Heal", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    private boolean hasPotion() {
        for (AbstractPotion potion : adp().potions) {
            if (!(potion instanceof PotionSlot)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canSpawn() {
        int teamSize = PlayerSpireFields.pokemonTeam.get(adp()).size();
        boolean hasPotion = false;
        for (AbstractPotion potion : adp().potions) {
            if (!(potion instanceof PotionSlot)) {
                hasPotion = true;
                break;
            }
        }
        return hasPotion || teamSize >= 3;
    }
}