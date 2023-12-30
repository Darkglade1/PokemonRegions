package code.ui;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.TopPanelGroup;
import basemod.TopPanelItem;
import basemod.abstracts.CustomSavable;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import code.cards.AbstractAllyPokemonCard;
import code.patches.PlayerSpireFields;
import code.util.TexLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.List;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeUIPath;
import static code.util.Wiz.adp;

public class PokemonTeamButton extends TopPanelItem implements CustomSavable<List<CardSave>> {
    private static final Texture IMG = TexLoader.getTexture(makeUIPath("PokemonTeamButton.png"));
    public static final String ID = makeID(PokemonTeamButton.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;

    public static final int MAX_TEAM_SIZE = 6;

    public PokemonTeamButton() {
        super(IMG, ID);
    }

    @Override
    protected void onClick() {
        CardCrawlGame.sound.play("DECK_OPEN");
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        String tipMsg = TEXT[0] + MAX_TEAM_SIZE + TEXT[1];
        AbstractDungeon.gridSelectScreen.open(pokemonTeam, 999, tipMsg, false, false, false, false);
        AbstractDungeon.overlayMenu.cancelButton.show(TEXT[2]);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont, Integer.toString(pokemonTeam.size()), this.x + 58.0f * Settings.scale, this.y + 25.0f * Settings.scale, Color.WHITE.cpy());
    }

    @Override
    public List<CardSave> onSave() {
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        ArrayList<CardSave> retVal = new ArrayList<>();
        for (AbstractCard card : pokemonTeam.group) {
            System.out.println("SAVING POKEMON");
            System.out.println(card.misc);
            retVal.add(new CardSave(card.cardID, card.timesUpgraded, card.misc));
        }
        return retVal;
    }

    @Override
    public void onLoad(List<CardSave> cardSaves) {
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        for (CardSave s : cardSaves) {
            System.out.println("LOADING POKEMON");
            System.out.println(s.misc);
            AbstractCard card = CardLibrary.getCopy(s.id, s.upgrades, s.misc);
            if (card instanceof AbstractAllyPokemonCard) {
                ((AbstractAllyPokemonCard) card).currentStamina = s.misc;
                ((AbstractAllyPokemonCard) card).initializeDescriptionFromMoves();
            }
            pokemonTeam.addToBottom(card);
        }
        ArrayList<TopPanelItem> topPanelItems = ReflectionHacks.getPrivate(TopPanelHelper.topPanelGroup, TopPanelGroup.class, "topPanelItems");
        for (TopPanelItem item : topPanelItems) {
            if (item instanceof PokemonTeamButton) {
                return;
            }
        }
        BaseMod.addTopPanelItem(this);
    }

    @Override
    protected void onHover() {
        super.onHover();
        if (this.hitbox.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
        if (this.hitbox.hovered) {
            TipHelper.renderGenericTip(1550.0F * Settings.scale, (float)Settings.HEIGHT - 120.0F * Settings.scale, TEXT[3], TEXT[4]);
        }
    }
}