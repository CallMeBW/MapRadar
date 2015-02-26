package de.bwirth.mapradar.listener;
import de.bwirth.mapradar.model.Business;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public interface OnCardClickListener {
    public void onCardClick(Business b);
    public void onCardMenuClicked(Business b);
    public void onMoreButtonClicked(Business b);
    public void onMapButtonClicked(Business b);
}
