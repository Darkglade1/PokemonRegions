package pokeregions.cards.cardMods;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import pokeregions.PokemonRegions;
import pokeregions.util.TexLoader;

public class ShadowCurseMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(ShadowCurseMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private final float BLOCK_MOD = 1.5F;
    private final float DAMAGE_MOD = 0.5F;
    public static final String STRING = PokemonRegions.makeMonsterPath("Lugia/ShadowCursed.png");
    private static final Texture TEXTURE = TexLoader.getTexture(STRING);
    private static final TextureRegion TEXTURE_REGION = new TextureRegion(TEXTURE);

    @Override
    public AbstractCardModifier makeCopy() {
        return new ShadowCurseMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
        if (card.baseBlock >= 0) {
            card.baseBlock = (int)((float)card.baseBlock * BLOCK_MOD);
        }
        if (card.baseDamage >= 0) {
            card.baseDamage = (int)((float)card.baseDamage * DAMAGE_MOD);
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        sb.draw(TEXTURE_REGION, card.hb.cX - (float) TEXTURE_REGION.getRegionWidth() / 2, card.hb.cY + (card.hb.height / 2) - (float) TEXTURE_REGION.getRegionHeight() / 2, (float) TEXTURE_REGION.getRegionWidth() / 2, (float) TEXTURE_REGION.getRegionHeight() / 2, TEXTURE_REGION.getRegionWidth(), TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
    }
}
